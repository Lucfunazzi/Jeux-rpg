package Personnage.FairyTail;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Ultear Milkovich — Elementaliste, rang S.
 * Ancienne de Grimoire Heart, se rachète après Tenrô et se bat aux côtés
 * de Fairy Tail. Magie de l'Arc du Temps.
 */
public class perso_Ultear extends PersonnageBase {

    public perso_Ultear() {
        this.nom    = "Ultear";
        this.type   = "Elementaliste";
        this.role   = "Support";
        this.rarete = "S";
        this.niveau = 1;
        double mult = 1.72;
        this.vie     = 580 * mult;
        this.attaque = 220 * mult;
        this.defense = 120 * mult;
        this.vitesse = 130 * mult;
        this.taux_critiques    = 0.16;
        this.degat_critiques   = 1.35;
        this.taux_precisions   = 108.00;
        this.taux_esquives     = 0.10;
        this.taux_blocage      = 0.08;
        this.reduction_blocage = 0.12;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Flèche Temporelle", "Marche Arrière", "Last Ages"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Ultear transperce " + cible.getNom() + " d'une Flèche Temporelle !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Ultear inflige une Marche Arrière à toute l'équipe ennemie !");
        double degats = this.getAttaque() * 1.35;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            }
        }
        double soin = this.getAttaque() * 0.50;
        for (PersonnageBase allie : equipeAlliee) {
            if (allie.estVivant()) {
                allie.recevoirSoin(soin, log);
            }
        }
        Purification.purifierEquipe(equipeAlliee, Math.random() < 0.50 ? 3 : 2, log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Ultear déchaîne son interdit ultime — Last Ages !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) multiplicateurRage += (this.getRage() - 100) / 100.0;
        double soin = this.getAttaque() * 1.00;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = (this.getAttaque() * 1.05) * multiplicateurRage;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                if (ennemi.getRole().equals("Support")) {
                    Combat.appliquerEffet(this, ennemi, new Silence(2), log);
                }
            }
        }
        for (PersonnageBase allie : equipeAlliee) {
            if (allie.estVivant()) {
                allie.recevoirSoin(soin, log);
                if (allie.getRole().equals("DPS")) {
                    Combat.appliquerEffet(this, allie, new ImmuniteControle(2), log);
                }
            }
        }
        Purification.purifierEquipe(equipeAlliee, Math.random() < 0.50 ? 3 : 2, log);
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Flèche Temporelle — Inflige 100% ATK.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Marche Arrière — Inflige 135% ATK à tout les ennemis et soigne l'equipe de 50% de l'attaque (purifie 2 effets negatifs, 3 avec 50% de chance, sur chaque allie).");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Last Ages — Inflige 105% ATK (bonus selon la Rage) à tous les ennemis et soigne toute l'equipe de 100% de l'attaque (purifie 2 effets negatifs, 3 avec 50% de chance, sur chaque allie) et immunise les attaquants aux effets de controle et inflige silence aux support pendants 2 tours.");
    }
}
