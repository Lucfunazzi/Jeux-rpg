package Personnage.pnj.Chapitre3Elite;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

public class EnnemiC17C3Elite extends PersonnageBase {

    public EnnemiC17C3Elite() { this(48); }

    public EnnemiC17C3Elite(int niveau) {
        this.nom    = "C-17";
        this.niveau = niveau;
        this.type   = "Guerrier";
        this.role   = "DPS";
        this.rarete = "S";

        double mult = 1.75;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 600.0 * mult * niv;
        this.attaque = 200.0 * mult * niv;
        this.defense = 120.0 * mult * niv;
        this.vitesse = 110.0 * mult * vit;

        this.taux_critiques    = 0.15;
        this.degat_critiques   = 1.5;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.1;
        this.taux_blocage      = 0.07;
        this.reduction_blocage = 0.15;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Frappe de Precision", "Android Barrier Blast", "Hell Flash"};
    }
    @Override public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("C-17 frappe " + cible.getNom() + " avec precision !");
        Combat.attaquer(this, cible, log);
        Combat.appliquerEffet(this, cible, new Ralentissement(2, 15), log);
    }
    @Override public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("C-17 tire un Android Barrier Blast !");
        Combat.appliquerDegatsAvecLog(this, cible, this.getAttaque() * 2.00, log);
        Combat.appliquerEffet(this, cible, new Silence(2), log);
        Combat.appliquerEffet(this, this, new Bouclier(this.getVieMax() * 0.20), log);
    }
    @Override public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("C-17 — Hell Flash !");
        for (PersonnageBase c : equipeEnnemie)
            if (c.estVivant()) {
                Combat.appliquerDegatsAvecLog(this, c, this.getAttaque() * 1.50, log);
                Combat.appliquerEffet(this, c, new ReductionAttaque(0.20, 3), log);
            }
        Combat.appliquerEffet(this, this, new BuffAttaque(0.20, 2), log);
    }
    @Override public void descriptionAttaqueBase()    { System.out.println("Frappe de Precision : attaque de base."); }
    @Override public void descriptionAttaqueSpeciale(){ System.out.println("Android Barrier Blast : attaque speciale."); }
    @Override public void descriptionAttaqueUltime()  { System.out.println("Hell Flash : attaque ultime."); }
}
