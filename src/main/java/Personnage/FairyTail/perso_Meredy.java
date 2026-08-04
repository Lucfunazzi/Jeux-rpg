package Personnage.FairyTail;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Meredy — Elementaliste, rang A.
 * Ancienne de Grimoire Heart, se rachète après Tenrô et rejoint plus tard
 * Crime Sorcière aux côtés de Jellal. Magie Sensorielle (Maguilty Sense).
 */
public class perso_Meredy extends PersonnageBase {

    public perso_Meredy() {
        this.nom    = "Meredy";
        this.type   = "Elementaliste";
        this.role   = "Support";
        this.rarete = "S";
        this.niveau = 1;
        double mult = 1.42;
        this.vie     = 506 * mult;
        this.attaque = 186 * mult;
        this.defense = 115 * mult;
        this.vitesse = 125 * mult;
        this.taux_critiques    = 0.10;
        this.degat_critiques   = 1.20;
        this.taux_precisions   = 105.00;
        this.taux_esquives     = 0.09;
        this.taux_blocage      = 0.06;
        this.reduction_blocage = 0.10;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Rayon Maguilty", "Lien Sensoriel", "Danse Écarlate"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Meredy tire un Rayon Maguilty sur " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Meredy tisse un Lien Sensoriel avec " + cible.getNom() + " !");
        double degats = this.getAttaque() * 1.20;
        boolean touche = Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        if (touche) {
            Combat.appliquerEffet(this, cible, new Fragilite(2, 0.20), log);
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Meredy déchaîne une Danse Écarlate sur toute l'équipe ennemie !");
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = this.getAttaque() * 0.85;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                Combat.appliquerEffet(this, ennemi, new Marquage(), log);
                Combat.appliquerEffet(this, ennemi, new ReductionVitesse(0.10, 2), log);
                Combat.appliquerEffet(this, ennemi, new BuffBlocage(-0.10, 2), log);
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Rayon Maguilty — Inflige 100% ATK.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Lien Sensoriel — Inflige 120% ATK et augmente les dégâts subis par la cible de 20% pendant 2 tours.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Danse Écarlate — Inflige 85% ATK à tous les ennemis. et inflige marquage et baisse la vitesse de 10% des adversaire et descend le taux de blocage de 10% ");
    }
}
