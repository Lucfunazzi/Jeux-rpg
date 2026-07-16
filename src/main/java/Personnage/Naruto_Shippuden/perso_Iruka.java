package Personnage.Naruto_Shippuden;
import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;

public class perso_Iruka extends PersonnageBase {

    public perso_Iruka() {
        this.nom = "Iruka";
        this.type = "Ninja";
        this.role = "Tank";
        this.rarete = "C";
        this.niveau = 1;
        double multiplicateurRarete = 1.00;
        this.vie = 500 * multiplicateurRarete;
        this.attaque = 80 * multiplicateurRarete;
        this.defense = 110 * multiplicateurRarete;
        this.vitesse = 70 * multiplicateurRarete;
        this.taux_critiques = 0.05;
        this.degat_critiques = 1.10;
        this.taux_precisions = 100.00;
        this.taux_esquives = 0.10;
        this.taux_blocage = 0.15;
        this.reduction_blocage = 0.20;
        this.degats_renvoi = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Coup de kunai", "Kunai explosif", "Enseignant assidu"};
    }

    // Attaque de base — 100% ATK
    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Iruka utilise Coup de kunai !");
        Combat.attaquer(this, cible, log);
    }

    // Spéciale — 80% ATK + BuffBlocage 10% sur soi 1 tour
    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Iruka utilise Kunai explosif !");
        double degats = this.getAttaque() * 0.80;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, new BuffBlocage(0.10, 1), log);
    }

    // Ultime — BuffBlocage 15% + BuffDefense 5% pendant 2 tours + soin 50% ATK
    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Iruka utilise Enseignant assidu !");

        Combat.appliquerEffet(this, new BuffBlocage(0.15, 2), log);
        Combat.appliquerEffet(this, new BuffDefense(0.05, 2), log);

        double soin = this.getAttaque() * 0.50;
        this.recevoirSoin(soin, log);    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Coup de kunai — inflige 100% ATK a une cible.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Kunai explosif — inflige 80% ATK a une cible "
                + "et gagne 10% de taux de blocage pendant 1 tour.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Enseignant assidu — gagne 15% de blocage et 5% de defense pendant 2 tours, "
                + "et se soigne de 50% ATK.");
    }
}