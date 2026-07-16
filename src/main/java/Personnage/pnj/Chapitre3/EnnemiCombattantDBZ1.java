package Personnage.pnj.Chapitre3;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

public class EnnemiCombattantDBZ1 extends PersonnageBase {

    public EnnemiCombattantDBZ1() { this(28); }

    public EnnemiCombattantDBZ1(int niveau) {
        this.nom    = "Combattant du Tournoi";
        this.niveau = niveau;
        this.type   = "Guerrier";
        this.role   = "DPS";
        this.rarete = "C";

        double niv = Math.pow(1.05, niveau - 1);
        double vit = Math.pow(1.03, niveau - 1);
        this.vie     = 280.0 * niv;
        this.attaque =  95.0 * niv;
        this.defense =  60.0 * niv;
        this.vitesse =  80.0 * vit;

        this.taux_critiques    = 0.10;
        this.degat_critiques   = 1.20;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.05;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.10;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Coup de poing", "Frappe puissante", "Assaut total"};
    }
    @Override public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " frappe " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
    }
    @Override public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " decoche une Frappe puissante !");
        Combat.appliquerDegatsAvecLog(this, cible, this.getAttaque() * 1.50, log);
    }
    @Override public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " lance un Assaut total !");
        for (PersonnageBase c : equipeEnnemie)
            if (c.estVivant()) Combat.appliquerDegatsAvecLog(this, c, this.getAttaque() * 1.10, log);
    }
    @Override public void descriptionAttaqueBase()    { System.out.println("Coup de poing : 100% ATK."); }
    @Override public void descriptionAttaqueSpeciale(){ System.out.println("Frappe puissante : 150% ATK."); }
    @Override public void descriptionAttaqueUltime()  { System.out.println("Assaut total : 110% ATK a tous."); }
}
