package Personnage.pnj.Chapitre2;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;

public class EnnemiHinata extends PersonnageBase {

    public EnnemiHinata() {
        this(14);
    }

    public EnnemiHinata(int niveau) {
        this.nom    = "Hinata";
        this.niveau = niveau;
        this.type   = "Ninja";
        this.role   = "Tank";
        this.rarete = "B";

        double mult = 1.30;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 424.3 * mult * niv;
        this.attaque =  63.6 * mult * niv;
        this.defense =  87.5 * mult * niv;
        this.vitesse =  68.1 * mult * vit;

        this.taux_critiques    = 0.05;
        this.degat_critiques   = 1.10;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.15;
        this.taux_blocage      = 0.25;
        this.reduction_blocage = 0.25;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Paume du Hakke", "Paume jumelles du lion", "64 points du Hakke"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Hinata utilise Paume du Hakke !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Hinata utilise Paume jumelles du lion !");
        double degats = this.getAttaque() * 1.10;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new ReductionDefense(0.10, 2), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Hinata utilise 64 points du Hakke !");
        Combat.appliquerEffet(this, new BuffBlocage(0.15, 2), log);
        Combat.appliquerEffet(this, new BuffDefense(0.15, 2), log);
        Combat.appliquerEffet(this, new BuffTauxEsquive(0.10, 2), log);
        double soin = this.getAttaque() * 0.50;
        this.recevoirSoin(soin, log);
    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Paume du Hakke — inflige 100% ATK a une cible.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Paume jumelles du lion — inflige 110% ATK a une cible et reduit sa defense de 10% pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("64 points du Hakke — augmente son blocage de 15%, sa defense de 15% et son esquive de 10% pendant 2 tours, et se soigne de 50% ATK.");
    }
}