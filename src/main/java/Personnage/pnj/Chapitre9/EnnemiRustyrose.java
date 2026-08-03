package Personnage.pnj.Chapitre9;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Rustyrose — l'un des Sept Enfants de la Purgation de Grimoire Heart, rang S.
 * Magie de Création Vivante : matérialise tout ce qu'il imagine, des golems
 * aux armées entières. Affronte Elfman et Evergreen au Chapitre 9 (stage 3).
 */
public class EnnemiRustyrose extends PersonnageBase {

    public EnnemiRustyrose() { this(105); }

    public EnnemiRustyrose(int niveau) {
        this.nom    = "Rustyrose";
        this.niveau = niveau;
        this.type   = "Elementaliste";
        this.role   = "Tank";
        this.rarete = "S";

        double mult = 1.68;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 740.0 * mult * niv;
        this.attaque = 195.0 * mult * niv;
        this.defense = 175.0 * mult * niv;
        this.vitesse = 100.0 * mult * vit;

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
        Combat.appliquerEffet(this, new Bouclier(this.getVieMax() * 0.10), log);
        double degats = this.getAttaque() * 1.20;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
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
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Coup de Carte — Inflige 100% ATK.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Création Vivante : Golem — Se protège d'un bouclier (10% PV max) et inflige 120% ATK.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Ceci Met Fin à Tout — Inflige 115% ATK (bonus selon la Rage) à tous les ennemis.");
    }
}
