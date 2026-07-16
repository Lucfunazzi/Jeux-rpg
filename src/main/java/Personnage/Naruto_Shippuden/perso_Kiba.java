package Personnage.Naruto_Shippuden;
import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;

public class perso_Kiba extends PersonnageBase {

    public perso_Kiba() {
        this.nom = "Kiba";
        this.niveau = 1;
        this.type = "Ninja";
        this.role = "DPS";
        this.rarete = "C";
        double multiplicateurRarete = 1.00;
        this.vie = 320 * multiplicateurRarete;
        this.attaque = 120 * multiplicateurRarete;
        this.defense = 70 * multiplicateurRarete;
        this.vitesse = 115 * multiplicateurRarete;
        this.taux_critiques = 0.12;
        this.degat_critiques = 1.25;
        this.taux_precisions = 100.00;
        this.taux_esquives = 0.10;
        this.taux_blocage = 0.03;
        this.reduction_blocage = 0.05;
        this.degats_renvoi = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Croc du fauve", "Danse du double croc", "Fang over Fang"};
    }

   
    @Override
public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Kiba utilise Croc du fauve !");
    Combat.attaquer(this, cible, log);
}

@Override
public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Kiba utilise Danse du double croc !");
    double degats = this.getAttaque() * 1.10;
    Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    Combat.appliquerEffet(this, cible, new ReductionDefense(0.10, 1), log);
}

@Override
public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Kiba utilise Fang over Fang !");
    double multiplicateurRage = 1.0;
    if (this.getRage() > 100) {
        multiplicateurRage += (this.getRage() - 100) / 100.0;
    }
    PersonnageBase cible = null;
    for (PersonnageBase ennemi : equipeEnnemie) {
        if (ennemi.estVivant()) {
            if (cible == null || ennemi.getVie() < cible.getVie())
                cible = ennemi;
        }
    }
    if (cible == null) return;
    double degats = (this.getAttaque() * 1.20) * multiplicateurRage;
    Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    Combat.appliquerEffet(this, new BuffTauxCritique(0.10, 2), log);
}
    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Croc du fauve — inflige 100% ATK a une cible.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Danse du double croc — inflige 110% ATK a une cible "
                + "et reduit sa defense de 10% pendant 1 tour.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Fang over Fang — inflige 120% ATK a l'ennemi avec le moins de PV "
                + "et augmente son taux critique de 10% pendant 2 tours.");
    }
}