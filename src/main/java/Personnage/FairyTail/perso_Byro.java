package Personnage.FairyTail;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Byro Cracy — Elementaliste, rang A.
 * Ancien soldat de l'Armée Royale d'Edolas, Magie du Temps.
 */
public class perso_Byro extends PersonnageBase {

    public perso_Byro() {
        this.nom    = "Byro";
        this.type   = "Elementaliste";
        this.role   = "Support";
        this.rarete = "A";
        this.niveau = 1;
        double mult = 1.40;
        this.vie     = 480 * mult;
        this.attaque = 150 * mult;
        this.defense = 110 * mult;
        this.vitesse = 105 * mult;
        this.taux_critiques    = 0.08;
        this.degat_critiques   = 1.15;
        this.taux_precisions   = 105.00;
        this.taux_esquives     = 0.06;
        this.taux_blocage      = 0.08;
        this.reduction_blocage = 0.12;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Poing du Temps", "Vieillissement Forcé", "Chronologie Brisée"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Byro frappe " + cible.getNom() + " d'un Poing du Temps !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Byro inflige un Vieillissement Forcé à " + cible.getNom() + " !");
        double degats = this.getAttaque() * 1.10;
        boolean touche = Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        if (touche) {
            Combat.appliquerEffet(this, cible, new ReductionAttaque(0.18, 2), log);
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Byro brise la Chronologie de toute l'équipe ennemie !");
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = this.getAttaque() * 0.85;
                boolean touche = Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                if (touche) {
                    Combat.appliquerEffet(this, ennemi, new ReductionVitesse(0.20, 2), log);
                }
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Poing du Temps — Inflige 100% ATK.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Vieillissement Forcé — Inflige 110% ATK et réduit l'ATK de la cible de 18% pendant 2 tours.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Chronologie Brisée — Inflige 85% ATK à tous les ennemis et réduit leur vitesse de 20% pendant 2 tours.");
    }
}
