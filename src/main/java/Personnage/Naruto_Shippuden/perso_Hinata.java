package Personnage.Naruto_Shippuden;
import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;

public class perso_Hinata extends PersonnageBase {

    public perso_Hinata() {
        this.nom = "Hinata";
        this.type = "Ninja";
        this.role = "Tank";
        this.rarete = "B";
        this.niveau = 1;
        double multiplicateurRarete = 1.30;
        this.vie = 700 * multiplicateurRarete;
        this.attaque = 110 * multiplicateurRarete;
        this.defense = 150 * multiplicateurRarete;
        this.vitesse = 90 * multiplicateurRarete;
        this.taux_critiques = 0.05;
        this.degat_critiques = 1.10;
        this.taux_precisions = 100.00;
        this.taux_esquives = 0.15;
        this.taux_blocage = 0.25;
        this.reduction_blocage = 0.25;
        this.degats_renvoi = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Paume du Hakke", "Paume jumelles du lion", "64 points du Hakke"};
    }

    // Attaque de base — 100% ATK
    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Hinata utilise Paume du Hakke !");
        Combat.attaquer(this, cible, log);
    }

    // Spéciale — 110% ATK + ReductionDefense 10% 2 tours
    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Hinata utilise Paume jumelles du lion !");
        double degats = this.getAttaque() * 1.10;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new ReductionDefense(0.10, 2), log);
    }

    // Ultime — BuffBlocage 15% + BuffDefense 15% + BuffTauxEsquive 10% pendant 2 tours + soin 50% ATK
    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Hinata utilise 64 points du Hakke !");

        Combat.appliquerEffet(this, new BuffBlocage(0.15, 2), log);
        Combat.appliquerEffet(this, new BuffDefense(0.15, 2), log);
        Combat.appliquerEffet(this, new BuffTauxEsquive(0.10, 2), log);

        double soin = this.getAttaque() * 0.50;
        this.recevoirSoin(soin, log);    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Paume du Hakke — inflige 100% ATK a une cible.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Paume jumelles du lion — inflige 110% ATK a une cible "
                + "et reduit sa defense de 10% pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("64 points du Hakke — augmente son blocage de 15%, "
                + "sa defense de 15% et son esquive de 10% pendant 2 tours, "
                + "et se soigne de 50% ATK.");
    }
}