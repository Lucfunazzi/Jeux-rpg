package Personnage.Naruto_Shippuden;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;

public class perso_Kankuro extends PersonnageBase {

    public perso_Kankuro() {
        this.nom = "Kankuro";
        this.niveau = 1;
        this.type = "Ninja";
        this.role = "DPS";
        this.rarete = "B";
        
        double multiplicateurRarete = 1.20; // Rang B
        this.vie = 350 * multiplicateurRarete;
        this.attaque = 130 * multiplicateurRarete;   // Bonne attaque pour un poseur de DoT
        this.defense = 75 * multiplicateurRarete;
        this.vitesse = 100 * multiplicateurRarete;
        
        this.taux_critiques = 0.10;
        this.degat_critiques = 1.50;
        this.taux_precisions = 100.00;
        this.taux_esquives = 0.08;
        
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Attaque Directe de Karasu", "Brume Empoisonnée de Sanshouo", "Technique Secrète : Arcane Noire"};
    }

   
   @Override
public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add(this.getNom() + " manipule Karasu pour entailler " + cible.getNom() + " !");
    double degats = this.getAttaque() * 1.00;
    Combat.appliquerDegatsAvecLog(this, cible, degats, log);

    boolean defenseReduite = cible.getEffet(ReductionDefense.class) != null;
    if (defenseReduite) {
        Combat.appliquerEffet(this, cible, new Poison(3, 0.08), log);
        log.add("Synergie ! La garde de l'ennemi était brisée, le poison pénètre profondément (8%/tour) !");
    } else {
        Combat.appliquerEffet(this, cible, new Poison(2, 0.05), log);
    }
}

@Override
public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add(this.getNom() + " fait éjecter un gaz toxique violet par sa marionnette Kuroari !");

    for (PersonnageBase ennemi : equipeEnnemie) {
        if (ennemi.estVivant()) {
            double degats = this.getAttaque() * 0.60;
            Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            Combat.appliquerEffet(this, ennemi, new Poison(2, 0.06), log);
        }
    }
    log.add("Tous les ennemis subissent Poison (6% PV max/tour). Parfait pour le Tombeau de Gaara !");
}

@Override
public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add(this.getNom() + " active l'Arcane Noire : Triple Chambre de Fer !");

    PersonnageBase cible = null;
    for (PersonnageBase ennemi : equipeEnnemie) {
        if (ennemi.estVivant() && ennemi.getEffet(Poison.class) != null) {
            cible = ennemi;
            break;
        }
    }
    if (cible == null) {
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                cible = ennemi;
                break;
            }
        }
    }
    if (cible == null) return;

    double multiplicateur = 1.40;
    if (cible.getEffet(Poison.class) != null) {
        multiplicateur = 1.90;
        log.add("L'ennemi piégé souffre déjà du poison ! Les aiguilles de l'Arcane Noire font d'affreux ravages !");
    }

    double degats = this.getAttaque() * multiplicateur;
    Combat.appliquerDegatsAvecLog(this, cible, degats, log);
}

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Attaque Directe de Karasu — Inflige 100% ATK. Si la cible a sa Défense Réduite, applique un Poison de 8% PV/tour pendant 3 tours. Sinon, applique un Poison de 5% PV/tour pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Brume Empoisonnée de Sanshouo — Attaque tous les ennemis à hauteur de 60% ATK et leur applique l'effet Poison (6% PV/tour pendant 2 tours).");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Technique Secrète : Arcane Noire — Enferme un ennemi et lui inflige 140% ATK. Les dégâts passent à 190% si la cible est déjà Empoisonnée.");
    }
}