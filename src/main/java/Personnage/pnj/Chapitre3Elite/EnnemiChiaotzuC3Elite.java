package Personnage.pnj.Chapitre3Elite;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

public class EnnemiChiaotzuC3Elite extends PersonnageBase {

    public EnnemiChiaotzuC3Elite() { this(38); }

    public EnnemiChiaotzuC3Elite(int niveau) {
        this.nom    = "Chiaotzu";
        this.niveau = niveau;
        this.type   = "Guerrier";
        this.role   = "Support";
        this.rarete = "C";

        double mult = 1.3;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 270.0 * mult * niv;
        this.attaque = 75.0 * mult * niv;
        this.defense = 80.0 * mult * niv;
        this.vitesse = 85.0 * mult * vit;

        this.taux_critiques    = 0.05;
        this.degat_critiques   = 1.1;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.08;
        this.taux_blocage      = 0.08;
        this.reduction_blocage = 0.15;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Telekinesis", "Paralysie", "Explosion Ultime"};
    }
    @Override public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Chiaotzu utilise sa Telekinesis sur " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
        Combat.appliquerEffet(this, cible, new Petrification(1), log);
    }
    @Override public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Chiaotzu paralyse " + cible.getNom() + " !");
        Combat.appliquerDegatsAvecLog(this, cible, this.getAttaque() * 1.20, log);
        Combat.appliquerEffet(this, cible, new Paralysie(2, 0.35), log);
    }
    @Override public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Chiaotzu — Explosion Ultime !");
        for (PersonnageBase c : equipeEnnemie)
            if (c.estVivant()) {
                Combat.appliquerDegatsAvecLog(this, c, this.getAttaque() * 1.40, log);
                Combat.appliquerEffet(this, c, new Etourdissement(1), log);
            }
    }
    @Override public void descriptionAttaqueBase()    { System.out.println("Telekinesis : attaque de base."); }
    @Override public void descriptionAttaqueSpeciale(){ System.out.println("Paralysie : attaque speciale."); }
    @Override public void descriptionAttaqueUltime()  { System.out.println("Explosion Ultime : attaque ultime."); }
}
