package Personnage.pnj.Chapitre3;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

public class EnnemiCombattantDBZ2 extends PersonnageBase {

    public EnnemiCombattantDBZ2() { this(30); }

    public EnnemiCombattantDBZ2(int niveau) {
        this.nom    = "Guerrier du Tournoi";
        this.niveau = niveau;
        this.type   = "Guerrier";
        this.role   = "DPS";
        this.rarete = "B";

        double mult = 1.30;
        double niv = Math.pow(1.05, niveau - 1);
        double vit = Math.pow(1.03, niveau - 1);
        this.vie     = 280.0 * mult * niv;
        this.attaque =  95.0 * mult * niv;
        this.defense =  60.0 * mult * niv;
        this.vitesse =  80.0 * mult * vit;

        this.taux_critiques    = 0.12;
        this.degat_critiques   = 1.25;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.07;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.10;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Coup de poing", "Frappe devastatrice", "Assaut total"};
    }
    @Override public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " frappe " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
        Combat.appliquerEffet(this, cible, new ReductionDefense(0.10, 1), log);
    }
    @Override public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " declenche une Frappe devastatrice !");
        Combat.appliquerDegatsAvecLog(this, cible, this.getAttaque() * 1.70, log);
        Combat.appliquerEffet(this, cible, new ReductionDefense(0.15, 2), log);
    }
    @Override public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " lance un Assaut total !");
        for (PersonnageBase c : equipeEnnemie)
            if (c.estVivant()) Combat.appliquerDegatsAvecLog(this, c, this.getAttaque() * 1.20, log);
        Combat.appliquerEffet(this, new BuffAttaque(0.20, 2), log);
    }
    @Override public void descriptionAttaqueBase()    { System.out.println("Coup de poing : 100% ATK + -10% DEF cible 1 tour."); }
    @Override public void descriptionAttaqueSpeciale(){ System.out.println("Frappe devastatrice : 170% ATK + -15% DEF 2 tours."); }
    @Override public void descriptionAttaqueUltime()  { System.out.println("Assaut total : 120% ATK a tous + +20% ATK soi 2 tours."); }
}
