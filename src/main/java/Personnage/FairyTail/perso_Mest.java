package Personnage.FairyTail;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Mest (Doranbolt) — Elementaliste, rang B.
 * Agent secret du Conseil de la Magie infiltré à Fairy Tail durant l'examen de
 * rang S ; se révèle finalement loyal à la guilde. Télépathie et Magie de la Mémoire.
 */
public class perso_Mest extends PersonnageBase {

    public perso_Mest() {
        this.nom    = "Mest";
        this.type   = "Elementaliste";
        this.role   = "Support";
        this.rarete = "A";
        this.niveau = 1;
        double mult = 1.40;
        this.vie     = 460 * mult;
        this.attaque = 161 * mult;
        this.defense = 102 * mult;
        this.vitesse = 130 * mult;
        this.taux_critiques    = 0.10;
        this.degat_critiques   = 1.20;
        this.taux_precisions   = 108.00;
        this.taux_esquives     = 0.12;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.08;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Frappe Téléportée", "Manipulation de Mémoire", "Vision du Conseil"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Mest apparaît derrière " + cible.getNom() + " et frappe !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Mest brouille l'esprit de " + cible.getNom() + " — Manipulation de Mémoire !");
        double degats = this.getAttaque() * 1.25;
        boolean touche = Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        if (touche) {
            Combat.appliquerEffet(this, cible, new Confusion(2), log);
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Mest ouvre une Vision du Conseil sur toute l'équipe ennemie !");
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = this.getAttaque() * 0.85;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Frappe Téléportée — Inflige 100% ATK.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Manipulation de Mémoire — Inflige 125% ATK et confond la cible pendant 2 tours.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Vision du Conseil — Inflige 85% ATK à tous les ennemis.");
    }
}
