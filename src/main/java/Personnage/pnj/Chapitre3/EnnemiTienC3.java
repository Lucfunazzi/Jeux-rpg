package Personnage.pnj.Chapitre3;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

public class EnnemiTienC3 extends PersonnageBase {

    public EnnemiTienC3() { this(30); }

    public EnnemiTienC3(int niveau) {
        this.nom    = "Tien";
        this.niveau = niveau;
        this.type   = "Guerrier";
        this.role   = "Tank";
        this.rarete = "C";

        double mult = 1.0;
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
        this.taux_blocage      = 0.2;
        this.reduction_blocage = 0.25;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Poing de diamant", "Dodon Pa", "Canon Triplase"};
    }

    @Override public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Tien utilise Poing de diamant sur " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
    }

    @Override public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Tien utilise Dodon Pa !");
        double degats = this.getAttaque() * 0.80;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, new BuffDefense(0.10, 2), log);
        Combat.appliquerEffet(this, new BuffBlocage(0.10, 2), log);
    }

    @Override public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Tien utilise Canon Triplase !");
        PersonnageBase cible = null;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                if (cible == null || ennemi.getVie() < cible.getVie())
                    cible = ennemi;
            }
        }
        if (cible != null) {
            double degats = this.getAttaque() * 1.00;
            Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        }
        Combat.appliquerEffet(this, new Invincibilite(1), log);
        Combat.appliquerEffet(this, new BuffDefense(0.10, 2), log);
    }

    @Override public void descriptionAttaqueBase()     { System.out.println("Poing de diamant : inflige 100% ATK a une cible."); }
    @Override public void descriptionAttaqueSpeciale() { System.out.println("Dodon Pa : inflige 80% ATK a une cible, renforce sa defense de 10% et son blocage de 10% pendant 2 tours."); }
    @Override public void descriptionAttaqueUltime()   { System.out.println("Canon Triplase : inflige 100% ATK a l'ennemi avec le moins de PV, devient invincible 1 tour et gagne +10% DEF pendant 2 tours."); }
}
