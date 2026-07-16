package Personnage.Naruto_Shippuden;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;
public class perso_Temari extends PersonnageBase {

    public perso_Temari() {
        this.nom = "Temari";
        this.niveau = 1;
        this.type = "Ninja";
        this.role = "Support";
        this.rarete = "B";
        
        double multiplicateurRarete = 1.20; // Rang B
        this.vie = 340 * multiplicateurRarete;
        this.attaque = 115 * multiplicateurRarete;
        this.defense = 80 * multiplicateurRarete;
        this.vitesse = 110 * multiplicateurRarete; // Rapide pour appliquer la réduction de défense avant le tour des autres
        
        this.taux_critiques = 0.12;
        this.degat_critiques = 1.40;
        this.taux_precisions = 100.00;
        this.taux_esquives = 0.08;
        
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Lame du Vent Coupante", "Danse du Grand Éventail", "Invocation : Kamatari (Faucheuse)"};
    }

   
    @Override
public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add(this.getNom() + " donne un coup sec de son éventail et génère des lames de vent !");
    double degats = this.getAttaque() * 1.00;
    Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    Combat.appliquerEffet(this, cible, new ReductionDefense(0.20, 2), log);
}

@Override
public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add(this.getNom() + " crée un courant d'air ascendant pour propulser son équipe !");
    for (PersonnageBase allie : equipeAlliee) {
        if (allie.estVivant()) {
            Combat.appliquerEffet(this, allie, new BuffVitesse(0.25, 2), log);
        }
    }
}

@Override
public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add(this.getNom() + " utilise la Technique de la Tempête de Sable et du Vent !");
    for (PersonnageBase ennemi : equipeEnnemie) {
        if (ennemi.estVivant()) {
            double degats = this.getAttaque() * 1.15;
            Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            Combat.appliquerEffet(this, ennemi, new ReductionAttaque(0.15, 2), log);
        }
    }
}

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Lame du Vent Coupante — Inflige 100% ATK et applique Réduction de Défense (20% pendant 2 tours).");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Danse du Grand Éventail — Augmente la vitesse de tous les alliés de 25% pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Invocation : Kamatari — Attaque tous les ennemis à hauteur de 115% ATK et réduit leur Attaque de 15% pendant 2 tours.");
    }
}