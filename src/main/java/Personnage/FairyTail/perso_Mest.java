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
        double mult = 1.52;
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
        log.add("Mest apparaît derrière les ennemis les plus offensifs et frappe !");
        List<PersonnageBase> cibles = equipeEnnemie.stream()
                .filter(PersonnageBase::estVivant)
                .sorted(java.util.Comparator.comparingDouble(PersonnageBase::getAttaque).reversed())
                .limit(2)
                .toList();
        for (PersonnageBase c : cibles) {
            double degats = this.getAttaque() * 1.00;
            Combat.appliquerDegatsAvecLog(this, c, degats, log);
        }
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        PersonnageBase cibleReelle = equipeEnnemie.stream()
                .filter(PersonnageBase::estVivant)
                .max(java.util.Comparator.comparingDouble(PersonnageBase::getAttaque))
                .orElse(null);
        if (cibleReelle == null) return;
        log.add("Mest brouille l'esprit de " + cibleReelle.getNom() + " — Manipulation de Mémoire !");
        double degats = this.getAttaque() * 1.60;
        boolean touche = Combat.appliquerDegatsAvecLog(this, cibleReelle, degats, log);
        if (touche) {
            Combat.appliquerEffet(this, cibleReelle, new Confusion(2), log);
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
        for (PersonnageBase allie : equipeAlliee) {
            if (allie.estVivant() && allie.getRole().equals("Support")) {
                Combat.appliquerEffet(this, allie, new BuffVitesse(1.00, 2), log);
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Frappe Téléportée — Inflige 100% ATK à deux ennemis avec le plus d'attaques.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Manipulation de Mémoire — Inflige 160% ATK et confond la cible avec le plus d'attaque pendant 2 tours.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Vision du Conseil — Inflige 85% ATK à tous les ennemis et augmente la vitesse des support de 100% pendants 2 tours.");
    }
}
