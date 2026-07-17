package Personnage.pnj.Chapitre3;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

public class EnnemiVegetaC3 extends PersonnageBase {

    public EnnemiVegetaC3() { this(35); }

    public EnnemiVegetaC3(int niveau) {
        this.nom    = "Vegeta";
        this.niveau = niveau;
        this.type   = "Guerrier";
        this.role   = "DPS";
        this.rarete = "A";

        double mult = 1.4;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 200.0 * mult * niv;
        this.attaque = 210.0 * mult * niv;
        this.defense = 100.0 * mult * niv;
        this.vitesse = 125.0 * mult * vit;

        this.taux_critiques    = 0.18;
        this.degat_critiques   = 1.5;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.1;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.1;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Galick Ho", "Big Bang Attack", "Final Flash"};
    }
    @Override public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Vegeta tire son Galick Ho sur " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
        Combat.appliquerEffet(this, cible, new ReductionDefense(0.15, 2), log);
    }
    @Override public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Vegeta declenche une Big Bang Attack !");
        Combat.appliquerDegatsAvecLog(this, cible, this.getAttaque() * 2.00, log);
        Combat.appliquerEffet(this, cible, new Etourdissement(1), log);
    }
    @Override public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Vegeta libere son Final Flash devastateur !");
        for (PersonnageBase c : equipeEnnemie)
            if (c.estVivant()) {
                Combat.appliquerDegatsAvecLog(this, c, this.getAttaque() * 1.50, log);
                Combat.appliquerEffet(this, c, new ReductionDefense(0.20, 2), log);
            }
    }
    @Override public void descriptionAttaqueBase()    { System.out.println("Galick Ho : attaque de base."); }
    @Override public void descriptionAttaqueSpeciale(){ System.out.println("Big Bang Attack : attaque speciale."); }
    @Override public void descriptionAttaqueUltime()  { System.out.println("Final Flash : attaque ultime."); }
}
