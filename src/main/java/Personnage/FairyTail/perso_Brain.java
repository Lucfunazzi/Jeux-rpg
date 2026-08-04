package Personnage.FairyTail;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Brain — ancien chef d'Oración Seis, rang S.
 * Ancien membre du Conseil de la Magie, maître de la manipulation mentale et du drain de magie.
 */
public class perso_Brain extends PersonnageBase {

    public perso_Brain() {
        this.nom    = "Brain";
        this.type   = "Elementaliste";
        this.role   = "Support";
        this.rarete = "S";
        this.niveau = 1;
        double mult = 1.55;
        this.vie     = 540 * mult;
        this.attaque = 170 * mult;
        this.defense = 120 * mult;
        this.vitesse = 110 * mult;
        this.taux_critiques    = 0.10;
        this.degat_critiques   = 1.20;
        this.taux_precisions   = 108.00;
        this.taux_esquives     = 0.08;
        this.taux_blocage      = 0.10;
        this.reduction_blocage = 0.12;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Barrière de Particules Magiques", "Manipulation Mentale", "Drain Magique Absolu"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Brain projette des Particules Magiques sur l'équipe ennemie !");
        List<PersonnageBase> vivants = new java.util.ArrayList<>();
        for (PersonnageBase ennemi : equipeEnnemie) if (ennemi.estVivant()) vivants.add(ennemi);
        java.util.Collections.shuffle(vivants);
        for (int i = 0; i < Math.min(3, vivants.size()); i++) {
            double degats = this.getAttaque() * 1.35;
            Combat.appliquerDegatsAvecLog(this, vivants.get(i), degats, log);
        }
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Brain envahit l'esprit de " + cible.getNom() + " — Manipulation Mentale !");
        double degats = this.getAttaque() * 1.20;
        boolean touche = Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        if (touche) {
            Combat.appliquerEffet(this, cible, new Confusion(2), log);
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Brain draine la magie de toute l'équipe ennemie !");
        double degatsTotaux = 0;
        List<PersonnageBase> touches = new java.util.ArrayList<>();
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = this.getAttaque() * 2.00;
                boolean touche = Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                if (touche) {
                    degatsTotaux += degats;
                    Combat.appliquerEffet(this, ennemi, new ReductionAttaque(0.10, 2), log);
                    touches.add(ennemi);
                }
            }
        }
        double soin = degatsTotaux * 0.20;
        if (soin > 0) {
            this.recevoirSoin(soin, log);
        }
        if (!touches.isEmpty()) {
            PersonnageBase cibleSilence = touches.get((int) (Math.random() * touches.size()));
            Combat.appliquerEffet(this, cibleSilence, new Silence(2), log);
            PersonnageBase cibleMalediction = touches.get((int) (Math.random() * touches.size()));
            Combat.appliquerEffet(this, cibleMalediction, new Malediction(2, 0.30), log);
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Barrière de Particules Magiques — Inflige 135% ATK a 3 ennemis aleatoires.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Manipulation Mentale — Inflige 120% ATK et confond la cible la plus forte pendant 2 tours.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Drain Magique Absolu — Inflige 200% ATK à tous les ennemis et se soigne de 20% des dégâts infligés. inflige silence a 1 personne aleatoire , inflige malediction a 1 personne aleatoire , inflige reduction d'attaque de 10% a tout les ennemis");
    }
}
