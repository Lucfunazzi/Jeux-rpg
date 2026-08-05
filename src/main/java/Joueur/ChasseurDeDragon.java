package Joueur;

import Personnage.PersonnageBase;
import Effets.*;
import Combat.Combat;
import java.util.List;

/**
 * Chasseur de Dragon — magie draconique de l'eau.
 *
 * Spéciale de base : Poings du Dragon d'Eau     (100% ATK mono-cible)
 * Ultime de base   : Hurlement du Dragon d'Eau  (120% ATK mono-cible + 5% ATK aux DPS alliés)
 * Spéciale Arbre 1 : Fouet du Dragon d'Eau      (frappe + réduction défense)
 * Spéciale Arbre 2 : Tir à haute pression du dragon d'eau (frappe lourde + étourdissement)
 */
public class ChasseurDeDragon implements Competences {

    @Override
    public String[] getNomsCompetences() {
        return new String[]{"Poings du Dragon d'Eau", "Hurlement du Dragon d'Eau"};
    }

    // ── Spéciale de base ─────────────────────────────────────────────────
    @Override
    public void attaqueSpeciale(PersonnageBase utilisateur, PersonnageBase cible,
            List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Poings du Dragon d'Eau !");
        PersonnageBase cibleFinale = Combat.choisirCible(utilisateur, equipeEnnemie);
        double degats = utilisateur.getAttaque() * 1.00;
        Combat.appliquerDegatsAvecLog(utilisateur, cibleFinale, degats, log);
        Combat.appliquerEffet(utilisateur, new BuffPrecision(1.00, 2), log);
    }

    // ── Ultime de base ───────────────────────────────────────────────────
    @Override
    public void ultime(PersonnageBase utilisateur, List<PersonnageBase> equipeAlliee,
            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Hurlement du Dragon d'Eau !");
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
        log.add("Fouet du Dragon d'Eau !");
        PersonnageBase cibleFinale = Combat.choisirCible(utilisateur, equipeEnnemie);
        double degats = utilisateur.getAttaque() * 1.20;
        Combat.appliquerDegatsAvecLog(utilisateur, cibleFinale, degats, log);
        Combat.appliquerEffet(cibleFinale, new ReductionDefense(0.15,2), log);
    }

    // ── Spéciale Arbre 2 ───────────────────────────────────────────────────
    @Override
    public void competenceArbre2(Personnage_principale utilisateur, PersonnageBase cible,
            List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Tir à haute pression du dragon d'eau !");
        PersonnageBase cibleFinale = Combat.choisirCible(utilisateur, equipeEnnemie);
        double degats = utilisateur.getAttaque() * 1.50;
        Combat.appliquerDegatsAvecLog(utilisateur, cibleFinale, degats, log);
        Combat.appliquerEffet(cibleFinale, new Etourdissement(2), log);
    }

    // ── Spéciale Arbre 3 ───────────────────────────────────────────────────
    @Override
    public void competenceArbre3(Personnage_principale utilisateur, PersonnageBase cible,
            List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Triples Tir du Dragon de l'eau !");
        PersonnageBase cibleFinale = Combat.choisirCible(utilisateur, equipeEnnemie);
        double degatsParTir = utilisateur.getAttaque() * 1.30 / 3;
        for (int tir = 1; tir <= 3 && cibleFinale.estVivant(); tir++) {
            log.add("Tir n°" + tir + " !");
            Combat.appliquerDegatsAvecLog(utilisateur, cibleFinale, degatsParTir, log);
        }

        for (PersonnageBase allie : equipeAlliee) {
            if (allie.estVivant() && allie.getRole().equals("DPS")) {
                Combat.appliquerEffet(utilisateur, allie, new ImmuniteControle(2), log);
            }
        }
    }

    @Override public void descriptionCompetenceArbre3() {
        System.out.println("Triples Tir du Dragon de l'eau — Inflige 130% ATK au Tank ennemi.Immunise les attaquants aux effets de contrôle (Etourdissement,Paralysie,Sommeil,Petrification");
    }

    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Poings du Dragon d'Eau — Inflige 100% ATK au Tank ennemi. +100% Précision au lanceur (2 tours).");
    }
    @Override public void descriptionUltime() {
        System.out.println("Hurlement du Dragon d'Eau — Inflige 120% ATK au Tank ennemi. +5% ATK aux DPS alliés (2 tours).");
    }
    @Override public void descriptionCompetenceArbre() {
        System.out.println("Fouet du dragon d'eau -- Inflige 120% ATK au Tank ennemi. Réduit la défense de la cible de 15% pendants 2 tours");
        
    }
    @Override public void descriptionCompetenceArbre2() {
        System.out.println("Tir à haute préssion du dragon d'eau -- Inflige 150% ATK au Tank ennemi. Etourdit la cible pendant 2 tours");
    }

    // ── Spéciale Arbre 5 ─────────────────────────────────────────────────
    @Override
    public void competenceArbre5(Personnage_principale utilisateur, PersonnageBase cible,
            List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Canon du Dragon de l'Eau !");
        PersonnageBase cibleFinale = Combat.choisirCible(utilisateur, equipeEnnemie);
        double degats = utilisateur.getAttaque() * 1.85;
        Combat.appliquerDegatsAvecLog(utilisateur, cibleFinale, degats, log);
        Combat.appliquerEffet(cibleFinale, new Malediction(3, 0.20), log);
    }

    @Override public void descriptionCompetenceArbre5() {
        System.out.println("Canon du Dragon de l'Eau — Inflige 185% ATK au Tank ennemi. Inflige Malediction (soins réduits de 20%) pendant 3 tours.");
    }

    // ── Spéciale Arbre 6 ─────────────────────────────────────────────────
    @Override
    public void competenceArbre6(Personnage_principale utilisateur, PersonnageBase cible,
            List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Tourbillon du Dragon d'Eau !");
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
        System.out.println("Tourbillon du Dragon d'Eau — Inflige 130% ATK à tous les attaquants ennemis. Augmente l'attaque des attaquants alliés de 20% pendant 2 tours.");
    }

    // ── Spéciale Arbre 7 ─────────────────────────────────────────────────
    @Override
    public void competenceArbre7(Personnage_principale utilisateur, PersonnageBase cible,
            List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Pluie de la Nuit Nocturne du Dragon d'Eau et de la Neige !");
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
        System.out.println("Pluie de la Nuit Nocturne du Dragon d'Eau et de la Neige — Inflige 175% ATK au Tank ennemi et à tous les attaquants ennemis. Donne au lanceur 20% d'absorption de vie pendant 2 tours. Inflige Aveuglement (-20% précision) aux attaquants ennemis pendant 2 tours.");
    }

    // ── Ultime Arbre 4 ───────────────────────────────────────────────────
    @Override
    public void ultimeArbre4(Personnage_principale utilisateur, List<PersonnageBase> equipeAlliee,
            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Tsunamie du Dragon d'Eau !");
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
        System.out.println("Tsunamie du Dragon d'Eau — Inflige 80% ATK à toute l'équipe ennemie. Soigne le lanceur de 10% des dégâts totaux infligés.");
    }

    // ── Ultime Arbre 8 ───────────────────────────────────────────────────
    @Override
    public void ultimeArbre8(Personnage_principale utilisateur, List<PersonnageBase> equipeAlliee,
            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Tsunamie et Blizzard du Dragon de l'Eau et de la Neige !");
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
        System.out.println("Tsunamie et Blizzard du Dragon de l'Eau et de la Neige — Inflige 180% ATK à tous les ennemis. "
                + "Immunise l'équipe aux effets de contrôle pendant 2 tours. Inflige Fragilité (+20% dégâts reçus) pendant 2 tours à tous les ennemis. "
                + "50% de chance d'infliger Silence aux Support ennemis, Poison aux Tank ennemis et Réduction d'attaque (-15%) aux attaquants ennemis, pendant 2 tours.");
    }
}