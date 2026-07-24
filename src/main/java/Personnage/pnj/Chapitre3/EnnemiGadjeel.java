/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Personnage.pnj.Chapitre3;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

/**
 *
 * @author Lucas
 */
public class EnnemiGadjeel extends PersonnageBase {
      public EnnemiGadjeel() { this(30); }

    public EnnemiGadjeel(int niveau) {
    
    this.nom = "Gajeel";
        this.type = "ChasseurDeDragon";
        this.role = "DPS";
        this.rarete = "A";
        this.niveau = niveau;
        double multiplicateurRarete = 1.30;
        // Stats de base calibrees pour le niveau 30 (constructeur par defaut) ;
        // mise a l'echelle pour permettre la reutilisation en Chapitre 3 Elite.
        double niv = Math.pow(1.05, niveau - 30);
        double vit = Math.pow(1.03, niveau - 30);
        this.vie = 580 * multiplicateurRarete * niv;
        this.attaque = 220 * multiplicateurRarete * niv;
        this.defense = 130 * multiplicateurRarete * niv;
        this.vitesse = 110 * multiplicateurRarete * vit;
        this.taux_critiques = 0.20;
        this.degat_critiques = 1.40;
        this.taux_precisions = 100.00;
        this.taux_esquives = 0.05;
        this.taux_blocage      = 0.10;
        this.reduction_blocage = 0.15;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Griffes de fer", "Lance de fer du dragon", "Rugissement du dragon de fer"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Gajeel utilise Griffes de fer !");
        double degats = this.getAttaque() * 1.10;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Gajeel utilise Lance de fer du dragon !");
        double degats = this.getAttaque() * 1.50;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        // Réduction DEF garantie
        Combat.appliquerEffet(this, cible, new ReductionDefense(0.25, 2), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Gajeel utilise Rugissement du dragon de fer !");
        // Cible l'ennemi avec le plus de DEF
        PersonnageBase cible = equipeEnnemie.stream()
                .filter(PersonnageBase::estVivant)
                .max(java.util.Comparator.comparingDouble(PersonnageBase::getDefense))
                .orElse(null);

        if (cible == null) return;

        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) {
            multiplicateurRage += (this.getRage() - 100) / 100.0;
        }
        double degats = (this.getAttaque() * 2.00) * multiplicateurRage;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        // Réduction DEF lourde garantie
        Combat.appliquerEffet(this, cible, new ReductionDefense(0.40, 3), log);
    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Griffes de fer — Inflige 110% ATK a la cible.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Lance de fer du dragon — Inflige 150% ATK a la cible. "
                + "Reduit sa DEF de 25% pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Rugissement du dragon de fer — Inflige 200% ATK a l'ennemi avec le plus de DEF. "
                + "Reduit sa DEF de 40% pendant 3 tours. "
                + "Puissance augmentee par la Rage.");
    }
}
