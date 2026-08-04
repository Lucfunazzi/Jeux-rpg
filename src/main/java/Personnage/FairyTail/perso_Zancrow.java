package Personnage.FairyTail;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Zancrow — Chasseur de Dragon, rang S.
 * Ancien des Sept Enfants de la Purgation de Grimoire Heart, God Slayer des Flammes.
 */
public class perso_Zancrow extends PersonnageBase {

    public perso_Zancrow() {
        this.nom    = "Zancrow";
        this.type   = "ChasseurDeDragon";
        this.role   = "DPS";
        this.rarete = "S";
        this.niveau = 1;
        double mult = 1.60;
        this.vie     = 580 * mult;
        this.attaque = 225 * mult;
        this.defense = 125 * mult;
        this.vitesse = 130 * mult;
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
        if (touche) {
            this.recevoirSoin(degats * 0.25, log);
            Combat.appliquerEffet(this, cible, new Brulure(2, 0.08), log);
        }
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
                    Combat.appliquerEffet(this, ennemi, new Malediction(2, 0.30), log);
                }
            }
        }
        this.recevoirSoin(this.getAttaque() * 0.25, log);
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Flamme du Dieu — Inflige 100% ATK.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Dévoration des Flammes — Inflige 130% ATK et se soigne de 25% des dégâts infligés et brule l'ennemi pendants 2 tours.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Grand Incendie Divin — Inflige 115% ATK (bonus selon la Rage) à tous les ennemis, maledictions pendant 2 tours et se soigne de 25% de son attaque.");
    }
}
