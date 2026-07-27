package Personnage.pnj.Chapitre4;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Miliana — compagne d'enfance d'Erza a la Tour du Paradis, sous l'emprise de Jellal, rang C.
 */
public class EnnemiMiliana extends PersonnageBase {

    public EnnemiMiliana() { this(47); }

    public EnnemiMiliana(int niveau) {
        this.nom    = "Miliana";
        this.niveau = niveau;
        this.type   = "Elementaliste";
        this.role   = "Support";
        this.rarete = "C";

        double mult = 1.00;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 360.0 * mult * niv;
        this.attaque =  80.0 * mult * niv;
        this.defense =  95.0 * mult * niv;
        this.vitesse = 145.0 * mult * vit;

        this.taux_critiques    = 0.10;
        this.degat_critiques   = 1.20;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.06;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.12;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Coup de poing félin", "Entraves Féline multiples ", "Kitten Blast"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Miliana utilise coup de poing félin sur " + cible.getNom());
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Miliana utilise entraves feline multiples sur les Supports ennemis !");
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant() && ennemi.getRole().equals("Support")) {
                double degats = this.getAttaque() * 0.80;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                if (Math.random() < 0.50) {
                    Combat.appliquerEffet(this, ennemi, new Etourdissement(1), log);
                }
            }
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Miliana utilise Kitten Blast sur le Tank ennemi !");
        PersonnageBase cibleTank = Combat.cibleParRole(equipeEnnemie, "Tank");
        if (cibleTank == null) return;
        double degats = this.getAttaque() * 1.30;
        Combat.appliquerDegatsAvecLog(this, cibleTank, degats, log);
        if (Math.random() < 0.50) {
            Combat.appliquerEffet(this, cibleTank, new Etourdissement(1), log);
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Coup de poing félin — Inflige 100% ATK");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Entraves Féline multiples — Inflige 80% ATK aux Supports ennemis, 50% de chance d'étourdir chacun d'eux pendant 1 tour.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Kitten Blast — Inflige 130% ATK au Tank ennemi, 50% de chance de l'étourdir pendant 1 tour.");
    }
}
