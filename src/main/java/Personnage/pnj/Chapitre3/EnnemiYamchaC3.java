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
        return new String[]{"Coup de pied", "Kamehameha", "Sokidan"};
    }

    @Override public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Yamcha utilise Coup de pied sur " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
    }

    @Override public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Yamcha utilise Kamehameha !");
        double degats = this.getAttaque() * 1.10;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, new BuffTauxCritique(0.10, 2), log);
    }

    @Override public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Yamcha utilise Sokidan !");
        PersonnageBase cible = null;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                if (cible == null || ennemi.getVie() < cible.getVie())
                    cible = ennemi;
            }
        }
        if (cible == null) return;
        double degats = this.getAttaque() * 1.10;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new Brulure(2, 0.08), log);
    }

    @Override public void descriptionAttaqueBase()     { System.out.println("Coup de pied : inflige 100% ATK a une cible."); }
    @Override public void descriptionAttaqueSpeciale() { System.out.println("Kamehameha : inflige 110% ATK a une cible et gagne +10% taux critique pendant 2 tours."); }
    @Override public void descriptionAttaqueUltime()   { System.out.println("Sokidan : inflige 110% ATK a l'ennemi avec le moins de PV et applique Brulure (8% PV/tour) pendant 2 tours."); }
}
