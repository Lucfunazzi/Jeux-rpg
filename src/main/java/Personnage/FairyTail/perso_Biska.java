package Personnage.FairyTail;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class perso_Biska extends PersonnageBase {

    public perso_Biska() {
        this.nom = "Bisca";
        this.niveau = 1;
        this.type="Elementaliste";
        this.role = "DPS";
        this.rarete = "C";
        double multiplicateurRarete = 1.00;
        this.vie = 300 * multiplicateurRarete;
        this.attaque = 110 * multiplicateurRarete;
        this.defense = 65 * multiplicateurRarete;
        this.vitesse = 95 * multiplicateurRarete;
        this.taux_critiques = 0.18;
        this.degat_critiques = 1.35;
        this.taux_precisions = 108.00;
        this.taux_esquives = 0.06;
        this.taux_blocage = 0.02;
        this.reduction_blocage = 0.05;
        this.degats_renvoi = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Tir rapide", "Stinger Shot", "Tir à tête chercheuse"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Bisca utilise Tir rapide !");
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
        log.add("Bisca utilise Stinger Shot !");
        double degats = this.getAttaque() * 0.80;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new Saignement(2, 0.03), log);

        for (PersonnageBase allie : equipeAlliee) {
            if (allie.getNom().equals("Alzack") && allie.estVivant()) {
                Combat.appliquerEffet(this, new BuffAttaque(0.05, 2), log);
                log.add("Synergie Duo de tireurs : Alzack couvre Bisca ! +5% ATK.");
                break;
            }
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Bisca utilise Tir à tête chercheuses !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) {
            multiplicateurRage += (this.getRage() - 100) / 100.0;
        }
        ArrayList<PersonnageBase> ennemisVivants = new ArrayList<>();
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) ennemisVivants.add(ennemi);
        }
        ennemisVivants.sort(Comparator.comparingDouble(PersonnageBase::getVie).reversed());
        int ciblesAttaquees = 0;
        for (PersonnageBase ennemi : ennemisVivants) {
            if (ciblesAttaquees < 2) {
                double degats = (this.getAttaque() * 1.20) * multiplicateurRage;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                Combat.appliquerEffet(this, ennemi, new Saignement(2, 0.03), log);
                ciblesAttaquees++;
            }
        }
    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Tir rapide — inflige 100% ATK a une cible.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Stinger Shot — inflige 120% ATK a une cible, "
                + "applique Saignement (3% PV/tour) pendant 2 tours. "
                + "[Synergie Duo de tireurs] Alzack vivant : +5% ATK pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Tir a tete chercheuse — inflige 140% ATK aux 2 ennemis ayant le plus de PV, "
                + "applique Saignement (3% PV/tour) pendant 2 tours sur chacun. "
                + "Puissance augmentee par la Rage.");
    }
}