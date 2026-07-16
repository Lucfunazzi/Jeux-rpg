package Personnage.DragonBallZ;
import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;

public class perso_Tien extends PersonnageBase {

    public perso_Tien() {
        this.nom = "Tien";
        this.niveau = 1;
        this.type = "Guerrier";
        this.role = "Tank";
        this.rarete = "C";
        double multiplicateurRarete = 1.00;
        this.vie = 520 * multiplicateurRarete;
        this.attaque = 85 * multiplicateurRarete;
        this.defense = 120 * multiplicateurRarete;
        this.vitesse = 75 * multiplicateurRarete;
        this.taux_critiques = 0.05;
        this.degat_critiques = 1.10;
        this.taux_precisions = 100.00;
        this.taux_esquives = 0.05;
        this.taux_blocage = 0.20;
        this.reduction_blocage = 0.25;
        this.degats_renvoi = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Poing de diamant", "Dodon Pa", "Canon Triplase"};
    }

   
  @Override
public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Tien utilise Poing de diamant !");
    Combat.attaquer(this, cible, log);
}

@Override
public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Tien utilise Dodon Pa !");
    double degats = this.getAttaque() * 0.80;
    Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    Combat.appliquerEffet(this, new BuffDefense(0.10, 2), log);
    Combat.appliquerEffet(this, new BuffBlocage(0.10, 2), log);
}

@Override
public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Tien utilise Canon Triplase !");
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
    if (cible != null) {
        double degats = (this.getAttaque() * 1.00) * multiplicateurRage;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    }
    Combat.appliquerEffet(this, new Invincibilite(1), log);
    Combat.appliquerEffet(this, new BuffDefense(0.10, 2), log);
}

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Poing de diamant — inflige 100% ATK a une cible.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Dodon Pa — inflige 80% ATK a une cible, "
                + "renforce sa defense de 10% et son blocage de 10% pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Canon Triplase — inflige 100% ATK a l'ennemi avec le moins de PV, "
                + "devient invincible pendant 1 tour "
                + "et gagne 10% de defense pendant 2 tours.");
    }
}