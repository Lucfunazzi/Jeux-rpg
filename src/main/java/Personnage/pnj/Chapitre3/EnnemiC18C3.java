package Personnage.pnj.Chapitre3;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

public class EnnemiC18C3 extends PersonnageBase {

    public EnnemiC18C3() { this(36); }

    public EnnemiC18C3(int niveau) {
        this.nom    = "C-18";
        this.niveau = niveau;
        this.type   = "Guerrier";
        this.role   = "Support";
        this.rarete = "S";

        double mult = 1.5;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 700.0 * mult * niv;
        this.attaque = 150.0 * mult * niv;
        this.defense = 90.0 * mult * niv;
        this.vitesse = 120.0 * mult * vit;

        this.taux_critiques    = 0.1;
        this.degat_critiques   = 1.3;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.12;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.1;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Frappe Android", "Soin Android", "Duo Lethal"};
    }
    @Override public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("C-18 frappe " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
        Combat.appliquerEffet(this, cible, new ReductionVitesse(0.15, 2), log);
    }
    @Override public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("C-18 repare son equipe avec un Soin Android !");
        for (PersonnageBase a : equipeAlliee)
            if (a.estVivant()) {
                double soin = this.getAttaque() * 0.70;
                a.recevoirSoin(soin, log);
            }
    }
    @Override public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("C-17 et C-18 executent leur Duo Lethal !");
        for (PersonnageBase c : equipeEnnemie)
            if (c.estVivant()) {
                Combat.appliquerDegatsAvecLog(this, c, this.getAttaque() * 1.60, log);
                Combat.appliquerEffet(this, c, new Etourdissement(1), log);
            }
    }
    @Override public void descriptionAttaqueBase()    { System.out.println("Frappe Android : attaque de base."); }
    @Override public void descriptionAttaqueSpeciale(){ System.out.println("Soin Android : attaque speciale."); }
    @Override public void descriptionAttaqueUltime()  { System.out.println("Duo Lethal : attaque ultime."); }
}
