package Personnage.pnj.Chapitre2;

import Personnage.pnj.Chapitre1.*;
import Personnage.PersonnageBase;
import Combat.Combat;
import java.util.ArrayList;
import java.util.List;

public class EnnemiNinja1 extends PersonnageBase {

    public EnnemiNinja1() {
        this(6);
    }

    public EnnemiNinja1(int niveau) {
        this.nom    = "Ninja de l'Ombre";
        this.niveau = niveau;
        this.type   = "Ninja";
        this.role   = "Tank";
        this.rarete = "C";

        double niv = Math.pow(1.05, niveau - 1);
        double vit = Math.pow(1.03, niveau - 1);
        this.vie     = 450.0 * niv;
        this.attaque =  55.0 * niv;
        this.defense =  95.0 * niv;
        this.vitesse =  75.0 * vit;

        this.taux_critiques    = 0.05;
        this.degat_critiques   = 1.10;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.12;
        this.taux_blocage      = 0.18;
        this.reduction_blocage = 0.20;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " lance un shuriken sur " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " frappe " + cible.getNom() + " avec une technique de l'ombre !");
        double degats = this.getAttaque() * 1.30;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " invoque des clones sur tous les ennemis !");
        for (PersonnageBase cible : equipeEnnemie) {
            if (cible.estVivant()) {
                double degats = this.getAttaque() * 1.10;
                Combat.appliquerDegatsAvecLog(this, cible, degats, log);
            }
        }
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Shuriken", "Technique de l'Ombre", "Invasion de Clones"};
    }
    @Override public void descriptionAttaqueBase() {
        System.out.println("Shuriken : attaque de base sur la cible.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Technique de l'Ombre : inflige 130% ATK a la cible.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Invasion de Clones : inflige 110% ATK a toute l'equipe ennemie.");
    }
}
