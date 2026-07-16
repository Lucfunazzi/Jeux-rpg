package Personnage.pnj.Chapitre2Elite;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;

public class EnnemiHakuElite extends PersonnageBase {

    public EnnemiHakuElite() {
        this(25);
    }

    public EnnemiHakuElite(int niveau) {
        this.nom    = "Haku";
        this.niveau = niveau;
        this.type   = "Ninja";
        this.role   = "Support";
        this.rarete = "B";

        double mult = 1.40;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 200.6 * mult * niv;
        this.attaque =  70.6 * mult * niv;
        this.defense =  55.9 * mult * niv;
        this.vitesse =  120.6 * mult * vit;

        this.taux_critiques    = 0.10;
        this.degat_critiques   = 1.30;
        this.taux_precisions   = 110.00;
        this.taux_esquives     = 0.15;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.10;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Aiguilles Senbon", "Miroirs de Glace Demoniaques", "Averses de Pointes de Glace"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.getNom() + " lance des aiguilles Senbon sur les points vitaux de " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.getNom() + " erige les Miroirs de Glace Demoniaques !");
        for (PersonnageBase allie : equipeAlliee) {
            if (allie.estVivant()) {
                Combat.appliquerEffet(this, allie, new BuffVitesse(0.20, 2), log);
            }
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.getNom() + " dechaîne une pluie d'aiguilles glaciales depuis ses miroirs !");
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = this.getAttaque() * 0.80;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                if (Math.random() < 0.40) {
                    Combat.appliquerEffet(this, ennemi, new Gel(2), log);
                } else {
                    Combat.appliquerEffet(this, ennemi, new ReductionVitesse(0.15, 2), log);
                }
            }
        }
    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Aiguilles Senbon — Inflige 100% ATK a une cible.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Miroirs de Glace Demoniaques — Augmente la Vitesse de tous les allies de 20% pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Averses de Pointes de Glace — Inflige 80% ATK a tous les ennemis, 40% de chance de Gel 2 tours, sinon ReductionVitesse 15% 2 tours.");
    }
}