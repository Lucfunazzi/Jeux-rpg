package Personnage.FairyTail;
import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;
public class perso_Elfman extends PersonnageBase {

    public perso_Elfman() {
        this.nom = "Elfman";
        this.niveau = 1;
        this.type="Chevalier";
        this.role = "Tank";
        this.rarete = "B";
        double multiplicateurRarete = 1.20;
        this.vie = 420 * multiplicateurRarete;
        this.attaque = 135 * multiplicateurRarete;
        this.defense = 120 * multiplicateurRarete;
        this.vitesse = 90 * multiplicateurRarete;
        this.taux_critiques = 0.08;
        this.degat_critiques = 1.30;
        this.taux_precisions = 100.00;
        this.taux_esquives = 0.08;
        this.taux_blocage = 0.15;
        this.reduction_blocage = 0.15;
        this.degats_renvoi = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Poing de bete", "Prise du demon", "Prise totale — Forme bestiale"};
    }

 
   @Override
public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Elfman utilise Poing de bete !");
    Combat.attaquer(this, cible, log);
}

@Override
public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Elfman utilise Prise du demon !");
    double degats = this.getAttaque() * 1.10;
    Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    Combat.appliquerEffet(this, new BuffBlocage(0.15, 1), log);
}

@Override
public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Elfman utilise Prise totale — Forme bestiale !");
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
    double degats = (this.getAttaque() * 0.80) * multiplicateurRage;
    Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    Combat.appliquerEffet(this, new BuffDefense(0.15, 2), log);
}
    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Poing de bete — inflige 100% ATK a une cible.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Prise du demon — inflige 110% ATK a une cible "
                + "et gagne 10% d'attaque pendant 1 tour.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Prise totale Forme bestiale — inflige 120% ATK a l'ennemi "
                + "avec le moins de PV et gagne 10% d'attaque pendant 2 tours.");
    }
}