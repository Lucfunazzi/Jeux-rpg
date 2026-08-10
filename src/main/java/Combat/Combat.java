package Combat;

import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import Effets.*;

public class Combat {
    private List<PersonnageBase> equipeJoueur;
    private List<PersonnageBase> equipeAdverse;
    private int toursUtilises  = 0;
    private boolean donnerXP   = true;
    private List<CombatEvent> evenements; // non-null uniquement pendant lancerCombatEnregistre()

    /** Etat d'un personnage a un instant T, pour rejouer le combat visuellement (barres PV/rage). */
    public static final class PersonnageSnapshot {
        public final String nom;
        public final String role;
        public final String rarete;
        public final int niveau;
        public final double vie;
        public final double vieMax;
        public final double rage;
        public final boolean vivant;
        public final boolean coteJoueur;
        public final List<String> effets;
        public final double pointsBouclier;

        PersonnageSnapshot(PersonnageBase p, boolean coteJoueur) {
            this.nom        = p.getNom();
            this.role       = p.getRole();
            this.rarete     = p.getRarete();
            this.niveau     = p.getNiveau();
            this.vie        = Math.max(0, p.getVie());
            this.vieMax     = p.getVieMax();
            this.rage       = p.getRage();
            this.vivant     = p.estVivant();
            this.coteJoueur = coteJoueur;
            this.effets     = new ArrayList<>();
            for (Effet e : p.getEffetsActifs()) {
                if (!e.estTermine()) this.effets.add(e.getNom());
            }
            Bouclier bouclier = p.getEffet(Bouclier.class);
            this.pointsBouclier = bouclier != null ? bouclier.getPointsBouclier() : 0;
        }
    }

    /** Une etape du combat (une action, un tick d'effets, un debut de tour ou la fin du combat). */
    public static final class CombatEvent {
        public final String titre;
        public final List<String> lignes;
        public final List<PersonnageSnapshot> etat;
        public final boolean finDeCombat;
        /** Non-null uniquement quand cet evenement est le declenchement d'une speciale/ultime : nom de
         *  l'attaque (via PersonnageBase.getNomsAttaques()), pour l'affichage d'une banniere cote UI. */
        public final String actionNom;
        /** true si actionNom designe un ultime (banniere plus marquee), false si c'est une speciale. */
        public final boolean estUltime;

        CombatEvent(String titre, List<String> lignes, List<PersonnageSnapshot> etat, boolean finDeCombat,
                    String actionNom, boolean estUltime) {
            this.titre       = titre;
            this.lignes      = lignes;
            this.etat        = etat;
            this.finDeCombat = finDeCombat;
            this.actionNom   = actionNom;
            this.estUltime   = estUltime;
        }
    }

    public Combat(List<PersonnageBase> equipeJoueur,
                  List<PersonnageBase> equipeAdverse) {
        this.equipeJoueur  = equipeJoueur;
        this.equipeAdverse = equipeAdverse;
    }

    /** Constructeur pour les combats sans gain d'XP (arène). */
    public Combat(List<PersonnageBase> equipeJoueur,
                  List<PersonnageBase> equipeAdverse,
                  boolean donnerXP) {
        this.equipeJoueur  = equipeJoueur;
        this.equipeAdverse = equipeAdverse;
        this.donnerXP      = donnerXP;
    }


    // ORDRE ET UTILITAIRES

    public List<PersonnageBase> ordreDattaque() {
        List<PersonnageBase> tousLesPersos = new ArrayList<>();
        for (PersonnageBase perso : equipeJoueur)
            if (perso.estVivant()) tousLesPersos.add(perso);
        for (PersonnageBase perso : equipeAdverse)
            if (perso.estVivant()) tousLesPersos.add(perso);

        // Mélange d'abord pour que les ex-aequo en vitesse soient résolus aléatoirement
        // sans violer le contrat de transitvité du Comparator
        Collections.shuffle(tousLesPersos);
        tousLesPersos.sort((p1, p2) -> Double.compare(p2.getVitesse(), p1.getVitesse()));
        return tousLesPersos;
    }

    public static PersonnageBase cibleMoinsPv(List<PersonnageBase> equipe) {
        PersonnageBase cible = null;
        for (PersonnageBase perso : equipe) {
            if (perso.estVivant()) {
                if (cible == null || perso.getVie() < cible.getVie())
                    cible = perso;
            }
        }
        return cible;
    }

    public boolean equipeKO(List<PersonnageBase> equipe) {
        for (PersonnageBase perso : equipe)
            if (perso.estVivant()) return false;
        return true;
    }

    // CIBLAGE

    public static PersonnageBase choisirCible(PersonnageBase attaquant, List<PersonnageBase> equipeEnnemie) {
        Provocation provoc = attaquant.getEffet(Provocation.class);
        if (provoc != null) {
            PersonnageBase cibleForcee = provoc.getSource();
            if (equipeEnnemie.contains(cibleForcee) && cibleForcee.estVivant()) {
                System.out.println("[Provocation] " + attaquant.getNom() + " est force d'attaquer " + cibleForcee.getNom() + " !");
                return cibleForcee;
            }
        }

        List<PersonnageBase> ciblesPossibles = equipeEnnemie;
        Peur peur = attaquant.getEffet(Peur.class);
        if (peur != null) {
            PersonnageBase cibleEvitee = peur.getSource();
            List<PersonnageBase> sansCibleEvitee = new ArrayList<>();
            for (PersonnageBase p : equipeEnnemie) if (p != cibleEvitee) sansCibleEvitee.add(p);
            if (!sansCibleEvitee.isEmpty()) {
                System.out.println("[Peur] " + attaquant.getNom() + " evite d'attaquer " + cibleEvitee.getNom() + " !");
                ciblesPossibles = sansCibleEvitee;
            }
        }

        PersonnageBase cible = cibleParRole(ciblesPossibles, "Tank");
        if (cible != null) return cible;
        cible = cibleParRole(ciblesPossibles, "DPS");
        if (cible != null) return cible;
        cible = cibleParRole(ciblesPossibles, "Support");
        if (cible != null) return cible;

        return cibleMoinsPv(ciblesPossibles);
    }

    /**
     * Role prioritaire encore vivant dans l'equipe, pour les attaques de zone qui ciblent
     * un role precis (ex: ultimes "tous les DPS"). Ordre de repli : DPS > Tank > Support,
     * pour ne jamais gaspiller un tour si le role prefere n'a plus de survivant.
     */
    public static String rolePrioritaireVivant(List<PersonnageBase> equipe) {
        if (cibleParRole(equipe, "DPS")     != null) return "DPS";
        if (cibleParRole(equipe, "Tank")    != null) return "Tank";
        return "Support";
    }

    /** Membre vivant du role donne avec le moins de PV, ou null si aucun n'est vivant dans ce role. */
    public static PersonnageBase cibleParRole(List<PersonnageBase> equipe, String role) {
        PersonnageBase meilleure = null;
        for (PersonnageBase perso : equipe) {
            if (perso.estVivant() && perso.getRole().equals(role)) {
                if (meilleure == null || perso.getVie() < meilleure.getVie())
                    meilleure = perso;
            }
        }
        return meilleure;
    }

    // BOUCLE DE COMBAT

    /**
     * Lance le combat comme lancerCombat(), mais enregistre chaque etape
     * (action, tick d'effets, tour, fin) avec un instantane des PV/rage de
     * tous les personnages, pour permettre une relecture visuelle tour par tour.
     */
    public List<CombatEvent> lancerCombatEnregistre() {
        evenements = new ArrayList<>();
        lancerCombat();
        List<CombatEvent> resultat = evenements;
        evenements = null;
        return resultat;
    }

    private void enregistrer(String titre, List<String> lignes) {
        enregistrer(titre, lignes, false, null, false);
    }

    private void enregistrer(String titre, List<String> lignes, boolean fin) {
        enregistrer(titre, lignes, fin, null, false);
    }

    private void enregistrer(String titre, List<String> lignes, boolean fin, String actionNom, boolean estUltime) {
        if (evenements == null) return;
        evenements.add(new CombatEvent(titre, new ArrayList<>(lignes), snapshotEquipes(equipeJoueur, equipeAdverse), fin, actionNom, estUltime));
    }

    /**
     * Instantane des deux equipes (PV/rage/etat) a un instant T.
     * A appeler AVANT lancerCombatEnregistre() pour recuperer l'etat de depart
     * (equipes a pleine vie) que l'ecran de combat utilisera comme point de reference.
     */
    public static List<PersonnageSnapshot> snapshotEquipes(List<PersonnageBase> equipeJoueur,
                                                            List<PersonnageBase> equipeAdverse) {
        List<PersonnageSnapshot> etat = new ArrayList<>();
        for (PersonnageBase p : equipeJoueur)  etat.add(new PersonnageSnapshot(p, true));
        for (PersonnageBase p : equipeAdverse) etat.add(new PersonnageSnapshot(p, false));
        return etat;
    }

    public void lancerCombat() {
        System.out.println("\n=== COMBAT ===\n");

        int numeroTour = 1;
        final int MAX_TOURS = 20;

        while (!equipeKO(equipeJoueur) && !equipeKO(equipeAdverse) && numeroTour <= MAX_TOURS) {
            System.out.println("\n--- Tour " + numeroTour + " ---");
            toursUtilises = numeroTour;
            enregistrer("Tour " + numeroTour, List.of());

            afficherEffetsGroupes(equipeJoueur);
            afficherEffetsGroupes(equipeAdverse);
            List<String> logEffets = new ArrayList<>();
            for (PersonnageBase perso : equipeJoueur) if (perso.estVivant()) perso.appliquerEffets(logEffets);
            for (PersonnageBase perso : equipeAdverse) if (perso.estVivant()) perso.appliquerEffets(logEffets);
            for (String ligne : logEffets) System.out.println(ligne);
            if (!logEffets.isEmpty()) enregistrer("Effets", logEffets);

            afficherRage(equipeJoueur);
            afficherRage(equipeAdverse);

            List<PersonnageBase> ordre = ordreDattaque();

            System.out.println("Ordre d'attaque :");
            for (PersonnageBase perso : ordre)
                System.out.println("- " + perso.getNom() + " (vitesse : " + perso.getVitesse() + ")");
            System.out.println();

            for (PersonnageBase attaquant : ordre) {
                if (!attaquant.estVivant() || equipeKO(equipeJoueur) || equipeKO(equipeAdverse)) continue;

                boolean estDuCoteJoueur = equipeJoueur.contains(attaquant);

                if (attaquant.aEffet(Petrification.class)) {
                    String msg = "[PETRIFICATION] " + attaquant.getNom() + " est petrife et ne peut pas agir !";
                    System.out.println(msg);
                    enregistrer(attaquant.getNom(), List.of(msg));
                    continue;
                }
                if (attaquant.aEffet(Gel.class)) {
                    String msg = "[GEL] " + attaquant.getNom() + " est gele et passe son tour !";
                    System.out.println(msg);
                    enregistrer(attaquant.getNom(), List.of(msg));
                    continue;
                }
                if (attaquant.aEffet(Etourdissement.class)) {
                    String msg = "[ETOURDISSEMENT] " + attaquant.getNom() + " est etourdi et ne peut pas agir !";
                    System.out.println(msg);
                    enregistrer(attaquant.getNom(), List.of(msg));
                    continue;
                }
                if (attaquant.aEffet(Sommeil.class)) {
                    String msg = "[SOMMEIL] " + attaquant.getNom() + " dort profondement...";
                    System.out.println(msg);
                    enregistrer(attaquant.getNom(), List.of(msg));
                    continue;
                }
                Paralysie paralysie = attaquant.getEffet(Paralysie.class);
                if (paralysie != null && !paralysie.peutAgir()) continue;

                List<PersonnageBase> equipeEnnemie = equipeJoueur.contains(attaquant) ? equipeAdverse : equipeJoueur;
                List<PersonnageBase> equipeAlliee  = equipeJoueur.contains(attaquant) ? equipeJoueur : equipeAdverse;

                List<PersonnageBase> ennemisVirtuels = equipeEnnemie;
                List<PersonnageBase> alliesVirtuels  = equipeAlliee;

                Confusion confusion = attaquant.getEffet(Confusion.class);
                PersonnageBase cible;

                if (confusion != null) {
                    cible = confusion.redirigerVersAllie(equipeAlliee, attaquant);
                    if (cible != null) {
                        ennemisVirtuels = equipeAlliee;
                        alliesVirtuels  = equipeEnnemie;
                    } else {
                        cible = choisirCible(attaquant, equipeEnnemie);
                    }
                } else {
                    cible = choisirCible(attaquant, equipeEnnemie);
                }

                if (cible == null) continue;

                // Le log est créé ici et imprimé après chaque action
                List<String> log = new ArrayList<>();
                String actionDeclenchee = null;
                boolean actionEstUltime = false;

                if (attaquant.getRage() >= 100) {
                    Silence silenceUltime = attaquant.getEffet(Silence.class);
                    if (silenceUltime != null && silenceUltime.empecheSpeciale()) {
                        System.out.println("[SILENCE] " + attaquant.getNom() + " est reduit au silence ! Attaque de base.");
                        log.add("[SILENCE] " + attaquant.getNom() + " est reduit au silence ! Attaque de base.");
                        attaquer(attaquant, cible, log);
                        attaquant.ajouterRage(20);
                    } else {
                        System.out.println("\n[ULTIME] " + attaquant.getNom() + " declenche son ultime !");
                        log.add("[ULTIME] " + attaquant.getNom() + " declenche son ultime !");
                        attaquant.attaqueUltime(alliesVirtuels, ennemisVirtuels, log);
                        attaquant.reinitialiserRage();
                        String[] nomsAttaques = attaquant.getNomsAttaques();
                        actionDeclenchee = (nomsAttaques != null && nomsAttaques.length > 2) ? nomsAttaques[2] : "Ultime";
                        actionEstUltime = true;
                    }

                } else if (attaquant.getRage() >= 50 && !attaquant.getSpecialeUtilisee()) {
                    Silence silence = attaquant.getEffet(Silence.class);
                    if (silence != null && silence.empecheSpeciale()) {
                        System.out.println("[SILENCE] " + attaquant.getNom() + " est reduit au silence ! Attaque de base.");
                        log.add("[SILENCE] " + attaquant.getNom() + " est reduit au silence ! Attaque de base.");
                        attaquer(attaquant, cible, log);
                        attaquant.ajouterRage(20);
                    } else {
                        System.out.println("\n[SPECIALE] " + attaquant.getNom() + " utilise sa competence speciale !");
                        log.add("[SPECIALE] " + attaquant.getNom() + " utilise sa competence speciale !");
                        attaquant.attaqueSpeciale(cible, alliesVirtuels, ennemisVirtuels, log);
                        attaquant.setSpecialeUtilisee(true);
                        String[] nomsAttaques = attaquant.getNomsAttaques();
                        actionDeclenchee = (nomsAttaques != null && nomsAttaques.length > 1) ? nomsAttaques[1] : "Speciale";
                    }
                } else {
                    log.add(attaquant.getNom() + " lance une attaque de base sur " + cible.getNom());
                    boolean attaquantEstJoueur = equipeJoueur.contains(attaquant);
                    attaquant.attaqueBase(cible,
                            attaquantEstJoueur ? equipeJoueur  : equipeAdverse,
                            attaquantEstJoueur ? equipeAdverse : equipeJoueur,
                            log);
                    attaquant.ajouterRage(50);
                    log.add("[RAGE] " + attaquant.getNom() + " : "
                            + String.format("%.0f", attaquant.getRage()) + "/100");
                }

                // Affichage du log de la compétence
                for (String ligne : log) {
                    System.out.println(ligne);
                }
                if (!log.isEmpty()) enregistrer(attaquant.getNom(), log, false, actionDeclenchee, actionEstUltime);
            }
            numeroTour++;
        }

        System.out.println("\n=== FIN DU COMBAT ===");
        String resultatFinal;
        if (equipeKO(equipeAdverse)) {
            resultatFinal = "Votre equipe a gagne !";
            System.out.println(resultatFinal);
            if (donnerXP) donnerExperience();
        } else if (equipeKO(equipeJoueur)) {
            resultatFinal = "Votre equipe a perdu !";
            System.out.println(resultatFinal);
        } else {
            resultatFinal = "Combat termine apres " + MAX_TOURS + " tours sans vainqueur. Defaite par epuisement.";
            System.out.println(resultatFinal);
        }
        enregistrer("Fin du combat", List.of(resultatFinal), true);
    }

    private void afficherEffetsGroupes(List<PersonnageBase> equipe) {
        java.util.Map<String, java.util.List<String>> effetsParNom = new java.util.LinkedHashMap<>();
        for (PersonnageBase perso : equipe) {
            if (!perso.estVivant()) continue;
            for (Effets.Effet e : perso.getEffetsActifs()) {
                if (e.estTermine()) continue;
                effetsParNom.computeIfAbsent(e.getNom(), k -> new java.util.ArrayList<>())
                            .add(perso.getNom());
            }
        }
        for (java.util.Map.Entry<String, java.util.List<String>> entry : effetsParNom.entrySet()) {
            String noms = String.join(", ", entry.getValue());
            System.out.println("[" + entry.getKey() + "] " + noms);
        }
    }

    private void afficherRage(List<PersonnageBase> equipe) {
        for (PersonnageBase perso : equipe) {
            if (perso.estVivant() && perso.getRage() > 0) {
                System.out.println(perso.getNom() + " — Rage : "
                        + String.format("%.0f", perso.getRage()) + "/100");
            }
        }
    }

    // MECANIQUE D'ATTAQUE

    public static boolean attaqueTouche(PersonnageBase attaquant, PersonnageBase cible) {
        // Précision réduit l'esquive : esquive effective = esquive / précision
        // Ex : 30% esquive vs 120% précision → 30/120 = 25% esquive réelle
        double precisionFactor = attaquant.getTauxPrecisions() / 100.0;
        double esquiveEffective = Math.min(cible.getTauxEsquives() / precisionFactor, 0.90);
        return Math.random() >= esquiveEffective;
    }

    /** Degats bruts d'une attaque de base (100% ATK) : la defense n'est appliquee qu'une
     *  seule fois, dans PersonnageBase.subirDegats(), comme pour les speciales/ultimes. */
    public static double calculerDegats(PersonnageBase attaquant, PersonnageBase cible) {
        return attaquant.getAttaque();
    }

    /** @param cible Fournit le taux de Contre (100 = neutre) qui reduit les chances de subir un critique. */
    public static boolean estCritique(PersonnageBase attaquant, PersonnageBase cible) {
        double critiqueEffectif = Math.min(attaquant.getTauxCritique() / (cible.getTauxContre() / 100.0), 0.90);
        return Math.random() < critiqueEffectif;
    }

    public static boolean attaquer(PersonnageBase attaquant, PersonnageBase cible, List<String> log) {
        if (!attaqueTouche(attaquant, cible)) {
            log.add(cible.getNom() + " esquive !");
            return false;
        }

        double degats = calculerDegats(attaquant, cible);
        if (estCritique(attaquant, cible)) {
            degats *= attaquant.getTauxDegatCritique();
            log.add("Coup critique !");
        }

        double pvAvant = cible.getVie();
        PersonnageBase.ResultatDegats resultat = cible.subirDegats(degats, attaquant.getTauxAttaqueS());

        if (resultat.invincible) {
            log.add(cible.getNom() + " est invincible ! Degats bloques.");
        } else {
            if (resultat.bouclierAbsorbe) {
                log.add("[BOUCLIER] " + cible.getNom() + " absorbe "
                        + String.format("%.0f", resultat.degatsAbsorbesBouclier)
                        + " degats (" + String.format("%.0f", resultat.pvRestantsBouclier) + " PV restants)");
            }
            if (resultat.bloque) {
                log.add("→ " + String.format("%.0f", resultat.degatsAppliques)
                        + " degats sur " + cible.getNom()
                        + " (" + String.format("%.0f", pvAvant) + " → "
                        + cible.getNom() + " bloque ! Degats reduits a "
                        + String.format("%.0f", resultat.degatsAppliques) + " → "
                        + (cible.estVivant() ? String.format("%.0f", cible.getVie()) + " PV)" : "KO !)"));
            } else {
                log.add("→ " + String.format("%.0f", resultat.degatsAppliques)
                        + " degats sur " + cible.getNom()
                        + " (" + String.format("%.0f", pvAvant) + " → "
                        + (cible.estVivant() ? String.format("%.0f", cible.getVie()) + " PV)" : "KO !)"));
            }
        }

        if (resultat.ko) {
            log.add(cible.getNom() + " est KO !");
        }

        if (resultat.bloque && attaquant.estVivant()) {
            double degatsRenvoi = cible.getAttaqueBase() * cible.getDegatsRenvoi();
            double pvAvantRenvoi = attaquant.getVie();
            PersonnageBase.ResultatDegats resultatRenvoi = attaquant.subirDegats(degatsRenvoi);
            log.add("[RENVOI] " + cible.getNom() + " renvoie "
                    + String.format("%.0f", degatsRenvoi) + " degats a " + attaquant.getNom()
                    + " (" + String.format("%.0f", pvAvantRenvoi) + " → "
                    + (attaquant.estVivant() ? String.format("%.0f", attaquant.getVie()) + " PV)" : "KO !)"));
            if (resultatRenvoi.ko) {
                log.add(attaquant.getNom() + " est KO !");
            }
        }

        ContreAttaque contreAttaque = cible.getEffet(ContreAttaque.class);
        if (contreAttaque != null && attaquant.estVivant()) {
            contreAttaque.riposte(cible, attaquant, log);
        }

        Absorption absorption = attaquant.getEffet(Absorption.class);
        if (absorption != null) {
            absorption.volerVie(attaquant, resultat.degatsAppliques, log);
        }
        return true;
    }

    // METHODES CENTRALISEES

    /**
     * Applique des dégâts d'une compétence avec log uniforme.
     * Remplace les appels directs à cible.subirDegats() dans les compétences.
     */
    public static boolean appliquerDegatsAvecLog(PersonnageBase source, PersonnageBase cible,
                                               double degats, List<String> log) {
        if (source != null && !attaqueTouche(source, cible)) {
            log.add(cible.getNom() + " esquive !");
            return false;
        }

        // Bonus de furie : +2,5% degats par tranche de 10 points de rage actuellement en reserve
        // (attaques speciales/ultimes uniquement, cette methode n'est pas utilisee par l'attaque de base).
        if (source != null) {
            double bonusFurie = Math.floor(source.getRage() / 10.0) * 0.025;
            if (bonusFurie > 0) degats *= (1 + bonusFurie);
        }

        double pvAvant = cible.getVie();
        PersonnageBase.ResultatDegats resultat = cible.subirDegats(degats, source != null ? source.getTauxAttaqueS() : 100.0);
        String nomSource = (source != null) ? source.getNom() : "Esprit Celeste";

        if (resultat.invincible) {
            log.add(cible.getNom() + " est invincible ! Degats bloques.");
            return false;
        }

        if (resultat.bouclierAbsorbe) {
            log.add("[BOUCLIER] " + cible.getNom() + " absorbe "
                    + String.format("%.0f", resultat.degatsAbsorbesBouclier)
                    + " degats (" + String.format("%.0f", resultat.pvRestantsBouclier) + " PV restants)");
        }

        if (resultat.bloque) {
            log.add(nomSource + " inflige " + String.format("%.0f", resultat.degatsAppliques)
                    + " degats a " + cible.getNom()
                    + " (" + String.format("%.0f", pvAvant) + " → "
                    + cible.getNom() + " bloque ! Degats reduits a "
                    + String.format("%.0f", resultat.degatsAppliques) + " → "
                    + (cible.estVivant() ? String.format("%.0f", cible.getVie()) + " PV)" : "KO !)"));
        } else {
            log.add(nomSource + " inflige " + String.format("%.0f", resultat.degatsAppliques)
                    + " degats a " + cible.getNom()
                    + " (" + String.format("%.0f", pvAvant) + " → "
                    + (cible.estVivant() ? String.format("%.0f", cible.getVie()) + " PV)" : "KO !)"));
        }

        if (resultat.ko) {
            log.add(cible.getNom() + " est KO !");
        }

        if (resultat.bloque && source != null && source.estVivant()) {
            double degatsRenvoi = cible.getAttaqueBase() * cible.getDegatsRenvoi();
            double pvAvantRenvoi = source.getVie();
            PersonnageBase.ResultatDegats resultatRenvoi = source.subirDegats(degatsRenvoi);
            log.add("[RENVOI] " + cible.getNom() + " renvoie "
                    + String.format("%.0f", degatsRenvoi) + " degats a " + source.getNom()
                    + " (" + String.format("%.0f", pvAvantRenvoi) + " → "
                    + (source.estVivant() ? String.format("%.0f", source.getVie()) + " PV)" : "KO !)"));
            if (resultatRenvoi.ko) {
                log.add(source.getNom() + " est KO !");
            }
        }
        return true;
    }

    /**
     * Applique un effet avec log uniforme.
     * Remplace les appels directs à cible.ajouterEffet() dans les compétences.
     */
    public static void appliquerEffet(PersonnageBase source, PersonnageBase cible,
                                       Effet effet, List<String> log) {
        // Immunité — bloque les effets négatifs si la cible est immunisée
        if (effet instanceof Effets.EffetNegatif) {
            Effets.Immunite immunite = cible.getEffet(Effets.Immunite.class);
            if (immunite != null && !immunite.estTermine()) {
                log.add("🛡 " + cible.getNom() + " est immunisé — [" + effet.getNom() + "] bloqué !");
                return;
            }
        }
        // Immunité de contrôle — bloque uniquement Etourdissement/Paralysie/Sommeil/Petrification/Gel
        if (effet instanceof Etourdissement || effet instanceof Paralysie
                || effet instanceof Sommeil || effet instanceof Petrification || effet instanceof Gel) {
            Effets.ImmuniteControle immuniteControle = cible.getEffet(Effets.ImmuniteControle.class);
            if (immuniteControle != null && !immuniteControle.estTermine()) {
                log.add("🛡 " + cible.getNom() + " resiste aux effets de controle — [" + effet.getNom() + "] bloque !");
                return;
            }
        }
        // Cas spécial Poison : stack au lieu de remplacer
        if (effet instanceof Poison) {
            Poison poisonExistant = cible.getEffet(Poison.class);
            if (poisonExistant != null) {
                poisonExistant.ajouterStack();
                log.add("☠ " + cible.getNom() + " : Poison renforce (stack "
                        + poisonExistant.getStacks() + ") !");
                return;
            }
        }
        int tailleAvant = log.size();
        cible.getEffetsActifs().removeIf(e -> e.getClass() == effet.getClass());
        cible.getEffetsActifs().add(effet);
        effet.appliquer(cible, log);
        // Fallback générique si l'effet n'a rien loggé (ex: Brulure, Saignement)
        if (log.size() == tailleAvant) {
            log.add("➤ " + (source != null ? source.getNom() : "Esprit Celeste") + " applique [" + effet.getNom()
                    + "] sur " + cible.getNom() + " !");
        }
    }

    // Surcharge pratique quand la source et la cible sont la même personne (buffs sur soi)
    public static void appliquerEffet(PersonnageBase cible, Effet effet, List<String> log) {
        // Immunité — bloque les effets négatifs si la cible est immunisée
        if (effet instanceof Effets.EffetNegatif) {
            Effets.Immunite immunite = cible.getEffet(Effets.Immunite.class);
            if (immunite != null && !immunite.estTermine()) {
                log.add("🛡 " + cible.getNom() + " est immunisé — [" + effet.getNom() + "] bloqué !");
                return;
            }
        }
        // Immunité de contrôle — bloque uniquement Etourdissement/Paralysie/Sommeil/Petrification/Gel
        if (effet instanceof Etourdissement || effet instanceof Paralysie
                || effet instanceof Sommeil || effet instanceof Petrification || effet instanceof Gel) {
            Effets.ImmuniteControle immuniteControle = cible.getEffet(Effets.ImmuniteControle.class);
            if (immuniteControle != null && !immuniteControle.estTermine()) {
                log.add("🛡 " + cible.getNom() + " resiste aux effets de controle — [" + effet.getNom() + "] bloque !");
                return;
            }
        }
        if (effet instanceof Poison) {
            Poison poisonExistant = cible.getEffet(Poison.class);
            if (poisonExistant != null) {
                poisonExistant.ajouterStack();
                log.add("☠ " + cible.getNom() + " : Poison renforce (stack "
                        + poisonExistant.getStacks() + ") !");
                return;
            }
        }
        int tailleAvant = log.size();
        cible.getEffetsActifs().removeIf(e -> e.getClass() == effet.getClass());
        cible.getEffetsActifs().add(effet);
        effet.appliquer(cible, log);
        if (log.size() == tailleAvant) {
            log.add("➤ " + cible.getNom() + " gagne l'effet [" + effet.getNom() + "] !");
        }
    }

    /** @deprecated Utiliser la surcharge avec List<String> log à la place. */
    @Deprecated
    public static void appliquerDegatsAvecLog(PersonnageBase source, PersonnageBase cible, double degats) {
        System.out.println(source.getNom() + " inflige "
                + String.format("%.0f", degats) + " degats a " + cible.getNom());
        cible.subirDegats(degats);
        if (!cible.estVivant()) {
            System.out.println(cible.getNom() + " est KO !");
        }
    }
    
    
    // ═══════════════════════════════════════════════
//  ARÈNE
// ═══════════════════════════════════════════════

/**
 * Lance un combat d'arène.
 * Pas d'XP, pas de progression — retourne true si le joueur gagne.
 */
public static boolean lancerCombatArene(
        List<PersonnageBase> equipeJoueur,
        PersonnageBase principalJoueur,
        List<PersonnageBase> equipeAdverse,
        PersonnageBase principalAdverse,
        java.util.Scanner scanner) {

    System.out.println("\n╔══════════════════════════════════════╗");
    System.out.println("║         ⚔  COMBAT D'ARÈNE  ⚔        ║");
    System.out.println("╚══════════════════════════════════════╝");
    System.out.println("  Ton équipe  : "
        + equipeJoueur.stream().map(PersonnageBase::getNom)
                      .collect(java.util.stream.Collectors.joining(", ")));
    System.out.println("  Adversaires : "
        + equipeAdverse.stream().map(PersonnageBase::getNom)
                       .collect(java.util.stream.Collectors.joining(", ")));
    System.out.println("\nAppuie sur Entrée pour lancer le combat...");
    scanner.nextLine();

    Combat combat = new Combat(equipeJoueur, equipeAdverse, false);
    combat.lancerCombat();

    boolean victoire = combat.equipeKO(equipeAdverse);
    System.out.println("\n" + (victoire
        ? "  ✔ Victoire ! Tu as écrasé ton adversaire."
        : "  ✘ Défaite... Renforce ton équipe et réessaie."));
    System.out.println("\nAppuie sur Entrée pour continuer...");
    scanner.nextLine();

    return victoire;
}
    // ── CLEFS CÉLESTES ────────────────────────────────────────────────────


    // EXPERIENCE

    private void donnerExperience() {
        PersonnageBase persoPrincipal = null;
        for (PersonnageBase perso : equipeJoueur) {
            if (perso.estPersonnagePrincipal()) {
                persoPrincipal = perso;
                break;
            }
        }

        // XP de combat volontairement faible (~5% d'un palier de niveau par ennemi) : avec la
        // courbe d'XP lineaire, la montee de niveau doit rester portee par les quetes, pas par
        // le grind repete d'un meme stage (25/niveau donnait un bonus bien trop consequent).
        int experienceGagnee = 0;
        for (PersonnageBase ennemi : equipeAdverse)
            experienceGagnee += ennemi.getNiveau() * 5;

        System.out.println("Experience totale gagnee : " + experienceGagnee + " pts\n");

        for (PersonnageBase perso : equipeJoueur) {
            if (perso.estPersonnagePrincipal()) {
                perso.gagnerExperience(experienceGagnee);
            } else {
                if (persoPrincipal != null && perso.getNiveau() < persoPrincipal.getNiveau()) {
                    perso.gagnerExperience(experienceGagnee);
                    if (perso.getNiveau() > persoPrincipal.getNiveau())
                        perso.setNiveau(persoPrincipal.getNiveau());
                } else {
                    System.out.println("- " + perso.getNom() + " est deja au niveau maximum autorise !");
                }
            }
        }
    }

    public int getToursUtilises() { return toursUtilises; }
}