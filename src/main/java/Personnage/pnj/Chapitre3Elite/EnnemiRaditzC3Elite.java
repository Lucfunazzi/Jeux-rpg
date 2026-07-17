package Personnage.pnj.Chapitre3Elite;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

public class EnnemiRaditzC3Elite extends PersonnageBase {

    public EnnemiRaditzC3Elite() { this(42); }

    public EnnemiRaditzC3Elite(int niveau) {
        this.nom    = "Raditz";
        this.niveau = niveau;
        this.type   = "Guerrier";
        this.role   = "DPS";
        this.rarete = "B";

        double mult = 1.6;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 400.0 * mult * niv;
        this.attaque = 150.0 * mult * niv;
        this.defense = 100.0 * mult * niv;
        this.vitesse = 110.0 * mult * vit;

        this.taux_critiques    = 0.15;
        this.degat_critiques   = 1.25;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.08;
        this.taux_blocage      = 0.07;
        this.reduction_blocage = 0.15;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Double Sunday", "Saturday Crush", "Attaque du Guerrier Elite"};
    }
    @Override public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Raditz frappe avec Double Sunday sur " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
        Combat.appliquerEffet(this, cible, new Saignement(2, 0.06), log);
    }
    @Override public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Raditz declenche son Saturday Crush !");
        Combat.appliquerDegatsAvecLog(this, cible, this.getAttaque() * 1.90, log);
        Combat.appliquerEffet(this, cible, new Malediction(2, 0.15), log);
    }
    @Override public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Raditz — Attaque du Guerrier Elite !");
        for (PersonnageBase c : equipeEnnemie)
            if (c.estVivant())
                Combat.appliquerDegatsAvecLog(this, c, this.getAttaque() * 1.20, log);
        Combat.appliquerEffet(this, this, new BuffAttaque(0.25, 2), log);
    }
    @Override public void descriptionAttaqueBase()    { System.out.println("Double Sunday : attaque de base."); }
    @Override public void descriptionAttaqueSpeciale(){ System.out.println("Saturday Crush : attaque speciale."); }
    @Override public void descriptionAttaqueUltime()  { System.out.println("Attaque du Guerrier Elite : attaque ultime."); }
}
