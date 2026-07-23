package Personnage.FairyTail;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Totomaru — Elementaliste, rang B.
 * Magie des Flammes Arc-en-Ciel (Rainbow Fire) + Pyrokinésie.
 * Feu Blanc (brûlures), Feu Bleu (glacé), Feu Orange (nauséabond), Feu Arc-en-ciel (destructeur).
 * Contrôle les flammes adverses. Professeur de Romeo (enseigné la magie du feu arc-en-ciel).
 */
public class perso_Totomaru extends PersonnageBase {

    public perso_Totomaru() {
        this.nom    = "Totomaru";
        this.type   = "Elementaliste";
        this.role   = "DPS";
        this.rarete = "B";
        this.niveau = 1;
        double mult = 1.30;
        this.vie     = 370 * mult;
        this.attaque = 165 * mult;
        this.defense = 110 * mult;
        this.vitesse = 120 * mult;
        this.taux_critiques    = 0.20;
        this.degat_critiques   = 1.20;
        this.taux_precisions   = 105.00;
        this.taux_esquives     = 0.08;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.08;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Flamme Blanche", "Feu Orange Nauséabond", "Flammes Arc-en-Ciel"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Totomaru projette une flamme blanche intense sur " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Totomaru génère un feu orange nauséabond — " + cible.getNom() + " est submergé par l'odeur répugnante !");
        double degats = this.getAttaque() * 1.20;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new Poison(2, 0.06), log);
        
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Totomaru déchaîne les Flammes Arc-en-Ciel — la combinaison mortelle de toutes ses flammes !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) multiplicateurRage += (this.getRage() - 100) / 100.0;
        
        for (PersonnageBase cible : equipeEnnemie) {
            if (cible.estVivant()) {
                double degats = (this.getAttaque() * 1.00) * multiplicateurRage;
                Combat.appliquerDegatsAvecLog(this, cible, degats, log);
                Combat.appliquerEffet(this, cible, new Brulure(2, 0.07), log);
               
            }
        }
    }

    @Override public void descriptionAttaqueBase() { System.out.println("Flamme Blanche — 100% ATK."); }
    @Override public void descriptionAttaqueSpeciale() { System.out.println("Feu Orange Nauséabond — 120% ATK, empoisonne 2 tours."); }
    @Override public void descriptionAttaqueUltime() { System.out.println("Flammes Arc-en-Ciel —  100% ATK à tous (x rage), brûle  2 tours."); }
}
