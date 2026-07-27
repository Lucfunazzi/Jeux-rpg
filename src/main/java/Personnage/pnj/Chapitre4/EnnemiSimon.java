package Personnage.pnj.Chapitre4;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Simon — compagnon d'enfance d'Erza a la Tour du Paradis, encore sous l'emprise de
 * Jellal lors de ce combat, rang B.
 */
public class EnnemiSimon extends PersonnageBase {

    public EnnemiSimon() { this(49); }

    public EnnemiSimon(int niveau) {
        this.nom    = "Simon";
        this.niveau = niveau;
        this.type   = "Chevalier";
        this.role   = "Tank";
        this.rarete = "B";

        double mult = 1.20;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 480.0 * mult * niv;
        this.attaque = 110.0 * mult * niv;
        this.defense = 130.0 * mult * niv;
        this.vitesse = 100.0 * mult * vit;

        this.taux_critiques    = 0.08;
        this.degat_critiques   = 1.30;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.08;
        this.taux_blocage      = 0.10;
        this.reduction_blocage = 0.20;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Coup de poings", "Nuit fugitive", "Sombre Explosion"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Simon utilise coup de poings sur " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Simon utilise Nuit fugitive !");

        Combat.appliquerEffet(this, new BuffBlocage(0.10, 2), log);
        Combat.appliquerEffet(this, new BuffTauxEsquive(0.10, 2), log);
        for (PersonnageBase e : equipeEnnemie) {
            if (e.estVivant()) {
                Combat.appliquerEffet(this, e, new Aveuglement(0.10, 2), log);
            }
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Simon utilise Sombre Explosion !");

        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = this.getAttaque() * 1.20;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Coup de poing — inflige 100% ATK a une cible.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Nui fugitive — Augmente le blocage et l'equive de Simon de 10%, "
                + "applique Aveuglement 10% pendant 2 tours à tous les ennemis.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Sombre Explosion — inflige 120% ATK a tous les ennemis.");
    }
}
