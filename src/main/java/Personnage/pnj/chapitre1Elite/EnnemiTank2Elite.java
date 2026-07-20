package Personnage.pnj.chapitre1Elite;

import Personnage.PersonnageBase;
import Combat.Combat;
import Effets.Paralysie;
import Effets.Trempe;
import java.util.ArrayList;
import java.util.List;

public class EnnemiTank2Elite extends PersonnageBase {

    public EnnemiTank2Elite() {
        this(15);
    }

    public EnnemiTank2Elite(int niveau) {
        this.nom    = "Titan des Abysses";
        this.niveau = niveau;
        this.type="Elementaliste";
        this.role   = "Tank";
        this.rarete = "C";

        double niv = Math.pow(1.05, niveau - 1);
        double vit = Math.pow(1.03, niveau - 1);
        this.vie     = 404.1 * niv;
        this.attaque = 101.0 * niv;
        this.defense =  90.9 * niv;
        this.vitesse =  72.7 * vit;

        this.taux_critiques    = 0.10;
        this.degat_critiques   = 1.20;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.08;
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
        if (Math.random() < 0.35) {
            Combat.appliquerEffet(this, cible, new Paralysie(1, 0.40), log);
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " provoque un raz-de-maree sur tous les ennemis !");
        for (PersonnageBase cible : equipeEnnemie) {
            if (cible.estVivant()) {
                double degats = this.getAttaque() * 1.30;
                Combat.appliquerDegatsAvecLog(this, cible, degats, log);
                Combat.appliquerEffet(this, cible, new Trempe(2), log);
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
        System.out.println("Broiement : inflige 150% ATK a la cible. 35% de chance de Paralysie 1 tour.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Raz-de-Maree : inflige 130% ATK a toute l'equipe ennemie et applique Trempe 2 tours.");
    }
}