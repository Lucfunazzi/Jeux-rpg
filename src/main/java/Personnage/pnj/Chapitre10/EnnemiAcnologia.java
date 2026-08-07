package Personnage.pnj.Chapitre10;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Acnologia — le Dragon Noir, rang SS.
 * Chasseur de Dragons devenu dragon lui-même ; considéré comme la menace la plus
 * dévastatrice existante. Boss final du Chapitre 10 (stages 7 à 10), de plus en
 * plus terrifiant à chaque affrontement jusqu'à l'activation de la Sphère Féerique.
 */
public class EnnemiAcnologia extends PersonnageBase {

    public EnnemiAcnologia() { this(125); }

    public EnnemiAcnologia(int niveau) {
        this.nom    = "Acnologia";
        this.niveau = niveau;
        this.type   = "ChasseurDeDragon";
        this.role   = "DPS";
        this.rarete = "UR";

        double mult = 2.10;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 1000.0 * mult * niv;
        this.attaque =  320.0 * mult * niv;
        this.defense =  220.0 * mult * niv;
        this.vitesse =  140.0 * mult * vit;

        this.taux_critiques    = 0.20;
        this.degat_critiques   = 1.50;
        this.taux_precisions   = 115.00;
        this.taux_esquives     = 0.12;
        this.taux_blocage      = 0.10;
        this.reduction_blocage = 0.16;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    /** Rang UR : immunise aux effets de controle (Etourdissement, Paralysie, Sommeil,
     *  Petrification, Gel) pendant tout le combat. */
    @Override
    public void reinitialiserPourCombat() {
        super.reinitialiserPourCombat();
        appliquerImmuniteControlePassive();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Griffe du Dragon Noir", "Souffle Ardent du Dragon", "Extinction Draconique"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Acnologia lacère " + cible.getNom() + " d'une Griffe du Dragon Noir !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Acnologia libère un Souffle Ardent du Dragon sur toute l'équipe ennemie !");
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = this.getAttaque() * 1.05;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            }
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Acnologia déchaîne l'Extinction Draconique — l'île entière tremble !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) multiplicateurRage += (this.getRage() - 100) / 100.0;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = (this.getAttaque() * 1.40) * multiplicateurRage;
                boolean touche = Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                if (touche) {
                    Combat.appliquerEffet(this, ennemi, new ReductionDefense(0.25, 2), log);
                }
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Griffe du Dragon Noir — Inflige 100% ATK.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Souffle Ardent du Dragon — Inflige 105% ATK à tous les ennemis.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Extinction Draconique — Inflige 140% ATK (bonus selon la Rage) à tous les ennemis et réduit leur défense de 25% pendant 2 tours.");
    }
}
