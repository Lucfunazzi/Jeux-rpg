package Personnage.pnj.Chapitre3;

import Combat.Combat;
import Effets.Brulure;
import Effets.BuffAttaque;
import Effets.Etourdissement;
import Effets.Poison;
import Effets.ReductionDefense;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Aria — Element 4 de Phantom Lord (référencée Alya), magie du Ciel Vide, rang A.
 * Le plus puissant des Element 4 : draine et détruit.
 * Multiplicateur rang A : 1.40
 */
public class EnnemiAlya extends PersonnageBase {

    public EnnemiAlya() { this(30); }

    public EnnemiAlya(int niveau) {
        this.nom    = "Aria";
        this.niveau = niveau;
        this.type   = "Mage";
        this.role   = "DPS";
        this.rarete = "A";

        double mult = 1.40;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 310.0 * mult * niv;
        this.attaque = 130.0 * mult * niv;
        this.defense =  75.0 * mult * niv;
        this.vitesse = 100.0 * mult * vit;

        this.taux_critiques    = 0.20;
        this.degat_critiques   = 1.40;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.12;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.08;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Lacunes du Ciel Vide", "Poches d'Air — Drainage", "Magie du Néant"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Aria ouvre des lacunes dans le ciel et aspire " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
        Combat.appliquerEffet(this, cible, new ReductionDefense(0.10, 2), log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Aria crée des poches d'air qui drainent la vitalité de " + cible.getNom() + " !");
        double degats = this.getAttaque() * 1.55;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        // Drain : se soigne de 30% des dégâts infligés
        double soin = degats * 0.30;
        this.recevoirSoin(soin, log);
        Combat.appliquerEffet(this, cible, new Poison(2, 0.07), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Aria libère la Magie du Néant — le ciel s'effondre sur les ennemis !");
        Combat.appliquerEffet(this, new BuffAttaque(0.25, 2), log);
        for (PersonnageBase cible : equipeEnnemie) {
            if (cible.estVivant()) {
                double degats = this.getAttaque() * 1.20;
                Combat.appliquerDegatsAvecLog(this, cible, degats, log);
                Combat.appliquerEffet(this, cible, new ReductionDefense(0.20, 2), log);
                if (Math.random() < 0.25) {
                    Combat.appliquerEffet(this, cible, new Etourdissement(1), log);
                }
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Lacunes du Ciel Vide — Inflige 100% ATK et réduit DEF de 10% pendant 2 tours.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Poches d'Air — Inflige 155% ATK, draine 30% en soins, empoisonne 2 tours (7% PV max/tour).");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Magie du Néant — Gagne 25% ATK, inflige 120% ATK à tous, réduit DEF de 20%, 25% d'étourdissement.");
    }
}
