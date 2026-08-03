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
        double mult = 1.42;
        this.vie     = 540 * mult;
        this.attaque = 200 * mult;
        this.defense = 120 * mult;
        this.vitesse = 125 * mult;
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
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerDegatsAvecLog(this, cible, degats * 0.5, log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Capricorn déchaîne l'Ultime Discipline !");
        PersonnageBase cible = Combat.cibleParRole(equipeEnnemie, "DPS");
        if (cible == null) cible = Combat.cibleParRole(equipeEnnemie, "Tank");
        if (cible != null && cible.estVivant()) {
            double multiplicateurRage = 1.0;
            if (this.getRage() > 100) multiplicateurRage += (this.getRage() - 100) / 100.0;
            double degats = (this.getAttaque() * 2.00) * multiplicateurRage;
            Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Poing du Bouc — Inflige 100% ATK.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Danse du Combattant — Inflige 120% ATK à l'ennemi avec le plus d'attaque puis 60% ATK supplémentaires. et etourdit la cible ");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Ultime Discipline — Inflige 200% ATK (bonus selon la Rage) au DPS ennemi (ou au Tank) augmente le taux critique de lui même a 30%.");
    }
}
