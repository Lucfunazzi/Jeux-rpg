package Combat;

import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import Effets.*;

public class Combat {
    private List<PersonnageBase> equipeJoueur;
    private List<PersonnageBase> equipeAdverse;
    private double bonusTitre  = 0.0;
    private int toursUtilises  = 0;
    private boolean donnerXP   = true;
    private List<CombatEvent> evenements; // non-null uniquement pendant lancerCombatEnregistre()

    /** Etat d'un personnage a un instant T, pour rejouer le combat visuellement (barres PV/rage). */
    public static final class PersonnageSnapshot {
        public final String nom;
        public final String role;
        public final String rarete;
        public final double vie;
        public final double vieMax;
        public final double rage;
        public final boolean vivant;
        public final boolean coteJoueur;
        public final List<String> effets;

        PersonnageSnapshot(PersonnageBase p, boolean coteJoueur) {
            this.nom        = p.getNom();
            this.role       = p.getRole();
            this.rarete     = p.getRarete();
            this.vie        = Math.max(0, p.getVie());
            this.vieMax     = p.getVieMax();
            this.rage       = p.getRage();
            this.vivant     = p.estVivant();
            this.coteJoueur = coteJoueur;
            this.effets     = new ArrayList<>();
            for (Effet e : p.getEffetsActifs()) {
                if (!e.estTermine()) this.effets.add(e.getNom());
            }
        }
    }

    /** Une etape du combat (une action, un tick d'effets, un debut de tour ou la fin du combat). */
    public static final class CombatEvent {
        public final String titre;
        public final List<String> lignes;
        public final List<PersonnageSnapshot> etat;
        public final boolean finDeCombat;

        CombatEvent(String titre, List<String> lignes, List<PersonnageSnapshot> etat, boolean finDeCombat) {
            this.titre       = titre;
            this.lignes      = lignes;
            this.etat        = etat;
            this.finDeCombat = finDeCombat;
        }
    }

    public Combat(List<PersonnageBase> equipeJoueur,
                  List<PersonnageBase> equipeAdverse) {
        this.equipeJoueur  = equipeJoueur;
        this.equipeAdverse = equipeAdverse;
    }

    public Combat(List<PersonnageBase> equipeJoueur,
                  List<PersonnageBase> equipeAdverse,
                  double bonusTitre) {
        this.equipeJoueur  = equipeJoueur;
        this.equipeAdverse = equipeAdverse;
        this.bonusTitre    = bonusTitre;
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

        PersonnageBase cible = cibleParRole(equipeEnnemie, "Tank");
        if (cible != null) return cible;
        cible = cibleParRole(equipeEnnemie, "DPS");
        if (cible != null) return cible;
        cible = cibleParRole(equipeEnnemie, "Support");
        if (cible != null) return cible;

        return cibleMoinsPv(equipeEnnemie);
    }

    private static PersonnageBase cibleParRole(List<PersonnageBase> equipe, String role) {
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
        enregistrer(titre, lignes, false);
    }

    private void enregistrer(String titre, List<String> lignes, boolean fin) {
        if (evenements == null) return;
        evenements.add(new CombatEvent(titre, new ArrayList<>(lignes), snapshotEquipes(equipeJoueur, equipeAdverse), fin));
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

        if (bonusTitre > 0.0) {
            for (PersonnageBase perso : equipeJoueur) {
                perso.ajouterEffet(new BuffTitre(bonusTitre));
            }
            System.out.println("[TITRE] Bonus de " + (int)(bonusTitre * 100)
                    + "% sur toutes les stats de l'equipe !");
        }

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

                if (attaquant.getRage() >= 100) {
                    System.out.println("\n[ULTIME] " + attaquant.getNom() + " declenche son ultime !");
                    log.add("[ULTIME] " + attaquant.getNom() + " declenche son ultime !");
                    attaquant.attaqueUltime(alliesVirtuels, ennemisVirtuels, log);
                    attaquant.reinitialiserRage();

                } else if (attaquant.getRage() >= 50 && !attaquant.getSpecialeUtilisee()) {
                    Silence silence = attaquant.getEffet(Silence.class);
                    if (silence != null && silence.empecheSpeciale()) {
                        System.out.println("[SILENCE] " + attaquant.getNom() + " est reduit au silence ! Attaque de base.");
                        log.add("[SILENCE] " + attaquant.getNom() + " est reduit au silence ! Attaque de base.");
                        attaquer(attaquant, cible, log);
                        attaquant.ajouterRage(attaquant.isDernierCoupCritique() ? 40 : 20);
                    } else {
                        System.out.println("\n[SPECIALE] " + attaquant.getNom() + " utilise sa competence speciale !");
                        log.add("[SPECIALE] " + attaquant.getNom() + " utilise sa competence speciale !");
                        attaquant.attaqueSpeciale(cible, alliesVirtuels, ennemisVirtuels, log);
                        attaquant.setSpecialeUtilisee(true);
                    }
                } else {
                    log.add(attaquant.getNom() + " lance une attaque de base sur " + cible.getNom());
                    boolean attaquantEstJoueur = equipeJoueur.contains(attaquant);
                    attaquant.setDernierCoupCritique(false);
                    attaquant.attaqueBase(cible,
                            attaquantEstJoueur ? equipeJoueur  : equipeAdverse,
                            attaquantEstJoueur ? equipeAdverse : equipeJoueur,
                            log);
                    attaquant.ajouterRage(attaquant.isDernierCoupCritique() ? 100 : 50);
                    log.add("[RAGE] " + attaquant.getNom() + " : "
                            + String.format("%.0f", attaquant.getRage()) + "/100");
                }

                // Affichage du log de la compétence
                for (String ligne : log) {
                    System.out.println(ligne);
                }
                if (!log.isEmpty()) enregistrer(attaquant.getNom(), log);
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

    public static double calculerDegats(PersonnageBase attaquant, PersonnageBase cible) {
        double degatsBase = Math.max(attaquant.getAttaque() * 0.10,
                                     attaquant.getAttaque() - cible.getDefense());
        return degatsBase;
    }

    /** @param cible Fournit le taux de Contre (100 = neutre) qui reduit les chances de subir un critique. */
    public static boolean estCritique(PersonnageBase attaquant, PersonnageBase cible) {
        double critiqueEffectif = Math.min(attaquant.getTauxCritique() / (cible.getTauxContre() / 100.0), 0.90);
        return Math.random() < critiqueEffectif;
    }

    public static boolean attaquer(PersonnageBase attaquant, PersonnageBase cible, List<String> log) {
        attaquant.setDernierCoupCritique(false);
        if (!attaqueTouche(attaquant, cible)) {
            log.add(cible.getNom() + " esquive !");
            return false;
        }

        double degats = calculerDegats(attaquant, cible);
        if (estCritique(attaquant, cible)) {
            degats *= attaquant.getTauxDegatCritique();
            log.add("Coup critique !");
            attaquant.setDernierCoupCritique(true);
        }

        double pvAvant = cible.getVie();
        PersonnageBase.ResultatDegats resultat = cible.subirDegats(degats, attaquant.getTauxAttaqueS());

        if (resultat.invincible) {
            log.add(cible.getNom() + " est invincible ! Degats bloques.");
        } else {
            if (resultat.bouclierAbsorbe) {
                log.add("[BOUCLIER] " + cible.getNom() + " absorbe "
                        + String.format("%.1f", resultat.degatsAbsorbesBouclier)
                        + " degats (" + String.format("%.1f", resultat.pvRestantsBouclier) + " PV restants)");
            }
            if (resultat.bloque) {
                log.add("→ " + String.format("%.1f", resultat.degatsAppliques)
                        + " degats sur " + cible.getNom()
                        + " (" + String.format("%.1f", pvAvant) + " → "
                        + cible.getNom() + " bloque ! Degats reduits a "
                        + String.format("%.1f", resultat.degatsAppliques) + " → "
                        + (cible.estVivant() ? String.format("%.1f", cible.getVie()) + " PV)" : "KO !)"));
            } else {
                log.add("→ " + String.format("%.1f", resultat.degatsAppliques)
                        + " degats sur " + cible.getNom()
                        + " (" + String.format("%.1f", pvAvant) + " → "
                        + (cible.estVivant() ? String.format("%.1f", cible.getVie()) + " PV)" : "KO !)"));
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
                    + String.format("%.1f", degatsRenvoi) + " degats a " + attaquant.getNom()
                    + " (" + String.format("%.1f", pvAvantRenvoi) + " → "
                    + (attaquant.estVivant() ? String.format("%.1f", attaquant.getVie()) + " PV)" : "KO !)"));
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

        double pvAvant = cible.getVie();
        PersonnageBase.ResultatDegats resultat = cible.subirDegats(degats, source != null ? source.getTauxAttaqueS() : 100.0);
        String nomSource = (source != null) ? source.getNom() : "Esprit Celeste";

        if (resultat.invincible) {
            log.add(cible.getNom() + " est invincible ! Degats bloques.");
            return false;
        }

        if (resultat.bouclierAbsorbe) {
            log.add("[BOUCLIER] " + cible.getNom() + " absorbe "
                    + String.format("%.1f", resultat.degatsAbsorbesBouclier)
                    + " degats (" + String.format("%.1f", resultat.pvRestantsBouclier) + " PV restants)");
        }

        if (resultat.bloque) {
            log.add(nomSource + " inflige " + String.format("%.1f", resultat.degatsAppliques)
                    + " degats a " + cible.getNom()
                    + " (" + String.format("%.1f", pvAvant) + " → "
                    + cible.getNom() + " bloque ! Degats reduits a "
                    + String.format("%.1f", resultat.degatsAppliques) + " → "
                    + (cible.estVivant() ? String.format("%.1f", cible.getVie()) + " PV)" : "KO !)"));
        } else {
            log.add(nomSource + " inflige " + String.format("%.1f", resultat.degatsAppliques)
                    + " degats a " + cible.getNom()
                    + " (" + String.format("%.1f", pvAvant) + " → "
                    + (cible.estVivant() ? String.format("%.1f", cible.getVie()) + " PV)" : "KO !)"));
        }

        if (resultat.ko) {
            log.add(cible.getNom() + " est KO !");
        }

        if (resultat.bloque && source != null && source.estVivant()) {
            double degatsRenvoi = cible.getAttaqueBase() * cible.getDegatsRenvoi();
            double pvAvantRenvoi = source.getVie();
            PersonnageBase.ResultatDegats resultatRenvoi = source.subirDegats(degatsRenvoi);
            log.add("[RENVOI] " + cible.getNom() + " renvoie "
                    + String.format("%.1f", degatsRenvoi) + " degats a " + source.getNom()
                    + " (" + String.format("%.1f", pvAvantRenvoi) + " → "
                    + (source.estVivant() ? String.format("%.1f", source.getVie()) + " PV)" : "KO !)"));
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
                + String.format("%.1f", degats) + " degats a " + cible.getNom());
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

        int experienceGagnee = 0;
        for (PersonnageBase ennemi : equipeAdverse)
            experienceGagnee += ennemi.getNiveau() * 25;

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