package Personnage.pnj.chapitre1Elite;

import Combat.Combat;
import Personnage.PersonnageBase;
import Effets.Brulure;
import Effets.Malediction;
import java.util.ArrayList;
import java.util.List;

public class EnnemiMage4Elite extends PersonnageBase {

    public EnnemiMage4Elite() {
        this(12);
    }

    public EnnemiMage4Elite(int niveau) {
        this.nom    = "Sorcier Maudit";
        this.niveau = niveau;
        this.type="Elementaliste";
        this.role   = "DPS";
        this.rarete = "C";

        double niv = Math.pow(1.05, niveau - 1);
        double vit = Math.pow(1.03, niveau - 1);
        this.vie     = 380.0 * niv;
        this.attaque = 116.9 * niv;
        this.defense =  52.6 * niv;
        this.vitesse =  86.7 * vit;

        this.taux_critiques    = 0.12;
        this.degat_critiques   = 1.50;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.06;
        this.taux_blocage      = 0.02;
        this.reduction_blocage = 0.02;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " lance un eclair maudit sur " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " invoque une explosion arcanique sur " + cible.getNom() + " !");
        double degats = this.getAttaque() * 1.60;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new Brulure(2, 0.08), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " declenche une pluie de maledictions sur tous les ennemis !");
        for (PersonnageBase cible : equipeEnnemie) {
            if (cible.estVivant()) {
                double degats = this.getAttaque() * 1.35;
                Combat.appliquerDegatsAvecLog(this, cible, degats, log);
                Combat.appliquerEffet(this, cible, new Malediction(2, 0.10), log);
            }
        }
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Eclair Maudit", "Explosion Arcanique", "Pluie de Maledictions"};
    }
    @Override public void descriptionAttaqueBase() {
        System.out.println("Eclair Maudit : attaque de base sur la cible.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Explosion Arcanique : inflige 160% ATK a la cible et applique Brulure (8% PV/tour) 2 tours.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Pluie de Maledictions : inflige 135% ATK a toute l'equipe ennemie et applique Malediction 2 tours.");
    }
}