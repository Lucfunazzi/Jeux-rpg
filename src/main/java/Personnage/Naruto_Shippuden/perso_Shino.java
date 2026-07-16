package Personnage.Naruto_Shippuden;
import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;
public class perso_Shino extends PersonnageBase {

    public perso_Shino() {
        this.nom = "Shino";
        this.niveau = 1;
        this.type = "Ninja";
        this.role = "Support";
        this.rarete = "C";
        double multiplicateurRarete = 1.00;
        this.vie = 290 * multiplicateurRarete;
        this.attaque = 90 * multiplicateurRarete;
        this.defense = 85 * multiplicateurRarete;
        this.vitesse = 80 * multiplicateurRarete;
        this.taux_critiques = 0.05;
        this.degat_critiques = 1.10;
        this.taux_precisions = 100.00;
        this.taux_esquives = 0.08;
        this.taux_blocage = 0.05;
        this.reduction_blocage = 0.10;
        this.degats_renvoi = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Nuee d'insectes", "Essaim corrosif", "Colonie devoratrice"};
    }


    @Override
public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Shino utilise Nuee d'insectes !");
    boolean touche = Combat.attaqueTouche(this, cible);
    if (!touche) {
        log.add(cible.getNom() + " esquive !");
        this.ajouterRage(50);
        return;
    }
    Combat.attaquer(this, cible, log);
    Combat.appliquerEffet(this, cible, new Poison(1, 0.03), log);
}

@Override
public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Shino utilise Essaim corrosif !");
    double degats = this.getAttaque() * 0.70;
    Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    Combat.appliquerEffet(this, cible, new ReductionAttaque(0.10, 2), log);
    Combat.appliquerEffet(this, cible, new Poison(2, 0.05), log);
}

@Override
public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Shino utilise Colonie devoratrice !");
    PersonnageBase cible = null;
    for (PersonnageBase allie : equipeAlliee) {
        if (allie.estVivant()) {
            if (cible == null || allie.getVie() < cible.getVie())
                cible = allie;
        }
    }
    if (cible == null) return;
    double montantBouclier = this.getAttaque() * 0.80;
    Combat.appliquerEffet(this, cible, new Bouclier(montantBouclier), log);
}

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Nuee d'insectes — inflige 100% ATK a une cible, "
                + "applique Poison (3% PV) pendant 1 tour si elle touche.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Essaim corrosif — inflige 70% ATK a une cible, "
                + "reduit son attaque de 10% pendant 2 tours "
                + "et l'empoisonne (5% PV/tour) pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Colonie devoratrice — applique un bouclier de 80% ATK "
                + "a l'allie avec le moins de PV.");
    }
}