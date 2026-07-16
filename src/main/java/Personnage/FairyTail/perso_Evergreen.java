package Personnage.FairyTail;

import Combat.Combat;
import Effets.BuffAttaque;
import Effets.Petrification;
import Effets.ReductionDefense;
import Effets.Silence;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;

public class perso_Evergreen extends PersonnageBase {

    public perso_Evergreen() {
        this.nom = "Evergreen";
        this.niveau = 1;
        this.type = "Mage";
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
        return new String[]{"Poussiere de fee", "Regard de pierre", "Tempete de cristal"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Evergreen utilise Poussiere de fee !");
        boolean touche = Combat.attaqueTouche(this, cible);
        if (!touche) {
            log.add(cible.getNom() + " esquive !");
            this.ajouterRage(50);
            return;
        }
        double degats = this.getAttaque() * 1.00;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        if (Math.random() < 0.20) {
            Combat.appliquerEffet(this, cible, new Silence(1), log);
        }
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Evergreen utilise Regard de pierre !");
        double degats = this.getAttaque() * 1.10;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new ReductionDefense(0.10, 2), log);
        if (Math.random() < 0.40) {
            Combat.appliquerEffet(this, cible, new Petrification(1), log);

            // Synergie Equipe du Tonnerre : si Bickslow vivant → il gagne +25% ATK
            for (PersonnageBase allie : equipeAlliee) {
                if (allie.getNom().equals("Bickslow") && allie.estVivant()) {
                    Combat.appliquerEffet(this, allie, new BuffAttaque(0.25, 1), log);
                    log.add("Synergie Equipe du Tonnerre : Bickslow profite de la petrification ! +25% ATK.");
                    break;
                }
            }
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Evergreen utilise Tempete de cristal !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) {
            multiplicateurRage += (this.getRage() - 100) / 100.0;
        }
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = (this.getAttaque() * 1.20) * multiplicateurRage;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                Combat.appliquerEffet(this, ennemi, new ReductionDefense(0.15, 2), log);
                if (Math.random() < 0.25) {
                    Combat.appliquerEffet(this, ennemi, new Petrification(1), log);

                    // Synergie Equipe du Tonnerre : Bickslow +25% ATK par cible petrifiee
                    for (PersonnageBase allie : equipeAlliee) {
                        if (allie.getNom().equals("Bickslow") && allie.estVivant()) {
                            Combat.appliquerEffet(this, allie, new BuffAttaque(0.25, 1), log);
                            log.add("Synergie Equipe du Tonnerre : Bickslow profite de la petrification ! +25% ATK.");
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Poussiere de fee — inflige 100% ATK a une cible, "
                + "20% de chance de Silence pendant 1 tour.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Regard de pierre — inflige 110% ATK a une cible, "
                + "reduit sa defense de 10% pendant 2 tours, "
                + "40% de chance de Petrification pendant 1 tour. "
                + "[Synergie Equipe du Tonnerre] Si petrification : Bickslow gagne +25% ATK.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Tempete de cristal — inflige 120% ATK a tous les ennemis, "
                + "reduit leur defense de 15% pendant 2 tours, "
                + "25% de chance de Petrification sur chaque cible pendant 1 tour. "
                + "[Synergie Equipe du Tonnerre] Chaque petrification donne +25% ATK a Bickslow.");
    }
}
