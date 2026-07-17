package Personnage.pnj.Chapitre3Elite;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

public class EnnemiC18C3Elite extends PersonnageBase {

    public EnnemiC18C3Elite() { this(48); }

    public EnnemiC18C3Elite(int niveau) {
        this.nom    = "C-18";
        this.niveau = niveau;
        this.type   = "Guerrier";
        this.role   = "Support";
        this.rarete = "S";

        double mult = 1.75;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 700.0 * mult * niv;
        this.attaque = 150.0 * mult * niv;
        this.defense = 90.0 * mult * niv;
        this.vitesse = 120.0 * mult * vit;

        this.taux_critiques    = 0.1;
        this.degat_critiques   = 1.5;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.12;
        this.taux_blocage      = 0.08;
        this.reduction_blocage = 0.15;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Bras Mecanique", "Soin Android", "Barrage Android"};
    }
    @Override public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("C-18 frappe " + cible.getNom() + " avec son bras mecanique !");
        Combat.attaquer(this, cible, log);
        Combat.appliquerEffet(this, cible, new ReductionDefense(0.15, 2), log);
    }
    @Override public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("C-18 soigne son equipe !");
        for (PersonnageBase a : equipeAlliee)
            if (a.estVivant()) {
                a.recevoirSoin(this.getAttaque() * 0.45, log);
                Combat.appliquerEffet(this, a, new BuffDefense(0.15, 2), log);
            }
    }
    @Override public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("C-18 — Barrage Android !");
        for (PersonnageBase c : equipeEnnemie)
            if (c.estVivant()) {
                Combat.appliquerDegatsAvecLog(this, c, this.getAttaque() * 1.40, log);
                Combat.appliquerEffet(this, c, new Confusion(2), log);
            }
        for (PersonnageBase a : equipeAlliee)
            if (a.estVivant())
                Combat.appliquerEffet(this, a, new Invincibilite(1), log);
    }
    @Override public void descriptionAttaqueBase()    { System.out.println("Bras Mecanique : attaque de base."); }
    @Override public void descriptionAttaqueSpeciale(){ System.out.println("Soin Android : attaque speciale."); }
    @Override public void descriptionAttaqueUltime()  { System.out.println("Barrage Android : attaque ultime."); }
}
