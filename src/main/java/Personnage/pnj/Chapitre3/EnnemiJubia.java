package Personnage.pnj.Chapitre3;

import Combat.Combat;
import Effets.ReductionVitesse;
import Effets.Gel;
import Effets.BuffDefense;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Jubia Lockser — Mage de l'eau, Element 4 de Phantom Lord, rang A (PNJ).
 * Version ennemie : contrôle de terrain avec l'eau et le gel.
 * Multiplicateur rang A : 1.40
 */
public class EnnemiJubia extends PersonnageBase {

    public EnnemiJubia() { this(28); }

    public EnnemiJubia(int niveau) {
        this.nom    = "Jubia";
        this.niveau = niveau;
        this.type   = "Mage";
        this.role   = "Support";
        this.rarete = "A";

        double mult = 1.40;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 280.0 * mult * niv;
        this.attaque = 100.0 * mult * niv;
        this.defense =  85.0 * mult * niv;
        this.vitesse =  95.0 * mult * vit;

        this.taux_critiques    = 0.12;
        this.degat_critiques   = 1.25;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.12;
        this.taux_blocage      = 0.08;
        this.reduction_blocage = 0.10;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Sphère d'Eau", "Vague de Prison", "Requiem de Pluie"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Jubia enveloppe " + cible.getNom() + " dans une sphère d'eau !");
        Combat.attaquer(this, cible, log);
        Combat.appliquerEffet(this, cible, new ReductionVitesse(0.15, 1), log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Jubia emprisonne " + cible.getNom() + " dans une vague d'eau glacée !");
        double degats = this.getAttaque() * 1.40;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new ReductionVitesse(0.25, 2), log);
        if (Math.random() < 0.35) {
            Combat.appliquerEffet(this, cible, new Gel(1), log);
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Jubia déclenche son Requiem de Pluie — une tempête d'eau s'abat sur l'équipe !");
        for (PersonnageBase cible : equipeEnnemie) {
            if (cible.estVivant()) {
                double degats = this.getAttaque() * 1.05;
                Combat.appliquerDegatsAvecLog(this, cible, degats, log);
                Combat.appliquerEffet(this, cible, new ReductionVitesse(0.20, 2), log);
            }
        }
        Combat.appliquerEffet(this, new BuffDefense(0.20, 2), log);
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Sphère d'Eau — Inflige 100% ATK et réduit la Vitesse de 15% pendant 1 tour.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Vague de Prison — Inflige 140% ATK, réduit VIT de 25% pendant 2 tours, 35% de gel 1 tour.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Requiem de Pluie — Inflige 105% ATK à tous et réduit leur VIT de 20% pendant 2 tours. Gagne 20% DEF.");
    }
}
