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
        double mult = 1.66;
        this.vie     = 1161 * mult;
        this.attaque =  185 * mult;
        this.defense =  242 * mult;
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
        double degats = this.getAttaque() * 1.30;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Erza Knightwalker enchaîne la Technique Ransui sur " + cible.getNom() + " !");
        double degats = this.getAttaque() * 1.35;
        if (cible.aEffet(ReductionDefense.class)) degats *= 1.15;
        boolean touche = Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        if (touche) {
            Combat.appliquerEffet(this, cible, new Saignement(2, 0.06), log);
        }
        Combat.appliquerEffet(this, this, new Bouclier(this.getVieMax() * 0.20), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Erza Knightwalker lance la Chasse aux Fées sur toute l'équipe ennemie !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) multiplicateurRage += (this.getRage() - 100) / 100.0;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = (this.getAttaque() * 1.35) * multiplicateurRage;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            }
        }
        Combat.appliquerEffet(this, new BuffAttaque(0.15, 2), log);

        List<PersonnageBase> vivants = new java.util.ArrayList<>();
        for (PersonnageBase ennemi : equipeEnnemie) if (ennemi.estVivant()) vivants.add(ennemi);
        java.util.Collections.shuffle(vivants);
        for (int i = 0; i < Math.min(2, vivants.size()); i++) {
            Combat.appliquerEffet(this, vivants.get(i), new ReductionDefense(0.30, 2), log);
        }
        Combat.appliquerEffet(this, this, new BuffTauxEsquive(0.15, 3), log);
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Estocade de Lance — Inflige 130% ATK.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Technique Ransui — Inflige 135% ATK et inflige un Saignement pendant 2 tours (6% PV/tour) se donne un bouclier de 20% des pv Max.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Chasse aux Fées — Inflige 135% ATK (bonus selon la Rage) à tous les ennemis et augmente son ATK de 15% pendant 2 tours réduit la defense de 2 ennemis de 30% pendants 2 tours"
                + "se donne 15% d'esquive pendants 3 tours.");
    }
}
