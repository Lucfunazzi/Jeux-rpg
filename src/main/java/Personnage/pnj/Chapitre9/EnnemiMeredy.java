package Personnage.pnj.Chapitre9;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Meredy — mage de Grimoire Heart, rang A.
 * Magie Sensorielle (Maguilty Sense) : relie la douleur de sa cible à la sienne.
 * Affronte Erza et Jubia puis Jubia seule au Chapitre 9 (stages 5 et 6).
 */
public class EnnemiMeredy extends PersonnageBase {

    public EnnemiMeredy() { this(107); }

    public EnnemiMeredy(int niveau) {
        this.nom    = "Meredy";
        this.niveau = niveau;
        this.type   = "Elementaliste";
        this.role   = "Support";
        this.rarete = "S";

        double mult = 1.48;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 500.0 * mult * niv;
        this.attaque = 175.0 * mult * niv;
        this.defense = 115.0 * mult * niv;
        this.vitesse = 120.0 * mult * vit;

        this.taux_critiques    = 0.10;
        this.degat_critiques   = 1.20;
        this.taux_precisions   = 105.00;
        this.taux_esquives     = 0.09;
        this.taux_blocage      = 0.06;
        this.reduction_blocage = 0.10;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Rayon Maguilty", "Lien Sensoriel", "Danse Écarlate"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Meredy tire un Rayon Maguilty sur " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Meredy tisse un Lien Sensoriel avec " + cible.getNom() + " !");
        double degats = this.getAttaque() * 1.15;
        boolean touche = Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        if (touche) {
            Combat.appliquerEffet(this, cible, new Fragilite(2, 0.20), log);
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Meredy déchaîne une Danse Écarlate sur toute l'équipe ennemie !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) multiplicateurRage += (this.getRage() - 100) / 100.0;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = (this.getAttaque() * 1.05) * multiplicateurRage;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Rayon Maguilty — Inflige 100% ATK.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Lien Sensoriel — Inflige 115% ATK et augmente les dégâts subis par la cible de 20% pendant 2 tours.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Danse Écarlate — Inflige 105% ATK (bonus selon la Rage) à tous les ennemis.");
    }
}
