package Personnage.pnj.Chapitre3;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

public class EnnemiC17C3 extends PersonnageBase {

    public EnnemiC17C3() { this(36); }

    public EnnemiC17C3(int niveau) {
        this.nom    = "C-17";
        this.niveau = niveau;
        this.type   = "Guerrier";
        this.role   = "DPS";
        this.rarete = "S";

        double mult = 1.5;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 600.0 * mult * niv;
        this.attaque = 200.0 * mult * niv;
        this.defense = 120.0 * mult * niv;
        this.vitesse = 110.0 * mult * vit;

        this.taux_critiques    = 0.15;
        this.degat_critiques   = 1.4;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.1;
        this.taux_blocage      = 0.08;
        this.reduction_blocage = 0.1;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Rafale Android", "Barriere d'energie", "Infinite Energy"};
    }
    @Override public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("C-17 tire une Rafale Android sur " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
    }
    @Override public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("C-17 deploie une Barriere d'energie !");
        double bouclier = this.getVieMax() * 0.30;
        for (PersonnageBase a : equipeAlliee)
            if (a.estVivant()) Combat.appliquerEffet(this, a, new Bouclier(bouclier), log);
        Combat.appliquerDegatsAvecLog(this, cible, this.getAttaque() * 1.50, log);
    }
    @Override public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("C-17 active Infinite Energy et s'enchaine sur toute l'equipe !");
        for (PersonnageBase c : equipeEnnemie)
            if (c.estVivant()) {
                Combat.appliquerDegatsAvecLog(this, c, this.getAttaque() * 1.40, log);
                Combat.appliquerEffet(this, c, new ReductionVitesse(0.20, 2), log);
            }
    }
    @Override public void descriptionAttaqueBase()    { System.out.println("Rafale Android : attaque de base."); }
    @Override public void descriptionAttaqueSpeciale(){ System.out.println("Barriere d'energie : attaque speciale."); }
    @Override public void descriptionAttaqueUltime()  { System.out.println("Infinite Energy : attaque ultime."); }
}
