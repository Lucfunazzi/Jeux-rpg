/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Personnage.pnj.chapitre1Elite;

import Combat.Combat;
import Effets.Brulure;
import Effets.BuffAttaque;
import Effets.Etourdissement;
import Personnage.PersonnageBase;
import java.util.List;

/**
 *
 * @author Lucas
 */
public class EnnemiNatsuStage4 extends PersonnageBase {
    public  EnnemiNatsuStage4() {this(10);}
        
    public EnnemiNatsuStage4(int niveau){
     this.nom = "Natsu";
        this.type = "ChasseurDeDragon";
        this.role = "DPS";
        this.rarete = "A";
        this.niveau = niveau;
         double mult = 1.20;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 300.0 * mult * niv;
        this.attaque = 100.0 * mult * niv;
        this.defense = 45.0 * mult * niv;
        this.vitesse =  110.0 * mult * vit;

        this.taux_critiques    = 0.15;
        this.degat_critiques   = 1.30;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.14;
        this.taux_blocage      = 0.08;
        this.reduction_blocage = 0.10;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Coup de poings", "Poings d'acier du dragon de feu",
                "Lotus pourpre du dragon de feu"};
    }

    
    @Override
public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Natsu utilise Coup de poings !");
    boolean touche = Combat.attaquer(this, cible, log);
    if (!touche) {
        this.ajouterRage(50);
    }
    
}

@Override
public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Natsu utilise Poings d'acier du dragon de feu !");
    double degats = this.getAttaque() * 2.50;
    Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    if (Math.random() < 0.30) {
        Combat.appliquerEffet(this, cible, new Etourdissement(1), log);
    }
    Combat.appliquerEffet(this, cible, new Brulure(2, 0.10), log);
    Combat.appliquerEffet(this, new BuffAttaque(0.15, 2), log);
}

@Override
public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Natsu utilise Lotus pourpre du dragon de feu !");
    double multiplicateurRage = 1.0;
    if (this.getRage() > 100) {
        multiplicateurRage += (this.getRage() - 100) / 100.0;
    }
    for (PersonnageBase ennemi : equipeEnnemie) {
        if (ennemi.estVivant()) {
            double degats = (this.getAttaque() * 3.00) * multiplicateurRage;
            Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            Combat.appliquerEffet(this, ennemi, new Brulure(3, 0.12), log);
        }
    }
}

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Coup de poings — inflige 100% ATK a une cible, "
                + "applique Brulure legere (5% PV) pendant 1 tour si elle touche.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Poings d'acier du dragon de feu — inflige 130% ATK a une cible, "
                + "30% de chance d'etourdir pendant 1 tour, "
                + "applique Brulure (10% PV/tour) pendant 2 tours, "
                + "gagne 15% d'attaque pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Lotus pourpre du dragon de feu — inflige 160% ATK a tous les ennemis "
                + "et applique Brulure intense (12% PV/tour) pendant 3 tours.");
    }
}
