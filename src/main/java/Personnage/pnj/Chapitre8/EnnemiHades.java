package Personnage.pnj.Chapitre8;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Hades (Precht Gaebolg) — maître de Grimoire Heart, rang SS.
 * Ancien second maître de Fairy Tail, l'un des mages les plus puissants
 * de sa génération. Apparaît au Chapitre 8 (stage 9) puis de nouveau aux
 * Chapitres 9 et 10, de plus en plus fort à chaque affrontement.
 */
public class EnnemiHades extends PersonnageBase {

    public EnnemiHades() { this(101); }

    public EnnemiHades(int niveau) {
        this.nom    = "Hades";
        this.niveau = niveau;
        this.type   = "Elementaliste";
        this.role   = "DPS";
        this.rarete = "SSS";

        double mult = 1.95;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 850.0 * mult * niv;
        this.attaque = 280.0 * mult * niv;
        this.defense = 190.0 * mult * niv;
        this.vitesse = 125.0 * mult * vit;

        this.taux_critiques    = 0.18;
        this.degat_critiques   = 1.40;
        this.taux_precisions   = 110.00;
        this.taux_esquives     = 0.10;
        this.taux_blocage      = 0.10;
        this.reduction_blocage = 0.16;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    /** Rang SSS : immunise aux effets de controle (Etourdissement, Paralysie, Sommeil,
     *  Petrification, Gel) pendant tout le combat. */
    @Override
    public void reinitialiserPourCombat() {
        super.reinitialiserPourCombat();
        appliquerImmuniteControlePassive();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Choc des Ténèbres", "Amaterasu", "Genèse Zéro"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Hades frappe " + cible.getNom() + " d'un Choc des Ténèbres !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Hades embrase " + cible.getNom() + " d'un Amaterasu implacable !");
        double degats = this.getAttaque() * 1.50;
        boolean touche = Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        if (touche) {
            Combat.appliquerEffet(this, cible, new Brulure(2, 0.08), log);
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Hades ouvre la Genèse Zéro — le néant absorbe tout !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) multiplicateurRage += (this.getRage() - 100) / 100.0;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = (this.getAttaque() * 1.25) * multiplicateurRage;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Choc des Ténèbres — Inflige 100% ATK.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Amaterasu — Inflige 150% ATK et brûle la cible pendant 2 tours (8% PV/tour).");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Genèse Zéro — Inflige 125% ATK (bonus selon la Rage) à tous les ennemis.");
    }
}
