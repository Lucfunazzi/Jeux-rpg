package Personnage.pnj.Chapitre3;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

public class EnnemiYamchaC3 extends PersonnageBase {

    public EnnemiYamchaC3() { this(28); }

    public EnnemiYamchaC3(int niveau) {
        this.nom    = "Yamcha";
        this.niveau = niveau;
        this.type   = "Guerrier";
        this.role   = "DPS";
        this.rarete = "C";

        double mult = 1.0;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 350.0 * mult * niv;
        this.attaque = 130.0 * mult * niv;
        this.defense = 80.0 * mult * niv;
        this.vitesse = 100.0 * mult * vit;

        this.taux_critiques    = 0.12;
        this.degat_critiques   = 1.2;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.08;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.1;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Boule de feu du loup", "Fist du loup", "Kamehameha du loup"};
    }
    @Override public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Yamcha projette une boule de feu sur " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
    }
    @Override public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Yamcha execute son Fist du loup !");
        Combat.appliquerDegatsAvecLog(this, cible, this.getAttaque() * 1.60, log);
        Combat.appliquerEffet(this, cible, new ReductionAttaque(0.15, 2), log);
    }
    @Override public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Yamcha libere son Kamehameha du loup !");
        for (PersonnageBase c : equipeEnnemie)
            if (c.estVivant()) Combat.appliquerDegatsAvecLog(this, c, this.getAttaque() * 1.10, log);
    }
    @Override public void descriptionAttaqueBase()    { System.out.println("Boule de feu du loup : attaque de base."); }
    @Override public void descriptionAttaqueSpeciale(){ System.out.println("Fist du loup : attaque speciale."); }
    @Override public void descriptionAttaqueUltime()  { System.out.println("Kamehameha du loup : attaque ultime."); }
}
