package Personnage.pnj.Chapitre4;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Vivaldus — garde de la Tour du Paradis, magie musicale, rang A.
 */
public class EnnemiVivaldus extends PersonnageBase {

    public EnnemiVivaldus() { this(48); }

    public EnnemiVivaldus(int niveau) {
        this.nom    = "Vivaldus";
        this.niveau = niveau;
        this.type   = "Elementaliste";
        this.role   = "Support";
        this.rarete = "A";

        double mult = 1.40;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 575.0 * mult * niv;
        this.attaque = 150.0 * mult * niv;
        this.defense = 125.0 * mult * niv;
        this.vitesse = 135.0 * mult * vit;

        this.taux_critiques    = 0.10;
        this.degat_critiques   = 1.25;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.05;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.20;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Notes de musiques", "Rock of Succubus", "Rock and roll"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Vivaldus utilise Notes de musiques " + cible.getNom());
        double degats = this.getAttaque() * 1.00;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Vivaldus utilise Rock of Succubus !");

        PersonnageBase cibleSupport = equipeEnnemie.stream()
                .filter(e -> e.estVivant() && e.getRole().equals("Support"))
                .findFirst()
                .orElse(cible);

        double degats = this.getAttaque() * 1.00;
        Combat.appliquerDegatsAvecLog(this, cibleSupport, degats, log);
        Combat.appliquerEffet(this, cibleSupport, new Ralentissement(2, 0.20), log);
        if (Math.random() < 0.30) {
            Combat.appliquerEffet(this, cibleSupport, new Confusion(2), log);
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Vivaldus utilise Rock and roll !");
        String roleCible;
        if (Combat.cibleParRole(equipeEnnemie, "Support") != null) roleCible = "Support";
        else if (Combat.cibleParRole(equipeEnnemie, "DPS")  != null) roleCible = "DPS";
        else roleCible = "Tank";
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant() && ennemi.getRole().equals(roleCible)) {
                double degats = this.getAttaque() * 0.80;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            }
        }
        for (PersonnageBase allie : equipeAlliee) {
            if (allie.estVivant() && allie.getRole().equals("Support")) {
                Combat.appliquerEffet(this, allie, new Immunite(2), log);
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Notes de Musiques — inflige 100% ATK a une cible.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Rock of succubus — cible les Supports ennemi prioritairement  "
                + "inflige 100% ATK, à 30% de chance d'infliger confusions aux supports. ");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Rock and roll — Attaque les Supports ennemis à 80% et immunise les supports alliée aux effets négatifs pendants "
                + "2 tours. ");
    }
}
