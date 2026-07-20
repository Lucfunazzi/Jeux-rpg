package Personnage.pnj.Chapitre1;

import Personnage.PersonnageBase;
import Combat.Combat;
import java.util.ArrayList;
import java.util.List;

public class EnnemiMage4 extends PersonnageBase {

    public EnnemiMage4() {
        this(5);
    }

    public EnnemiMage4(int niveau) {
        this.nom    = "Sorcier Maudit";
        this.niveau = niveau;
        this.type="Elementaliste";
        this.role   = "DPS";
        this.rarete = "C";

        double niv = Math.pow(1.05, niveau - 1);
        double vit = Math.pow(1.03, niveau - 1);
        this.vie     = 230.0 * niv;
        this.attaque =  92.0 * niv;
        this.defense =  25.0 * niv;
        this.vitesse =  80.0 * vit;

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
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " declenche une pluie de maledictions sur tous les ennemis !");
        for (PersonnageBase cible : equipeEnnemie) {
            if (cible.estVivant()) {
                double degats = this.getAttaque() * 0.70;
                Combat.appliquerDegatsAvecLog(this, cible, degats, log);
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
        System.out.println("Explosion Arcanique : inflige 160% ATK a la cible.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Pluie de Maledictions : inflige 135% ATK a toute l'equipe ennemie.");
    }
}
