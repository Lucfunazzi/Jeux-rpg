package Personnage.pnj.Chapitre2;

import Combat.Combat;
import Effets.BuffDefense;
import Effets.Regeneration;
import Effets.Saignement;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Chery — Mage de l'île Galuna, rang C.
 * Rôle support/tank : se régénère et protège ses alliés.
 */
public class EnnemiChery extends PersonnageBase {

    public EnnemiChery() { this(12); }

    public EnnemiChery(int niveau) {
        this.nom    = "Chery";
        this.niveau = niveau;
        this.type   = "Mage";
        this.role   = "Tank";
        this.rarete = "C";

        double niv = Math.pow(1.05, niveau - 1);
        double vit = Math.pow(1.03, niveau - 1);
        this.vie     = 290.0 * niv;
        this.attaque =  65.0 * niv;
        this.defense =  75.0 * niv;
        this.vitesse =  60.0 * vit;

        this.taux_critiques    = 0.05;
        this.degat_critiques   = 1.10;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.04;
        this.taux_blocage      = 0.16;
        this.reduction_blocage = 0.18;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Coup de vague", "Écailles Lunaires", "Bénédiction de la Lune"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Chery frappe " + cible.getNom() + " d'un coup de vague !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Chery projette des écailles lunaires tranchantes sur " + cible.getNom() + " !");
        double degats = this.getAttaque() * 1.10;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new Saignement(2, 0.05), log);
        Combat.appliquerEffet(this, new BuffDefense(0.12, 2), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Chery invoque la bénédiction de la lune sur ses alliés !");
        for (PersonnageBase allie : equipeAlliee) {
            if (allie.estVivant()) {
                Combat.appliquerEffet(this, allie, new Regeneration(0.08,2), log);
                Combat.appliquerEffet(this, allie, new BuffDefense(0.10, 2), log);
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Coup de vague — Inflige 100% ATK à une cible.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Écailles Lunaires — Inflige 110% ATK, fait saigner la cible 2 tours (5% PV max/tour), gagne 12% DEF.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Bénédiction de la Lune — Régénère tous les alliés (8% PV max/tour) et augmente leur DEF de 10% pendant 2 tours.");
    }
}
