package Personnage.pnj.Chapitre5;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;

/**
 * Bickslow — version ennemie (Chapitre 5, stages 4 et 5). Meme identite et memes
 * attaques que perso_Bixrow, stats de base reprises telles quelles et mises a
 * l'echelle par niveau.
 */
public class EnnemiBixrow extends PersonnageBase {

    public EnnemiBixrow() { this(53); }

    public EnnemiBixrow(int niveau) {
        this.nom    = "Bickslow";
        this.niveau = niveau;
        this.type   = "Invocateur";
        this.role   = "DPS";
        this.rarete = "A";

        double mult = 1.40;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 400.0 * mult * niv;
        this.attaque = 150.0 * mult * niv;
        this.defense =  90.0 * mult * niv;
        this.vitesse = 115.0 * mult * vit;

        this.taux_critiques    = 0.12;
        this.degat_critiques   = 1.25;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.08;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.10;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Frappe de poupee", "Invasion des poupees", "Danse macabre"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Bickslow utilise Frappe de poupee !");
        double degats = this.getAttaque() * 1.00;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Bickslow utilise Invasion des poupees !");
        ArrayList<PersonnageBase> ennemisVivants = new ArrayList<>();
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) ennemisVivants.add(ennemi);
        }
        ennemisVivants.sort((a, b) -> Double.compare(a.getVie(), b.getVie()));

        int ciblesAttaquees = 0;
        for (PersonnageBase ennemi : ennemisVivants) {
            if (ciblesAttaquees >= 2) break;
            double degats = this.getAttaque() * 1.00;
            boolean touche = Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            if (touche && Math.random() < 0.25) {
                Combat.appliquerEffet(this, ennemi, new Paralysie(1, 0.30), log);
            }
            ciblesAttaquees++;
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Bickslow utilise Danse macabre !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) {
            multiplicateurRage += (this.getRage() - 100) / 100.0;
        }
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = (this.getAttaque() * 1.20) * multiplicateurRage;
                boolean touche = Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                if (touche && Math.random() < 0.30) {
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
        System.out.println("Invasion des poupees — inflige 100% ATK aux 2 ennemis ayant le moins de PV, "
                + "25% de chance de Paralysie (30%) pendant 1 tour sur chaque cible.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Danse macabre — inflige 120% ATK a tous les ennemis (bonus selon la Rage), "
                + "30% de chance de Paralysie (30%) pendant 2 tours sur chaque cible.");
    }
}
