package Personnage.FairyTail;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Ul Milkovich [SS] — Maitresse d'Ice-Make, mentor de Gray et Lyon.
 * Personnage jouable a stats elevees, sans rapport avec l'Ul du flashback
 * du Chapitre 2 (voir Personnage.pnj.Chapitre2.EnnemiUlFlashback).
 */
public class perso_Ul extends PersonnageBase {

    public perso_Ul() {
        this.nom    = "Ul Milkovich";
        this.type   = "Elementaliste";
        this.role   = "DPS";
        this.rarete = "SS";
        this.niveau = 1;

        double multiplicateurRarete = 1.75;
        this.vie     = 750 * multiplicateurRarete;
        this.attaque = 260 * multiplicateurRarete;
        this.defense = 130 * multiplicateurRarete;
        this.vitesse = 145 * multiplicateurRarete;

        this.taux_critiques    = 0.25;
        this.degat_critiques   = 1.55;
        this.taux_precisions   = 110.00;
        this.taux_esquives     = 0.12;
        this.taux_blocage      = 0.08;
        this.reduction_blocage = 0.15;
        this.degats_renvoi     = 0.90;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Ice-Make : Lance", "Ice-Make : Ours de Glace", "Ere de Glace"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " utilise Ice-Make : Lance sur " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " invoque Ice-Make : Ours de Glace sur " + cible.getNom() + " !");
        double degats = this.getAttaque() * 1.50;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        if (Math.random() < 0.30) {
            Combat.appliquerEffet(this, cible, new ReductionVitesse(0.20, 2), log);
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " dechaine son Ere de Glace !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) {
            multiplicateurRage += (this.getRage() - 100) / 100.0;
        }
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = (this.getAttaque() * 1.70) * multiplicateurRage;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                if (Math.random() < 0.25) {
                    Combat.appliquerEffet(this, ennemi, new Gel(1), log);
                }
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Ice-Make : Lance — inflige 100% ATK a la cible.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Ice-Make : Ours de Glace — inflige 150% ATK, 30% de chance de reduire la VIT de 20% pendant 2 tours.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Ere de Glace — inflige 170% ATK a tous les ennemis, 25% de chance de Gel 1 tour sur chacun.");
    }
}
