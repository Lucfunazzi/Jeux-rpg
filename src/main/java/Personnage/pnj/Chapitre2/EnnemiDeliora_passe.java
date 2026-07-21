/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Personnage.pnj.Chapitre2;

import Personnage.PersonnageBase;
import Combat.Combat;
import Effets.ReductionDefense;
import java.util.List;

/**
 *
 * @author Lucas
 */
public class EnnemiDeliora_passe extends PersonnageBase {

    public EnnemiDeliora_passe() {
        this.nom    = "Deliora";
        this.type   = "Demon";
        this.role   = "Tank";
        this.rarete = "SS";
        this.niveau = 1;

        // Stats fixes : demon quasi invincible face aux attaques normales,
        // seule Glace Absolue (Iced Shell) d'Ul peut le vaincre.
        this.vie     = 25000.0;
        this.attaque = 950.0;
        this.defense = 320.0;
        this.vitesse = 90.0;

        this.taux_critiques    = 0.10;
        this.degat_critiques   = 1.30;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.05;
        this.taux_blocage      = 0.10;
        this.reduction_blocage = 0.15;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " frappe " + cible.getNom() + " de son poing devastateur !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " libere un souffle destructeur sur " + cible.getNom() + " !");
        double degats = this.getAttaque() * 1.60;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new ReductionDefense(0.15, 2), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " dechaine toute sa puissance destructrice !");
        for (PersonnageBase cible : equipeEnnemie) {
            if (cible.estVivant()) {
                double degats = this.getAttaque() * 1.30;
                Combat.appliquerDegatsAvecLog(this, cible, degats, log);
            }
        }
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Poing Devastateur", "Souffle Destructeur", "Rage du Demon"};
    }
    @Override public void descriptionAttaqueBase() {
        System.out.println("Poing Devastateur : attaque de base sur la cible.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Souffle Destructeur : inflige 160% ATK et reduit la defense de la cible de 15% pendant 2 tours.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Rage du Demon : inflige 130% ATK a tous les ennemis.");
    }
}
