package Personnage.pnj.Chapitre3;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

public class EnnemiRaditzC3 extends PersonnageBase {

    public EnnemiRaditzC3() { this(30); }

    public EnnemiRaditzC3(int niveau) {
        this.nom    = "Raditz";
        this.niveau = niveau;
        this.type   = "Guerrier";
        this.role   = "DPS";
        this.rarete = "B";

        double mult = 1.3;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 400.0 * mult * niv;
        this.attaque = 150.0 * mult * niv;
        this.defense = 100.0 * mult * niv;
        this.vitesse = 110.0 * mult * vit;

        this.taux_critiques    = 0.15;
        this.degat_critiques   = 1.3;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.08;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.1;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Queue saiyan", "Vendredi Samedi", "Pluie de Ki"};
    }
    @Override public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Raditz frappe " + cible.getNom() + " avec sa queue !");
        Combat.attaquer(this, cible, log);
        Combat.appliquerEffet(this, cible, new Etourdissement(1), log);
    }
    @Override public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Raditz execute son attaque Vendredi Samedi !");
        Combat.appliquerDegatsAvecLog(this, cible, this.getAttaque() * 1.70, log);
        Combat.appliquerEffet(this, cible, new ReductionDefense(0.20, 2), log);
    }
    @Override public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Raditz declenche une Pluie de Ki !");
        for (PersonnageBase c : equipeEnnemie)
            if (c.estVivant()) Combat.appliquerDegatsAvecLog(this, c, this.getAttaque() * 1.20, log);
    }
    @Override public void descriptionAttaqueBase()    { System.out.println("Queue saiyan : attaque de base."); }
    @Override public void descriptionAttaqueSpeciale(){ System.out.println("Vendredi Samedi : attaque speciale."); }
    @Override public void descriptionAttaqueUltime()  { System.out.println("Pluie de Ki : attaque ultime."); }
}
