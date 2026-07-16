package Personnage.Naruto_Shippuden;
import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;
public class perso_Sakura extends PersonnageBase {

    public perso_Sakura() {
        this.nom = "Sakura";
        this.type = "Ninja";
        this.role = "DPS";
        this.rarete = "A";
        this.niveau = 1;
        double multiplicateurRarete = 1.40;
        this.vie = 550 * multiplicateurRarete;
        this.attaque = 180 * multiplicateurRarete;
        this.defense = 80 * multiplicateurRarete;
        this.vitesse = 120 * multiplicateurRarete;
        this.taux_critiques = 0.20;
        this.degat_critiques = 1.45;
        this.taux_precisions = 105.00;
        this.taux_esquives = 0.08;
        this.taux_blocage = 0.05;
        this.reduction_blocage = 0.10;
        this.degats_renvoi = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Poings du cerisier", "Impact de la fleur du cerisier",
                "Force amelioree au chakra"};
    }

    
    @Override
public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Sakura utilise Poings du cerisier !");
    Combat.attaquer(this, cible, log);
}

@Override
public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Sakura utilise Impact de la fleur du cerisier !");
    double degats = this.getAttaque() * 1.40;
    Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    if (Math.random() < 0.35) {
        Combat.appliquerEffet(this, cible, new Etourdissement(1), log);
    }
    PersonnageBase cibleSoin = null;
    for (PersonnageBase allie : equipeAlliee) {
        if (allie.estVivant()) {
            if (cibleSoin == null || allie.getVie() < cibleSoin.getVie())
                cibleSoin = allie;
            
        }
    }
    if (cibleSoin != null) {
        double soin = this.getAttaque() * 0.60;
        cibleSoin.recevoirSoin(soin, log);
        log.add("Sakura soigne " + cibleSoin.getNom()
                + " de " + String.format("%.1f", soin) + " PV !");
        Purification.purifier(cibleSoin,2,log);
    }
}
    
    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Sakura utilise Force amelioree au chakra !");

        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) {
            double surplus = this.getRage() - 100;
            multiplicateurRage += (surplus / 100.0);
        }

        Combat.appliquerEffet(this, new BuffAttaque(0.20, 2), log);
        Combat.appliquerEffet(this, new BuffDegatCritique(0.10, 2), log);

        PersonnageBase cible = null;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                if (cible == null || ennemi.getAttaque() > cible.getAttaque())
                    cible = ennemi;
            }
        }
        if (cible == null) return;

        double degats = (this.getAttaque() * 1.30) * multiplicateurRage;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Poings du cerisier — inflige 100% ATK a une cible.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Impact de la fleur du cerisier — inflige 140% ATK a une cible, "
                + "35% de chance d'etourdir pendant 1 tour, "
                + "soigne l'allie avec le moins de PV a hauteur de 60% ATK.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Force amelioree au chakra — gagne 20% d'attaque et 10% de degats critiques "
                + "pendant 2 tours, puis inflige 130% ATK a l'ennemi avec la plus haute attaque.");
    }
}