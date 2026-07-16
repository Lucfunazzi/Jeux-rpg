package Personnage.FairyTail;

import Combat.Combat;

import Effets.*;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;

public class perso_Loke extends PersonnageBase {

    public perso_Loke() {
        this.nom = "Loke";
        this.niveau = 1;
        this.type = "Mage";
        this.role = "DPS";
        this.rarete = "B";
        double multiplicateurRarete = 1.30;
        this.vie = 420 * multiplicateurRarete;
        this.attaque = 160 * multiplicateurRarete;
        this.defense = 90 * multiplicateurRarete;
        this.vitesse = 130 * multiplicateurRarete;
        this.taux_critiques = 0.20;
        this.degat_critiques = 1.35;
        this.taux_precisions = 105.00;
        this.taux_esquives = 0.12;
        this.taux_blocage = 0.05;
        this.reduction_blocage = 0.10;
        this.degats_renvoi = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Poing du Lion", "Regulus Impact", "Meteor Blade"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Loke utilise Poing du Lion !");
        boolean touche = Combat.attaqueTouche(this, cible);
        if (!touche) {
            log.add(cible.getNom() + " esquive !");
            this.ajouterRage(50);
            return;
        }
        double degats = this.getAttaque() * 1.00;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        if (Math.random() < 0.20) {
            Combat.appliquerEffet(this, cible, new Etourdissement(1), log);
        }
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Loke utilise Regulus Impact !");
        double degats = this.getAttaque() * 1.30;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new Aveuglement(0.15, 2), log);
        Combat.appliquerEffet(this, new BuffAttaque(0.10, 2), log);

        // Synergie Loke & Lucy : Lucy vivante → +10% ATK bonus
        for (PersonnageBase allie : equipeAlliee) {
            if (allie.getNom().equals("Lucy") && allie.estVivant()) {
                Combat.appliquerEffet(this, new BuffAttaque(0.10, 2), log);
                log.add("Synergie Esprit Celeste : Lucy renforce Loke ! +10% ATK bonus.");
                break;
            }
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Loke utilise Meteor Blade !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) {
            multiplicateurRage += (this.getRage() - 100) / 100.0;
        }

        // Synergie Lucy & Loke : si Lucy a utilisé son ultime, +20% ATK
        // (géré côté Lucy → Loke reçoit BuffAttaque avant cet appel)

        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = (this.getAttaque() * 1.50) * multiplicateurRage;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                if (Math.random() < 0.25) {
                    Combat.appliquerEffet(this, ennemi, new Etourdissement(1), log);
                }
            }
        }
    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Poing du Lion — inflige 100% ATK a une cible, "
                + "20% de chance d'etourdir pendant 1 tour.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Regulus Impact — inflige 130% ATK a une cible, "
                + "reduit sa defense de 15% pendant 2 tours, "
                + "gagne 10% ATK pendant 2 tours. "
                + "[Synergie Esprit Celeste] Lucy vivante : +10% ATK bonus.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Meteor Blade — inflige 150% ATK a tous les ennemis, "
                + "25% de chance d'etourdir chaque cible pendant 1 tour. "
                + "[Synergie Lucy & Loke] Si Lucy a utilise son ultime : +20% ATK.");
    }
}
