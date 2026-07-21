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

    public PersonnageBase cibleMoinsPv(List<PersonnageBase> equipe) {
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

    public PersonnageBase choisirCible(PersonnageBase attaquant, List<PersonnageBase> equipeEnnemie) {
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

    private PersonnageBase cibleParRole(List<PersonnageBase> equipe, String role) {
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

            afficherEffetsGroupes(equipeJoueur);
            afficherEffetsGroupes(equipeAdverse);
            List<String> logEffets = new ArrayList<>();
            for (PersonnageBase perso : equipeJoueur) if (perso.estVivant()) perso.appliquerEffets(logEffets);
            for (PersonnageBase perso : equipeAdverse) if (perso.estVivant()) perso.appliquerEffets(logEffets);
            for (String ligne : logEffets) System.out.println(ligne);

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
                    System.out.println("[PETRIFICATION] " + attaquant.getNom() + " est petrife et ne peut pas agir !");
                    continue;
                }
                if (attaquant.aEffet(Gel.class)) {
                    System.out.println("[GEL] " + attaquant.getNom() + " est gele et passe son tour !");
                    continue;
                }
                if (attaquant.aEffet(Etourdissement.class)) {
                    System.out.println("[ETOURDISSEMENT] " + attaquant.getNom() + " est etourdi et ne peut pas agir !");
                    continue;
                }
                if (attaquant.aEffet(Sommeil.class)) {
                    System.out.println("[SOMMEIL] " + attaquant.getNom() + " dort profondement...");
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
                    attaquant.attaqueUltime(alliesVirtuels, ennemisVirtuels, log);
                    attaquant.reinitialiserRage();

                } else if (attaquant.getRage() >= 50 && !attaquant.getSpecialeUtilisee()) {
                    Silence silence = attaquant.getEffet(Silence.class);
                    if (silence != null && silence.empecheSpeciale()) {
                        System.out.println("[SILENCE] " + attaquant.getNom() + " est reduit au silence ! Attaque de base.");
                        attaquer(attaquant, cible, log);
                        attaquant.ajouterRage(20);
                    } else {
                        System.out.println("\n[SPECIALE] " + attaquant.getNom() + " utilise sa competence speciale !");
                        attaquant.attaqueSpeciale(cible, alliesVirtuels, ennemisVirtuels, log);
                        attaquant.setSpecialeUtilisee(true);
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
            }
            numeroTour++;
        }

        System.out.println("\n=== FIN DU COMBAT ===");
        if (equipeKO(equipeAdverse)) {
            System.out.println("Votre equipe a gagne !");
            if (donnerXP) donnerExperience();
        } else if (equipeKO(equipeJoueur)) {
            System.out.println("Votre equipe a perdu !");
        } else {
            System.out.println("Combat termine apres " + MAX_TOURS + " tours sans vainqueur. Defaite par epuisement.");
        }
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
        double esquiveEffective = Math.min(cible.getTauxEsquives() / precisionFactor, 0.95);
        return Math.random() >= esquiveEffective;
    }

    public static double calculerDegats(PersonnageBase attaquant, PersonnageBase cible) {
        double degatsBase = Math.max(attaquant.getAttaque() * 0.10,
                                     attaquant.getAttaque() - cible.getDefense());
        return degatsBase;
    }

    public static boolean estCritique(PersonnageBase attaquant) {
        return Math.random() < attaquant.getTauxCritique();
    }

    public static void attaquer(PersonnageBase attaquant, PersonnageBase cible, List<String> log) {
        if (!attaqueTouche(attaquant, cible)) {
            log.add(cible.getNom() + " esquive !");
            return;
        }

        double degats = calculerDegats(attaquant, cible);
        if (estCritique(attaquant)) {
            degats *= attaquant.getTauxDegatCritique();
            log.add("Coup critique !");
        }

        double pvAvant = cible.getVie();
        PersonnageBase.ResultatDegats resultat = cible.subirDegats(degats);

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
    }

    // METHODES CENTRALISEES

    /**
     * Applique des dégâts d'une compétence avec log uniforme.
     * Remplace les appels directs à cible.subirDegats() dans les compétences.
     */
    public static void appliquerDegatsAvecLog(PersonnageBase source, PersonnageBase cible,
                                               double degats, List<String> log) {
        double pvAvant = cible.getVie();
        PersonnageBase.ResultatDegats resultat = cible.subirDegats(degats);
        String nomSource = (source != null) ? source.getNom() : "Esprit Celeste";

        if (resultat.invincible) {
            log.add(cible.getNom() + " est invincible ! Degats bloques.");
            return;
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