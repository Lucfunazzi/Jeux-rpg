package Personnage.FairyTail;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;

public class perso_Bixrow extends PersonnageBase {

    public perso_Bixrow() {
        this.nom = "Bickslow";
        this.niveau = 1;
        this.type = "Invocateur";
        this.role = "DPS";
        this.rarete = "A";
        double multiplicateurRarete = 1.30;
        this.vie = 400 * multiplicateurRarete;
        this.attaque = 150 * multiplicateurRarete;
        this.defense = 90 * multiplicateurRarete;
        this.vitesse = 115 * multiplicateurRarete;
        this.taux_critiques = 0.12;
        this.degat_critiques = 1.25;
        this.taux_precisions = 100.00;
        this.taux_esquives = 0.08;
        this.taux_blocage = 0.05;
        this.reduction_blocage = 0.10;
        this.degats_renvoi = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Frappe de poupee", "Invasion des poupees", "Danse macabre"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Bickslow utilise Frappe de poupee !");
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
        log.add("Bickslow utilise Formation 1 !");

        // Attaque les 2 ennemis avec le moins de PV
        ArrayList<PersonnageBase> ennemisVivants = new ArrayList<>();
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) ennemisVivants.add(ennemi);
        }
        ennemisVivants.sort((a, b) -> Double.compare(a.getVie(), b.getVie()));

        int ciblesAttaquees = 0;
        for (PersonnageBase ennemi : ennemisVivants) {
            if (ciblesAttaquees >= 2) break;
            double degats = this.getAttaque() * 1.00;
            if (ennemi.aEffet(Petrification.class)) {
                Petrification pet = ennemi.getEffet(Petrification.class);
                degats = pet.appliquerSurDegats(degats);
                log.add("Bickslow exploite la petrification sur " + ennemi.getNom() + " !");
            }
            Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            if (Math.random() < 0.25) {
                Combat.appliquerEffet(this, ennemi, new Paralysie(1, 0.30), log);
            }
            ciblesAttaquees++;
        }

        // Synergie Equipe du Tonnerre : Freed vivant → +5% ATK pour Freed
        for (PersonnageBase allie : equipeAlliee) {
            if (allie.getNom().equals("Freed") && allie.estVivant()) {
                Combat.appliquerEffet(this, allie, new BuffAttaque(0.05, 1), log);
                log.add("Synergie Equipe du Tonnerre : les poupees de Bickslow inspirent Freed ! +5% ATK.");
                break;
            }
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Bickslow utilise Formation X !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) {
            multiplicateurRage += (this.getRage() - 100) / 100.0;
        }
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = this.getAttaque() * 1.20;
                if (ennemi.aEffet(Petrification.class)) {
                    Petrification pet = ennemi.getEffet(Petrification.class);
                    degats = pet.appliquerSurDegats(degats);
                    log.add("Bickslow exploite la petrification sur " + ennemi.getNom() + " !");
                }
                degats *= multiplicateurRage;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                if (Math.random() < 0.30) {
                    Combat.appliquerEffet(this, ennemi, new Paralysie(2, 0.30), log);
                }
            }
        }
    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Frappe de poupee — inflige 100% ATK a une cible.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Invasion des poupees — inflige 80% ATK aux 3 ennemis ayant le moins de PV, "
                + "25% de chance de Paralysie (30%) pendant 1 tour sur chaque cible. "
                + "[Synergie Evergreen] Cible petrifiee : degats +20%. "
                + "[Synergie Equipe du Tonnerre] Freed vivant : +5% ATK pour Freed.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Danse macabre — inflige 110% ATK a tous les ennemis, "
                + "30% de chance de Paralysie (30%) pendant 2 tours sur chaque cible. "
                + "[Synergie Evergreen] Cible petrifiee : degats +20%.");
    }
}
