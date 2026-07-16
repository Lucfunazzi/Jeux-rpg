package Personnage.pnj.chapitre1Elite;

import Combat.Combat;
import Effets.BuffBlocage;
import Effets.BuffDefense;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;

public class EnnemiIrukaElite extends PersonnageBase {

    public EnnemiIrukaElite() {
        this(15);
    }

    public EnnemiIrukaElite(int niveau) {
        this.nom    = "Iruka";
        this.niveau = niveau;
        this.type   = "Ninja";
        this.role   = "Tank";
        this.rarete = "C";

        double niv = Math.pow(1.05, niveau - 1);
        double vit = Math.pow(1.03, niveau - 1);
        this.vie     = 757.6 * niv;
        this.attaque =  75.8 * niv;
        this.defense = 111.1 * niv;
        this.vitesse = 198.3 * vit;

        this.taux_critiques    = 0.16;
        this.degat_critiques   = 1.20;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.15;
        this.taux_blocage      = 0.25;
        this.reduction_blocage = 0.15;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Coup de kunai", "Kunai explosif", "Enseignant assidu"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Iruka utilise Coup de kunai !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Iruka utilise Kunai explosif !");
        double degats = this.getAttaque() * 0.80;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, new BuffBlocage(0.10, 1), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Iruka utilise Enseignant assidu !");
        Combat.appliquerEffet(this, new BuffBlocage(0.15, 2), log);
        Combat.appliquerEffet(this, new BuffDefense(0.05, 2), log);
        double soin = this.getAttaque() * 0.50;
        this.recevoirSoin(soin, log);
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Coup de kunai — inflige 100% ATK a une cible.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Kunai explosif — inflige 80% ATK a une cible et gagne 10% de taux de blocage pendant 1 tour.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Enseignant assidu — gagne 15% de blocage et 5% de defense pendant 2 tours, et se soigne de 50% ATK.");
    }
}