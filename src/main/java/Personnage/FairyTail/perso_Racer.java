package Personnage.FairyTail;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Racer (Sawyer) — Elementaliste, rang A.
 * Ancien membre d'Oración Seis, Magie du Ralentissement.
 */
public class perso_Racer extends PersonnageBase {

    public perso_Racer() {
        this.nom    = "Racer";
        this.type   = "Elementaliste";
        this.role   = "DPS";
        this.rarete = "A";
        this.niveau = 1;
        double mult = 1.44;
        this.vie     = 400 * mult;
        this.attaque = 170 * mult;
        this.defense =  90 * mult;
        this.vitesse = 175 * mult;
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
        System.out.println("Magie du Ralentissement — Inflige 125% ATK et réduit la vitesse de la cible de 25% pendant 2 tours.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Vitesse Absolue — Augmente sa vitesse de 30% pendant 2 tours et inflige 90% ATK à tous les ennemis.");
    }
}
