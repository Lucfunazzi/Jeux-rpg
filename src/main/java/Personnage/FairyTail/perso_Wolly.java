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
public class perso_Wolly extends PersonnageBase{
    public perso_Wolly(){
        this.nom = "Wolly";
        this.niveau = 1;
        this.type="Elementaliste";
        this.role = "DPS";
        this.rarete = "C";
        double multiplicateurRarete = 1.00;
        this.vie = 290 * multiplicateurRarete;
        this.attaque = 130 * multiplicateurRarete;
        this.defense = 65 * multiplicateurRarete;
        this.vitesse = 100 * multiplicateurRarete;
        this.taux_critiques = 0.15;
        this.degat_critiques = 1.30;
        this.taux_precisions = 110.00;
        this.taux_esquives = 0.05;
        this.taux_blocage = 0.03;
        this.reduction_blocage = 0.05;
        this.degats_renvoi = 0.80;
        initialiserVieMax();
    }
    
    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Pistolet bloc", "Pistolet des Polygones", "Pistolet multi Polygones"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Wolly utilise Pistolet Bloc!");
        boolean touche = Combat.attaqueTouche(this, cible);
        if (!touche) {
            log.add(cible.getNom() + " esquive !");
            this.ajouterRage(50);
            return;
        }
        double degats = this.getAttaque() * 1.00;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Wolly utilise Pistolet des Polygones!");
        double degats = this.getAttaque() * 1.50;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        

        
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Wolly utilise Pistolet multi Polygones !");
        
        
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = (this.getAttaque() * 1.20);
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            }
        }
    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Pistolet Bloc — inflige 100% ATK a une cible.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Pistolet des Polygones — inflige 150% ATK a une cible.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Pistolet multi polygone — inflige 120% ATK a tous les ennemis. ")
     ;
    }
}
