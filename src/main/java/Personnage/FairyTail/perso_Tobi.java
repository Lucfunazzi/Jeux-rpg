package Personnage.FairyTail;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Tobi Ololta (Toby) — Elementaliste, rang C.
 * Magie des Griffes Paralysantes : griffes qui paralysent au contact.
 * Coéquipier fidèle de Yuka, membre de Lamia Scale.
 */
public class perso_Tobi extends PersonnageBase {

    public perso_Tobi() {
        this.nom    = "Tobi";
        this.type   = "Elementaliste";
        this.role   = "DPS";
        this.rarete = "C";
        this.niveau = 1;
        double mult = 1.00;
        this.vie     = 250 * mult;
        this.attaque = 145 * mult;
        this.defense =  70 * mult;
        this.vitesse = 115 * mult;
        this.taux_critiques    = 0.15;
        this.degat_critiques   = 1.30;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.08;
        this.taux_blocage      = 0.04;
        this.reduction_blocage = 0.05;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Coup de Griffe", "Griffe Paralysante ", "Assaut de Griffes"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Tobi lacère " + cible.getNom() + " de ses griffes  !");
        Combat.attaquer(this, cible, log);
        
        
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Tobi  griffes et frappe " + cible.getNom() + " avec puissance!");
        double degats = this.getAttaque() * 1.35;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new Paralysie(1,0.20), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Tobi se lance dans un assaut frénétique et lacère toute l'équipe ennemie de ses griffes !");
        for (PersonnageBase cible : equipeEnnemie) {
            if (cible.estVivant()) {
                double degats = this.getAttaque() * 0.70;
                Combat.appliquerDegatsAvecLog(this, cible, degats, log);
                if (Math.random() < 0.35) {
                    Combat.appliquerEffet(this, cible, new Paralysie(1,0.20), log);
                }
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Coup de Griffe — Inflige 100% ATK.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Griffe Paralysante  — Inflige 135% ATK, paralyse la cible 1 tour.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Assaut de Griffes — Inflige 70% ATK à tous, 35% de chance de paralyser chacun 1 tour.");
    }
}
