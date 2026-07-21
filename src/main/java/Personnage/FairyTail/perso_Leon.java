package Personnage.FairyTail;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Lyon Bastia — Chevalier, rang B.
 * Magie Ice-Make Statique : crée des créatures de glace animées autonomes.
 * Rival de Gray, cherchait à ressusciter Deliora. Finalement allié de Fairy Tail.
 */
public class perso_Leon extends PersonnageBase {

    public perso_Leon() {
        this.nom    = "Lyon";
        this.type   = "Chevalier";
        this.role   = "DPS";
        this.rarete = "B";
        this.niveau = 1;
        double mult = 1.30;
        this.vie     = 360 * mult;
        this.attaque = 150 * mult;
        this.defense = 100 * mult;
        this.vitesse = 120 * mult;
        this.taux_critiques    = 0.18;
        this.degat_critiques   = 1.35;
        this.taux_precisions   = 105.00;
        this.taux_esquives     = 0.10;
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
        log.add("Lyon façonne un oiseau de glace qui fond sur " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Lyon invoque un lion de glace qui lacère " + cible.getNom() + " !");
        double degats = this.getAttaque() * 1.50;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new ReductionAttaque(0.20, 2), log);
        if (Math.random() < 0.35) {
            Combat.appliquerEffet(this, cible, new Gel(1), log);
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Lyon libère un tigre polaire colossal qui dévaste toute l'équipe ennemie !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) {
            multiplicateurRage += (this.getRage() - 100) / 100.0;
        }
        for (PersonnageBase cible : equipeEnnemie) {
            if (cible.estVivant()) {
                double degats = (this.getAttaque() * 1.10) * multiplicateurRage;
                Combat.appliquerDegatsAvecLog(this, cible, degats, log);
                Combat.appliquerEffet(this, cible, new ReductionVitesse(0.25, 2), log);
                if (Math.random() < 0.25) {
                    Combat.appliquerEffet(this, cible, new Gel(1), log);
                }
            }
        }
        Combat.appliquerEffet(this, new BuffDefense(0.15, 2), log);
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Ice-Make : Oiseau de Glace — Inflige 100% ATK.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Ice-Make : Lion de Glace — Inflige 150% ATK, réduit ATK de 20%, 35% de gel 1 tour.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Ice-Make : Tigre Polaire — Inflige 110% ATK à tous (x rage), réduit VIT de 25%, 25% de gel, +15% DEF.");
    }
}
