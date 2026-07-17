package Personnage.pnj.Chapitre3Elite;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

public class EnnemiFreezrC3Elite extends PersonnageBase {

    public EnnemiFreezrC3Elite() { this(48); }

    public EnnemiFreezrC3Elite(int niveau) {
        this.nom    = "Freezer";
        this.niveau = niveau;
        this.type   = "Guerrier";
        this.role   = "DPS";
        this.rarete = "S";

        double mult = 1.75;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 570.0 * mult * niv;
        this.attaque = 220.0 * mult * niv;
        this.defense = 110.0 * mult * niv;
        this.vitesse = 130.0 * mult * vit;

        this.taux_critiques    = 0.18;
        this.degat_critiques   = 1.5;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.12;
        this.taux_blocage      = 0.07;
        this.reduction_blocage = 0.15;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Energie Sombre", "Death Beam", "Nova Death Cannon"};
    }
    @Override public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Freezer frappe " + cible.getNom() + " avec une energie sombre !");
        Combat.attaquer(this, cible, log);
        Combat.appliquerEffet(this, cible, new Malediction(2, 0.20), log);
    }
    @Override public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Freezer libere son Death Beam !");
        Combat.appliquerDegatsAvecLog(this, cible, this.getAttaque() * 2.10, log);
        Combat.appliquerEffet(this, cible, new Brulure(2, 0.10), log);
        Combat.appliquerEffet(this, cible, new ReductionAttaque(0.20, 2), log);
    }
    @Override public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Freezer — Nova Death Cannon !");
        for (PersonnageBase c : equipeEnnemie)
            if (c.estVivant()) {
                Combat.appliquerDegatsAvecLog(this, c, this.getAttaque() * 1.70, log);
                Combat.appliquerEffet(this, c, new Brulure(3, 0.12), log);
            }
        Combat.appliquerEffet(this, this, new BuffAttaque(0.25, 2), log);
    }
    @Override public void descriptionAttaqueBase()    { System.out.println("Energie Sombre : attaque de base."); }
    @Override public void descriptionAttaqueSpeciale(){ System.out.println("Death Beam : attaque speciale."); }
    @Override public void descriptionAttaqueUltime()  { System.out.println("Nova Death Cannon : attaque ultime."); }
}
