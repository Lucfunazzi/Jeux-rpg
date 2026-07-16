package Personnage.pnj.Chapitre3;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

public class EnnemiNappaC3 extends PersonnageBase {

    public EnnemiNappaC3() { this(32); }

    public EnnemiNappaC3(int niveau) {
        this.nom    = "Nappa";
        this.niveau = niveau;
        this.type   = "Guerrier";
        this.role   = "Tank";
        this.rarete = "B";

        double mult = 1.3;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 620.0 * mult * niv;
        this.attaque = 140.0 * mult * niv;
        this.defense = 130.0 * mult * niv;
        this.vitesse = 80.0 * mult * vit;

        this.taux_critiques    = 0.08;
        this.degat_critiques   = 1.2;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.03;
        this.taux_blocage      = 0.2;
        this.reduction_blocage = 0.25;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Brise-Garde", "Bombe Saiyan", "Eruption de Ki"};
    }
    @Override public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Nappa brise la garde de " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
        Combat.appliquerEffet(this, cible, new ReductionDefense(0.15, 2), log);
    }
    @Override public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Nappa lance une Bombe Saiyan sur " + cible.getNom() + " !");
        Combat.appliquerDegatsAvecLog(this, cible, this.getAttaque() * 1.80, log);
    }
    @Override public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Nappa libere une Eruption de Ki devastatrice !");
        for (PersonnageBase c : equipeEnnemie)
            if (c.estVivant()) {
                Combat.appliquerDegatsAvecLog(this, c, this.getAttaque() * 1.30, log);
                Combat.appliquerEffet(this, c, new Etourdissement(1), log);
            }
    }
    @Override public void descriptionAttaqueBase()    { System.out.println("Brise-Garde : attaque de base."); }
    @Override public void descriptionAttaqueSpeciale(){ System.out.println("Bombe Saiyan : attaque speciale."); }
    @Override public void descriptionAttaqueUltime()  { System.out.println("Eruption de Ki : attaque ultime."); }
}
