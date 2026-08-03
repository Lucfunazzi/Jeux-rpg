package Personnage.pnj.Chapitre8;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Mest (Doranbolt) — version examinatrice (Chapitre 8, stage 6 : examen de rang S,
 * duel contre Gray et Loki aux côtés de Wendy). Agent secret du Conseil de la Magie
 * infiltré à Fairy Tail ; Télépathie et Magie de la Mémoire.
 */
public class EnnemiMest extends PersonnageBase {

    public EnnemiMest() { this(95); }

    public EnnemiMest(int niveau) {
        this.nom    = "Mest";
        this.niveau = niveau;
        this.type   = "Elementaliste";
        this.role   = "Support";
        this.rarete = "B";

        double mult = 1.35;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 460.0 * mult * niv;
        this.attaque = 150.0 * mult * niv;
        this.defense = 105.0 * mult * niv;
        this.vitesse = 125.0 * mult * vit;

        this.taux_critiques    = 0.10;
        this.degat_critiques   = 1.20;
        this.taux_precisions   = 108.00;
        this.taux_esquives     = 0.10;
        this.taux_blocage      = 0.06;
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
        double degats = this.getAttaque() * 1.20;
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
                double degats = this.getAttaque() * 0.75;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Frappe Téléportée — Inflige 100% ATK.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Manipulation de Mémoire — Inflige 120% ATK et confond la cible pendant 2 tours.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Vision du Conseil — Inflige 75% ATK à tous les ennemis.");
    }
}
