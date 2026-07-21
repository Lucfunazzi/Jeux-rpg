package Personnage.pnj.Chapitre1;

import Personnage.PersonnageBase;
import Combat.Combat;
import java.util.ArrayList;
import java.util.List;

public class EnnemiMage11 extends PersonnageBase {

    public EnnemiMage11() {
        this(5);
    }

    public EnnemiMage11(int niveau) {
        this.nom    = "Chasseur de Primes";
        this.niveau = niveau;
        this.type="Elementaliste";
        this.role   = "DPS";
        this.rarete = "C";

        double niv = Math.pow(1.05, niveau - 1);
        double vit = Math.pow(1.03, niveau - 1);
        this.vie     = 250.0 * niv;
        this.attaque =  95.0 * niv;
        this.defense =  38.0 * niv;
        this.vitesse =  90.0 * vit;

        this.taux_critiques    = 0.15;
        this.degat_critiques   = 1.45;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.08;
        this.taux_blocage      = 0.02;
        this.reduction_blocage = 0.02;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " decoche un tir fatal sur " + cible.getNom() + " !");
        double degats = this.getAttaque() * 1.60;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " arrose toute l'equipe ennemie !");
        for (PersonnageBase cible : equipeEnnemie) {
            if (cible.estVivant()) {
                double degats = this.getAttaque() * 0.70;
                Combat.appliquerDegatsAvecLog(this, cible, degats, log);
            }
        }
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Tir Precis", "Tir Fatal", "Tir en Rafale"};
    }
    @Override public void descriptionAttaqueBase() {
        System.out.println("Tir Precis : attaque de base sur la cible.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Tir Fatal : inflige 160% ATK a la cible.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Tir en Rafale : inflige 130% ATK a toute l'equipe ennemie.");
    }
}
