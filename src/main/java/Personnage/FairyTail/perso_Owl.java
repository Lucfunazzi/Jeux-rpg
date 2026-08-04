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
public class perso_Owl extends PersonnageBase {
     public perso_Owl() {
        this.nom = "Owl";
        this.type = "ChasseurDeDragon";
        this.role = "DPS";
        this.rarete = "A";
        this.niveau = 1;
        double multiplicateurRarete = 1.40;
        this.vie = 550 * multiplicateurRarete;
        this.attaque = 205 * multiplicateurRarete;
        this.defense = 90 * multiplicateurRarete;
        this.vitesse = 105 * multiplicateurRarete;
        this.taux_critiques = 0.10;
        this.degat_critiques = 1.30;
        this.taux_precisions = 105.00;
        this.taux_esquives = 0.08;
        this.taux_blocage = 0.02;
        this.reduction_blocage = 0.02;
        this.degats_renvoi = 0.80;
        initialiserVieMax();
     }
     
     @Override
    public String[] getNomsAttaques() {
        return new String[]{"Fire owl", "Mise à feu",
                "Missiles HOU HOU "};
    }
 @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Owl utilise Fire owl sur " + cible.getNom());
        double degats = this.getAttaque() * 1.00;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    }
    
       @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Owl utilise Mise à feu " + cible.getNom());
       double degats = this.getAttaque() *1.80;
       Combat.appliquerDegatsAvecLog(this, cible, degats, log);
       Combat.appliquerEffet(this, new BuffAttaque(0.20, 2), log);
           
        }
    
     @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Owl utilise Missiles HOU HOU  !");

        PersonnageBase cible = Combat.choisirCible(this, equipeEnnemie);
        if (cible == null) return; // sécurité : tous KO

    double degats = this.getAttaque() * 2.00;
    Combat.appliquerDegatsAvecLog(this, cible, degats, log);


    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Fire owl — Inflige 100% ATK à un ennemi");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Mise à feu — Inflige 180% ATK à un ennemi, augmente son attaque  de 20% pendants 2 tours.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Missiles HOU HOU— Inflige 200% ATK à un ennemi. ");
    }
}
