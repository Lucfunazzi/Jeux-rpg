package Personnage.pnj.Chapitre2;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;

public class EnnemiChoji extends PersonnageBase {

    public EnnemiChoji() {
        this(15);
    }

    public EnnemiChoji(int niveau) {
        this.nom    = "Choji";
        this.niveau = niveau;
        this.type   = "Ninja";
        this.role   = "Tank";
        this.rarete = "B";

        double mult = 1.30;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 378.8 * mult * niv;
        this.attaque =  60.6 * mult * niv;
        this.defense =  65.7 * mult * niv;
        this.vitesse =  52.9 * mult * vit;

        this.taux_critiques    = 0.05;
        this.degat_critiques   = 1.10;
        this.taux_precisions   = 95.00;
        this.taux_esquives     = 0.05;
        this.taux_blocage      = 0.20;
        this.reduction_blocage = 0.30;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Coup de Poing Decuple", "Boulet Humain", "Decuplement Partiel"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Choji utilise Coup de Poing Decuple !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Choji utilise Boulet Humain !");
        double degats = this.getAttaque() * 1.20;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new ReductionDefense(0.15, 2), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Choji utilise Decuplement Partiel !");
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = this.getAttaque() * 1.30;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
            }
        }
        Combat.appliquerEffet(this, new BuffDefense(0.25, 2), log);
    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Coup de Poing Decuple — inflige 100% ATK a une cible.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Boulet Humain — inflige 120% ATK a une cible et reduit sa defense de 15% pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Decuplement Partiel — inflige 130% ATK a tous les ennemis et augmente sa propre defense de 25% pendant 2 tours.");
    }
}