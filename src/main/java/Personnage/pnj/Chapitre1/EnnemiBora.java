package Personnage.pnj.Chapitre1;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Bora de Prominens — Mage de la Protubérance, rang C.
 * Arc Prologue : se faisait passer pour Salamander de Fairy Tail.
 * Magie de la Protubérance (feu) : flammes violettes en spirale + Charme Magique + Magie du Sommeil.
 * Sorts principaux : Fouet de la Protubérance, Typhon de la Protubérance, Douche Écarlate.
 */
public class EnnemiBora extends PersonnageBase {

    public EnnemiBora() { this(4); }

    public EnnemiBora(int niveau) {
        this.nom    = "Bora";
        this.niveau = niveau;
        this.type   = "Elementaliste";
        this.role   = "DPS";
        this.rarete = "C";

        double niv = Math.pow(1.05, niveau - 1);
        double vit = Math.pow(1.03, niveau - 1);
        this.vie     = 200.0 * niv;
        this.attaque =  85.0 * niv;
        this.defense =  45.0 * niv;
        this.vitesse =  75.0 * vit;

        this.taux_critiques    = 0.12;
        this.degat_critiques   = 1.20;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.08;
        this.taux_blocage      = 0.03;
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
        log.add("Bora invoque son sceau magique et projette des faisceaux ardents en arc sur " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
       
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Bora disperse une pluie de sphères de flammes écarlates sur " + cible.getNom() + " et ses alliés !");
       double degats = this.getAttaque() *1.20;
       Combat.appliquerDegatsAvecLog(this, cible, degats, log);
       Combat.appliquerEffet(this, new BuffPrecision(100, 2), log);
           
        }
    

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Bora écarte les bras et libère une colonne de feu en spirale — le Typhon de la Protubérance !");
        
                for (PersonnageBase cibleTank : equipeEnnemie) {
            if (cibleTank.estVivant() && cibleTank.getRole().equals("Tank")) {
                double degats = this.getAttaque() * 0.80;
                Combat.appliquerDegatsAvecLog(this, cibleTank, degats, log);
                
                Combat.appliquerEffet(this, cibleTank, new Brulure(2,0.05), log);
            }
      }
    }
    @Override public void descriptionAttaqueBase() {
        System.out.println("Fouet de la Protubérance — Inflige 100% ATK, brûle 1 tour (5% PV/tour).");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Douche Écarlate — Inflige 75% ATK à tous les ennemis, brûle chacun 1 tour (4% PV/tour).");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Typhon de la Protubérance — Inflige 110% ATK à tous, brûle 2 tours (6% PV/tour), 30% d'endormissement.");
    }
}
