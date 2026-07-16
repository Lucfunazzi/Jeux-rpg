package Personnage.Naruto_Shippuden;
import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;
public class perso_Tenten extends PersonnageBase {

    public perso_Tenten() {
        this.nom = "Tenten";
        this.niveau = 1;
        this.type = "Ninja";
        this.role = "DPS";
        this.rarete = "C";
        double multiplicateurRarete = 1.00;
        this.vie = 250 * multiplicateurRarete;
        this.attaque = 130 * multiplicateurRarete;   // ramené à niveau rang C cohérent
        this.defense = 95 * multiplicateurRarete;
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
        return new String[]{"Lancer de shuriken", "Les mille lames", "Lame de metal"};
    }

   
   @Override
public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Tenten utilise Lancer de shuriken !");
    Combat.attaquer(this, cible, log);
}

@Override
public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Tenten utilise Les mille lames !");
    double degats = this.getAttaque() * 1.10;
    Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    Combat.appliquerEffet(this, new BuffAttaque(0.10, 1), log);
}

@Override
public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
    log.add("Tenten utilise Lame de metal !");
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
    Combat.appliquerEffet(this, cible, new Saignement(2, 0.08), log);
}

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Lancer de shuriken — inflige 100% ATK a une cible.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Les mille lames — inflige 110% ATK a une cible "
                + "et gagne 10% d'attaque pendant 1 tour.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Lame de metal — inflige 120% ATK a l'ennemi avec le moins de PV "
                + "et lui applique Saignement (8% PV/tour) pendant 2 tours.");
    }
}