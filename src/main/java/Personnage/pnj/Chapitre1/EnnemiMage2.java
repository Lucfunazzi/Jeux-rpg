package Personnage.pnj.Chapitre1;

import Personnage.PersonnageBase;
import Combat.Combat;
import java.util.ArrayList;
import java.util.List;

public class EnnemiMage2 extends PersonnageBase {

    public EnnemiMage2() {
        this(2);
    }

    public EnnemiMage2(int niveau) {
        this.nom    = "Mage Ombral Ancien";
        this.niveau = niveau;
        this.type   = "Mage";
        this.role   = "DPS";
        this.rarete = "C";

        double niv = Math.pow(1.05, niveau - 1);
        double vit = Math.pow(1.03, niveau - 1);
        this.vie     = 220.0 * niv;
        this.attaque =  55.0 * niv;
        this.defense =  18.0 * niv;
        this.vitesse =  65.0 * vit;

        this.taux_critiques    = 0.10;
        this.degat_critiques   = 1.40;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.07;
        this.taux_blocage      = 0.02;
        this.reduction_blocage = 0.02;
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
        log.add(this.nom + " invoque un orbe de tenebres sur " + cible.getNom() + " !");
        double degats = this.getAttaque() * 1.50;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " declenche une tempete magique sur tous les ennemis !");
        for (PersonnageBase cible : equipeEnnemie) {
            if (cible.estVivant()) {
                double degats = this.getAttaque() * 0.60;
                Combat.appliquerDegatsAvecLog(this, cible, degats, log);
            }
        }
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Salve Magique", "Orbe de Tenebres", "Tempete Magique"};
    }
    @Override public void descriptionAttaqueBase() {
        System.out.println("Salve Magique : attaque de base sur la cible.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Orbe de Tenebres : inflige 150% ATK a la cible.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Tempete Magique : inflige 130% ATK a toute l'equipe ennemie.");
    }
}
