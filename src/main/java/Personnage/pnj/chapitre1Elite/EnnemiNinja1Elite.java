package Personnage.pnj.chapitre1Elite;

import Personnage.PersonnageBase;
import Combat.Combat;
import Effets.Saignement;
import Effets.BuffBlocage;
import java.util.ArrayList;
import java.util.List;

public class EnnemiNinja1Elite extends PersonnageBase {

    public EnnemiNinja1Elite() {
        this(15);
    }

    public EnnemiNinja1Elite(int niveau) {
        this.nom    = "Ninja de l'Ombre";
        this.niveau = niveau;
        this.type="Elementaliste";
        this.role   = "Tank";
        this.rarete = "C";

        double niv = Math.pow(1.05, niveau - 1);
        double vit = Math.pow(1.03, niveau - 1);
        this.vie     = 505.1 * niv;
        this.attaque =  75.8 * niv;
        this.defense =  60.6 * niv;
        this.vitesse =  59.5 * vit;

        this.taux_critiques    = 0.10;
        this.degat_critiques   = 1.30;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.12;
        this.taux_blocage      = 0.18;
        this.reduction_blocage = 0.30;
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
        Combat.appliquerEffet(this, cible, new Saignement(2, 0.04), log);
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
        Combat.appliquerEffet(this, new BuffBlocage(0.15, 2), log);
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Shuriken", "Technique de l'Ombre", "Invasion de Clones"};
    }
    @Override public void descriptionAttaqueBase() {
        System.out.println("Shuriken : attaque de base sur la cible.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Technique de l'Ombre : inflige 130% ATK a la cible et applique Saignement (4% PV max/tour) 2 tours.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Invasion de Clones : inflige 110% ATK a toute l'equipe ennemie et gagne 15% de blocage pendant 2 tours.");
    }
}