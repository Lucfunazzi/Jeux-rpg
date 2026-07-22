/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Personnage.pnj.chapitre1Elite;


import Combat.Combat;
import Effets.BuffVitesse;
import Effets.Gel;
import Effets.ReductionDefense;
import Effets.ReductionVitesse;
import Effets.Sommeil;
import Personnage.PersonnageBase;
import java.util.List;

/**
 *
 * @author Lucas
 */
public class EnnemiLullaby extends PersonnageBase {
public EnnemiLullaby(int niveau) {
    this.nom     = "Lullaby";
    this.type    = "Elementaliste";
    this.role    = "DPS";
    this.rarete  = "A";
    this.niveau  = niveau;

    // Stats fixes calibrées pour durer ~5 tours face à Erza/Natsu/Gray boostés
    this.vie     = 2400.0;
    this.attaque = 200.0;
    this.defense = 120.0;
    this.vitesse = 130.0;   

    this.taux_critiques    = 0.15;
    this.degat_critiques   = 1.30;
    this.taux_precisions   = 100.00;
    this.taux_esquives     = 0.08;
    this.taux_blocage      = 0.08;
    this.reduction_blocage = 0.10;
    this.degats_renvoi     = 0.80;

    initialiserVieMax();
}
       @Override
    public String[] getNomsAttaques() {
        return new String[]{"Rayon devastateur", "Magie Sonore", "Magie sonore ultra"};
    }

    
  @Override
public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Lullaby utilise Rayon devastateur !");
    
    double degats = this.getAttaque() * 1.50;
    Combat.appliquerDegatsAvecLog(this, cible, degats, log);
   
}

@Override
public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Magie sonore !");
    double degats = this.getAttaque() * 2.20;
    Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    Combat.appliquerEffet(this, cible, new ReductionDefense(0.20, 2), log);
  
}

@Override
public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Magie Sonore !");
    double multiplicateurRage = 1.0;
    if (this.getRage() > 100) {
        multiplicateurRage += (this.getRage() - 100) / 100.0;
    }
    for (PersonnageBase ennemi : equipeEnnemie) {
        if (ennemi.estVivant()) {
            double degats = (this.getAttaque() * 1.80) * multiplicateurRage;
            Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
           if (Math.random() < 0.30){
               Combat.appliquerEffet(this, ennemi, new Sommeil(2), log);
           }
        }
    }
    Combat.appliquerEffet(this, new BuffVitesse(0.20, 2), log);
}
    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Rayon devastateur — Inflige 120% ATK a la cible.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Magie Sonore — Inflige 220% ATK a la cible. "
                + "Reduit sa vitesse de 20% pendant 2 tours. "
                );
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Magie sonore ultra — Inflige 180% ATK a tous les ennemis. "
               
                );
    }
}
