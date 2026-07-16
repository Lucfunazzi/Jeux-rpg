package Personnage.pnj.Chapitre3;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

public class EnnemiKrillinC3 extends PersonnageBase {

    public EnnemiKrillinC3() { this(30); }

    public EnnemiKrillinC3(int niveau) {
        this.nom    = "Krillin";
        this.niveau = niveau;
        this.type   = "Guerrier";
        this.role   = "Support";
        this.rarete = "B";

        double mult = 1.3;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 500.0 * mult * niv;
        this.attaque = 110.0 * mult * niv;
        this.defense = 100.0 * mult * niv;
        this.vitesse = 130.0 * mult * vit;

        this.taux_critiques    = 0.1;
        this.degat_critiques   = 1.2;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.12;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.1;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Kienzan", "Soin Senzu", "Destructo Disk en chaine"};
    }
    @Override public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Krillin lance un Kienzan sur " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
        Combat.appliquerEffet(this, cible, new Saignement(2, 0.04), log);
    }
    @Override public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        PersonnageBase plusBas = null;
        for (PersonnageBase a : equipeAlliee)
            if (a.estVivant() && (plusBas == null || a.getVie() < plusBas.getVie())) plusBas = a;
        if (plusBas != null) {
            double soin = plusBas.getVieMax() * 0.30;
            plusBas.recevoirSoin(soin, log);
            log.add("Krillin soigne " + plusBas.getNom() + " avec un Senzu !");
        }
    }
    @Override public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Krillin declenche des Destructo Disk en chaine !");
        for (PersonnageBase c : equipeEnnemie)
            if (c.estVivant()) {
                Combat.appliquerDegatsAvecLog(this, c, this.getAttaque() * 1.10, log);
                Combat.appliquerEffet(this, c, new Saignement(2, 0.04), log);
            }
    }
    @Override public void descriptionAttaqueBase()    { System.out.println("Kienzan : attaque de base."); }
    @Override public void descriptionAttaqueSpeciale(){ System.out.println("Soin Senzu : attaque speciale."); }
    @Override public void descriptionAttaqueUltime()  { System.out.println("Destructo Disk en chaine : attaque ultime."); }
}
