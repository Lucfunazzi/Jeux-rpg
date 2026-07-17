package Personnage.pnj.Chapitre3Elite;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

public class EnnemiKrillinC3Elite extends PersonnageBase {

    public EnnemiKrillinC3Elite() { this(44); }

    public EnnemiKrillinC3Elite(int niveau) {
        this.nom    = "Krillin";
        this.niveau = niveau;
        this.type   = "Guerrier";
        this.role   = "Support";
        this.rarete = "B";

        double mult = 1.6;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 500.0 * mult * niv;
        this.attaque = 110.0 * mult * niv;
        this.defense = 100.0 * mult * niv;
        this.vitesse = 130.0 * mult * vit;

        this.taux_critiques    = 0.1;
        this.degat_critiques   = 1.25;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.12;
        this.taux_blocage      = 0.08;
        this.reduction_blocage = 0.15;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Destructo Disc", "Kamehameha", "Soutien Tactique"};
    }
    @Override public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Krillin lance son Destructo Disc sur " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
        Combat.appliquerEffet(this, cible, new Saignement(2, 0.08), log);
    }
    @Override public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Krillin declenche son Kamehameha !");
        Combat.appliquerDegatsAvecLog(this, cible, this.getAttaque() * 1.70, log);
        Combat.appliquerEffet(this, cible, new ReductionAttaque(0.20, 2), log);
    }
    @Override public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Krillin — Soutien Tactique !");
        for (PersonnageBase a : equipeAlliee)
            if (a.estVivant()) {
                a.recevoirSoin(this.getAttaque() * 0.40, log);
                Combat.appliquerEffet(this, a, new BuffDefense(0.15, 2), log);
            }
        for (PersonnageBase c : equipeEnnemie)
            if (c.estVivant())
                Combat.appliquerEffet(this, c, new Aveuglement(0.25, 2), log);
    }
    @Override public void descriptionAttaqueBase()    { System.out.println("Destructo Disc : attaque de base."); }
    @Override public void descriptionAttaqueSpeciale(){ System.out.println("Kamehameha : attaque speciale."); }
    @Override public void descriptionAttaqueUltime()  { System.out.println("Soutien Tactique : attaque ultime."); }
}
