package Personnage.DragonBallZ;
import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;

public class perso_Yamcha extends PersonnageBase {

    public perso_Yamcha() {
        this.nom = "Yamcha";
        this.type = "Guerrier";
        this.role = "DPS";
        this.rarete = "C";
        this.niveau = 1;
        double multiplicateurRarete = 1.00;
        this.vie = 350 * multiplicateurRarete;
        this.attaque = 130 * multiplicateurRarete;
        this.defense = 80 * multiplicateurRarete;
        this.vitesse = 100 * multiplicateurRarete;
        this.taux_critiques = 0.10;
        this.degat_critiques = 1.20;
        this.taux_precisions = 105.00;
        this.taux_esquives = 0.08;
        this.taux_blocage = 0.02;
        this.reduction_blocage = 0.02;
        this.degats_renvoi = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Coup de pied", "Kamehameha", "Sokidan"};
    }

   
   @Override
public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Yamcha utilise Coup de pied !");
    Combat.attaquer(this, cible, log);
}

@Override
public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Yamcha utilise Kamehameha !");
    double degats = this.getAttaque() * 1.10;
    Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    Combat.appliquerEffet(this, new BuffTauxCritique(0.10, 2), log);
}

@Override
public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Yamcha utilise Sokidan !");
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
    double degats = (this.getAttaque() * 1.10) * multiplicateurRage;
    Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    Combat.appliquerEffet(this, cible, new Brulure(2, 0.08), log);
}

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Coup de pied — inflige 100% ATK a une cible.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Kamehameha — inflige 110% ATK a une cible "
                + "et gagne 10% de taux critique pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Sokidan — inflige 110% ATK a l'ennemi avec le moins de PV "
                + "et lui applique Brulure (8% PV/tour) pendant 2 tours.");
    }
}