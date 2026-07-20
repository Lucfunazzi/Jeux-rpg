package Personnage.FairyTail;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;

/**
 * Mirajane Halphas [SS] — Evolution de Mirajane [S].
 * Forme démoniaque ultime : Halphas, démon des armées.
 * Multiplicateur SS = 1.75
 */
public class perso_Mirajane_Halphas extends PersonnageBase {

    public perso_Mirajane_Halphas() {
        this.nom    = "Mirajane Halphas";
        this.type="Elementaliste";
        this.role   = "DPS";
        this.rarete = "SS";
        this.niveau = 1;

        double multiplicateurRarete = 1.75;
        this.vie      = 650 * multiplicateurRarete;   // 1137
        this.attaque  = 260 * multiplicateurRarete;   // 455
        this.defense  = 100 * multiplicateurRarete;   // 175
        this.vitesse  = 140 * multiplicateurRarete;   // 245

        this.taux_critiques    = 0.25;   // +5% vs Mirajane S
        this.degat_critiques   = 1.55;   // +15% vs Mirajane S
        this.taux_precisions   = 110.00;
        this.taux_esquives     = 0.12;
        this.taux_blocage      = 0.08;
        this.reduction_blocage = 0.15;
        this.degats_renvoi     = 0.90;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Griffe de Halphas", "Ame devoree", "Apocalypse Demoniaque"};
    }

    // ── Attaque de base ───────────────────────────────────────────────────
    // Mirajane S : 100% ATK + Poison 10%/2 tours
    // Halphas    : 120% ATK + Poison 15%/3 tours + ReductionDEF 15%/2 tours
    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Mirajane Halphas utilise Griffe de Halphas !");
        boolean touche = Combat.attaqueTouche(this, cible);
        if (!touche) {
            log.add(cible.getNom() + " esquive !");
            this.ajouterRage(50);
            return;
        }
        double degats = this.getAttaque() * 1.20;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new Poison(3, 0.15), log);
        Combat.appliquerEffet(this, cible, new ReductionDefense(0.15, 2), log);
    }

    // ── Attaque spéciale ──────────────────────────────────────────────────
    // Mirajane S : AoE 140% ATK + Malediction 10%/2 tours + BuffCrit 10%/2 tours
    // Halphas    : AoE 170% ATK + Malediction 20%/3 tours + BuffCrit 20%/2 tours
    //              + drain de vie 10% sur chaque cible touchée
    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Mirajane Halphas utilise Ame devoree !");
        double totalAbsorbe = 0;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = this.getAttaque() * 1.70;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                Combat.appliquerEffet(this, ennemi, new Malediction(3, 0.20), log);
                // Drain : vole 10% des PV max de chaque ennemi touché
                double drain = ennemi.getVieMax() * 0.10;
                totalAbsorbe += drain;
            }
        }
        // Soin global par drain
        if (totalAbsorbe > 0) {
            this.recevoirSoin(totalAbsorbe, log);
            log.add("Mirajane Halphas absorbe " + String.format("%.1f", totalAbsorbe)
                    + " PV via le drain !");
        }
        Combat.appliquerEffet(this, new BuffTauxCritique(0.20, 2), log);
    }

    // ── Attaque ultime ────────────────────────────────────────────────────
    // Mirajane S : AoE 160% ATK + Paralysie 40%/2 tours + Absorption 15%/2 tours
    // Halphas    : AoE 200% ATK + Paralysie 60%/3 tours + Absorption 25%/3 tours
    //              + bonus Rage encore plus puissant + ReductionATK 20% sur tous
    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Mirajane Halphas utilise Apocalypse Demoniaque !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) {
            multiplicateurRage += (this.getRage() - 100) / 80.0; // plus fort que Mirajane S (/100)
        }
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = (this.getAttaque() * 2.00) * multiplicateurRage;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                Combat.appliquerEffet(this, ennemi, new Paralysie(3, 0.60), log);
                Combat.appliquerEffet(this, ennemi, new ReductionAttaque(0.20, 2), log);
            }
        }
        Combat.appliquerEffet(this, new Absorption(3, 0.25), log);
    }

    // ── Descriptions ──────────────────────────────────────────────────────
    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Griffe de Halphas — inflige 120% ATK a une cible, "
                + "applique Poison (15% PV/tour) pendant 3 tours "
                + "et reduit sa DEF de 15% pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Ame devoree — inflige 170% ATK a tous les ennemis, "
                + "applique Malediction (soins reduits de 20%) pendant 3 tours, "
                + "vole 10% des PV max de chaque ennemi touche, "
                + "et gagne 20% de taux critique pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Apocalypse Demoniaque — inflige 200% ATK a tous les ennemis "
                + "(bonus selon la Rage), paralyse 3 tours (60% chance de liberation), "
                + "reduit leur ATK de 20% pendant 2 tours, "
                + "et applique Absorption (25% vol de vie) pendant 3 tours.");
    }
}
