package Personnage.FairyTail;

import Combat.Combat;
import Joueur.*;
import java.util.ArrayList;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

public class perso_Lucas extends PersonnageBase {

    public perso_Lucas() {
        this.nom = "Lucas";
        this.type="Elementaliste";
        this.role = "Support";
        this.rarete = "SS";
        this.niveau = 1;
        double multiplicateurRarete = 1.75;
        this.niveau = 1;
        this.vie = 4500 * multiplicateurRarete;
        this.attaque = 1400 * multiplicateurRarete;
        this.defense = 750 * multiplicateurRarete;
        this.vitesse = 1400 * multiplicateurRarete;
        this.taux_critiques = 0.15;
        this.degat_critiques = 1.15;
        this.taux_precisions = 105.00;
        this.taux_esquives = 0.12;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.10;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Gifle de l'amour", "Livre des peches capitaux", "ARC EN CIEL"};
    }

   
    @Override
public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Lucas utilise Gifle de l'amour !");
    Combat.attaquer(this, cible, log);
}

@Override
public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Lucas ouvre le Livre des peches capitaux !");

    double degats = this.getAttaque() * 1.50;
    Combat.appliquerDegatsAvecLog(this, cible, degats, log);

    for (PersonnageBase allie : equipeAlliee) {
        if (allie.estVivant()) {
            double soin = allie.getVie() * 0.20;
            allie.recevoirSoin(soin, log);
            log.add(allie.getNom() + " récupère " + String.format("%.1f", soin) + " PV !");
        }
    }

    PersonnageBase ennemiPlusFort = null;
    for (PersonnageBase ennemi : equipeEnnemie) {
        if (ennemi.estVivant()) {
            if (ennemiPlusFort == null || ennemi.getAttaque() > ennemiPlusFort.getAttaque())
                ennemiPlusFort = ennemi;
        }
    }
    if (ennemiPlusFort != null) {
        Combat.appliquerEffet(this, ennemiPlusFort, new ReductionAttaque(0.10, 3), log);
    }

    for (PersonnageBase ennemi : equipeEnnemie) {
        if (ennemi.estVivant()) {
            Combat.appliquerEffet(this, ennemi, new ReductionVitesse(0.10, 3), log);
        }
    }

    PersonnageBase allieFaible = null;
    for (PersonnageBase allie : equipeAlliee) {
        if (allie.estVivant()) {
            if (allieFaible == null || allie.getVie() < allieFaible.getVie())
                allieFaible = allie;
        }
    }
    if (allieFaible != null) {
        Combat.appliquerEffet(this, allieFaible, new BuffDefense(0.10, 2), log);
        Combat.appliquerEffet(this, allieFaible, new BuffAttaque(0.10, 2), log);
    }

    Combat.appliquerEffet(this, new Regeneration(0.10, 3), log);
}

@Override
public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Lucas invoque l'Arc-en-ciel protecteur !");
    double multiplicateurRage = 1.0;
    if (this.getRage() > 100) {
        multiplicateurRage += (this.getRage() - 100) / 100.0;
    }
    for (PersonnageBase allie : equipeAlliee) {
        if (allie.estVivant() && allie != this) {
            Combat.appliquerEffet(this, allie, new Bouclier(this.getVieMax() * 0.50), log);
        }
    }
    for (PersonnageBase ennemi : equipeEnnemie) {
        if (ennemi.estVivant()) {
            double degats = (this.getAttaque() * 1.20) * multiplicateurRage;
            Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
        }
    }
}
    
    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Gifle de l'amour — Inflige 100% ATK à une cible.");
    }
    
    @Override
    public void descriptionAttaqueSpeciale() { 
        System.out.println("Livre des peches capitaux — Attaque une cible à 150% ATK. Soigne l'équipe à hauteur de 20% de leurs PV. "
                + "Réduit l'ATK de l'ennemi le plus fort de 10% et la vitesse de tous les ennemis de 10% pendant 3 tours. "
                + "Augmente l'ATK et la DEF de l'allié le plus faible de 10% pendant 2 tours et s'octroie une Régénération."); 
    }
    
    @Override
    public void descriptionAttaqueUltime() { 
        System.out.println("ARC EN CIEL — Applique un bouclier à tous les alliés (sauf lui) égal à 50% des PV Max de Lucas. "
                + "Attaque tous les ennemis à 120% ATK. S'octroie Invincibilité pendant 1 tour et ressuscite un allié KO avec 30% PV."); 
    }
}