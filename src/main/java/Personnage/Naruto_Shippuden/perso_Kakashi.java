package Personnage.Naruto_Shippuden;
import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;

public class perso_Kakashi extends PersonnageBase {

    public perso_Kakashi() {
        this.nom = "Kakashi";
        this.type = "Ninja";
        this.role = "DPS";
        this.rarete = "S";
        this.niveau = 1;
        double multiplicateurRarete = 1.50;
        this.vie = 650 * multiplicateurRarete;
        this.attaque = 240 * multiplicateurRarete;
        this.defense = 90 * multiplicateurRarete;
        this.vitesse = 150 * multiplicateurRarete;
        this.taux_critiques = 0.25;
        this.degat_critiques = 1.25;
        this.taux_precisions = 105.00;
        this.taux_esquives = 0.12;
        this.taux_blocage = 0.10;
        this.reduction_blocage = 0.10;
        this.degats_renvoi = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Chidori", "Katon boule de feu supreme", "Kamui"};
    }

   
    @Override
public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Kakashi lance Chidori !");
    boolean touche = Combat.attaqueTouche(this, cible);
    if (!touche) {
        log.add(cible.getNom() + " esquive !");
        this.ajouterRage(50);
        return;
    }
    Combat.attaquer(this, cible, log);
    if (Math.random() < 0.20) {
        Combat.appliquerEffet(this, cible, new Paralysie(1, 0.30), log);
    }
}

@Override
public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Kakashi utilise Katon boule de feu supreme !");
    for (PersonnageBase ennemi : equipeEnnemie) {
        if (ennemi.estVivant()) {
            double degats = this.getAttaque() * 1.40;
            Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            Combat.appliquerEffet(this, ennemi, new Brulure(2, 0.10), log);
        }
    }
}

@Override
public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Kakashi utilise Kamui !");
    double multiplicateurRage = 1.0;
    if (this.getRage() > 100) {
        multiplicateurRage += (this.getRage() - 100) / 100.0;
    }
    PersonnageBase ciblePrincipale = null;
    for (PersonnageBase ennemi : equipeEnnemie) {
        if (ennemi.estVivant()) {
            if (ciblePrincipale == null || ennemi.getAttaque() > ciblePrincipale.getAttaque())
                ciblePrincipale = ennemi;
        }
    }
    if (ciblePrincipale != null) {
        double degats = (this.getAttaque() * 1.70) * multiplicateurRage;
        Combat.appliquerDegatsAvecLog(this, ciblePrincipale, degats, log);
        Combat.appliquerEffet(this, ciblePrincipale, new Sommeil(2), log);
    }
    for (PersonnageBase ennemi : equipeEnnemie) {
        if (ennemi.estVivant()) {
            Combat.appliquerEffet(this, ennemi, new ReductionVitesse(0.20, 2), log);
        }
    }
    this.ajouterRage(20);
    log.add(this.getNom() + " regagne 20 points de rage !");
}
    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Chidori — inflige 100% ATK a une cible, "
                + "20% de chance de paralyser pendant 1 tour.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Katon boule de feu supreme — inflige 140% ATK a tous les ennemis "
                + "et applique Brulure (10% PV/tour) pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Kamui — inflige 170% ATK a l'ennemi avec la plus haute attaque "
                + "et l'endort pendant 2 tours. "
                + "Reduit la vitesse de tous les ennemis de 20% pendant 2 tours. "
                + "Kakashi regagne 20 points de rage.");
    }
}