package Personnage.FairyTail;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Panther Lily — Chevalier, rang A.
 * Ancien Capitaine des Chevaliers Exceed de l'Armée Royale d'Edolas, rallie Fairy Tail
 * après les événements d'Edolas et devient un pilier fidèle de la guilde.
 */
public class perso_PantherLily extends PersonnageBase {

    public perso_PantherLily() {
        this.nom    = "Panther Lily";
        this.type   = "Chevalier";
        this.role   = "DPS";
        this.rarete = "A";
        this.niveau = 1;
        double mult = 1.48;
        this.vie     = 450 * mult;
        this.attaque = 175 * mult;
        this.defense = 110 * mult;
        this.vitesse = 132 * mult;
        this.taux_critiques    = 0.14;
        this.degat_critiques   = 1.28;
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
        double degats = this.getAttaque() * 1.20;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Panther Lily assène un Trancheur Titanesque à toute l'équipe ennemie !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) multiplicateurRage += (this.getRage() - 100) / 100.0;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = (this.getAttaque() * 1.00) * multiplicateurRage;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            }
        }
        Combat.appliquerEffet(this, new BuffVitesse(0.10, 2), log);
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Frappe de la Grande Épée — Inflige 100% ATK.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Battle Mode — Augmente son ATK de 20% pendant 2 tours et inflige 120% ATK.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Trancheur Titanesque — Inflige 100% ATK (bonus selon la Rage) à tous les ennemis. et augmente sa vitesse de 10% pendants 2 tours");
    }
}
