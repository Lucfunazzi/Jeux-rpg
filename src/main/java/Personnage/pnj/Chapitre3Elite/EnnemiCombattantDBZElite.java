package Personnage.pnj.Chapitre3Elite;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

public class EnnemiCombattantDBZElite extends PersonnageBase {

    public EnnemiCombattantDBZElite() { this(42); }

    public EnnemiCombattantDBZElite(int niveau) {
        this.nom    = "Combattant d'Elite";
        this.niveau = niveau;
        this.type   = "Guerrier";
        this.role   = "DPS";
        this.rarete = "B";

        double mult = 1.3;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 280.0 * mult * niv;
        this.attaque = 95.0 * mult * niv;
        this.defense = 60.0 * mult * niv;
        this.vitesse = 80.0 * mult * vit;

        this.taux_critiques    = 0.12;
        this.degat_critiques   = 1.25;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.07;
        this.taux_blocage      = 0.07;
        this.reduction_blocage = 0.15;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Frappe Elite", "Attaque Puissante", "Assaut Elite"};
    }
    @Override public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.getNom() + " attaque " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
        Combat.appliquerEffet(this, cible, new Marquage(1, 0.10), log);
    }
    @Override public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.getNom() + " declenche une attaque puissante !");
        Combat.appliquerDegatsAvecLog(this, cible, this.getAttaque() * 1.80, log);
        Combat.appliquerEffet(this, cible, new Etourdissement(1), log);
    }
    @Override public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.getNom() + " — Assaut Elite !");
        for (PersonnageBase c : equipeEnnemie)
            if (c.estVivant()) {
                Combat.appliquerDegatsAvecLog(this, c, this.getAttaque() * 1.20, log);
                Combat.appliquerEffet(this, c, new ReductionDefense(0.10, 2), log);
            }
    }
    @Override public void descriptionAttaqueBase()    { System.out.println("Frappe Elite : attaque de base."); }
    @Override public void descriptionAttaqueSpeciale(){ System.out.println("Attaque Puissante : attaque speciale."); }
    @Override public void descriptionAttaqueUltime()  { System.out.println("Assaut Elite : attaque ultime."); }
}
