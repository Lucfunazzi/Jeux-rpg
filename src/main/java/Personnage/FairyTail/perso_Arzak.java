package Personnage.FairyTail;

import Combat.Combat;
import Effets.BuffAttaque;
import Effets.ReductionVitesse;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;

public class perso_Arzak extends PersonnageBase {

    public perso_Arzak() {
        this.nom = "Alzack";
        this.niveau = 1;
        this.type = "Mage";
        this.role = "DPS";
        this.rarete = "C";
        double multiplicateurRarete = 1.00;
        this.vie = 320 * multiplicateurRarete;
        this.attaque = 115 * multiplicateurRarete;
        this.defense = 70 * multiplicateurRarete;
        this.vitesse = 90 * multiplicateurRarete;
        this.taux_critiques = 0.15;
        this.degat_critiques = 1.30;
        this.taux_precisions = 110.00;
        this.taux_esquives = 0.05;
        this.taux_blocage = 0.03;
        this.reduction_blocage = 0.05;
        this.degats_renvoi = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Tir magique", "Tir de folie", "Pluie de balles magiques"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Alzack utilise Tir magique !");
        boolean touche = Combat.attaqueTouche(this, cible);
        if (!touche) {
            log.add(cible.getNom() + " esquive !");
            this.ajouterRage(50);
            return;
        }
        double degats = this.getAttaque() * 1.00;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Alzack utilise Tir de folie !");
        double degats = this.getAttaque() * 1.20;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new ReductionVitesse(0.15, 2), log);

        for (PersonnageBase allie : equipeAlliee) {
            if (allie.getNom().equals("Bisca") && allie.estVivant()) {
                Combat.appliquerEffet(this, new BuffAttaque(0.05, 2), log);
                log.add("Synergie Duo de tireurs : Bisca couvre Alzack ! +5% ATK.");
                break;
            }
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Alzack utilise Tornado Shot !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) {
            multiplicateurRage += (this.getRage() - 100) / 100.0;
        }
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = (this.getAttaque() * 0.70) * multiplicateurRage;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            }
        }
    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Tir magique — inflige 100% ATK a une cible.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Tir de folie — inflige 120% ATK a une cible, "
                + "reduit sa vitesse de 15% pendant 2 tours. "
                + "[Synergie Duo de tireurs] Bisca vivante : +5% ATK pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Tornado Shot — inflige 70% ATK a tous les ennemis. "
                + "Puissance augmentee par la Rage.");
    }
}