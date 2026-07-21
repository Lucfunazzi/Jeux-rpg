package Personnage.FairyTail;
import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;

import java.util.List;

public class perso_Kana extends PersonnageBase {
    public perso_Kana() {
        this.nom = "Cana";
        this.niveau = 1;
        this.type="Elementaliste";
        this.role = "Support";
        this.rarete = "B";
        
        double multiplicateurRarete = 1.30;
        this.vie = 350 * multiplicateurRarete;
        this.attaque = 120 * multiplicateurRarete;
        this.defense = 100 * multiplicateurRarete;
        this.vitesse = 110 * multiplicateurRarete;
        this.taux_critiques = 0.10;
        this.degat_critiques = 1.20;
        this.taux_precisions = 100.00;
        this.taux_esquives = 0.08;
        this.taux_blocage = 0.05;
        this.reduction_blocage = 0.10;
        this.degats_renvoi = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Lancer de carte", "Carte de soin", "Carte de Foudre"};
    }

    
    @Override
public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Cana utilise Lancer de cartes !");
    Combat.attaquer(this, cible, log);
}

@Override
public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Cana utilise Carte de soin !");
    PersonnageBase cibleSoin = null;
    for (PersonnageBase allie : equipeAlliee) {
        if (allie.estVivant()) {
            if (cibleSoin == null || allie.getVie() < cibleSoin.getVie())
                cibleSoin = allie;
        }
    }
    if (cibleSoin == null) return;
    double soin = this.getAttaque() * 1.00;
    cibleSoin.recevoirSoin(soin, log);
}

@Override
public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Cana utilise Carte de Foudre !");
    double multiplicateurRage = 1.0;
    if (this.getRage() > 100) {
        multiplicateurRage += (this.getRage() - 100) / 100.0;
    }
    for (PersonnageBase ennemi : equipeEnnemie) {
        if (ennemi.estVivant()) {
            double degats = (this.getAttaque() * 0.60) * multiplicateurRage;
            Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            Combat.appliquerEffet(this, ennemi, new Paralysie(2, 0.30), log);
        }
    }
}

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Lancer de carte — inflige 100% ATK a une cible.");
    }
    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Carte de soin — soigne l'allie avec le moins de PV a hauteur de 130% ATK.");
    }
    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Carte de foudre — inflige 70% ATK a toute l'equipe adverse, "
                + "paralyse pendant 2 tours et reduit l'attaque des DPS/Tank de 10% pendant 2 tours.");
    }
}