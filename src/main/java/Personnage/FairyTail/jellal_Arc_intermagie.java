/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Personnage.FairyTail;
import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;
/**
 *
 * @author Lucas
 */
public class jellal_Arc_intermagie extends PersonnageBase {
    public jellal_Arc_intermagie(){
     this.nom="Jellal";
         this.type="Elementaliste";
        this.role = "DPS";
        this.rarete = "SS";
        this.niveau = 1;
        double multiplicateurRarete = 1.75;
        this.vie = 730 * multiplicateurRarete;      
        this.attaque = 340 * multiplicateurRarete;    
        this.defense = 170 * multiplicateurRarete;   
        this.vitesse = 225 * multiplicateurRarete;
        this.taux_critiques = 0.15;
        this.degat_critiques = 1.50;
        this.taux_precisions = 105.00;
        this.taux_esquives = 0.05;
        this.taux_blocage = 0.05;
        this.reduction_blocage = 0.10;
        this.degats_renvoi = 0.80;
        initialiserVieMax();
    }
    
     @Override
    public String[] getNomsAttaques() {
        return new String[]{"Rayon Célestes", "Grande Ourse", "Altairis"};
    }

    
    @Override
public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Jellal utilise Rayon Célestes !");
    boolean touche = Combat.attaquer(this, cible, log);
    if (!touche) {
        this.ajouterRage(50);
    }
}

@Override
public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Jellal utilise Grande Chariot !");
    for (PersonnageBase ennemi : equipeEnnemie) {
        if (ennemi.estVivant()) {
            double degats = this.getAttaque() * 1.80;
            Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            Combat.appliquerEffet(this, ennemi, new Ralentissement(2, 0.15), log);
            Combat.appliquerEffet(ennemi, new Marquage(2,0.30), log);
        }
    }
    Combat.appliquerEffet(this, new BuffPrecision(0.30,2), log);
    
}

@Override
public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Jellal utilise Altairis !");
    double multiplicateurRage = 1.0;
    if (this.getRage() > 100) {
        multiplicateurRage += (this.getRage() - 100) / 100.0;
    }
    for (PersonnageBase ennemi : equipeEnnemie) {
        if (ennemi.estVivant()) {
            double degats = (this.getAttaque() * 2.50) * multiplicateurRage;
            Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            Combat.appliquerEffet(ennemi, new Poison(2,0.05), log);
            
        }
    }
    Combat.appliquerEffet(this, new BuffAttaque(0.15,2), log);
    Combat.appliquerEffet(this, new BuffTauxCritique(0.15,2), log);
}

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Rayon Célestes — inflige 100% ATK a une cible.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Grand chariot — inflige 180% ATK a tous les ennemis, "
                + "applique Ralentissement (gain de rage réduit de 15%)  pendant 2 tours, "
                + "et inflige marquage de 30% pendants 2 tours ."
                + "et à un bonus de précisions de 30%");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Altairis — inflige 250% ATK a tous les ennemis et inflige poison à l'ennemi (5%) pendants 2 tours, "
                + "Jellal reçoit un bonus d'attaque de 15% et un bonus de taux critiques de 15%"
                + " pendant 2 tours. ");
    }
}
