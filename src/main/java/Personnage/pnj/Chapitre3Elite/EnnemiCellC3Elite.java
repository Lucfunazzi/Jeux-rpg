package Personnage.pnj.Chapitre3Elite;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

public class EnnemiCellC3Elite extends PersonnageBase {

    public EnnemiCellC3Elite() { this(52); }

    public EnnemiCellC3Elite(int niveau) {
        this.nom    = "Cell Parfait";
        this.niveau = niveau;
        this.type   = "Guerrier";
        this.role   = "Tank";
        this.rarete = "S";

        double mult = 2.0;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 680.0 * mult * niv;
        this.attaque = 190.0 * mult * niv;
        this.defense = 160.0 * mult * niv;
        this.vitesse = 100.0 * mult * vit;

        this.taux_critiques    = 0.18;
        this.degat_critiques   = 1.5;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.08;
        this.taux_blocage      = 0.18;
        this.reduction_blocage = 0.3;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Absorption Parfaite", "Kamehameha Divin", "Forme Ultime"};
    }
    @Override public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Cell absorbe l'energie de " + cible.getNom() + " !");
        double degats = this.getAttaque() * 1.10;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        this.recevoirSoin(degats * 0.25, log);
    }
    @Override public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Cell Parfait libere son Kamehameha Divin !");
        Combat.appliquerDegatsAvecLog(this, cible, this.getAttaque() * 2.20, log);
        Combat.appliquerEffet(this, cible, new ReductionDefense(0.30, 3), log);
        Combat.appliquerEffet(this, cible, new Poison(3, 0.08), log);
    }
    @Override public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Cell Parfait — Forme Ultime !");
        for (PersonnageBase c : equipeEnnemie)
            if (c.estVivant()) {
                Combat.appliquerDegatsAvecLog(this, c, this.getAttaque() * 1.70, log);
                Combat.appliquerEffet(this, c, new Poison(3, 0.09), log);
                Combat.appliquerEffet(this, c, new ReductionDefense(0.20, 3), log);
            }
        this.recevoirSoin(this.getVieMax() * 0.20, log);
        log.add("Cell Parfait se regenere de 20% de ses PV max !");
    }
    @Override public void descriptionAttaqueBase()    { System.out.println("Absorption Parfaite : attaque de base."); }
    @Override public void descriptionAttaqueSpeciale(){ System.out.println("Kamehameha Divin : attaque speciale."); }
    @Override public void descriptionAttaqueUltime()  { System.out.println("Forme Ultime : attaque ultime."); }
}
