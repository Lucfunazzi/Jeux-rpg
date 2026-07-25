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
public class perso_Ikaruga extends PersonnageBase {
    public perso_Ikaruga(){
        this.nom="Ikaruga";
        this.type="Chevalier";
        this.role="Tank";
        this.rarete="A";
        this.niveau = 1;
        double multiplicateurRarete = 1.30;
        this.vie = 650 * multiplicateurRarete;
        this.attaque=150 *multiplicateurRarete;
        this.defense=140 * multiplicateurRarete;
        this.vitesse = 100 *multiplicateurRarete;
        this.taux_critiques = 0.05;
        this.degat_critiques = 1.25;
        this.taux_esquives = 0.10;
        this.taux_blocage=0.15;
        this.reduction_blocage = 0.20;
        this.degats_renvoi = 0.80;
        initialiserVieMax();
        
    }
    
    @Override
    public String[] getNomsAttaques(){
        return new String[]{"Coup de sabre","Flammes du Garuda","Eclats des Esprits"};
    }
    
     @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Ikaruga utilise coup de sabre sur " + cible.getNom());
        double degats = this.getAttaque() * 1.00;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    }
    
     @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Ikaruga utilise Flammes du Garuda ! ");
        
        PersonnageBase cibleDPS = equipeEnnemie.stream()
                .filter(e -> e.estVivant() && e.getRole().equals("DPS"))
                .findFirst()
                .orElse(cible);
        
        double degats = this.getAttaque() * 1.15;
        Combat.appliquerDegatsAvecLog(this, cibleDPS, degats, log);
        Combat.appliquerEffet(this, cibleDPS, new Brulure(2, 0.6), log); 
        Combat.appliquerEffet(this, new ContreAttaque(2,0.20), log);
    }
    

@Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Ikaruga utilise Eclats des Esprits !");
        Combat.appliquerEffet(this, new BuffDefense(0.10, 2), log);

        String roleCible = Combat.rolePrioritaireVivant(equipeEnnemie);
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant() && ennemi.getRole().equals(roleCible)) {
                double degats = this.getAttaque() * 0.80;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                Combat.appliquerEffet(this, ennemi, new ReductionDefense(0.10, 2), log);
            }
        }
    }
    
    @Override public void descriptionAttaqueBase() {
        System.out.println("Coup de sabre — inflige 100% ATK a une cible.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Flammes du garuda — cible les DPS ennemi prioritairement  "
                + "inflige 115% ATK, applique brûlure (6% pv) pendant 2 tours, "
                + "Se donne contre attaque de 20% pendant 2 tours.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Eclats des Esprits — se donne 10% de défense pendant 2 tours, "
                + "puis inflige 80% ATK a tous les DPS ennemis  "
                  + "avec Reduction de défense -10% sur ses cibles pendant 2 tours.");
    }
    
}


