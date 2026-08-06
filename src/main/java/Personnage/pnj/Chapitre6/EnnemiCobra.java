package Personnage.pnj.Chapitre6;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Cobra (Erik) — Chasseur de Dragon du Poison d'Oración Seis, rang A.
 * Son oreille magique lui permet d'entendre les pensées et les âmes de toute chose,
 * ce qui lui donne une esquive redoutable en combat.
 */
public class EnnemiCobra extends PersonnageBase {

    public EnnemiCobra() { this(60); }

    public EnnemiCobra(int niveau) {
        this.nom    = "Cobra";
        this.niveau = niveau;
        this.type   = "ChasseurDeDragon";
        this.role   = "DPS";
        this.rarete = "A";

        double mult = 1.45;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 420.0 * mult * niv;
        this.attaque = 175.0 * mult * niv;
        this.defense = 105.0 * mult * niv;
        this.vitesse = 130.0 * mult * vit;

        this.taux_critiques    = 0.14;
        this.degat_critiques   = 1.30;
        this.taux_precisions   = 105.00;
        this.taux_esquives     = 0.05;
        this.taux_blocage      = 0.06;
        this.reduction_blocage = 0.10;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Écailles du Serpent", "Ecailles du Dagon Venimeux", "Hurlement du Dragon Venimeux"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Cobra lacère " + cible.getNom() + " de ses Écailles du Serpent !");
        Combat.attaquer(this, cible, log);
        Combat.appliquerEffet(this, cible, new Poison(3,0.06), log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Cobra lance Ecailles du Dagon Venimeux sur " + cible.getNom() + " !");
        double degats = this.getAttaque() * 1.75;
        boolean touche = Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        if (touche) {
            Combat.appliquerEffet(this, cible, new Poison(3, 0.06), log);
        }
        Combat.appliquerEffet(this, new BuffTauxEsquive(0.15,2), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Cobra entend chaque pensée ennemie — Perception Totale !");
        
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = this.getAttaque() * 1.10;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                Combat.appliquerEffet(this, ennemi, new Poison(3, 0.06), log);
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Écailles du Serpent — Inflige 100% ATK et empoisonne la cible pendant 3 tours (6% Pv/tour.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Ecailles du Dragon Venimeux — Inflige 175% ATK et empoisonne la cible pendant 3 tours (6% PV/tour) "
                + "Et augmente son esquive de 15% pendants 2 tours.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Hurlement du Dragon Venimeux — inflige 110% ATK à tous les ennemis et empoisonne les cibles pendants 3 tours (6% PV/Tour).");
    }
}
