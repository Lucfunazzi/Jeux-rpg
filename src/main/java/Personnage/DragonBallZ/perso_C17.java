package Personnage.DragonBallZ;
import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;

public class perso_C17 extends PersonnageBase {

    public perso_C17() {
        this.nom = "C-17";
        this.type = "Guerrier";
        this.role = "DPS";
        this.rarete = "S";
        this.niveau = 1;
        double multiplicateurRarete = 1.50;
        this.vie = 600 * multiplicateurRarete;
        this.attaque = 200 * multiplicateurRarete;
        this.defense = 120 * multiplicateurRarete;
        this.vitesse = 110 * multiplicateurRarete;
        this.taux_critiques = 0.25;
        this.degat_critiques = 1.35;
        this.taux_precisions = 100.00;
        this.taux_esquives = 0.05;
        this.taux_blocage = 0.05;
        this.reduction_blocage = 0.10;
        this.degats_renvoi = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Kikoha", "Boule d'energie a pleine puissance", "Flash Photonique"};
    }

    
    @Override
public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("C-17 utilise Kikoha !");
    Combat.attaquer(this, cible, log);
}

@Override
public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("C-17 utilise Boule d'energie a pleine puissance !");
    double degats = this.getAttaque() * 1.60;
    boolean auMoinsUneCible = false;
    for (PersonnageBase ennemi : equipeEnnemie) {
        if (ennemi.estVivant() && ennemi.getRole().equalsIgnoreCase("DPS")) {
            Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            Combat.appliquerEffet(this, ennemi, new Marquage(2, 0.20), log);
            auMoinsUneCible = true;
        }
    }
    if (!auMoinsUneCible) {
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                Combat.appliquerEffet(this, ennemi, new Marquage(2, 0.20), log);
            }
        }
    }
    this.ajouterRage(30);
    log.add(this.getNom() + " gagne 30 points de rage !");
    for (PersonnageBase allie : equipeAlliee) {
        if (allie.getNom().equals("C-18") && allie.estVivant()) {
            allie.ajouterRage(20);
            log.add("C-18 gagne 20 points de rage (synergie) !");
        }
    }
}

@Override
public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("C-17 utilise Flash Photonique !");
    double multiplicateurRage = 1.0;
    if (this.getRage() > 100) {
        multiplicateurRage += (this.getRage() - 100) / 100.0;
    }
    for (PersonnageBase ennemi : equipeEnnemie) {
        if (ennemi.estVivant()) {
            double degats = (this.getAttaque() * 1.60) * multiplicateurRage;
            Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            if (ennemi.getRole().equalsIgnoreCase("DPS")) {
                Combat.appliquerEffet(this, ennemi, new ReductionVitesse(0.15, 2), log);
            }
        }
    }
    for (PersonnageBase allie : equipeAlliee) {
        if (allie.estVivant() && allie.getRole().equalsIgnoreCase("DPS")) {
            Combat.appliquerEffet(this, allie, new BuffAttaque(0.20, 2), log);
        }
    }
}

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Kikoha — inflige 100% ATK a une cible.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Boule d'energie a pleine puissance — inflige 160% ATK a tous les DPS ennemis "
                + "(tous les ennemis si aucun DPS), applique Marquage 2 tours "
                + "et gagne 30 rage. Donne 20 rage a C-18 si elle est alliee.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Flash Photonique — inflige 160% ATK a tous les ennemis, "
                + "ralentit les DPS ennemis de 15% pendant 2 tours "
                + "et donne 20% d'attaque a tous les DPS allies pendant 2 tours.");
    }
}