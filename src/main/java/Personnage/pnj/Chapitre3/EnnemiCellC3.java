package Personnage.pnj.Chapitre3;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

public class EnnemiCellC3 extends PersonnageBase {

    public EnnemiCellC3() { this(40); }

    public EnnemiCellC3(int niveau) {
        this.nom    = "Cell";
        this.niveau = niveau;
        this.type   = "Guerrier";
        this.role   = "Tank";
        this.rarete = "S";

        double mult = 1.6;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 680.0 * mult * niv;
        this.attaque = 190.0 * mult * niv;
        this.defense = 160.0 * mult * niv;
        this.vitesse = 100.0 * mult * vit;

        this.taux_critiques    = 0.15;
        this.degat_critiques   = 1.5;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.08;
        this.taux_blocage      = 0.15;
        this.reduction_blocage = 0.2;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Absorption cellulaire", "Kamehameha Parfait", "Forme Impure — Etat critique"};
    }
    @Override public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Cell absorbe l'energie de " + cible.getNom() + " !");
        double degats = this.getAttaque() * 1.00;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        double soin = degats * 0.20;
        this.recevoirSoin(soin, log);
        log.add("Cell recupere " + String.format("%.1f", soin) + " PV !");
    }
    @Override public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Cell libere un Kamehameha Parfait !");
        Combat.appliquerDegatsAvecLog(this, cible, this.getAttaque() * 2.00, log);
        Combat.appliquerEffet(this, cible, new ReductionDefense(0.25, 3), log);
    }
    @Override public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Cell entre en Etat critique !");
        for (PersonnageBase c : equipeEnnemie)
            if (c.estVivant()) {
                Combat.appliquerDegatsAvecLog(this, c, this.getAttaque() * 1.60, log);
                Combat.appliquerEffet(this, c, new Poison(3, 0.07), log);
                Combat.appliquerEffet(this, c, new ReductionDefense(0.15, 2), log);
            }
        this.recevoirSoin(this.getVieMax() * 0.15, log);
        log.add("Cell se regenere de 15% de ses PV max !");
    }
    @Override public void descriptionAttaqueBase()    { System.out.println("Absorption cellulaire : attaque de base."); }
    @Override public void descriptionAttaqueSpeciale(){ System.out.println("Kamehameha Parfait : attaque speciale."); }
    @Override public void descriptionAttaqueUltime()  { System.out.println("Forme Impure — Etat critique : attaque ultime."); }
}
