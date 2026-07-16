package Personnage.pnj.chapitre1Elite;

import Personnage.PersonnageBase;
import Combat.Combat;
import Effets.Silence;
import Effets.Malediction;
import java.util.ArrayList;
import java.util.List;

public class EnnemiMage2Elite extends PersonnageBase {

    public EnnemiMage2Elite() {
        this(8);
    }

    public EnnemiMage2Elite(int niveau) {
        this.nom    = "Mage Ombral Ancien";
        this.niveau = niveau;
        this.type   = "Mage";
        this.role   = "DPS";
        this.rarete = "C";

        double niv = Math.pow(1.05, niveau - 1);
        double vit = Math.pow(1.03, niveau - 1);
        this.vie     = 227.4 * niv;
        this.attaque =  71.1 * niv;
        this.defense =  56.9 * niv;
        this.vitesse = 122.0 * vit;

        this.taux_critiques    = 0.30;
        this.degat_critiques   = 1.20;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.10;
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
        Combat.appliquerEffet(this, cible, new Silence(1), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " declenche une tempete magique sur tous les ennemis !");
        for (PersonnageBase cible : equipeEnnemie) {
            if (cible.estVivant()) {
                double degats = this.getAttaque() * 1.30;
                Combat.appliquerDegatsAvecLog(this, cible, degats, log);
                Combat.appliquerEffet(this, cible, new Malediction(2, 0.10), log);
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
        System.out.println("Orbe de Tenebres : inflige 150% ATK a la cible et applique Silence 1 tour.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Tempete Magique : inflige 130% ATK a toute l'equipe ennemie et applique Malediction 2 tours.");
    }
}