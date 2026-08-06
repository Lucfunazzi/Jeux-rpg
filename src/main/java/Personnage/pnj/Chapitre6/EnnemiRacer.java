package Personnage.pnj.Chapitre6;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Racer (Sawyer) — Mage de la Vitesse d'Oración Seis, rang A.
 * Sa Magie du Ralentissement fait s'écouler le temps plus lentement autour de lui :
 * il se déplace à vitesse normale pendant que tout le reste semble ralenti.
 */
public class EnnemiRacer extends PersonnageBase {

    public EnnemiRacer() { this(61); }

    public EnnemiRacer(int niveau) {
        this.nom    = "Racer";
        this.niveau = niveau;
        this.type   = "Elementaliste";
        this.role   = "DPS";
        this.rarete = "A";

        double mult = 1.40;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 380.0 * mult * niv;
        this.attaque = 180.0 * mult * niv;
        this.defense =  90.0 * mult * niv;
        this.vitesse = 175.0 * mult * vit;

        this.taux_critiques    = 0.16;
        this.degat_critiques   = 1.35;
        this.taux_precisions   = 105.00;
        this.taux_esquives     = 0.16;
        this.taux_blocage      = 0.04;
        this.reduction_blocage = 0.08;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Frappe Éclair", "Magie du Ralentissement", "Vitesse Absolue"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Racer frappe " + cible.getNom() + " à une vitesse folle !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Racer ralentit le temps autour de " + cible.getNom() + " !");
        double degats = this.getAttaque() * 1.25;
        boolean touche = Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        if (touche) {
            Combat.appliquerEffet(this, cible, new ReductionVitesse(0.25, 2), log);
        }
        Combat.appliquerEffet(this, new BuffTauxCritique(0.10,2), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Racer atteint sa Vitesse Absolue et déchaîne une rafale de coups !");
        Combat.appliquerEffet(this, new BuffVitesse(0.30, 2), log);
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = this.getAttaque() * 0.90;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Frappe Éclair — Inflige 100% ATK.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Magie du Ralentissement — Inflige 125% ATK et réduit la vitesse de la cible de 25% et "
                + "augmente son taux critique de 10% pendant 2 tours.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Vitesse Absolue — Augmente sa vitesse de 30% pendant 2 tours et inflige 90% ATK à tous les ennemis.");
    }
}
