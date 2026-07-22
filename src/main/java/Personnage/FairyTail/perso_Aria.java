package Personnage.FairyTail;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Aria — Elementaliste, rang A.
 * Magie des Espaces Aériens (Airspace) : attaques invisibles de vent/air.
 * Metsu : draine toute la magie de la cible. Zetsu : jets d'air multiples. Zéro : aspire l'air.
 * Le plus puissant des Element 4 de Phantom Lord. Porte un bandeau pour contenir sa magie.
 */
public class perso_Aria extends PersonnageBase {

    public perso_Aria() {
        this.nom    = "Aria";
        this.type   = "Elementaliste";
        this.role   = "DPS";
        this.rarete = "A";
        this.niveau = 1;
        double mult = 1.40;
        this.vie     = 420 * mult;
        this.attaque = 190 * mult;
        this.defense = 110 * mult;
        this.vitesse = 130 * mult;
        this.taux_critiques    = 0.20;
        this.degat_critiques   = 1.45;
        this.taux_precisions   = 105.00;
        this.taux_esquives     = 0.14;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.08;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Zetsu — Jets d'Air Invisibles", "Metsu — Drain Magique", "Zéro — Vide Absolu"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Aria projette des jets d'air invisible sur " + cible.getNom() + " — l'attaque est indétectable !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Aria place ses mains vers " + cible.getNom() + " — METSU ! Sa magie est complètement aspirée !");
        double degats = this.getAttaque() * 1.60;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        
        Combat.appliquerEffet(this, cible, new Silence(2), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Aria retire son bandeau — ZÉRO ! L'air est aspiré de tout l'espace autour des ennemis !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) multiplicateurRage += (this.getRage() - 100) / 100.0;
        Combat.appliquerEffet(this, new BuffAttaque(0.25, 2), log);
        for (PersonnageBase cible : equipeEnnemie) {
            if (cible.estVivant()) {
                double degats = (this.getAttaque() * 1.20) * multiplicateurRage;
                Combat.appliquerDegatsAvecLog(this, cible, degats, log);
                Combat.appliquerEffet(this, cible, new BuffTauxCritique(0.20,2), log);
                
            }
        }
    }

    @Override public void descriptionAttaqueBase() { System.out.println("Zetsu — Jets Invisibles : 100% ATK."); }
    @Override public void descriptionAttaqueSpeciale() { System.out.println("Metsu — Drain Magique : 160% ATK, maudit et silence 2 tours."); }
    @Override public void descriptionAttaqueUltime() { System.out.println("Zéro — Vide Absolu : +25% ATK, 120% ATK à tous (x rage), silence 3 tours, réduit ATK de 25%."); }
}
