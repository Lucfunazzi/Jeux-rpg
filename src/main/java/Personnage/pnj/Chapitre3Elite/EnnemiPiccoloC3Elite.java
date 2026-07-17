package Personnage.pnj.Chapitre3Elite;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

public class EnnemiPiccoloC3Elite extends PersonnageBase {

    public EnnemiPiccoloC3Elite() { this(46); }

    public EnnemiPiccoloC3Elite(int niveau) {
        this.nom    = "Piccolo";
        this.niveau = niveau;
        this.type   = "Guerrier";
        this.role   = "Support";
        this.rarete = "A";

        double mult = 1.7;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 560.0 * mult * niv;
        this.attaque = 180.0 * mult * niv;
        this.defense = 130.0 * mult * niv;
        this.vitesse = 115.0 * mult * vit;

        this.taux_critiques    = 0.12;
        this.degat_critiques   = 1.35;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.08;
        this.taux_blocage      = 0.08;
        this.reduction_blocage = 0.15;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Canon Special Beam", "Allongement", "Makankosappo"};
    }
    @Override public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Piccolo tire son Canon Special Beam sur " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
        Combat.appliquerEffet(this, cible, new Marquage(2, 0.15), log);
    }
    @Override public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Piccolo allonge son bras !");
        Combat.appliquerDegatsAvecLog(this, cible, this.getAttaque() * 1.80, log);
        Combat.appliquerEffet(this, cible, new Petrification(2), log);
    }
    @Override public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Piccolo — Makankosappo !");
        for (PersonnageBase c : equipeEnnemie)
            if (c.estVivant()) {
                Combat.appliquerDegatsAvecLog(this, c, this.getAttaque() * 1.30, log);
                Combat.appliquerEffet(this, c, new ReductionDefense(0.20, 3), log);
            }
        for (PersonnageBase a : equipeAlliee)
            if (a.estVivant())
                a.recevoirSoin(this.getAttaque() * 0.30, log);
    }
    @Override public void descriptionAttaqueBase()    { System.out.println("Canon Special Beam : attaque de base."); }
    @Override public void descriptionAttaqueSpeciale(){ System.out.println("Allongement : attaque speciale."); }
    @Override public void descriptionAttaqueUltime()  { System.out.println("Makankosappo : attaque ultime."); }
}
