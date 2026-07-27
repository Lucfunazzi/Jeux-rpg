package Personnage.pnj.Chapitre4;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Jellal — maitre de la Tour du Paradis, corrompu par la magie noire de Zeref, rang S.
 * Magie Celeste (Heavenly Body Magic) : Rayon Celeste, Grand Chariot, Altairis.
 */
public class EnnemiJellal extends PersonnageBase {

    public EnnemiJellal() { this(50); }

    public EnnemiJellal(int niveau) {
        this.nom    = "Jellal";
        this.niveau = niveau;
        this.type   = "Elementaliste";
        this.role   = "DPS";
        this.rarete = "S";

        double mult = 1.50;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 360.0 * mult * niv;
        this.attaque = 150.0 * mult * niv;
        this.defense = 100.0 * mult * niv;
        this.vitesse = 120.0 * mult * vit;

        this.taux_critiques    = 0.18;
        this.degat_critiques   = 1.55;
        this.taux_precisions   = 108.00;
        this.taux_esquives     = 0.12;
        this.taux_blocage      = 0.06;
        this.reduction_blocage = 0.12;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Rayon Celeste", "Grand Chariot", "Altairis"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Jellal utilise Rayon Celeste sur " + cible.getNom() + " !");
        boolean touche = Combat.attaquer(this, cible, log);
        if (!touche) {
            this.ajouterRage(50);
        }
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Jellal utilise Grand Chariot !");
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = this.getAttaque() * 1.50;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                Combat.appliquerEffet(this, ennemi, new Ralentissement(2, 0.15), log);
                Combat.appliquerEffet(ennemi, new Marquage(2, 0.25), log);
            }
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Jellal utilise Altairis !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) {
            multiplicateurRage += (this.getRage() - 100) / 100.0;
        }
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = (this.getAttaque() * 2.10) * multiplicateurRage;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            }
        }
        Combat.appliquerEffet(this, new BuffAttaque(0.15, 2), log);
        Combat.appliquerEffet(this, new BuffTauxCritique(0.15, 2), log);
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Rayon Celeste — inflige 100% ATK a une cible.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Grand Chariot — inflige 150% ATK a tous les ennemis, applique Ralentissement "
                + "(gain de rage reduit) pendant 2 tours et Marquage +25% degats recus pendant 2 tours.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Altairis — inflige 210% ATK (amplifie par la rage) a tous les ennemis, Jellal "
                + "gagne 15% d'attaque et 15% de taux critique pendant 2 tours.");
    }
}
