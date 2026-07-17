package Personnage.pnj.Chapitre3Elite;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

public class EnnemiProtecteurDBZElite extends PersonnageBase {

    public EnnemiProtecteurDBZElite() { this(42); }

    public EnnemiProtecteurDBZElite(int niveau) {
        this.nom    = "Protecteur d'Elite";
        this.niveau = niveau;
        this.type   = "Guerrier";
        this.role   = "Tank";
        this.rarete = "B";

        double mult = 1.3;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 520.0 * mult * niv;
        this.attaque = 65.0 * mult * niv;
        this.defense = 120.0 * mult * niv;
        this.vitesse = 55.0 * mult * vit;

        this.taux_critiques    = 0.05;
        this.degat_critiques   = 1.1;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.03;
        this.taux_blocage      = 0.18;
        this.reduction_blocage = 0.3;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Frappe Defensive", "Renforcement", "Muraille Indestructible"};
    }
    @Override public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.getNom() + " frappe " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
        Combat.appliquerEffet(this, this, new BuffDefense(0.10, 1), log);
    }
    @Override public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.getNom() + " se renforce !");
        Combat.appliquerEffet(this, this, new Bouclier(this.getVieMax() * 0.25), log);
        Combat.appliquerEffet(this, this, new BuffDefense(0.20, 2), log);
    }
    @Override public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.getNom() + " — Muraille Indestructible !");
        for (PersonnageBase a : equipeAlliee)
            if (a.estVivant())
                Combat.appliquerEffet(this, a, new BuffDefense(0.15, 2), log);
        for (PersonnageBase c : equipeEnnemie)
            if (c.estVivant()) {
                Combat.appliquerDegatsAvecLog(this, c, this.getAttaque() * 1.10, log);
                Combat.appliquerEffet(this, c, new ReductionAttaque(0.15, 2), log);
            }
    }
    @Override public void descriptionAttaqueBase()    { System.out.println("Frappe Defensive : attaque de base."); }
    @Override public void descriptionAttaqueSpeciale(){ System.out.println("Renforcement : attaque speciale."); }
    @Override public void descriptionAttaqueUltime()  { System.out.println("Muraille Indestructible : attaque ultime."); }
}
