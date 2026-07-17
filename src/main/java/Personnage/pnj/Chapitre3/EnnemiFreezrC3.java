package Personnage.pnj.Chapitre3;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

public class EnnemiFreezrC3 extends PersonnageBase {

    public EnnemiFreezrC3() { this(32); }

    public EnnemiFreezrC3(int niveau) {
        this.nom    = "Freezer";
        this.niveau = niveau;
        this.type   = "Guerrier";
        this.role   = "DPS";
        this.rarete = "S";

        double mult = 1.5;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 250.0 * mult * niv;
        this.attaque = 180.0 * mult * niv;
        this.defense = 110.0 * mult * niv;
        this.vitesse = 130.0 * mult * vit;

        this.taux_critiques    = 0.18;
        this.degat_critiques   = 1.5;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.12;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.1;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Vague de ki", "Death Ball", "Death Beam en rafale"};
    }
    @Override public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Freezer tire une Vague de ki sur " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
        Combat.appliquerEffet(this, cible, new ReductionDefense(0.15, 2), log);
    }
    @Override public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Freezer charge sa Death Ball devastatrice !");
        Combat.appliquerDegatsAvecLog(this, cible, this.getAttaque() * 2.20, log);
        Combat.appliquerEffet(this, cible, new Etourdissement(1), log);
    }
    @Override public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Freezer libere un Death Beam en rafale sur toute l'equipe !");
        for (PersonnageBase c : equipeEnnemie)
            if (c.estVivant()) {
                Combat.appliquerDegatsAvecLog(this, c, this.getAttaque() * 1.60, log);
                Combat.appliquerEffet(this, c, new Poison(3, 0.06), log);
            }
    }
    @Override public void descriptionAttaqueBase()    { System.out.println("Vague de ki : attaque de base."); }
    @Override public void descriptionAttaqueSpeciale(){ System.out.println("Death Ball : attaque speciale."); }
    @Override public void descriptionAttaqueUltime()  { System.out.println("Death Beam en rafale : attaque ultime."); }
}
