package Joueur;

import Personnage.PersonnageBase;
import Effets.*;
import Combat.Combat;
import java.util.List;

/**
 * Mage — magie de glace et de cristaux, inspiré de Gray Fullbuster.
 *
 * Spéciale de base : Lance de Glace      (100% ATK mono-cible)
 * Ultime de base   : Bazooka de Glace    (120% ATK mono-cible + 5% ATK aux DPS alliés)
 * Spéciale Arbre 1 : Rayon sacré         (frappe + réduction défense)
 * Spéciale Arbre 2 : Décharge de foudre  (frappe lourde + étourdissement)
 */
public class Elementaliste implements Competences {

    @Override
    public String[] getNomsCompetences() {
        return new String[]{"Lance de Glace", "Bazooka de Glace"};
    }

    // ── Spéciale de base ─────────────────────────────────────────────────
    @Override
    public void attaqueSpeciale(PersonnageBase utilisateur, PersonnageBase cible,
            List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Lance de Glace !");
        PersonnageBase cibleFinale = Combat.choisirCible(utilisateur, equipeEnnemie);
        double degats = utilisateur.getAttaque() * 1.00;
        Combat.appliquerDegatsAvecLog(utilisateur, cibleFinale, degats, log);
        Combat.appliquerEffet(utilisateur, new BuffPrecision(1.00, 2), log);
    }

    // ── Ultime de base ───────────────────────────────────────────────────
    @Override
    public void ultime(PersonnageBase utilisateur, List<PersonnageBase> equipeAlliee,
            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Bazooka de Glace !");
        PersonnageBase cibleFinale = Combat.choisirCible(utilisateur, equipeEnnemie);
        if (cibleFinale != null) {
            double degats = utilisateur.getAttaque() * 1.20;
            Combat.appliquerDegatsAvecLog(utilisateur, cibleFinale, degats, log);
        }
        for (PersonnageBase allie : equipeAlliee) {
            if (allie.estVivant() && allie.getRole().equals("DPS")) {
                Combat.appliquerEffet(utilisateur, allie, new BuffAttaque(0.05, 2), log);
            }
        }
    }

    // ── Spéciale Arbre 1 ─────────────────────────────────────────────────
    @Override
    public void competenceArbre(Personnage_principale utilisateur, PersonnageBase cible,
            List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Rayon sacré!");
        PersonnageBase cibleFinale = Combat.choisirCible(utilisateur, equipeEnnemie);
        double degats = utilisateur.getAttaque() * 1.20;
        Combat.appliquerDegatsAvecLog(utilisateur, cibleFinale, degats, log);
        Combat.appliquerEffet(cibleFinale, new ReductionDefense(0.15,2), log);
    }

    // ── Spéciale Arbre 2 ───────────────────────────────────────────────────
    @Override
    public void competenceArbre2(Personnage_principale utilisateur, PersonnageBase cible,
            List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Décharge de foudre !");
        PersonnageBase cibleFinale = Combat.choisirCible(utilisateur, equipeEnnemie);
        double degats = utilisateur.getAttaque() * 1.50;
        Combat.appliquerDegatsAvecLog(utilisateur, cibleFinale, degats, log);
        Combat.appliquerEffet(cibleFinale, new Etourdissement(2), log);
    }

    // ── Spéciale Arbre 3 ───────────────────────────────────────────────────
    @Override
    public void competenceArbre3(Personnage_principale utilisateur, PersonnageBase cible,
            List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Epines fleuries !");
        PersonnageBase cibleFinale = Combat.choisirCible(utilisateur, equipeEnnemie);
        double degats = utilisateur.getAttaque() * 1.30;
        Combat.appliquerDegatsAvecLog(utilisateur, cibleFinale, degats, log);

        for (PersonnageBase allie : equipeAlliee) {
            if (allie.estVivant() && allie.getRole().equals("DPS")) {
                Combat.appliquerEffet(utilisateur, allie, new ImmuniteControle(2), log);
            }
        }
    }

    @Override public void descriptionCompetenceArbre3() {
        System.out.println("Epines fleuries — Inflige 130% ATK au Tank ennemi. Immunise les attaquants aux effets de contrôle (Etourdissement,Paralysie,Sommeil,Petrification");
    }

    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Lance de Glace — Inflige 100% ATK au Tank ennemi. +100% Précision au lanceur (2 tours).");
    }
    @Override public void descriptionUltime() {
        System.out.println("Bazooka de Glace — Inflige 120% ATK au Tank ennemi. +5% ATK aux DPS alliés (2 tours).");
    }
     @Override public void descriptionCompetenceArbre() {
        System.out.println("Rayon sacré -- Inflige 120% ATK au Tank ennemi. Réduit la défense de la cible de 15% pendants 2 tours");
        
    }
    @Override public void descriptionCompetenceArbre2() {
        System.out.println("Décharge de foudre -- Inflige 150% ATK au Tank ennemi. Etourdit la cible pendant 2 tours");
    }

    // ── Spéciale Arbre 5 ─────────────────────────────────────────────────
    @Override
    public void competenceArbre5(Personnage_principale utilisateur, PersonnageBase cible,
            List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Les 5 Flèches Sacrées !");
        PersonnageBase cibleFinale = Combat.choisirCible(utilisateur, equipeEnnemie);
        double degats = utilisateur.getAttaque() * 1.85;
        Combat.appliquerDegatsAvecLog(utilisateur, cibleFinale, degats, log);
        Combat.appliquerEffet(cibleFinale, new Malediction(3, 0.20), log);
    }

    @Override public void descriptionCompetenceArbre5() {
        System.out.println("Les 5 Flèches Sacrées — Inflige 185% ATK au Tank ennemi. Inflige Malediction (soins réduits de 20%) pendant 3 tours.");
    }

    // ── Spéciale Arbre 6 ─────────────────────────────────────────────────
    @Override
    public void competenceArbre6(Personnage_principale utilisateur, PersonnageBase cible,
            List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Dogme de la Flamme Éternelle !");
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant() && ennemi.getRole().equals("DPS")) {
                double degats = utilisateur.getAttaque() * 1.30;
                Combat.appliquerDegatsAvecLog(utilisateur, ennemi, degats, log);
            }
        }
        for (PersonnageBase allie : equipeAlliee) {
            if (allie.estVivant() && allie.getRole().equals("DPS")) {
                Combat.appliquerEffet(utilisateur, allie, new BuffAttaque(0.20, 2), log);
            }
        }
    }

    @Override public void descriptionCompetenceArbre6() {
        System.out.println("Dogme de la Flamme Éternelle — Inflige 130% ATK à tous les attaquants ennemis. Augmente l'attaque des attaquants alliés de 20% pendant 2 tours.");
    }

    // ── Spéciale Arbre 7 ─────────────────────────────────────────────────
    @Override
    public void competenceArbre7(Personnage_principale utilisateur, PersonnageBase cible,
            List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Floraisons Électriques !");
        PersonnageBase tank = Combat.cibleParRole(equipeEnnemie, "Tank");
        if (tank != null) {
            double degats = utilisateur.getAttaque() * 1.75;
            Combat.appliquerDegatsAvecLog(utilisateur, tank, degats, log);
        }
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant() && ennemi.getRole().equals("DPS")) {
                double degats = utilisateur.getAttaque() * 1.75;
                Combat.appliquerDegatsAvecLog(utilisateur, ennemi, degats, log);
                Combat.appliquerEffet(ennemi, new Aveuglement(0.20, 2), log);
            }
        }
        Combat.appliquerEffet(utilisateur, new Absorption(2, 0.20), log);
    }

    @Override public void descriptionCompetenceArbre7() {
        System.out.println("Floraisons Électriques — Inflige 175% ATK au Tank ennemi et à tous les attaquants ennemis. Donne au lanceur 20% d'absorption de vie pendant 2 tours. Inflige Aveuglement (-20% précision) aux attaquants ennemis pendant 2 tours.");
    }

    // ── Ultime Arbre 4 ───────────────────────────────────────────────────
    @Override
    public void ultimeArbre4(Personnage_principale utilisateur, List<PersonnageBase> equipeAlliee,
            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Cataclysme d'Elipse !");
        double degatsTotaux = 0;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = utilisateur.getAttaque() * 0.80;
                boolean touche = Combat.appliquerDegatsAvecLog(utilisateur, ennemi, degats, log);
                if (touche) degatsTotaux += degats;
            }
        }
        if (degatsTotaux > 0) utilisateur.recevoirSoin(degatsTotaux * 0.10, log);
    }

    @Override public void descriptionUltimeArbre4() {
        System.out.println("Cataclysme d'Elipse — Inflige 80% ATK à toute l'équipe ennemie. Soigne le lanceur de 10% des dégâts totaux infligés.");
    }

    // ── Ultime Arbre 8 ───────────────────────────────────────────────────
    @Override
    public void ultimeArbre8(Personnage_principale utilisateur, List<PersonnageBase> equipeAlliee,
            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Nature Volcanique du Purgatoire !");
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = utilisateur.getAttaque() * 1.80;
                Combat.appliquerDegatsAvecLog(utilisateur, ennemi, degats, log);
                Combat.appliquerEffet(ennemi, new Fragilite(2, 0.20), log);
                if (ennemi.getRole().equals("Support") && Math.random() < 0.50) {
                    Combat.appliquerEffet(ennemi, new Silence(2), log);
                }
                if (ennemi.getRole().equals("Tank") && Math.random() < 0.50) {
                    Combat.appliquerEffet(ennemi, new Poison(2, 0.06), log);
                }
                if (ennemi.getRole().equals("DPS") && Math.random() < 0.50) {
                    Combat.appliquerEffet(ennemi, new ReductionAttaque(0.15, 2), log);
                }
            }
        }
        for (PersonnageBase allie : equipeAlliee) {
            if (allie.estVivant()) {
                Combat.appliquerEffet(utilisateur, allie, new ImmuniteControle(2), log);
            }
        }
    }

    @Override public void descriptionUltimeArbre8() {
        System.out.println("Nature Volcanique du Purgatoire — Inflige 180% ATK à tous les ennemis. "
                + "Immunise l'équipe aux effets de contrôle pendant 2 tours. Inflige Fragilité (+20% dégâts reçus) pendant 2 tours à tous les ennemis. "
                + "50% de chance d'infliger Silence aux Support ennemis, Poison aux Tank ennemis et Réduction d'attaque (-15%) aux attaquants ennemis, pendant 2 tours.");
    }
}