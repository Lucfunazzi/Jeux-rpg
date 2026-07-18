package Personnage.pnj.Chapitre1;

import Combat.Combat;
import Effets.BuffDefense;
import Effets.ReductionAttaque;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Evaro — Sbire de Bora, rang C.
 * Rôle Tank/Support : protège Bora et affaiblit les attaquants.
 */
public class EnnemiEvaro extends PersonnageBase {

    public EnnemiEvaro() { this(3); }

    public EnnemiEvaro(int niveau) {
        this.nom    = "Evaro";
        this.niveau = niveau;
        this.type   = "Guerrier";
        this.role   = "Tank";
        this.rarete = "C";

        double niv = Math.pow(1.05, niveau - 1);
        double vit = Math.pow(1.03, niveau - 1);
        this.vie     = 270.0 * niv;
        this.attaque =  60.0 * niv;
        this.defense =  70.0 * niv;
        this.vitesse =  55.0 * vit;

        this.taux_critiques    = 0.05;
        this.degat_critiques   = 1.10;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.04;
        this.taux_blocage      = 0.18;
        this.reduction_blocage = 0.20;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Coup de bouclier", "Provocation", "Rempart du sbire"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Evaro frappe " + cible.getNom() + " avec son bouclier !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Evaro provoque " + cible.getNom() + " et l'affaiblit !");
        double degats = this.getAttaque() * 0.60;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new ReductionAttaque(0.12, 2), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Evaro se fortifie et renforce ses alliés !");
        Combat.appliquerEffet(this, new BuffDefense(0.15, 2), log);
        for (PersonnageBase allie : equipeAlliee) {
            if (allie.estVivant() && allie != this) {
                Combat.appliquerEffet(this, allie, new BuffDefense(0.10, 2), log);
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Coup de bouclier — Inflige 100% ATK à une cible.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Provocation — Inflige 60% ATK et réduit l'Attaque de la cible de 12% pendant 2 tours.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Rempart du sbire — Augmente sa Défense de 15% et celle de ses alliés de 10% pendant 2 tours.");
    }
}
