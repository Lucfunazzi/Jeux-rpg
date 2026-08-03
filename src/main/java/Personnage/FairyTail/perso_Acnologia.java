package Personnage.FairyTail;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Acnologia — Chasseur de Dragon, rang SS.
 * Le Dragon Noir, considéré comme la menace la plus dévastatrice existante.l'ultime dps du jeu
 */
public class perso_Acnologia extends PersonnageBase {

    public perso_Acnologia() {
        this.nom    = "Acnologia";
        this.type   = "ChasseurDeDragon";
        this.role   = "DPS";
        this.rarete = "UR";
        this.niveau = 1;
        double mult = 3;
        this.vie     = 3000 * mult;
        this.attaque = 4500 * mult;
        this.defense = 1200 * mult;
        this.vitesse = 800 * mult;
        this.taux_critiques    = 0.20;
        this.degat_critiques   = 2.10;
        this.taux_precisions   = 115.00;
        this.taux_esquives     = 0.05;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.16;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
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
        System.out.println("Griffe du Dragon Noir — Inflige 400% ATK à un adversaire. et augmente son taux critique de 15% et ses degats critiques de 20%");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Souffle Ardent du Dragon — Inflige 350% ATK à tous les ennemis. inflige Marquage,Poison,Buff d'attaque de 20% et de vitesse de 15%");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Extinction Draconique — Inflige 450% ATK (bonus selon la Rage) à tous les ennemis et réduit leur défense de 25% pendant 2 tours. inflige brulure a 10% poison,et silence pendants 2 tours a deux adversaires aleatoires");
    }
}
