package Personnage.pnj.Chapitre2Elite;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;

public class EnnemiInoElite extends PersonnageBase {

    public EnnemiInoElite() {
        this(26);
    }

    public EnnemiInoElite(int niveau) {
        this.nom    = "Ino";
        this.niveau = niveau;
        this.type   = "Ninja";
        this.role   = "Support";
        this.rarete = "B";

        double mult = 1.50;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 216.5 * mult * niv;
        this.attaque =  52.9 * mult * niv;
        this.defense =  48.1 * mult * niv;
        this.vitesse =  73.8 * mult * vit;

        this.taux_critiques    = 0.05;
        this.degat_critiques   = 1.10;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.10;
        this.taux_blocage      = 0.08;
        this.reduction_blocage = 0.10;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Claque de la Fleur", "Grande Transposition", "Ninjutsu Medical"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Ino utilise Claque de la Fleur !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Ino utilise Grande Transposition !");
        double degats = this.getAttaque() * 0.60;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new Confusion(2), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Ino utilise Ninjutsu Medical !");
        for (PersonnageBase allie : equipeAlliee) {
            if (allie.estVivant()) {
                double soin = this.getAttaque() * 0.80;
                allie.recevoirSoin(soin, log);
                log.add(allie.getNom() + " est soigne par Ino pour " + String.format("%.0f", soin) + " PV !");
            }
        }
    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Claque de la Fleur — inflige 100% ATK a une cible.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Grande Transposition — inflige 60% ATK a une cible et applique Confusion 2 tours.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Ninjutsu Medical — soigne toute l'equipe alliee de 80% ATK.");
    }
}