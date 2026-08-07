package Personnage.FairyTail;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Hughes — Elementaliste, rang A.
 * Ancien soldat de l'Armée Royale d'Edolas, Magie des Poupées.
 */
public class perso_Hughes extends PersonnageBase {

    public perso_Hughes() {
        this.nom    = "Hughes";
        this.type   = "Elementaliste";
        this.role   = "Support";
        this.rarete = "A";
        this.niveau = 1;
        double mult = 1.48;
        this.vie     = 460 * mult;
        this.attaque = 185 * mult;
        this.defense =  95 * mult;
        this.vitesse = 120 * mult;
        this.taux_critiques    = 0.14;
        this.degat_critiques   = 1.30;
        this.taux_precisions   = 105.00;
        this.taux_esquives     = 0.08;
        this.taux_blocage      = 0.06;
        this.reduction_blocage = 0.10;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Frappe de Poupée", "Légion de Poupées", "Poupée Titan"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Hughes lance une Frappe de Poupée sur " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Hughes déchaîne sa Légion de Poupées sur toute l'équipe ennemie !");
        List<PersonnageBase> vivants = new java.util.ArrayList<>();
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = this.getAttaque() * 0.75;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                vivants.add(ennemi);
            }
        }
        java.util.Collections.shuffle(vivants);
        for (int i = 0; i < Math.min(2, vivants.size()); i++) {
            Combat.appliquerEffet(this, vivants.get(i), new ReductionDefense(0.10, 2), log);
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Hughes invoque sa Poupée Titan !");
        String roleCible = Combat.cibleParRole(equipeEnnemie, "Tank") != null ? "Tank" : "DPS";
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) multiplicateurRage += (this.getRage() - 100) / 100.0;
        double degats = (this.getAttaque() * 1.25) * multiplicateurRage;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant() && ennemi.getRole().equals(roleCible)) {
                boolean touche = Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                double chanceConfusion = 0.20;
                if (ennemi.aEffet(ReductionVitesse.class)) chanceConfusion += 0.15;
                if (touche && Math.random() < chanceConfusion) {
                    Combat.appliquerEffet(this, ennemi, new Confusion(2), log);
                }
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Frappe de Poupée — Inflige 100% ATK.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Légion de Poupées — Inflige 75% ATK à tous les ennemis et reduit la defense de 2 ennemis aleatoire de 10% .");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Poupée Titan — Inflige 125% ATK (bonus selon la Rage) au Tank ennemi (ou à tous les DPS ennemis) et a 20% de chance d'infliger confusion pendant 2 tours par cible touchée.");
    }
}
