package Personnage.Naruto_Shippuden;
import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;
public class perso_Kurenai extends PersonnageBase {

    public perso_Kurenai() {
        this.nom = "Kurenai";
        this.niveau = 1;
        this.type = "Ninja";
        this.role = "Support";
        this.rarete = "A";
        double multiplicateurRarete = 1.40; 
        this.vie = 450 * multiplicateurRarete;
        this.attaque = 120 * multiplicateurRarete;
        this.defense = 110 * multiplicateurRarete;
        this.vitesse = 105 * multiplicateurRarete; 
        this.taux_critiques = 0.05;
        this.degat_critiques = 1.15;
        this.taux_precisions = 100.00;
        this.taux_esquives = 0.12;
        this.taux_blocage = 0.10;
        this.reduction_blocage = 0.15;
        this.degats_renvoi = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Camouflage de la Brume", "Illusion de la Liaison de Mort", "Genjutsu: Mirage Végétal"};
    }

   
   @Override
public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Kurenai utilise Camouflage de la Brume !");
    Combat.attaquer(this, cible, log);
}

@Override
public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Kurenai utilise Illusion de la Liaison de Mort !");
    double degats = this.getAttaque() * 1.20;
    Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    Combat.appliquerEffet(this, cible, new ReductionVitesse(0.15, 2), log);
    Combat.appliquerEffet(this, cible, new Sommeil(2), log);
}

@Override
public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Kurenai déclenche son Genjutsu: Mirage Végétal !");
    for (PersonnageBase allie : equipeAlliee) {
        if (allie.estVivant()) {
            Combat.appliquerEffet(this, allie, new BuffTauxEsquive(0.30, 2), log);
        }
    }
}

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Camouflage de la Brume — Inflige 100% ATK à une cible.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Illusion de la Liaison de Mort — Inflige 120% ATK à une cible et réduit sa Vitesse de 15% pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Genjutsu: Mirage Végétal — Dissimule l'équipe alliée, augmentant l'Esquive et la Défense de tous les alliés de 15% pendant 2 tours.");
    }
}