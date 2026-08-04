package Personnage.FairyTail;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Loki (Leo le Lion) — Invocateur, rang S.
 * Esprit Céleste de la Porte du Lion, le plus loyal des Douze Portes du Zodiaque
 * de Lucy. Magie de la Lumière de Regulus.
 */
public class perso_Loki extends PersonnageBase {

    public perso_Loki() {
        this.nom    = "Loki";
        this.type   = "Invocateur";
        this.role   = "DPS";
        this.rarete = "S";
        this.niveau = 1;
        double mult = 1.55;
        this.vie     = 560 * mult;
        this.attaque = 215 * mult;
        this.defense = 126 * mult;
        this.vitesse = 145 * mult;
        this.taux_critiques    = 0.18;
        this.degat_critiques   = 1.35;
        this.taux_precisions   = 105.00;
        this.taux_esquives     = 0.12;
        this.taux_blocage      = 0.06;
        this.reduction_blocage = 0.10;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Poing de Regulus", "Lion Brillant", "Impact Regulus"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Loki frappe " + cible.getNom() + " d'un Poing de Regulus !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Loki rayonne comme un Lion Brillant !");
        Combat.appliquerEffet(this, new BuffAttaque(0.20, 2), log);
        double degats = this.getAttaque() * 1.20;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Loki libère un Impact Regulus dévastateur !");
        PersonnageBase cible = Combat.cibleParRole(equipeEnnemie, "DPS");
        if (cible == null) cible = Combat.cibleParRole(equipeEnnemie, "Tank");
        if (cible != null && cible.estVivant()) {
            double multiplicateurRage = 1.0;
            if (this.getRage() > 100) multiplicateurRage += (this.getRage() - 100) / 100.0;
            double degats = (this.getAttaque() * 2.30) * multiplicateurRage;
            Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Poing de Regulus — Inflige 100% ATK.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Lion Brillant — Augmente son ATK de 20% pendant 2 tours et inflige 120% ATK.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Impact Regulus — Inflige 230% ATK (bonus selon la Rage) au DPS ennemi (ou au Tank).");
    }
}
