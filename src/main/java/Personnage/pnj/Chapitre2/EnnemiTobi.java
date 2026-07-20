package Personnage.pnj.Chapitre2;

import Combat.Combat;
import Effets.Gel;
import Effets.ReductionVitesse;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Tobi — Mage de glace du clan de l'île Galuna, rang C.
 * Utilise la magie de glace pour ralentir et geler les ennemis.
 */
public class EnnemiTobi extends PersonnageBase {

    public EnnemiTobi() { this(12); }

    public EnnemiTobi(int niveau) {
        this.nom    = "Tobi";
        this.niveau = niveau;
        this.type="Elementaliste";
        this.role   = "DPS";
        this.rarete = "C";

        double niv = Math.pow(1.05, niveau - 1);
        double vit = Math.pow(1.03, niveau - 1);
        this.vie     = 210.0 * niv;
        this.attaque =  90.0 * niv;
        this.defense =  50.0 * niv;
        this.vitesse =  80.0 * vit;

        this.taux_critiques    = 0.10;
        this.degat_critiques   = 1.20;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.08;
        this.taux_blocage      = 0.04;
        this.reduction_blocage = 0.05;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Lame de Glace", "Blizzard Cinglant", "Tempête Glaciale"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Tobi projette une lame de glace sur " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
        Combat.appliquerEffet(this, cible, new ReductionVitesse(0.10, 1), log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Tobi déchaîne un blizzard cinglant sur " + cible.getNom() + " !");
        double degats = this.getAttaque() * 1.25;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new ReductionVitesse(0.20, 2), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Tobi invoque une tempête glaciale sur toute l'équipe ennemie !");
        for (PersonnageBase cible : equipeEnnemie) {
            if (cible.estVivant()) {
                double degats = this.getAttaque() * 0.65;
                Combat.appliquerDegatsAvecLog(this, cible, degats, log);
                if (Math.random() < 0.20) {
                    Combat.appliquerEffet(this, cible, new Gel(1), log);
                }
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Lame de Glace — Inflige 100% ATK et réduit la Vitesse de 10% pendant 1 tour.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Blizzard Cinglant — Inflige 125% ATK et réduit la Vitesse de 20% pendant 2 tours.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Tempête Glaciale — Inflige 65% ATK à tous, 20% de chance de geler chaque cible 1 tour.");
    }
}
