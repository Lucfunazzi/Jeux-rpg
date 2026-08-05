package Personnage.pnj.Chapitre6;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Hibiki Lates — Elementaliste, rang A. Membre des Trimens de Blue Pegasus.
 * Magie des Archives : projette un clavier holographique pour analyser et frapper à distance.
 */
public class EnnemiHibiki extends PersonnageBase {

    public EnnemiHibiki() { this(36); }

    public EnnemiHibiki(int niveau) {
        this.nom    = "Hibiki";
        this.niveau = niveau;
        this.type   = "Elementaliste";
        this.role   = "Support";
        this.rarete = "A";

        double mult = 1.45;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 420.0 * mult * niv;
        this.attaque = 150.0 * mult * niv;
        this.defense = 110.0 * mult * niv;
        this.vitesse = 125.0 * mult * vit;

        this.taux_critiques    = 0.12;
        this.degat_critiques   = 1.25;
        this.taux_precisions   = 108.00;
        this.taux_esquives     = 0.07;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.08;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Frappe d'Archive", "Analyse Tactique", "Hexadecagon"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Hibiki frappe " + cible.getNom() + " d'une Frappe d'Archive !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Hibiki analyse " + cible.getNom() + " et expose ses failles !");
        double degats = this.getAttaque() * 1.25;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        for (PersonnageBase allie : equipeAlliee) {
            if (allie.estVivant()) {
                Combat.appliquerEffet(this, allie, new BuffDefense(0.10, 2), log);
                Combat.appliquerEffet(this, allie, new BuffVitesse(0.10, 2), log);
            }
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Hibiki déploie un Hexadecagon de lumière !");
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = this.getAttaque() * 1.20;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            }
        }
        for (PersonnageBase allie : equipeAlliee) {
            if (allie.estVivant() && allie.getRole().equals("DPS")) {
                Combat.appliquerEffet(this, allie, new BuffAttaque(0.15, 2), log);
                Combat.appliquerEffet(this, allie, new BuffTauxCritique(0.10, 2), log);
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Frappe d'Archive — Inflige 100% ATK.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Analyse Tactique — Inflige 125% ATK, augmente la défense et la vitesse de l'équipe de 10% pendant 2 tours.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Hexadecagon — Inflige 120% ATK à tous les ennemis, augmente l'attaque de 15% et le taux critique de 10% des DPS alliés pendant 2 tours.");
    }
}
