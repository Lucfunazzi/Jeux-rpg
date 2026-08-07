package Personnage.pnj.Chapitre9;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Capricorn — Esprit Céleste de la Porte de la Chèvre, rang A.
 * Asservi par Zoldio au sein de Grimoire Heart, il affronte Loki (Leo le Lion)
 * au Chapitre 9 (stage 4). Maître du combat rapproché.
 */
public class EnnemiCapricorn extends PersonnageBase {

    public EnnemiCapricorn() { this(106); }

    public EnnemiCapricorn(int niveau) {
        this.nom    = "Capricorn";
        this.niveau = niveau;
        this.type   = "Invocateur";
        this.role   = "DPS";
        this.rarete = "S";

        double mult = 1.50;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 560.0 * mult * niv;
        this.attaque = 215.0 * mult * niv;
        this.defense = 130.0 * mult * niv;
        this.vitesse = 125.0 * mult * vit;

        this.taux_critiques    = 0.15;
        this.degat_critiques   = 1.30;
        this.taux_precisions   = 108.00;
        this.taux_esquives     = 0.10;
        this.taux_blocage      = 0.08;
        this.reduction_blocage = 0.12;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Poing du Bouc", "Danse du Combattant", "Ultime Discipline"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Capricorn frappe " + cible.getNom() + " d'un Poing du Bouc !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Capricorn enchaîne une Danse du Combattant sur " + cible.getNom() + " !");
        double degats = this.getAttaque() * 1.20;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerDegatsAvecLog(this, cible, degats * 0.5, log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Capricorn déchaîne l'Ultime Discipline !");
        String roleCible = Combat.cibleParRole(equipeEnnemie, "DPS") != null ? "DPS" : "Tank";
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) multiplicateurRage += (this.getRage() - 100) / 100.0;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant() && ennemi.getRole().equals(roleCible)) {
                double degats = (this.getAttaque() * 2.00) * multiplicateurRage;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Poing du Bouc — Inflige 100% ATK.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Danse du Combattant — Inflige 120% ATK puis 60% ATK supplémentaires.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Ultime Discipline — Inflige 200% ATK (bonus selon la Rage) à tous les DPS ennemis (ou au Tank).");
    }
}
