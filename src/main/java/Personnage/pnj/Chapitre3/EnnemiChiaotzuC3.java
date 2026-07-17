package Personnage.pnj.Chapitre3;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

public class EnnemiChiaotzuC3 extends PersonnageBase {

    public EnnemiChiaotzuC3() { this(33); }

    public EnnemiChiaotzuC3(int niveau) {
        this.nom    = "Chiaotzu";
        this.niveau = niveau;
        this.type   = "Guerrier";
        this.role   = "Support";
        this.rarete = "C";

        double mult = 1.0;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 270.0 * mult * niv;
        this.attaque = 75.0 * mult * niv;
        this.defense = 80.0 * mult * niv;
        this.vitesse = 85.0 * mult * vit;

        this.taux_critiques    = 0.05;
        this.degat_critiques   = 1.1;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.06;
        this.taux_blocage      = 0.03;
        this.reduction_blocage = 0.05;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Onde psychique", "Paralysie mentale", "Sacrifice heroique"};
    }

    @Override public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Chiaotzu utilise Onde psychique sur " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
    }

    @Override public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Chiaotzu utilise Paralysie mentale sur " + cible.getNom() + " !");
        if (Math.random() < 0.25) {
            Combat.appliquerEffet(this, cible, new Sommeil(1), log);
        } else {
            Combat.appliquerEffet(this, cible, new ReductionAttaque(0.10, 1), log);
        }
    }

    @Override public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Chiaotzu utilise Sacrifice heroique !");
        PersonnageBase cibleSoin = null;
        for (PersonnageBase allie : equipeAlliee) {
            if (allie.estVivant()) {
                if (cibleSoin == null || allie.getVie() < cibleSoin.getVie())
                    cibleSoin = allie;
            }
        }
        if (cibleSoin != null) {
            double soin = this.getAttaque() * 0.80;
            cibleSoin.recevoirSoin(soin, log);
        }
    }

    @Override public void descriptionAttaqueBase()     { System.out.println("Onde psychique : inflige 100% ATK a une cible."); }
    @Override public void descriptionAttaqueSpeciale() { System.out.println("Paralysie mentale : 25% de chance d'endormir la cible 1 tour, sinon reduit son attaque de 10% pendant 1 tour."); }
    @Override public void descriptionAttaqueUltime()   { System.out.println("Sacrifice heroique : soigne l'allie avec le moins de PV a hauteur de 80% ATK."); }
}
