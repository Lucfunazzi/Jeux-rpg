package Personnage.pnj.Chapitre7;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Panther Lily — Capitaine des Chevaliers Exceed de l'Armée Royale d'Edolas, rang A.
 * Version antagoniste (avant qu'il ne rallie Fairy Tail) : manie une immense épée
 * qu'il peut faire grandir à volonté grâce à sa propre magie.
 */
public class EnnemiPantherLily extends PersonnageBase {

    public EnnemiPantherLily() { this(79); }

    public EnnemiPantherLily(int niveau) {
        this.nom    = "Panther Lily";
        this.niveau = niveau;
        this.type   = "Chevalier";
        this.role   = "DPS";
        this.rarete = "A";

        double mult = 1.45;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 520.0 * mult * niv;
        this.attaque = 205.0 * mult * niv;
        this.defense = 120.0 * mult * niv;
        this.vitesse = 115.0 * mult * vit;

        this.taux_critiques    = 0.15;
        this.degat_critiques   = 1.30;
        this.taux_precisions   = 105.00;
        this.taux_esquives     = 0.08;
        this.taux_blocage      = 0.10;
        this.reduction_blocage = 0.14;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Frappe de la Grande Épée", "Battle Mode", "Trancheur Titanesque"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Panther Lily frappe " + cible.getNom() + " de sa Grande Épée !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Panther Lily active son Battle Mode et grandit !");
        Combat.appliquerEffet(this, new BuffAttaque(0.20, 2), log);
        double degats = this.getAttaque() * 1.10;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Panther Lily assène un Trancheur Titanesque !");
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
        System.out.println("Frappe de la Grande Épée — Inflige 100% ATK.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Battle Mode — Augmente son ATK de 20% pendant 2 tours et inflige 110% ATK.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Trancheur Titanesque — Inflige 115% ATK (bonus selon la Rage) à tous les ennemis.");
    }
}
