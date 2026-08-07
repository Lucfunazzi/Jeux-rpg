package Personnage.pnj.Chapitre8;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Gildarts Clive — version examinatrice (Chapitre 8, stage 4 : examen de rang S,
 * duel contre Natsu). Magie de Décomposition (Crash) : réduit tout ce qu'il touche
 * en miettes. Considéré comme le mage le plus puissant de Fairy Tail.
 */
public class EnnemiGildarts extends PersonnageBase {

    public EnnemiGildarts() { this(93); }

    public EnnemiGildarts(int niveau) {
        this.nom    = "Gildarts";
        this.niveau = niveau;
        this.type   = "Elementaliste";
        this.role   = "DPS";
        this.rarete = "SSS";

        double mult = 1.90;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 700.0 * mult * niv;
        this.attaque = 270.0 * mult * niv;
        this.defense = 160.0 * mult * niv;
        this.vitesse = 135.0 * mult * vit;

        this.taux_critiques    = 0.20;
        this.degat_critiques   = 1.45;
        this.taux_precisions   = 110.00;
        this.taux_esquives     = 0.12;
        this.taux_blocage      = 0.08;
        this.reduction_blocage = 0.14;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    /** Rang SSS : immunise aux effets de controle (Etourdissement, Paralysie, Sommeil,
     *  Petrification, Gel) pendant tout le combat. */
    @Override
    public void reinitialiserPourCombat() {
        super.reinitialiserPourCombat();
        appliquerImmuniteControlePassive();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Poing Fracassant", "Crash", "Grand Crash"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Gildarts fracasse " + cible.getNom() + " d'un Poing Fracassant !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Gildarts désintègre tout sur son passage — Crash !");
        double degats = this.getAttaque() * 1.45;
        boolean touche = Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        if (touche) {
            Combat.appliquerEffet(this, cible, new ReductionDefense(0.20, 2), log);
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Gildarts déchaîne un Grand Crash — tout s'effondre en poussière !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) multiplicateurRage += (this.getRage() - 100) / 100.0;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = (this.getAttaque() * 1.20) * multiplicateurRage;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Poing Fracassant — Inflige 100% ATK.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Crash — Inflige 145% ATK et réduit la défense de la cible de 20% pendant 2 tours.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Grand Crash — Inflige 120% ATK (bonus selon la Rage) à tous les ennemis.");
    }
}
