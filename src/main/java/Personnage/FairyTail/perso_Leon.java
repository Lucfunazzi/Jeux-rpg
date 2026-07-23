package Personnage.FairyTail;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Leon Bastia — Chevalier, rang B.
 * Magie Ice-Make Statique : crée des créatures de glace animées autonomes.
 * Rival de Gray, cherchait à ressusciter Deliora. Finalement allié de Fairy Tail.
 */
public class perso_Leon extends PersonnageBase {

    public perso_Leon() {
        this.nom    = "Leon";
        this.type   = "Chevalier";
        this.role   = "DPS";
        this.rarete = "B";
        this.niveau = 1;
        double mult = 1.30;
        this.vie     = 380 * mult;
        this.attaque = 170 * mult;
        this.defense = 105 * mult;
        this.vitesse = 130 * mult;
        this.taux_critiques    = 0.15;
        this.degat_critiques   = 1.35;
        this.taux_precisions   = 105.00;
        this.taux_esquives     = 0.05;
        this.taux_blocage      = 0.06;
        this.reduction_blocage = 0.08;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Ice-Make : Oiseau de Glace", "Ice-Make : Lion de Glace", "Ice-Make : Tigre Polaire"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Leon façonne un oiseau de glace qui fond sur " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
        
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Leon invoque un lion de glace qui lacère " + cible.getNom() + " !");
        double degats = this.getAttaque() * 1.50;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        
        if (Math.random() < 0.35) {
            Combat.appliquerEffet(this, cible, new Gel(1), log);
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Leon libère un tigre polaire colossal — la créature de glace s'abat sur toute l'équipe ennemie !");
        for (PersonnageBase cible : equipeEnnemie) {
            if (cible.estVivant()) {
                double degats = this.getAttaque() * 1.10;
                Combat.appliquerDegatsAvecLog(this, cible, degats, log);
                Combat.appliquerEffet(this, cible, new ReductionVitesse(0.10, 2), log);
                if (Math.random() < 0.25) {
                    Combat.appliquerEffet(this, cible, new Gel(1), log);
                }
            }
        }
       
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Ice-Make : Oiseau de Glace — Inflige 100% ATK.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Ice-Make : Lion de Glace — Inflige 150% ATK, et 35% de gel l'ennemi pendant 1 tour.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Ice-Make : Tigre Polaire — Inflige 110% ATK à toute l'équipe ennemie, réduit la VIT de 10% "
                + "pendant 2 tours, et 25% de chance de geler chaque ennemi pendant 1 tour.");
    }
}
