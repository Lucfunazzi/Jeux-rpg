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
public class perso_Shaw extends PersonnageBase {
    public perso_Shaw(){
         this.nom    = "Shaw";
        this.type   = "Chevalier";
        this.role   = "Support";
        this.rarete = "B";
        this.niveau = 1;
        double mult = 1.30;
        this.vie     = 370 * mult;
        this.attaque = 150 * mult;
        this.defense = 110 * mult;
        this.vitesse = 150 * mult;
        this.taux_critiques    = 0.10;
        this.degat_critiques   = 1.25;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.08;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.08;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }
    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Projection leger de carte", "Projection de Cartes avancée", "Cartes Dimensionnelles"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Shaw utilise projection leger de carte sur " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
        
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Shaw utilise projection de cartes avancées sur " + cible.getNom() + " !");
        double degats = this.getAttaque() * 1.20;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(cible, new Fragilite(2, 0.20), log);
        
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Shaw utilise Cartes Dimensionnelles sur le Tank et les DPS ennemis !");
        boolean frappe = false;
        for (PersonnageBase cible : equipeEnnemie) {
            if (cible.estVivant() && (cible.getRole().equals("Tank") || cible.getRole().equals("DPS"))) {
                frappe = true;
                double degats = this.getAttaque() * 1.20;
                Combat.appliquerDegatsAvecLog(this, cible, degats, log);
                if (Math.random() < 0.20) {
                    Combat.appliquerEffet(this, cible, new Silence(2), log);
                }
            }
        }
        if (!frappe) {
            PersonnageBase repli = Combat.choisirCible(this, equipeEnnemie);
            if (repli != null) {
                double degats = this.getAttaque() * 1.20;
                Combat.appliquerDegatsAvecLog(this, repli, degats, log);
                if (Math.random() < 0.20) {
                    Combat.appliquerEffet(this, repli, new Silence(2), log);
                }
            }
        }
        Combat.appliquerEffet(this, this, new BuffTauxCritique(0.10, 2), log);
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Projection leger de carte— Inflige 100% ATK.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Projection de cartes avancées — Inflige 120% ATK, et inflige marquage à la cible de 20% à la cible pendants 2 tour.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Cartes Dimensionnelles — Inflige 120% ATK au Tank et aux DPS ennemis, "
                + "20% de chance d'infliger Silence (2 tour) sur chacune de ces cibles, "
                + "et Shaw gagne 10% de taux critique pendant 2 tours.");
    }
    
    
    
}
