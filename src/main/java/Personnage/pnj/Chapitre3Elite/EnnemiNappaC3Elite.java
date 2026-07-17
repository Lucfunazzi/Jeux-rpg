package Personnage.pnj.Chapitre3Elite;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

public class EnnemiNappaC3Elite extends PersonnageBase {

    public EnnemiNappaC3Elite() { this(44); }

    public EnnemiNappaC3Elite(int niveau) {
        this.nom    = "Nappa";
        this.niveau = niveau;
        this.type   = "Guerrier";
        this.role   = "Tank";
        this.rarete = "B";

        double mult = 1.6;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 620.0 * mult * niv;
        this.attaque = 140.0 * mult * niv;
        this.defense = 130.0 * mult * niv;
        this.vitesse = 80.0 * mult * vit;

        this.taux_critiques    = 0.08;
        this.degat_critiques   = 1.25;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.03;
        this.taux_blocage      = 0.18;
        this.reduction_blocage = 0.3;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Ecrasement", "Giant Storm", "Explosion de Puissance"};
    }
    @Override public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Nappa ecrase " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
        Combat.appliquerEffet(this, cible, new ReductionDefense(0.15, 2), log);
    }
    @Override public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Nappa libere sa Giant Storm !");
        Combat.appliquerDegatsAvecLog(this, cible, this.getAttaque() * 2.00, log);
        Combat.appliquerEffet(this, cible, new Etourdissement(2), log);
    }
    @Override public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Nappa — Explosion de Puissance !");
        for (PersonnageBase c : equipeEnnemie)
            if (c.estVivant()) {
                Combat.appliquerDegatsAvecLog(this, c, this.getAttaque() * 1.40, log);
                Combat.appliquerEffet(this, c, new Fragilite(2, 0.20), log);
            }
        Combat.appliquerEffet(this, this, new BuffDefense(0.20, 2), log);
    }
    @Override public void descriptionAttaqueBase()    { System.out.println("Ecrasement : attaque de base."); }
    @Override public void descriptionAttaqueSpeciale(){ System.out.println("Giant Storm : attaque speciale."); }
    @Override public void descriptionAttaqueUltime()  { System.out.println("Explosion de Puissance : attaque ultime."); }
}
