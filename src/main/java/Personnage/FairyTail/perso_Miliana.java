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
public class perso_Miliana extends PersonnageBase{
    public perso_Miliana (){
         this.nom    = "Miliana";
        this.type   = "Elementaliste";
        this.role   = "Support";
        this.rarete = "C";
        this.niveau = 1;
        double mult = 1.12;
        this.vie     = 360 * mult;
        this.attaque =  90 * mult;
        this.defense =  95 * mult;
        this.vitesse =  145 * mult;
        this.taux_critiques    = 0.10;
        this.degat_critiques   = 1.20;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.06;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.12;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }
    
    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Coup de poing félin", "Entraves Féline multiples ", "Kitten Blast"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Miliana utilise coup de poing félin sur " + cible.getNom());
        Combat.attaquer(this, cible, log);
        
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Miliana utilise entraves feline multiples sur les Supports ennemis !");
        boolean toucheSupport = false;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant() && ennemi.getRole().equals("Support")) {
                toucheSupport = true;
                double degats = this.getAttaque() * 0.80;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                if (Math.random() < 0.50) {
                    Combat.appliquerEffet(this, ennemi, new Etourdissement(1), log);
                }
            }
        }
        if (!toucheSupport) {
            PersonnageBase repli = Combat.choisirCible(this, equipeEnnemie);
            if (repli != null) {
                double degats = this.getAttaque() * 0.80;
                Combat.appliquerDegatsAvecLog(this, repli, degats, log);
                if (Math.random() < 0.50) {
                    Combat.appliquerEffet(this, repli, new Etourdissement(1), log);
                }
            }
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Miliana utilise Kitten Blast sur le Tank ennemi !");
        PersonnageBase cibleTank = Combat.cibleParRole(equipeEnnemie, "Tank");
        if (cibleTank == null) cibleTank = Combat.choisirCible(this, equipeEnnemie);
        if (cibleTank == null) return;
        double degats = this.getAttaque() * 1.30;
        Combat.appliquerDegatsAvecLog(this, cibleTank, degats, log);
        if (Math.random() < 0.50) {
            Combat.appliquerEffet(this, cibleTank, new Etourdissement(1), log);
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Coup de poing félin — Inflige 100% ATK");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Entraves Féline multiples — Inflige 80% ATK aux Supports ennemis, 50% de chance d'étourdir chacun d'eux pendant 1 tour.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Kitten Blast — Inflige 130% ATK au Tank ennemi, 50% de chance de l'étourdir pendant 1 tour.");
    }
    
}
