package Personnage.pnj.Chapitre9;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Bluenote Stinger — l'un des Sept Enfants de la Purgation de Grimoire Heart, rang S.
 * Magie de la Gravité : écrase ses adversaires sous un poids écrasant.
 * Affronte Gildarts au Chapitre 9 (stage 7).
 */
public class EnnemiBluenote extends PersonnageBase {

    public EnnemiBluenote() { this(108); }

    public EnnemiBluenote(int niveau) {
        this.nom    = "Bluenote";
        this.niveau = niveau;
        this.type   = "Elementaliste";
        this.role   = "Tank";
        this.rarete = "SS";

        double mult = 1.70;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 780.0 * mult * niv;
        this.attaque = 210.0 * mult * niv;
        this.defense = 185.0 * mult * niv;
        this.vitesse =  95.0 * mult * vit;

        this.taux_critiques    = 0.10;
        this.degat_critiques   = 1.20;
        this.taux_precisions   = 105.00;
        this.taux_esquives     = 0.05;
        this.taux_blocage      = 0.14;
        this.reduction_blocage = 0.18;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Poing de Gravité", "Noyau de Gravité", "Écrasement Gravitationnel"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Bluenote écrase " + cible.getNom() + " d'un Poing de Gravité !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Bluenote alourdit " + cible.getNom() + " avec un Noyau de Gravité !");
        double degats = this.getAttaque() * 1.30;
        boolean touche = Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        if (touche) {
            Combat.appliquerEffet(this, cible, new ReductionVitesse(0.30, 2), log);
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Bluenote déclenche un Écrasement Gravitationnel !");
        PersonnageBase cible = Combat.cibleParRole(equipeEnnemie, "Tank");
        if (cible == null) cible = Combat.cibleParRole(equipeEnnemie, "DPS");
        if (cible != null && cible.estVivant()) {
            double multiplicateurRage = 1.0;
            if (this.getRage() > 100) multiplicateurRage += (this.getRage() - 100) / 100.0;
            double degats = (this.getAttaque() * 1.90) * multiplicateurRage;
            boolean touche = Combat.appliquerDegatsAvecLog(this, cible, degats, log);
            if (touche) {
                Combat.appliquerEffet(this, cible, new ReductionDefense(0.20, 2), log);
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Poing de Gravité — Inflige 100% ATK.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Noyau de Gravité — Inflige 130% ATK et réduit la vitesse de la cible de 30% pendant 2 tours.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Écrasement Gravitationnel — Inflige 190% ATK (bonus selon la Rage) au Tank ennemi (ou au DPS), réduit sa défense de 20% pendant 2 tours.");
    }
}
