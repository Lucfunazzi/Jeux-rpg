package Personnage.pnj.Chapitre7;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Hughes — soldat de l'Armée Royale d'Edolas, rang A.
 * Magie des Poupées : anime une légion de pantins articulés au combat.
 */
public class EnnemiHughes extends PersonnageBase {

    public EnnemiHughes() { this(77); }

    public EnnemiHughes(int niveau) {
        this.nom    = "Hughes";
        this.niveau = niveau;
        this.type   = "Elementaliste";
        this.role   = "DPS";
        this.rarete = "A";

        double mult = 1.45;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 480.0 * mult * niv;
        this.attaque = 195.0 * mult * niv;
        this.defense = 100.0 * mult * niv;
        this.vitesse = 120.0 * mult * vit;

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
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = this.getAttaque() * 0.75;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            }
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Hughes invoque sa Poupée Titan !");
        PersonnageBase cible = Combat.cibleParRole(equipeEnnemie, "Tank");
        if (cible == null) cible = Combat.cibleParRole(equipeEnnemie, "DPS");
        if (cible != null && cible.estVivant()) {
            double multiplicateurRage = 1.0;
            if (this.getRage() > 100) multiplicateurRage += (this.getRage() - 100) / 100.0;
            double degats = (this.getAttaque() * 2.10) * multiplicateurRage;
            Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Frappe de Poupée — Inflige 100% ATK.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Légion de Poupées — Inflige 75% ATK à tous les ennemis.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Poupée Titan — Inflige 210% ATK (bonus selon la Rage) au Tank ennemi (ou au DPS).");
    }
}
