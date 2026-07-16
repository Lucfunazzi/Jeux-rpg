/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Personnage.Naruto_Shippuden;

import Combat.Combat;
import Effets.BuffAttaque;
import Effets.ReductionDefense;
import Effets.BuffTauxCritique;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author Lucas
 */
public class perso_Lee extends PersonnageBase{
    public perso_Lee(){
         this.nom = "Lee";
        this.role = "DPS";
        this.rarete = "B";
        this.niveau = 1;
        double multiplicateurRarete = 1.30;
        this.vie = 400 * multiplicateurRarete;
        this.attaque = 150 * multiplicateurRarete;
        this.defense = 100 * multiplicateurRarete;
        this.vitesse = 130 * multiplicateurRarete;
        this.taux_critiques = 0.15;
        this.degat_critiques = 1.30;
        this.taux_precisions = 105.00;
        this.taux_esquives = 0.08;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.10;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }
    
      @Override
    public String[] getNomsAttaques() {
        return new String[]{"Coup de pieds", "Danse de konoha", "Premier Porte"};
    }

    
   @Override
public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Coup de pieds !");
    Combat.attaquer(this, cible, log);
}

@Override
public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Danse de konoha !");
    PersonnageBase cibleDanse = null;
    for (PersonnageBase ennemi : equipeEnnemie) {
        if (ennemi.estVivant()) {
            if (cibleDanse == null || ennemi.getAttaque() > cibleDanse.getAttaque())
                cibleDanse = ennemi;
        }
    }
    if (cibleDanse == null) return;
    double degats = this.getAttaque() * 1.80;
    Combat.appliquerDegatsAvecLog(this, cibleDanse, degats, log);
    Combat.appliquerEffet(this, cibleDanse, new ReductionDefense(0.10, 2), log);
}

@Override
public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Lee lance Premier Porte !");
    Combat.appliquerEffet(this, new BuffTauxCritique(0.15, 2), log);
    Combat.appliquerEffet(this, new BuffAttaque(0.10, 2), log);
}
    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Coup de pieds — inflige 100% ATK à une cible.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Danse de konoha — inflige 105% ATK à tous les DPS ennemis, "
                + "leur applique Saignement pendant 2 tours "
                + "et restaure 50 rage à Krillin.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Premier Porte — inflige 110% ATK à tous les ennemis, "
                + " inflige saignement aux dps ennemies pendants 2 tours "
                + "et reduit leurs defense de 10% pendants 2 tours.");
    }
    
}
