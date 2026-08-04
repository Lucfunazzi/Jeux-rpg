package Personnage.FairyTail;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Gildarts Clive — Elementaliste, rang SS.
 * Mage de rang S le plus ancien et le plus puissant de Fairy Tail.
 * Magie de Décomposition (Crash) : réduit tout ce qu'il touche en miettes.
 */
public class perso_Gildarts extends PersonnageBase {

    public perso_Gildarts() {
        this.nom    = "Gildarts";
        this.type   = "Elementaliste";
        this.role   = "DPS";
        this.rarete = "SSS";
        this.niveau = 1;
        double mult = 1.75;
        this.vie     = 1500 * mult;
        this.attaque = 2500 * mult;
        this.defense = 600 * mult;
        this.vitesse = 500 * mult;
        this.taux_critiques    = 0.18;
        this.degat_critiques   = 1.40;
        this.taux_precisions   = 105.00;
        this.taux_esquives     = 0.10;
        this.taux_blocage      = 0.08;
        this.reduction_blocage = 0.12;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Poing Fracassant", "Crash", "Grand Crash"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Gildarts fracasse toute l'équipe ennemie d'un Poing Fracassant !");
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = this.getAttaque() * 1.70;
                boolean touche = Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                if (touche) {
                    Combat.appliquerEffet(this, ennemi, new ReductionDefense(0.05, 1), log);
                }
            }
        }
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Gildarts désintègre tout sur son passage — Crash !");
        double degats = this.getAttaque() * 4.00;
        boolean touche = Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        if (touche) {
            Combat.appliquerEffet(this, cible, new ReductionDefense(0.50, 2), log);
        }
        Combat.appliquerEffet(this, new BuffAttaque(0.20, 2), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Gildarts déchaîne un Grand Crash — tout s'effondre en poussière !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) multiplicateurRage += (this.getRage() - 100) / 100.0;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = (this.getAttaque() * 3.00) * multiplicateurRage;
                boolean touche = Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                if (touche) {
                    Combat.appliquerEffet(this, ennemi, new Ralentissement(2, 0.15), log);
                    Combat.appliquerEffet(this, ennemi, new Silence(2), log);
                }
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Poing Fracassant — Inflige 170% ATK a tout les ennemis et reduit leurs defense de 5% pendant 1 tour.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Crash — Inflige 400% ATK à un ennemi et réduit la défense de la cible de 50% pendant 2 tours. boost sont attaque de 20%");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Grand Crash — Inflige 300% ATK (bonus selon la Rage) à tous les ennemis.Inflige ralentissement , et Silence pendants 2 tours");
    }
}
