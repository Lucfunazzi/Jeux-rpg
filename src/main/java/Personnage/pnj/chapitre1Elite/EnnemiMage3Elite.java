package Personnage.pnj.Chapitre1Elite;

import Personnage.PersonnageBase;
import Combat.Combat;
import Effets.ReductionDefense;
import Effets.BuffAttaque;
import java.util.ArrayList;
import java.util.List;

public class EnnemiMage3Elite extends PersonnageBase {

    public EnnemiMage3Elite() {
        this(10);
    }

    public EnnemiMage3Elite(int niveau) {
        this.nom    = "Mage Ombral Majeur [ELITE]";
        this.niveau = niveau;
        this.type   = "Mage";
        this.role   = "DPS";
        this.rarete = "B";

        double niv = Math.pow(1.05, niveau - 1);
        double vit = Math.pow(1.03, niveau - 1);
        this.vie     = 412.5 * niv;
        this.attaque =  83.8 * niv;
        this.defense =  28.4 * niv;
        this.vitesse = 110.4 * vit;

        this.taux_critiques    = 0.20;
        this.degat_critiques   = 1.70;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.12;
        this.taux_blocage      = 0.04;
        this.reduction_blocage = 0.04;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " lance une salve magique sur " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " invoque un orbe maudit sur " + cible.getNom() + " !");
        double degats = this.getAttaque() * 1.50;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new ReductionDefense(0.25, 2), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " declenche une tempete des tenebres sur tous les ennemis !");
        Combat.appliquerEffet(this, new BuffAttaque(0.30, 2), log);
        for (PersonnageBase cible : equipeEnnemie) {
            if (cible.estVivant()) {
                double degats = this.getAttaque() * 1.30;
                Combat.appliquerDegatsAvecLog(this, cible, degats, log);
            }
        }
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Salve Magique", "Orbe Maudit", "Tempete des Tenebres"};
    }
    @Override public void descriptionAttaqueBase() {
        System.out.println("Salve Magique : attaque de base sur la cible.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Orbe Maudit : inflige 150% ATK a la cible et reduit sa DEF de 25% pendant 2 tours.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Tempete des Tenebres : augmente sa propre ATK de 30% pendant 2 tours, puis inflige 130% ATK a toute l'equipe ennemie.");
    }
}