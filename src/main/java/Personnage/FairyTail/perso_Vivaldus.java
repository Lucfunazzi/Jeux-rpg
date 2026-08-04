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
public class perso_Vivaldus extends PersonnageBase {
        public perso_Vivaldus(){ 
        this.nom="Vivaldus";
        this.type="Elementaliste";
        this.role="Support";
        this.rarete="A";
        this.niveau = 1;
        double multiplicateurRarete = 1.40;
        this.vie = 575 * multiplicateurRarete;
        this.attaque=160 *multiplicateurRarete;
        this.defense=125 * multiplicateurRarete;
        this.vitesse = 135 *multiplicateurRarete;
        this.taux_critiques = 0.10;
        this.degat_critiques = 1.25;
        this.taux_precisions = 100.00;
        this.taux_esquives = 0.05;
        this.taux_blocage=0.05;
        this.reduction_blocage = 0.20;
        this.degats_renvoi = 0.80;
        initialiserVieMax();
       }
       
       @Override
    public String[] getNomsAttaques() {
        return new String[]{"Notes de musiques", "Rock of Succubus", "Absorption"};
    }
    
     @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Vivaldus utilise Notes de musiques " + cible.getNom());
        double degats = this.getAttaque() * 1.00;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    }
    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Vivaldus utilise Rock of Succubus !");
        
        PersonnageBase cibleSupport = equipeEnnemie.stream()
                .filter(e -> e.estVivant() && e.getRole().equals("Support"))
                .findFirst()
                .orElse(cible);

        double degats = this.getAttaque() * 1.00;
        Combat.appliquerDegatsAvecLog(this, cibleSupport, degats, log);
        if (Math.random() < 0.30){
            Combat.appliquerEffet(this, cibleSupport, new Confusion(2), log);
        }
    }
    
    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Vivaldus utilise Absorption !");
        String roleCible;
        if (Combat.cibleParRole(equipeEnnemie, "Support") != null) roleCible = "Support";
        else if (Combat.cibleParRole(equipeEnnemie, "DPS")  != null) roleCible = "DPS";
        else roleCible = "Tank";
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant() && ennemi.getRole().equals(roleCible)) {
                double degats = this.getAttaque() * 0.80;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                
                
            }
        }
        for (PersonnageBase allie : equipeAlliee) {
        if (allie.estVivant() && allie.getRole().equals("Support")) {
            Combat.appliquerEffet(this, allie, new Immunite(2), log);
        }
    }
    }
    
     @Override public void descriptionAttaqueBase() {
        System.out.println("Notes de Musiques — inflige 100% ATK a une cible.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Rock of succubus — cible les Supports ennemi prioritairement  "
                + "inflige 100% ATK, à 30% de chance d'infliger confusions aux supports. "
              );
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Absorption — Attaque les Supports ennemis à 80% et immunise les supports alliée aux effets négatifs pendants "
                + "2 tours. ");
                 
            
    }
}
