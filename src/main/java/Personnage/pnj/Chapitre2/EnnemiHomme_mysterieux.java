/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Personnage.pnj.Chapitre2;
import java.util.List;
import Effets.*;
import Personnage.PersonnageBase;
import Combat.Combat;
import java.util.ArrayList;

/**
 *
 * @author Lucas
 */
public class EnnemiHomme_mysterieux extends PersonnageBase {
    public EnnemiHomme_mysterieux() { this(14); }

    public EnnemiHomme_mysterieux(int niveau){
        this.nom="Homme mystérieux";
        this.type="Elementaliste";
        this.role="Support";
        this.rarete = "S";
        this.niveau = niveau;
        
        double mult = 1.50;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 200.0 * mult * niv;
        this.attaque =  80.0 * mult * niv;
        this.defense =  50.0 * mult * niv;
        this.vitesse =  200.0 * mult * vit;

        this.taux_critiques    = 0.20;
        this.degat_critiques   = 1.10;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.15;
        this.taux_blocage      = 0.06;
        this.reduction_blocage = 0.08;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
        
    }
    
     @Override
    public String[] getNomsAttaques() {
        return new String[]{"Lancer de boule du temps", "magie inconnu 1", "magie inconnu 2"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Homme mysterieux lance une boule du temps sur " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
        
    }

    @Override
public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add(this.nom + " utilise une magie mystérieuse !");
    for (PersonnageBase p : equipeEnnemie) {
        if (p.estVivant()) {
            double degats = this.getAttaque() * 0.70; // ratio réduit car c'est de l'AoE
            Combat.appliquerDegatsAvecLog(this, p, degats, log);
        }
    }
    for (PersonnageBase allie : equipeAlliee) {
        if (allie.estVivant()) {
            double soin = this.getAttaque() * 0.50;
            allie.recevoirSoin(soin, log);
        }
    }
    Purification.purifierEquipe(equipeAlliee, 2, log);
}

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
       List<PersonnageBase> vivants = new ArrayList<>();
        for (PersonnageBase allie : equipeAlliee) if (allie.estVivant()) vivants.add(allie);
        vivants.sort((a, b) -> Double.compare(a.getVie(), b.getVie())); // trie du plus bas au plus haut

        int nbCibles = Math.min(2, vivants.size());
        for (int i = 0; i < nbCibles; i++) {
            double soin = this.getAttaque() * 0.80;
            vivants.get(i).recevoirSoin(soin, log);
        }
        Purification.purifierEquipe(vivants, 2, log);

        for (PersonnageBase p : equipeEnnemie) {
            if (p.estVivant()) {
                double degats = this.getAttaque() * 1.00;
                Combat.appliquerDegatsAvecLog(this, p, degats, log);
                if (Math.random() < 0.50) {
                    Combat.appliquerEffet(this, p, new Silence(2), log);
                }
            }
        }
    }
            

    @Override public void descriptionAttaqueBase() {
        System.out.println("Boule du Temps : attaque de base sur la cible.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Magie Mystérieuse : inflige 70% ATK a tous les ennemis, soigne tous les allies a 50% ATK et purifie 2 effets negatifs de l'equipe.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Distorsion Temporelle : soigne les 2 allies les plus bas en PV a 80% ATK et les purifie de 2 effets negatifs, inflige 100% ATK a tous les ennemis et applique Silence (2 tours, 50% de chance) sur chacun.");
    }
}

