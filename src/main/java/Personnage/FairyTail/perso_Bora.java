package Personnage.FairyTail;

import Combat.Combat;
import Effets.Brulure;
import Effets.Sommeil;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Bora de Prominens — Elementaliste, rang C.
 * Magie de la Protubérance (Prominens) : flammes violettes.
 * Sorts : Tapis Écarlate (vol), Fouet de la Protubérance, Typhon de la Protubérance, Douche Écarlate.
 * Aussi : Charme Magique (bague) + Magie du Sommeil (bague).
 */
public class perso_Bora extends PersonnageBase {

    public perso_Bora() {
        this.nom    = "Bora";
        this.type   = "Elementaliste";
        this.role   = "DPS";
        this.rarete = "C";
        this.niveau = 1;
        double mult = 1.00;
        this.vie     = 300 * mult;
        this.attaque = 115 * mult;
        this.defense =  65 * mult;
        this.vitesse =  85 * mult;
        this.taux_critiques    = 0.12;
        this.degat_critiques   = 1.20;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.10;
        this.taux_blocage      = 0.04;
        this.reduction_blocage = 0.05;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Fouet de la Protubérance", "Douche Écarlate", "Typhon de la Protubérance"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Bora projette des faisceaux ardents en arc sur " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
        Combat.appliquerEffet(this, cible, new Brulure(1, 0.05), log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Bora disperse une pluie de sphères de flammes écarlates sur l'équipe ennemie !");
        for (PersonnageBase c : equipeEnnemie) {
            if (c.estVivant()) {
                double degats = this.getAttaque() * 0.75;
                Combat.appliquerDegatsAvecLog(this, c, degats, log);
                Combat.appliquerEffet(this, c, new Brulure(1, 0.04), log);
            }
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Bora libère le Typhon de la Protubérance — une colonne de feu en spirale dévaste tout !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) multiplicateurRage += (this.getRage() - 100) / 100.0;
        for (PersonnageBase cible : equipeEnnemie) {
            if (cible.estVivant()) {
                double degats = (this.getAttaque() * 1.10) * multiplicateurRage;
                Combat.appliquerDegatsAvecLog(this, cible, degats, log);
                Combat.appliquerEffet(this, cible, new Brulure(2, 0.06), log);
                if (Math.random() < 0.30) Combat.appliquerEffet(this, cible, new Sommeil(1), log);
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Fouet de la Protubérance — 100% ATK, brûle 1 tour (5% PV/tour).");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Douche Écarlate — 75% ATK à tous les ennemis, brûle chacun 1 tour (4% PV/tour).");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Typhon de la Protubérance — 110% ATK à tous (x rage), brûle 2 tours, 30% d'endormissement.");
    }
}
