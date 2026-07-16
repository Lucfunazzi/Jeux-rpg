package Personnage.pnj.Chapitre3;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

public class EnnemiChiaotzuC3 extends PersonnageBase {

    public EnnemiChiaotzuC3() { this(28); }

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
        return new String[]{"Telekinésie", "Paralysie mentale", "Benediction de l'equipe"};
    }
    @Override public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Chiaotzu utilise la telekinésie sur " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
        Combat.appliquerEffet(this, cible, new Ralentissement(1,0.20), log);
    }
    @Override public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Chiaotzu paralyse " + cible.getNom() + " !");
        Combat.appliquerEffet(this, cible, new Paralysie(2,0.20), log);
        Combat.appliquerDegatsAvecLog(this, cible, this.getAttaque() * 1.20, log);
    }
    @Override public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Chiaotzu benit toute son equipe !");
        for (PersonnageBase a : equipeAlliee)
            if (a.estVivant()) {
                a.recevoirSoin(this.getAttaque() * 0.60, log);
                Combat.appliquerEffet(this, a, new BuffAttaque(0.10, 2), log);
            }
    }
    @Override public void descriptionAttaqueBase()    { System.out.println("Telekinésie : attaque de base."); }
    @Override public void descriptionAttaqueSpeciale(){ System.out.println("Paralysie mentale : attaque speciale."); }
    @Override public void descriptionAttaqueUltime()  { System.out.println("Benediction de l'equipe : attaque ultime."); }
}
