package Personnage.pnj.chapitre1Elite;

import Personnage.PersonnageBase;
import Combat.Combat;
import Effets.Saignement;
import Effets.Marquage;
import java.util.ArrayList;
import java.util.List;

public class EnnemiGuerrier4Elite extends PersonnageBase {

    public EnnemiGuerrier4Elite() {
        this(7);
    }

    public EnnemiGuerrier4Elite(int niveau) {
        this.nom    = "Chasseur de Primes";
        this.niveau = niveau;
        this.type="Elementaliste";
        this.role   = "DPS";
        this.rarete = "C";

        double niv = Math.pow(1.05, niveau - 1);
        double vit = Math.pow(1.03, niveau - 1);
        this.vie     = 447.7 * niv;
        this.attaque = 186.6 * niv;
        this.defense =  82.1 * niv;
        this.vitesse =  83.7 * vit;

        this.taux_critiques    = 0.25;
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
        log.add(this.nom + " attaque " + cible.getNom() + " avec precision !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " decoche un tir fatal sur " + cible.getNom() + " !");
        double degats = this.getAttaque() * 1.60;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new Saignement(2, 0.05), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " arrose toute l'equipe ennemie !");
        for (PersonnageBase cible : equipeEnnemie) {
            if (cible.estVivant()) {
                double degats = this.getAttaque() * 1.30;
                Combat.appliquerDegatsAvecLog(this, cible, degats, log);
                Combat.appliquerEffet(this, cible, new Marquage(2, 0.10), log);
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
        System.out.println("Tir Fatal : inflige 160% ATK a la cible et applique Saignement (5% PV max/tour) 2 tours.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Tir en Rafale : inflige 130% ATK a toute l'equipe ennemie et applique Marquage 2 tours.");
    }
}