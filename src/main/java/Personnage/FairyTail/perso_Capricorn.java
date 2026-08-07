package Personnage.FairyTail;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Capricorn — Invocateur, rang S.
 * Esprit Céleste de la Porte de la Chèvre, maître du combat rapproché,
 * libéré de l'emprise de Zoldio après Tenrô.
 */
public class perso_Capricorn extends PersonnageBase {

    public perso_Capricorn() {
        this.nom    = "Capricorn";
        this.type   = "Invocateur";
        this.role   = "DPS";
        this.rarete = "S";
        this.niveau = 1;
        double mult = 1.66;
        this.vie     = 621 * mult;
        this.attaque = 264 * mult;
        this.defense = 120 * mult;
        this.vitesse = 144 * mult;
        this.taux_critiques    = 0.15;
        this.degat_critiques   = 1.30;
        this.taux_precisions   = 108.00;
        this.taux_esquives     = 0.10;
        this.taux_blocage      = 0.08;
        this.reduction_blocage = 0.12;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Poing du Bouc", "Danse du Combattant", "Ultime Discipline"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Capricorn frappe " + cible.getNom() + " d'un Poing du Bouc !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Capricorn enchaîne une Danse du Combattant sur " + cible.getNom() + " !");
        double degats = this.getAttaque() * 1.20;
        boolean touche1 = Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        boolean touche2 = Combat.appliquerDegatsAvecLog(this, cible, degats * 0.5, log);
        if (touche1 || touche2) {
            Combat.appliquerEffet(this, cible, new Etourdissement(1), log);
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Capricorn déchaîne l'Ultime Discipline !");
        String roleCible = Combat.cibleParRole(equipeEnnemie, "DPS") != null ? "DPS" : "Tank";
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) multiplicateurRage += (this.getRage() - 100) / 100.0;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant() && ennemi.getRole().equals(roleCible)) {
                double degats = (this.getAttaque() * 2.00) * multiplicateurRage;
                if (ennemi.aEffet(Fragilite.class)) degats *= 1.15;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            }
        }
        Combat.appliquerEffet(this, new BuffTauxCritique(0.30, 2), log);
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Poing du Bouc — Inflige 140% ATK aux supports.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Danse du Combattant — Inflige 160% de degats aux supports et l'immunise aux controle pendants 2 tours ");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Ultime Discipline — Inflige 200% ATK (bonus selon la Rage) à tous les DPS ennemis (ou au Tank), augmente le taux critique de lui même a 30%.");
    }
}
