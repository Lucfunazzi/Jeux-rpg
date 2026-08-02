package Personnage.FairyTail;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Luxus Dreyar [SS] — Mage de rang S de Fairy Tail, Dragon Slayer de la Foudre.
 * Multiplicateur SS = 1.75
 */
public class perso_Luxus extends PersonnageBase {

    public perso_Luxus() {
        this.nom    = "Luxus";
        this.type   = "ChasseurDeDragon";
        this.role   = "DPS";
        this.rarete = "SS";
        this.niveau = 1;

        double multiplicateurRarete = 1.75;
        this.vie     = 700 * multiplicateurRarete;
        this.attaque = 400 * multiplicateurRarete;
        this.defense = 160 * multiplicateurRarete;
        this.vitesse = 200 * multiplicateurRarete;

        this.taux_critiques    = 0.20;
        this.degat_critiques   = 1.60;
        this.taux_precisions   = 110.00;
        this.taux_esquives     = 0.10;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.10;
        this.degats_renvoi     = 0.85;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Rugissement du Dragon de Foudre", "Poing Destructeur du Dragon de Foudre", "Palais du Tonnerre"};
    }

    // ── Attaque de base : Rugissement du Dragon de Foudre — 100% ATK a tous les ennemis ──
    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Luxus utilise Rugissement du Dragon de Foudre !");
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = this.getAttaque() * 1.00;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            }
        }
    }

    // ── Attaque speciale : Poing Destructeur du Dragon de Foudre — 250% ATK sur le Tank et les Supports, étourdit ──
    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Luxus utilise Poing Destructeur du Dragon de Foudre !");
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant() && (ennemi.getRole().equals("Tank") || ennemi.getRole().equals("Support"))) {
                double degats = this.getAttaque() * 2.50;
                boolean touche = Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                if (touche) {
                    Combat.appliquerEffet(this, ennemi, new Etourdissement(1), log);
                }
            }
        }
    }

    // ── Attaque ultime : Palais du Tonnerre — 200% ATK a tous les ennemis, paralyse, se renforce ──
    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Luxus utilise Palais du Tonnerre !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) {
            multiplicateurRage += (this.getRage() - 100) / 100.0;
        }
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = (this.getAttaque() * 2.00) * multiplicateurRage;
                boolean touche = Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                if (touche) {
                    Combat.appliquerEffet(this, ennemi, new Paralysie(2, 0.50), log);
                }
            }
        }
        Combat.appliquerEffet(this, new BuffAttaque(0.15, 2), log);
        Combat.appliquerEffet(this, new BuffTauxCritique(0.15, 2), log);
    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Rugissement du Dragon de Foudre — inflige 100% ATK a tous les ennemis.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Poing Destructeur du Dragon de Foudre — inflige 250% ATK au Tank et aux Supports ennemis, "
                + "et les etourdit pendant 1 tour.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Palais du Tonnerre — inflige 200% ATK a tous les ennemis (bonus selon la Rage), "
                + "les paralyse pendant 2 tours (50% chance de liberation), "
                + "et Luxus gagne 15% d'attaque et 15% de taux critique pendant 2 tours.");
    }
}
