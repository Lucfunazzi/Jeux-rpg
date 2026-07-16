package Personnage.Naruto_Shippuden;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;

public class perso_Zabuza extends PersonnageBase {
   public perso_Zabuza() {
        this.nom = "Zabuza";
        this.niveau = 1;
        this.type = "Ninja";
        this.role = "DPS";
        this.rarete = "B";
        
       
        double multiplicateurRarete = 1.20; 
        this.vie = 360 * multiplicateurRarete;
        this.attaque = 140 * multiplicateurRarete;
        this.defense = 80 * multiplicateurRarete;
        this.vitesse = 100 * multiplicateurRarete;
        
        this.taux_critiques = 0.15;
        this.degat_critiques = 1.50;
        this.taux_precisions = 100.00;
        this.taux_esquives = 0.10;
        
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Tranchant de la Décapiteuse", "Technique du Camouflage dans la Brume", "Exécution Silencieuse"};
    }

   
    @Override
public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add(this.getNom() + " assène un coup lourd avec sa Décapiteuse !");
    double degats = this.getAttaque() * 1.00;

    boolean synergieActive = cible.getEffet(Gel.class) != null || cible.getEffet(Marquage.class) != null;
    if (synergieActive) {
        degats *= 1.30;
        log.add("Synergie activée ! Zabuza profite de l'ouverture pour frapper plus fort !");
    }

    Combat.appliquerDegatsAvecLog(this, cible, degats, log);

    if (synergieActive) {
        Combat.appliquerEffet(this, cible, new Saignement(2, 0.05), log);
    }
}

@Override
public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add(this.getNom() + " utilise la Brume Clignotante pour disparaître !");
    Combat.appliquerEffet(this, new BuffAttaque(0.10, 2), log);
    Combat.appliquerEffet(this, new BuffTauxEsquive(0.05, 2), log);
}

@Override
public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add(this.getNom() + " surgit de la brume pour une Exécution Silencieuse !");

    PersonnageBase cible = null;
    for (PersonnageBase ennemi : equipeEnnemie) {
        if (ennemi.estVivant()) {
            if (ennemi.getEffet(Gel.class) != null || ennemi.getEffet(Marquage.class) != null) {
                cible = ennemi;
                break;
            }
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

    double multiplicateurDegats = 1.50;
    if (cible.getEffet(Gel.class) != null) {
        multiplicateurDegats = 1.80;
        log.add("Combo Glace & Brume ! L'ennemi est immobile, Zabuza l'exécute sans pitié !");
    }

    double multiplicateurRage = 1.0;
    if (this.getRage() > 100) {
        multiplicateurRage += (this.getRage() - 100) / 100.0;
    }

    double degats = (this.getAttaque() * multiplicateurDegats) * multiplicateurRage;
    Combat.appliquerDegatsAvecLog(this, cible, degats, log);

    if (cible.estVivant()) {
        Combat.appliquerEffet(this, cible, new Saignement(3, 0.10), log);
    }
}

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Tranchant de la Décapiteuse — Inflige 100% ATK à une cible. Si la cible est Gelée ou Marquée, inflige +30% de dégâts et applique Saignement (5% PV/tour pendant 2 tours).");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Technique du Camouflage dans la Brume — Augmente l'Attaque de 25% et l'Esquive de 30% pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Exécution Silencieuse — Assène un coup mortel à une cible à hauteur de 150% ATK. Les dégâts passent à 220% si la cible est Gelée. Applique un Saignement de 10% PV/tour pendant 3 tours.");
    }
}