package Personnage.FairyTail;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Rustyrose — Elementaliste, rang S.
 * Ancien des Sept Enfants de la Purgation de Grimoire Heart, Magie de Création Vivante.
 */
public class perso_Rustyrose extends PersonnageBase {

    public perso_Rustyrose() {
        this.nom    = "Rustyrose";
        this.type   = "Elementaliste";
        this.role   = "Tank";
        this.rarete = "S";
        this.niveau = 1;
        double mult = 1.66;
        this.vie     = 720 * mult;
        this.attaque = 180 * mult;
        this.defense = 165 * mult;
        this.vitesse = 108 * mult;
        this.taux_critiques    = 0.08;
        this.degat_critiques   = 1.20;
        this.taux_precisions   = 105.00;
        this.taux_esquives     = 0.06;
        this.taux_blocage      = 0.14;
        this.reduction_blocage = 0.18;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Coup de Carte", "Création Vivante : Golem", "Ceci Met Fin à Tout"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Rustyrose frappe " + cible.getNom() + " d'un Coup de Carte !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Rustyrose matérialise un Golem de Création Vivante !");
        double ratioBouclier = 0.10;
        for (PersonnageBase allie : equipeAlliee) {
            if (allie.getNom().equals("Azuma") && allie.estVivant()) {
                ratioBouclier = 0.15;
            }
        }
        Combat.appliquerEffet(this, new Bouclier(this.getVieMax() * ratioBouclier), log);
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = this.getAttaque() * 1.20;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            }
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Rustyrose proclame : « Ceci Met Fin à Tout ! »");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) multiplicateurRage += (this.getRage() - 100) / 100.0;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = (this.getAttaque() * 1.15) * multiplicateurRage;
                boolean touche = Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                if (touche) {
                    Combat.appliquerEffet(this, ennemi, new Brulure(2, 0.08), log);
                }
            }
        }
        Combat.appliquerEffet(this, new Regeneration(0.05, 2), log);
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Coup de Carte — Inflige 100% ATK.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Création Vivante : Golem — Se protège d'un bouclier (10% PV max) et inflige 120% ATK. a tout les ennemis");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Ceci Met Fin à Tout — Inflige 115% ATK (bonus selon la Rage) à tous les ennemis. inflige brulure pendants 2 tours aux adversaires et s'applique regeneration de 5% pendants 2 tours");
    }
}
