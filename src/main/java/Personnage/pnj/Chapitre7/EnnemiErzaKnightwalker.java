package Personnage.pnj.Chapitre7;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Erza Knightwalker — Capitaine de l'Armée Royale d'Edolas, rang S.
 * Double d'Erza dans ce monde sans magie : mène la traque des fées ("Fairy Hunt")
 * avec une froide efficacité à l'arme blanche plutôt qu'à la magie.
 */
public class EnnemiErzaKnightwalker extends PersonnageBase {

    public EnnemiErzaKnightwalker() { this(75); }

    public EnnemiErzaKnightwalker(int niveau) {
        this.nom    = "Erza Knightwalker";
        this.niveau = niveau;
        this.type   = "Chevalier";
        this.role   = "Tank";
        this.rarete = "S";

        double mult = 1.65;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 1150.0 * mult * niv;
        this.attaque =  200.0 * mult * niv;
        this.defense =  240.0 * mult * niv;
        this.vitesse =  115.0 * mult * vit;

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
