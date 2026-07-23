package Personnage.FairyTail;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;

public class perso_Erza extends PersonnageBase {

    public perso_Erza() {
        this.nom = "Erza";
        this.type = "Chevalier";
        this.role = "Tank";
        this.rarete = "S";
        this.niveau = 1;
        double multiplicateurRarete = 1.50;
        this.vie = 1200 * multiplicateurRarete;
        this.attaque = 140 * multiplicateurRarete;
        this.defense = 250 * multiplicateurRarete;
        this.vitesse = 100 * multiplicateurRarete;
        this.taux_critiques = 0.05;
        this.degat_critiques = 1.10;
        this.taux_precisions = 100.00;
        this.taux_esquives = 0.10;
        this.taux_blocage = 0.15;
        this.reduction_blocage = 0.20;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Coup d'épée", "Armure adamantine", "Roue céleste"};
    }

   
    @Override
public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Erza dégaine son épée !");
    Combat.attaquer(this, cible, log);
}

@Override
public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Erza enfile l'Armure Adamantine !");
     double degats = this.getAttaque() * 1.30;
    Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    Combat.appliquerEffet(this, new Bouclier(this.getVieMax() * 0.30), log);
    Combat.appliquerEffet(this, new BuffDefense(0.15, 3), log);
    Combat.appliquerEffet(this, new BuffBlocage(0.15, 3), log);
}

@Override
public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Erza déchaîne la Roue Céleste !");
    double multiplicateurRage = 1.0;
    if (this.getRage() > 100) {
        multiplicateurRage += (this.getRage() - 100) / 100.0;
    }
    for (PersonnageBase ennemi : equipeEnnemie) {
        if (ennemi.estVivant()) {
            double degats = (this.getAttaque() * 1.30) * multiplicateurRage;
            Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            Combat.appliquerEffet(this, ennemi, new Saignement(2, 0.05), log);
            Combat.appliquerEffet(this, ennemi, new ReductionAttaque(0.10, 3), log);
            if (ennemi.getRole().equalsIgnoreCase("support") && Math.random() < 0.30) {
                Combat.appliquerEffet(this, ennemi, new Silence(2), log);
            }
        }
    }
    Combat.appliquerEffet(this, new BuffDefense(0.20, 3), log);
}

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Coup d'épée — inflige 100% ATK à une cible.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Armure Adamantine — attaque à 130% un ennemi se protège avec un bouclier (50% PV max), "
                + "augmente sa défense de 15% et son blocage de 15% pendant 3 tours.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Roue Céleste — attaque tous les ennemis à 130% ATK, "
                + "inflige Saignement et réduit leur attaque de 10% pendant 3 tours. "
                + "Augmente sa propre défense de 20% pendant 3 tours. "
                + "30% de chance de Silence sur les Supports ennemis.");
    }
}