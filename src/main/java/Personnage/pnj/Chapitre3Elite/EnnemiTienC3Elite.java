package Personnage.pnj.Chapitre3Elite;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

public class EnnemiTienC3Elite extends PersonnageBase {

    public EnnemiTienC3Elite() { this(42); }

    public EnnemiTienC3Elite(int niveau) {
        this.nom    = "Tien";
        this.niveau = niveau;
        this.type   = "Guerrier";
        this.role   = "Tank";
        this.rarete = "C";

        double mult = 1.3;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 520.0 * mult * niv;
        this.attaque = 85.0 * mult * niv;
        this.defense = 120.0 * mult * niv;
        this.vitesse = 75.0 * mult * vit;

        this.taux_critiques    = 0.08;
        this.degat_critiques   = 1.2;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.05;
        this.taux_blocage      = 0.18;
        this.reduction_blocage = 0.3;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Triple Combo", "Canon Solaire", "Kikoho"};
    }
    @Override public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Tien frappe avec son Triple Combo !");
        Combat.attaquer(this, cible, log);
        Combat.appliquerEffet(this, cible, new ReductionAttaque(0.10, 2), log);
    }
    @Override public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Tien declenche son Canon Solaire !");
        Combat.appliquerDegatsAvecLog(this, cible, this.getAttaque() * 1.80, log);
        Combat.appliquerEffet(this, cible, new Aveuglement(0.30, 2), log);
    }
    @Override public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Tien — Kikoho devastateur !");
        for (PersonnageBase c : equipeEnnemie)
            if (c.estVivant()) {
                Combat.appliquerDegatsAvecLog(this, c, this.getAttaque() * 1.30, log);
                Combat.appliquerEffet(this, c, new ReductionDefense(0.15, 2), log);
            }
    }
    @Override public void descriptionAttaqueBase()    { System.out.println("Triple Combo : attaque de base."); }
    @Override public void descriptionAttaqueSpeciale(){ System.out.println("Canon Solaire : attaque speciale."); }
    @Override public void descriptionAttaqueUltime()  { System.out.println("Kikoho : attaque ultime."); }
}
