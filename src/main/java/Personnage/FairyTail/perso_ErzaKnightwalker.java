package Personnage.FairyTail;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Erza Knightwalker — Chevalier, rang S.
 * Capitaine de l'Armée Royale d'Edolas, double d'Erza dans ce monde sans magie.
 */
public class perso_ErzaKnightwalker extends PersonnageBase {

    public perso_ErzaKnightwalker() {
        this.nom    = "Erza Knightwalker";
        this.type   = "Chevalier";
        this.role   = "Tank";
        this.rarete = "S";
        this.niveau = 1;
        double mult = 1.55;
        this.vie     = 1100 * mult;
        this.attaque =  185 * mult;
        this.defense =  225 * mult;
        this.vitesse =  115 * mult;
        this.taux_critiques    = 0.10;
        this.degat_critiques   = 1.25;
        this.taux_precisions   = 105.00;
        this.taux_esquives     = 0.08;
        this.taux_blocage      = 0.16;
        this.reduction_blocage = 0.20;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Estocade de Lance", "Technique Ransui", "Chasse aux Fées"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Erza Knightwalker transperce " + cible.getNom() + " d'une Estocade de Lance !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Erza Knightwalker enchaîne la Technique Ransui sur " + cible.getNom() + " !");
        double degats = this.getAttaque() * 1.35;
        boolean touche = Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        if (touche) {
            Combat.appliquerEffet(this, cible, new Saignement(2, 0.06), log);
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Erza Knightwalker lance la Chasse aux Fées sur toute l'équipe ennemie !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) multiplicateurRage += (this.getRage() - 100) / 100.0;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = (this.getAttaque() * 1.20) * multiplicateurRage;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            }
        }
        Combat.appliquerEffet(this, new BuffAttaque(0.15, 2), log);
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Estocade de Lance — Inflige 100% ATK.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Technique Ransui — Inflige 135% ATK et inflige un Saignement pendant 2 tours (6% PV/tour).");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Chasse aux Fées — Inflige 120% ATK (bonus selon la Rage) à tous les ennemis et augmente son ATK de 15% pendant 2 tours.");
    }
}
