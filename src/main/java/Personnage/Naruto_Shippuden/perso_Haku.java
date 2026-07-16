package Personnage.Naruto_Shippuden;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;

public class perso_Haku extends PersonnageBase {

    public perso_Haku() {
        this.nom = "Haku";
        this.niveau = 1;
        this.type = "Ninja";
        this.role = "Support";
        this.rarete = "B";

        double multiplicateurRarete = 1.20;
        this.vie = 320 * multiplicateurRarete;
        this.attaque = 110 * multiplicateurRarete;
        this.defense = 75 * multiplicateurRarete;
        this.vitesse = 125 * multiplicateurRarete;

        this.taux_critiques = 0.10;
        this.degat_critiques = 1.30;
        this.taux_precisions = 110.00;
        this.taux_esquives = 0.15;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Aiguilles Senbon", "Miroirs de Glace Démoniaques", "Averses de Pointes de Glace"};
    }

    // Attaque de base — 100% ATK
    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.getNom() + " lance des aiguilles Senbon sur les points vitaux de " + cible.getNom() + " !");
        double degats = this.getAttaque() * 1.00;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    }

    // Spéciale — BuffVitesse 20% sur toute l'équipe pendant 2 tours
    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.getNom() + " érige les Miroirs de Glace Démoniaques !");

        for (PersonnageBase allie : equipeAlliee) {
            if (allie.estVivant()) {
                Combat.appliquerEffet(this, allie, new BuffVitesse(0.20, 2), log);
            }
        }
    }

    // Ultime — 80% ATK AoE + 40% de chances de Gel, sinon ReductionVitesse 15%
    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.getNom() + " déchaîne une pluie d'aiguilles glaciales depuis ses miroirs !");

        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = this.getAttaque() * 0.80;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);

                if (Math.random() < 0.40) {
                    Combat.appliquerEffet(this, ennemi, new Gel(2), log);
                    log.add("[GEL] " + ennemi.getNom() + " est totalement congelé ! (Prêt pour l'Exécution de Zabuza !)");
                } else {
                    Combat.appliquerEffet(this, ennemi, new ReductionVitesse(0.15, 2), log);
                }
            }
        }
    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Aiguilles Senbon — Inflige 100% ATK à une cible et lui applique Marquage pendant 2 tours (+15% de dégâts subis, active la synergie de Zabuza).");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Miroirs de Glace Démoniaques — Octroie à Haku un Bouclier équivalent à 35% de ses PV max et augmente la Vitesse de tous les alliés de 20% pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Averses de Pointes de Glace — Inflige 110% ATK à tous les ennemis, avec 40% de chances d'appliquer Gel. Réduit la Vitesse de 15% pendant 2 tours s'ils ne sont pas gelés.");
    }
}