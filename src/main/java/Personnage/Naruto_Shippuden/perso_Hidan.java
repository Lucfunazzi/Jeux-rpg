package Personnage.Naruto_Shippuden;
import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;

public class perso_Hidan extends PersonnageBase {

    public perso_Hidan() {
        this.nom = "Hidan";
        this.type = "Ninja";
        this.role = "Tank";
        this.rarete = "A";
        this.niveau = 1;
        double multiplicateurRarete = 1.40;
        this.vie = 700 * multiplicateurRarete;
        this.attaque = 160 * multiplicateurRarete;
        this.defense = 110 * multiplicateurRarete;
        this.vitesse = 80 * multiplicateurRarete;
        this.taux_critiques = 0.25;
        this.degat_critiques = 1.20;
        this.taux_precisions = 105.00;
        this.taux_esquives = 0.12;
        this.taux_blocage = 0.15;
        this.reduction_blocage = 0.20;
        this.degats_renvoi = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Coup de Faux", "Rituel de Jashin", "Immortalite"};
    }

    // Attaque de base — 100% ATK + Saignement 8% 1 tour (20% de chance)
    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Hidan utilise Coup de Faux !");
        Combat.attaquer(this, cible, log);
        if (Math.random() < 0.20) {
            Combat.appliquerEffet(this, cible, new Saignement(1, 0.08), log);
        }
    }

    // Spéciale — 130% ATK + Saignement 12% 3 tours + coût 8% PV max sur soi + synergie Kakuzu
    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Hidan utilise Rituel de Jashin !");

        double degats = this.getAttaque() * 1.30;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new Saignement(3, 0.12), log);

        // Prix du rituel : Hidan subit 8% de ses PV max
        double coutRituel = this.getVieMax() * 0.08;
        this.vie = Math.max(1, this.vie - coutRituel);
        log.add("Hidan paie le prix du rituel et perd "
                + String.format("%.1f", coutRituel) + " PV !");

        // Synergie Kakuzu
        for (PersonnageBase allie : equipeAlliee) {
            if (allie.getNom().equals("Kakuzu") && allie.estVivant()) {
                allie.ajouterRage(15);
                log.add("Kakuzu gagne 15 rage (synergie Jashin) !");
            }
        }
    }

    // Ultime — Résurrection 40% + Régénération 10% 2 tours + ReductionDefense 10% 2 tours
    //        + synergie Kakuzu : BuffDefense 15% 2 tours
    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Hidan utilise Immortalite !");

        Combat.appliquerEffet(this, new Resurrection(0.40), log);
        Combat.appliquerEffet(this, new Regeneration(0.10, 2), log);
        Combat.appliquerEffet(this, new ReductionDefense(0.10, 2), log);

        // Synergie Kakuzu
        for (PersonnageBase allie : equipeAlliee) {
            if (allie.getNom().equals("Kakuzu") && allie.estVivant()) {
                Combat.appliquerEffet(this, allie, new BuffDefense(0.15, 2), log);
            }
        }
    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Coup de Faux — inflige 100% ATK a une cible, "
                + "20% de chance d'infliger Saignement (8% PV) pendant 1 tour.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Rituel de Jashin — inflige 130% ATK a une cible, "
                + "applique Saignement (12% PV/tour) pendant 3 tours. "
                + "Hidan perd 8% de ses PV max en contrepartie. "
                + "Donne 15 rage a Kakuzu s'il est allie.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Immortalite — applique Resurrection (40% PV) et Regeneration (10% PV/tour) "
                + "pendant 2 tours sur Hidan, reduit sa defense de 10% pendant 2 tours. "
                + "Donne 15% de defense a Kakuzu s'il est allie.");
    }
}