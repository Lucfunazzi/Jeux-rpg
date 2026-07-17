package Personnage.pnj.Chapitre1;

import Personnage.PersonnageBase;
import Combat.Combat;
import java.util.ArrayList;
import java.util.List;

public class EnnemiMage1 extends PersonnageBase {

    public EnnemiMage1() {
        this(1);
    }

    public EnnemiMage1(int niveau) {
        this.nom    = "Mage Ombral";
        this.niveau = niveau;
        this.type   = "Mage";
        this.role   = "DPS";
        this.rarete = "C";

        double niv = Math.pow(1.05, niveau - 1);
        double vit = Math.pow(1.03, niveau - 1);
        this.vie     = 180.0 * niv;
        this.attaque =  45.0 * niv;
        this.defense =  35.0 * niv;
        this.vitesse =  55.0 * vit;

        this.taux_critiques    = 0.05;
        this.degat_critiques   = 1.30;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.05;
        this.taux_blocage      = 0.02;
        this.reduction_blocage = 0.02;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " lance un projectile magique sur " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " incante un sort sombre sur " + cible.getNom() + " !");
        double degats = this.getAttaque() * 1.30;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " declenche une onde magique sur tous les ennemis !");
        for (PersonnageBase cible : equipeEnnemie) {
            if (cible.estVivant()) {
                double degats = this.getAttaque() * 0.60;
                Combat.appliquerDegatsAvecLog(this, cible, degats, log);
            }
        }
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Projectile Magique", "Sort Sombre", "Onde Magique"};
    }
    @Override public void descriptionAttaqueBase() {
        System.out.println("Projectile Magique : attaque de base magique sur la cible.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Sort Sombre : inflige 130% ATK a la cible.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Onde Magique : inflige 110% ATK a toute l'equipe ennemie.");
    }
}
