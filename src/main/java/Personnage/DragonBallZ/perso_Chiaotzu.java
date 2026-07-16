package Personnage.DragonBallZ;
import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;

public class perso_Chiaotzu extends PersonnageBase {

    public perso_Chiaotzu() {
        this.nom = "Chiaotzu";
        this.niveau = 1;
        this.type = "Guerrier";
        this.role = "Support";
        this.rarete = "C";
        double multiplicateurRarete = 1.00;
        this.vie = 270 * multiplicateurRarete;
        this.attaque = 75 * multiplicateurRarete;
        this.defense = 80 * multiplicateurRarete;
        this.vitesse = 85 * multiplicateurRarete;
        this.taux_critiques = 0.05;
        this.degat_critiques = 1.10;
        this.taux_precisions = 100.00;
        this.taux_esquives = 0.12;
        this.taux_blocage = 0.05;
        this.reduction_blocage = 0.10;
        this.degats_renvoi = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Onde psychique", "Paralysie mentale", "Sacrifice heroique"};
    }


   @Override
public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Chiaotzu utilise Onde psychique !");
    Combat.attaquer(this, cible, log);
}

@Override
public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Chiaotzu utilise Paralysie mentale !");
    if (Math.random() < 0.25) {
        Combat.appliquerEffet(this, cible, new Sommeil(1), log);
    } else {
        Combat.appliquerEffet(this, cible, new ReductionAttaque(0.10, 1), log);
    }
}

@Override
public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Chiaotzu utilise Sacrifice heroique !");
    PersonnageBase cibleSoin = null;
    for (PersonnageBase allie : equipeAlliee) {
        if (allie.estVivant()) {
            if (cibleSoin == null || allie.getVie() < cibleSoin.getVie())
                cibleSoin = allie;
        }
    }
    if (cibleSoin != null) {
        double soin = this.getAttaque() * 0.80;
        cibleSoin.recevoirSoin(soin, log);
        log.add("Chiaotzu soigne " + cibleSoin.getNom()
                + " de " + String.format("%.1f", soin) + " PV !");
    }
}
    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Onde psychique — inflige 100% ATK a une cible.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Paralysie mentale — 25% de chance d'endormir la cible 1 tour, "
                + "sinon reduit son attaque de 10% pendant 1 tour.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Sacrifice heroique — soigne l'allie avec le moins de PV a hauteur de 80% ATK.");
    }
}