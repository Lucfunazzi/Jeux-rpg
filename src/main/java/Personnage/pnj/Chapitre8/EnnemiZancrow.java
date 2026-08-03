package Personnage.pnj.Chapitre8;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Zancrow — l'un des Sept Enfants de la Purgation de Grimoire Heart, rang S.
 * God Slayer des Flammes : dévore le feu pour s'en nourrir et devenir immunisé
 * aux brûlures. Apparaît au Chapitre 8 (stage 10) puis de nouveau au Chapitre 9.
 */
public class EnnemiZancrow extends PersonnageBase {

    public EnnemiZancrow() { this(103); }

    public EnnemiZancrow(int niveau) {
        this.nom    = "Zancrow";
        this.niveau = niveau;
        this.type   = "ChasseurDeDragon";
        this.role   = "DPS";
        this.rarete = "S";

        double mult = 1.70;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 600.0 * mult * niv;
        this.attaque = 240.0 * mult * niv;
        this.defense = 135.0 * mult * niv;
        this.vitesse = 130.0 * mult * vit;

        this.taux_critiques    = 0.16;
        this.degat_critiques   = 1.35;
        this.taux_precisions   = 108.00;
        this.taux_esquives     = 0.10;
        this.taux_blocage      = 0.06;
        this.reduction_blocage = 0.10;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Flamme du Dieu", "Dévoration des Flammes", "Grand Incendie Divin"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Zancrow embrase " + cible.getNom() + " d'une Flamme du Dieu !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Zancrow dévore les flammes et se régénère !");
        double degats = this.getAttaque() * 1.30;
        boolean touche = Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        if (touche) this.recevoirSoin(degats * 0.25, log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Zancrow déchaîne un Grand Incendie Divin sur toute l'équipe ennemie !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) multiplicateurRage += (this.getRage() - 100) / 100.0;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = (this.getAttaque() * 1.15) * multiplicateurRage;
                boolean touche = Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                if (touche) {
                    Combat.appliquerEffet(this, ennemi, new Brulure(2, 0.07), log);
                }
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Flamme du Dieu — Inflige 100% ATK.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Dévoration des Flammes — Inflige 130% ATK et se soigne de 25% des dégâts infligés.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Grand Incendie Divin — Inflige 115% ATK (bonus selon la Rage) à tous les ennemis, brûlure pendant 2 tours (7% PV/tour).");
    }
}
