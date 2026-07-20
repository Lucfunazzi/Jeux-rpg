package Personnage.pnj.Chapitre3;

import Combat.Combat;
import Effets.Brulure;
import Effets.BuffAttaque;
import Effets.ReductionDefense;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Totomaru — Element 4 de Phantom Lord, magie des sept flammes, rang B.
 * Multiplicateur rang B : 1.30
 */
public class EnnemiTotomaru extends PersonnageBase {

    public EnnemiTotomaru() { this(26); }

    public EnnemiTotomaru(int niveau) {
        this.nom    = "Totomaru";
        this.niveau = niveau;
        this.type="Elementaliste";
        this.role   = "DPS";
        this.rarete = "B";

        double mult = 1.30;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 240.0 * mult * niv;
        this.attaque = 105.0 * mult * niv;
        this.defense =  65.0 * mult * niv;
        this.vitesse =  85.0 * mult * vit;

        this.taux_critiques    = 0.15;
        this.degat_critiques   = 1.30;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.08;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.08;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Flamme Blanche", "Sept Flammes — Écarlate", "Danse des Sept Flammes"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Totomaru projette une flamme blanche sur " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
        Combat.appliquerEffet(this, cible, new Brulure(1, 0.06), log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Totomaru lance la flamme écarlate des sept feux sur " + cible.getNom() + " !");
        double degats = this.getAttaque() * 1.50;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new Brulure(2, 0.08), log);
        Combat.appliquerEffet(this, cible, new ReductionDefense(0.15, 2), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Totomaru déclenche la danse des sept flammes — un ballet dévastateur !");
        Combat.appliquerEffet(this, new BuffAttaque(0.20, 2), log);
        for (PersonnageBase cible : equipeEnnemie) {
            if (cible.estVivant()) {
                double degats = this.getAttaque() * 0.90;
                Combat.appliquerDegatsAvecLog(this, cible, degats, log);
                Combat.appliquerEffet(this, cible, new Brulure(2, 0.07), log);
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Flamme Blanche — Inflige 100% ATK et brûle la cible 1 tour (6% PV/tour).");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Sept Flammes Écarlate — Inflige 150% ATK, brûle 2 tours (8% PV/tour), réduit DEF de 15%.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Danse des Sept Flammes — Gagne 20% ATK, inflige 90% ATK à tous et les brûle 2 tours (7% PV/tour).");
    }
}
