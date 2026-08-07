package Personnage.pnj.Chapitre7;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Dorma Anim — dragon mécanique géant piloté par le Roi Faust d'Edolas, rang SS.
 * Boss final du Chapitre 7 : une machine de guerre alimentée par la force vitale
 * d'Exceeds capturés, capable de raser des villes entières.
 */
public class EnnemiDormaAnim extends PersonnageBase {

    public EnnemiDormaAnim() { this(82); }

    public EnnemiDormaAnim(int niveau) {
        this.nom    = "Dorma Anim";
        this.niveau = niveau;
        this.type   = "Chevalier";
        this.role   = "Tank";
        this.rarete = "SS";

        double mult = 1.85;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 900.0 * mult * niv;
        this.attaque = 250.0 * mult * niv;
        this.defense = 210.0 * mult * niv;
        this.vitesse =  95.0 * mult * vit;

        this.taux_critiques    = 0.10;
        this.degat_critiques   = 1.20;
        this.taux_precisions   = 108.00;
        this.taux_esquives     = 0.04;
        this.taux_blocage      = 0.14;
        this.reduction_blocage = 0.18;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Griffe Mécanique", "Souffle Destructeur", "Rugissement du Jugement"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Dorma Anim lacère " + cible.getNom() + " de sa Griffe Mécanique !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Dorma Anim libère un Souffle Destructeur sur toute l'équipe ennemie !");
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = this.getAttaque() * 1.20;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            }
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Dorma Anim déchaîne son Rugissement du Jugement — la terre elle-même tremble !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) multiplicateurRage += (this.getRage() - 100) / 100.0;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = (this.getAttaque() * 1.70) * multiplicateurRage;
                boolean touche = Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                if (touche) {
                    Combat.appliquerEffet(this, ennemi, new ReductionDefense(0.20, 2), log);
                }
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Griffe Mécanique — Inflige 100% ATK.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Souffle Destructeur — Inflige 120% ATK à tous les ennemis.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Rugissement du Jugement — Inflige 170% ATK (bonus selon la Rage) à tous les ennemis et réduit leur défense de 20% pendant 2 tours.");
    }
}
