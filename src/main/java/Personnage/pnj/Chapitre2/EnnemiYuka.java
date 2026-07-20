package Personnage.pnj.Chapitre2;

import Combat.Combat;
import Effets.Purification;
import Effets.ReductionAttaque;
import Effets.ReductionDefense;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Yuka — Mage de l'île Galuna, magie de l'annulation, rang C.
 * Purge les buffs ennemis et affaiblit l'attaque adverse.
 */
public class EnnemiYuka extends PersonnageBase {

    public EnnemiYuka() { this(12); }

    public EnnemiYuka(int niveau) {
        this.nom    = "Yuka";
        this.niveau = niveau;
        this.type="Elementaliste";
        this.role   = "Support";
        this.rarete = "C";

        double niv = Math.pow(1.05, niveau - 1);
        double vit = Math.pow(1.03, niveau - 1);
        this.vie     = 225.0 * niv;
        this.attaque =  75.0 * niv;
        this.defense =  60.0 * niv;
        this.vitesse =  70.0 * vit;

        this.taux_critiques    = 0.08;
        this.degat_critiques   = 1.15;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.06;
        this.taux_blocage      = 0.08;
        this.reduction_blocage = 0.10;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Onde d'Annulation", "Frappe Inhibitrice", "Annulation Totale"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Yuka projette une onde d'annulation sur " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
        Combat.appliquerEffet(this, cible, new ReductionAttaque(0.08, 1), log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Yuka frappe " + cible.getNom() + " et inhibe ses capacités !");
        double degats = this.getAttaque() * 1.10;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new ReductionAttaque(0.15, 2), log);
        Combat.appliquerEffet(this, cible, new ReductionDefense(0.10, 2), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Yuka annule tous les effets positifs des ennemis !");
        for (PersonnageBase cible : equipeEnnemie) {
            if (cible.estVivant()) {
                Purification.purifier(cible, 3, log);
                double degats = this.getAttaque() * 0.55;
                Combat.appliquerDegatsAvecLog(this, cible, degats, log);
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Onde d'Annulation — Inflige 100% ATK et réduit l'Attaque de 8% pendant 1 tour.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Frappe Inhibitrice — Inflige 110% ATK, réduit ATK de 15% et DEF de 10% pendant 2 tours.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Annulation Totale — Purge les buffs de tous les ennemis et inflige 55% ATK à chacun.");
    }
}
