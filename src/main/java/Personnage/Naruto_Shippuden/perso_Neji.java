package Personnage.Naruto_Shippuden;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;
public class perso_Neji extends PersonnageBase {

    public perso_Neji() {
        this.nom = "Neji";
        this.niveau = 1;
        this.type = "Ninja";
        this.role = "Tank";
        this.rarete = "A";
        
        
        double multiplicateurRarete = 1.40;
        this.vie = 520 * multiplicateurRarete;       
        this.attaque = 100 * multiplicateurRarete;  
        this.defense = 130 * multiplicateurRarete;   
        this.vitesse = 95 * multiplicateurRarete;
        
        this.taux_critiques = 0.05;
        this.degat_critiques = 1.15;
        this.taux_precisions = 100.00;
        this.taux_esquives = 0.05;
        
        // Statistiques de Tank basées sur ton système de combat :
        this.taux_blocage = 0.15;        
        this.reduction_blocage = 0.40;  
        this.degats_renvoi = 0.80;       
        
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Jûken: Le Poing Souple", "Les 64 Poings du Hakke", "Hakkeshô Kaiten (Tourbillon Divin)"};
    }

    
    @Override
public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Neji frappe les cavités de chakra avec le Jûken !");
    Combat.attaquer(this, cible, log);
    Combat.appliquerEffet(this, cible, new ReductionDefense(0.10, 2), log);
}

@Override
public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Neji utilise la technique suprême : Les 64 Poings du Hakke !");
    double degats = this.getAttaque() * 1.30;
    Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    Combat.appliquerEffet(this, cible, new ReductionAttaque(0.15, 2), log);
}

@Override
public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Neji crie : Hakkeshô Kaiten ! Il tourbillonne pour repousser toutes les attaques !");
    double montantBouclier = this.getVieMax() * 0.35;
    Combat.appliquerEffet(this, new Bouclier(montantBouclier), log);
    Combat.appliquerEffet(this, new BuffBlocage(0.15, 2), log);
}

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Jûken: Le Poing Souple — Inflige 100% ATK à une cible et réduit sa Défense de 10% pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Les 64 Poings du Hakke — Enchaîne une cible (130% ATK) et bloque son chakra, réduisant son Attaque de 25% pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Hakkeshô Kaiten — Déclenche la défense absolue. Octroie un Bouclier équivalent à 35% de ses PV max et augmente son Taux de Blocage de 40% pendant 2 tours.");
    }
}