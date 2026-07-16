package Personnage.Naruto_Shippuden;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;
public class perso_Sasuke extends PersonnageBase {

    public perso_Sasuke() {
        this.nom = "Sasuke";
        this.type = "Ninja";
        this.role = "DPS";
        this.rarete = "S";
        this.niveau = 1;
        double multiplicateurRarete = 1.50;
        this.vie = 550 * multiplicateurRarete;
        this.attaque = 280 * multiplicateurRarete;
        this.defense = 70 * multiplicateurRarete;
        this.vitesse = 150 * multiplicateurRarete;
        this.taux_critiques = 0.15;
        this.degat_critiques = 1.40;
        this.taux_precisions = 105.00;
        this.taux_esquives = 0.08;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.10;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Kusanagi", "Katon boule de feu supreme", "Amaterasu"};
    }

 @Override
public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Sasuke attaque !");
    Combat.attaquer(this, cible, log);
}

@Override
public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Katon boule de feu supreme !");
    for (PersonnageBase ennemi : equipeEnnemie) {
        if (ennemi.estVivant()) {
            double degats = this.getAttaque() * 1.35;
            Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            Combat.appliquerEffet(this, ennemi, new Malediction(2, 0.10), log);
            ennemi.ajouterRage(-5);
        }
    }
}

@Override
public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Amaterasu !");
    double multiplicateurRage = 1.0;
    if (this.getRage() > 100) {
        multiplicateurRage += (this.getRage() - 100) / 100.0;
    }
    for (PersonnageBase ennemi : equipeEnnemie) {
        if (ennemi.estVivant()) {
            double degats = (this.getAttaque() * 1.50) * multiplicateurRage;
            Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            Combat.appliquerEffet(this, ennemi, new Brulure(3, 0.10), log);
            Combat.appliquerEffet(this, ennemi, new ReductionDefense(0.15, 3), log);
        }
    }
    this.ajouterRage(20);
    log.add(this.getNom() + " regagne 20 points de rage !");
    for (PersonnageBase allie : equipeAlliee) {
        if (allie.getNom().equals("Itachi") && allie.estVivant()) {
            allie.ajouterRage(20);
            log.add("Itachi gagne 20 points de rage (synergie Sasuke) !");
        }
    }
}

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Kusanagi : inflige 120% de degats a la cible.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Katon boule de feu supreme : inflige 135% de degats a tous les ennemis,\n"
                + "applique Malediction (soins reduits de 10%) pendant 2 tours,\n"
                + "et reduit la rage de 5 points pour chaque adversaire.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Amaterasu : inflige 150% de degats a tous les ennemis,\n"
                + "applique Brulure intense (10% PV/tour) pendant 3 tours,\n"
                + "reduit la defense des adversaires de 15% pendant 3 tours,\n"
                + "et redonne 20 points de rage a Sasuke (et a Itachi s'il est dans l'equipe).");
    }
}