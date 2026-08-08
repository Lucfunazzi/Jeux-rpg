/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Personnage.FairyTail;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author Lucas
 */
public class perso_Lucy_intermagie extends PersonnageBase{
     public perso_Lucy_intermagie() {
        this.nom    = "Lucy intermagie";
        this.type="Invocateur";
        this.role   = "Support";
        this.rarete = "S";
        this.niveau = 1;
        double multiplicateurRarete = 1.73;
        this.vie     = 850 * multiplicateurRarete;
        this.attaque = 210 * multiplicateurRarete;
        this.defense = 140  * multiplicateurRarete;
        this.vitesse = 220 * multiplicateurRarete;
        this.taux_critiques    = 0.10;
        this.degat_critiques   = 1.20;
        this.taux_precisions   = 105.00;
        this.taux_esquives     = 0.08;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.10;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }
     
     @Override
    public String[] getNomsAttaques() {
        return new String[]{"Invocation Sagittarius et Scorpion", "Invocation Aries et Leo", "Uranos Metria"};
    }
    
    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Lucy Invoque Sagittarius et Scorpion sur " + cible.getNom() + " !");
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = this.getAttaque() * 0.70;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                if ( Math.random() < 0.30 ){
                    Combat.appliquerEffet(ennemi, new Saignement(2,0.06), log);
                    Combat.appliquerEffet(ennemi, new Aveuglement(0.20,2), log);
                }
                    
            }
        }
    }
    
    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Lucy invoque aries et leo " + cible.getNom());
        for(PersonnageBase ennemi : equipeEnnemie){
            if (ennemi.estVivant()){
                double degats = this.getAttaque() *0.80;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                        
            }
        }
                
        for (PersonnageBase al : equipeAlliee) if (al.estVivant()){
            al.ajouterRage(25);
            Combat.appliquerEffet(al, new BuffBlocage(0.10,2), log);
        }
    }
    
    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Lucy utilise urano metria!");
        double degatsTotaux = 0;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = this.getAttaque() * 1.20;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                Combat.appliquerEffet(ennemi, new Ralentissement(2,0.20), log);
                
            }
        }
        
        Combat.appliquerEffet(this, new Bouclier(this.getVieMax() * 0.20), log);
        
        
    }
    
     @Override
    public void descriptionAttaqueBase() {
        System.out.println("Inovaction Sagittarius et Scorpion — inflige 70% ATK à l'équipe ennemi"
                + " à 30% de chance d'infliger saignement et aveuglement pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Invocation aries et leo — attaque à 80% l'équipe ennemie , ajoute 25 de rage à l'équipe "
                + "et donne un buff de blocage de 10% pendants 2 tours à l'équipe.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Urano Metria — attaque tous les ennemis à 120% ATK, "
                + "inflige Ralentissement de 20% 2 tours. "
                + "s'offre un bouclier de 20% de ses pv max ");
                
    }
    
    
}
