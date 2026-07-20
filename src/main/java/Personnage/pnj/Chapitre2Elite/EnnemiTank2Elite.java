package Personnage.pnj.Chapitre2Elite;

import Personnage.pnj.Chapitre1.*;
import Personnage.PersonnageBase;
import Combat.Combat;
import java.util.ArrayList;
import java.util.List;

public class EnnemiTank2Elite extends PersonnageBase {

    public EnnemiTank2Elite() {
        this(18);
    }

    public EnnemiTank2Elite(int niveau) {
        this.nom    = "Titan des Abysses";
        this.niveau = niveau;
        this.type="Elementaliste";
        this.role   = "Tank";
        this.rarete = "C";

        double niv = Math.pow(1.05, niveau - 1);
        double vit = Math.pow(1.03, niveau - 1);
        this.vie     = 600.0 * niv;
        this.attaque =  72.0 * niv;
        this.defense = 130.0 * niv;
        this.vitesse =  42.0 * vit;

        this.taux_critiques    = 0.05;
        this.degat_critiques   = 1.10;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.04;
        this.taux_blocage      = 0.28;
        this.reduction_blocage = 0.35;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " ecrase " + cible.getNom() + " sous son poids !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " broie " + cible.getNom() + " !");
        double degats = this.getAttaque() * 1.50;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " provoque un raz-de-maree sur tous les ennemis !");
        for (PersonnageBase cible : equipeEnnemie) {
            if (cible.estVivant()) {
                double degats = this.getAttaque() * 1.30;
                Combat.appliquerDegatsAvecLog(this, cible, degats, log);
            }
        }
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Ecrasement", "Broiement", "Raz-de-Maree"};
    }
    @Override public void descriptionAttaqueBase() {
        System.out.println("Ecrasement : attaque de base sur la cible.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Broiement : inflige 150% ATK a la cible.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Raz-de-Maree : inflige 130% ATK a toute l'equipe ennemie.");
    }
}
