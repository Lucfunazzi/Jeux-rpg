/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Personnage.DragonBallZ;

import Combat.Combat;
import Effets.Saignement;
import Effets.ReductionDefense;
import Effets.BuffDefense;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Lucas
 */
public class perso_Krillin extends PersonnageBase {
    
    
    public perso_Krillin(){
        this.nom = "Krillin";
        this.type = "Guerrier";
        this.role = "Support";
        this.rarete = "B";
        this.niveau = 1;
        double multiplicateurRarete = 1.30;
        this.vie = 500 * multiplicateurRarete;
        this.attaque = 110 * multiplicateurRarete;
        this.defense = 100 * multiplicateurRarete;
        this.vitesse = 130 * multiplicateurRarete;
        this.taux_critiques = 0.08;
        this.degat_critiques = 1.15;
        this.taux_precisions = 105.00;
        this.taux_esquives = 0.08;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.10;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }
    
     @Override
    public String[] getNomsAttaques() {
        return new String[]{"Coup de pieds", "Kienzan", "Super kienzan"};
    }

    
   @Override
public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Krillin attaque !");
    Combat.attaquer(this, cible, log);
}

@Override
public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Krillin lance son Kienzan !");
    PersonnageBase cibleKienzan = null;
    for (PersonnageBase ennemi : equipeEnnemie) {
        if (ennemi.estVivant()) {
            if (cibleKienzan == null || ennemi.getDefense() > cibleKienzan.getDefense())
                cibleKienzan = ennemi;
        }
    }
    if (cibleKienzan == null) return;
    double degats = this.getAttaque() * 1.10;
    Combat.appliquerDegatsAvecLog(this, cibleKienzan, degats, log);
    Combat.appliquerEffet(this, cibleKienzan, new ReductionDefense(0.10, 2), log);
}

@Override
public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Krillin utilise Super Kienzan !");
    double multiplicateurRage = 1.0;
    if (this.getRage() > 100) {
        multiplicateurRage += (this.getRage() - 100) / 100.0;
    }
    for (PersonnageBase ennemi : equipeEnnemie) {
        if (ennemi.estVivant()) {
            double degats = (this.getAttaque() * 1.10) * multiplicateurRage;
            Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            Combat.appliquerEffet(this, ennemi, new Saignement(2, 0.08), log);
        }
    }
    for (PersonnageBase allie : equipeAlliee) {
        if (allie.estVivant()) {
            Combat.appliquerEffet(this, allie, new BuffDefense(0.10, 2), log);
        }
    }
}
    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Coup de pieds — inflige 100% ATK à une cible.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Kienzan — inflige 105% ATK à tous les DPS ennemis, "
                + "leur applique Saignement pendant 2 tours "
                + "et restaure 50 rage à Krillin.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Super Kienzan — inflige 110% ATK à tous les ennemis, "
                + " inflige saignement aux dps ennemies pendants 2 tours "
                + "et reduit leurs defense de 10% pendants 2 tours.");
    }
    
}
