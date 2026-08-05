package Personnage.pnj.Chapitre10;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Zeref Dragnir — le Mage Noir, rang SS.
 * Maudit par Ankhseram : la moindre étincelle de vie autour de lui attire la mort.
 * Apparaît au Chapitre 10 (stage 6), errant sur l'île de Tenrô.
 */
public class EnnemiZeref extends PersonnageBase {

    public EnnemiZeref() { this(123); }

    public EnnemiZeref(int niveau) {
        this.nom    = "Zeref";
        this.niveau = niveau;
        this.type   = "Elementaliste";
        this.role   = "DPS";
        this.rarete = "UR";

        double mult = 1.95;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 880.0 * mult * niv;
        this.attaque = 290.0 * mult * niv;
        this.defense = 195.0 * mult * niv;
        this.vitesse = 120.0 * mult * vit;

        this.taux_critiques    = 0.16;
        this.degat_critiques   = 1.40;
        this.taux_precisions   = 112.00;
        this.taux_esquives     = 0.08;
        this.taux_blocage      = 0.10;
        this.reduction_blocage = 0.16;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Toucher de la Mort", "Malédiction d'Ankhseram", "Fin de Toute Vie"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Zeref effleure " + cible.getNom() + " d'un Toucher de la Mort !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("La Malédiction d'Ankhseram s'abat sur " + cible.getNom() + " !");
        double degats = this.getAttaque() * 1.45;
        boolean touche = Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        if (touche) {
            Combat.appliquerEffet(this, cible, new Malediction(2, 0.40), log);
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Zeref déchaîne la Fin de Toute Vie sur toute l'équipe ennemie !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) multiplicateurRage += (this.getRage() - 100) / 100.0;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = (this.getAttaque() * 1.30) * multiplicateurRage;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Toucher de la Mort — Inflige 100% ATK.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Malédiction d'Ankhseram — Inflige 145% ATK et réduit les soins reçus par la cible de 40% pendant 2 tours.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Fin de Toute Vie — Inflige 130% ATK (bonus selon la Rage) à tous les ennemis.");
    }
}
