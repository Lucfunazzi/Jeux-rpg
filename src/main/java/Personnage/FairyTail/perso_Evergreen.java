package Personnage.FairyTail;

import Combat.Combat;
import Effets.*;

import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;

public class perso_Evergreen extends PersonnageBase {

    public perso_Evergreen() {
        this.nom = "Evergreen";
        this.niveau = 1;
        this.type="Elementaliste";
        this.role = "Support";
        this.rarete = "B";
        double multiplicateurRarete = 1.30;
        this.vie = 380 * multiplicateurRarete;
        this.attaque = 140 * multiplicateurRarete;
        this.defense = 100 * multiplicateurRarete;
        this.vitesse = 110 * multiplicateurRarete;
        this.taux_critiques = 0.10;
        this.degat_critiques = 1.20;
        this.taux_precisions = 105.00;
        this.taux_esquives = 0.10;
        this.taux_blocage = 0.05;
        this.reduction_blocage = 0.10;
        this.degats_renvoi = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Stone Eyes", "Mitrailleuse féerique Leprechaun", "Explosion Féerique du Gremlin"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Evergreen utilise Stone Eyes !");
        boolean touche = Combat.attaqueTouche(this, cible);
        if (!touche) {
            log.add(cible.getNom() + " esquive !");
            this.ajouterRage(50);
            return;
        }
        double degats = this.getAttaque() * 1.00;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        if (Math.random() < 0.40) {
            Combat.appliquerEffet(this, cible, new Petrification(1), log);
        }
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Evergreen utilise Mitrailleuse féerique Leprechaun !");
        PersonnageBase cibleSupport = equipeEnnemie.stream()
                .filter(e -> e.estVivant() && e.getRole().equals("Support"))
                .findFirst()
                .orElse(cible);
        double degats = this.getAttaque() * 1.50;
        Combat.appliquerDegatsAvecLog(this, cibleSupport, degats, log);
        Combat.appliquerEffet(this, cibleSupport, new ReductionDefense(0.10, 2), log);

        for (PersonnageBase allie : equipeAlliee) {
            if (allie.getNom().equals("Bickslow") && allie.estVivant()) {
                Combat.appliquerEffet(this, allie, new BuffAttaque(0.25, 1), log);
                log.add("Synergie Equipe du Tonnerre : Bickslow profite de la cible ! +25% ATK.");
                break;
            }
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Evergreen utilise Explosion féerique du Gremlin !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) {
            multiplicateurRage += (this.getRage() - 100) / 100.0;
        }
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = (this.getAttaque() * 0.90) * multiplicateurRage;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                Combat.appliquerEffet(this, ennemi, new ReductionDefense(0.15, 2), log);
                if (Math.random() < 0.25) {
                    Combat.appliquerEffet(this, ennemi, new Brulure(2, 0.05), log);
                    for (PersonnageBase allie : equipeAlliee) {
                        if (allie.getNom().equals("Bickslow") && allie.estVivant()) {
                            Combat.appliquerEffet(this, allie, new BuffAttaque(0.25, 1), log);
                            log.add("Synergie Equipe du Tonnerre : Bickslow profite de la brulure ! +25% ATK.");
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Stone Eyes — inflige 100% ATK a une cible, "
                + "40% de chance de Petrification pendant 1 tour.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Mitrailleuse ferique Leprechaun — inflige 150% ATK au Support ennemi (fallback cible normale), "
                + "reduit sa defense de 10% pendant 2 tours. "
                + "[Synergie Equipe du Tonnerre] Bickslow vivant : +25% ATK pendant 1 tour.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Explosion ferique du Gremlin — inflige 90% ATK a tous les ennemis, "
                + "reduit leur defense de 15% pendant 2 tours, "
                + "25% de chance de Brulure (5% PV/tour) sur chaque cible pendant 2 tours. "
                + "[Synergie Equipe du Tonnerre] Chaque brulure donne +25% ATK a Bickslow. "
                + "Puissance augmentee par la Rage.");
    }
}