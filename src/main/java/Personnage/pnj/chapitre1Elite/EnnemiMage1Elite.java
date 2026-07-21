package Personnage.pnj.chapitre1Elite;

import Personnage.PersonnageBase;
import Combat.Combat;
import Effets.ReductionDefense;
import Effets.BuffAttaque;
import java.util.ArrayList;
import java.util.List;

public class EnnemiMage1Elite extends PersonnageBase {

    public EnnemiMage1Elite() {
        this(10);
    }

    public EnnemiMage1Elite(int niveau) {
        this.nom    = "Mage Ombral";
        this.niveau = niveau;
        this.type="Elementaliste";
        this.role   = "DPS";
        this.rarete = "C";

        double niv = Math.pow(1.05, niveau - 1);
        double vit = Math.pow(1.03, niveau - 1);
        this.vie     = 257.8 * niv;
        this.attaque =  90.2 * niv;
        this.defense =  50.3 * niv;
        this.vitesse =  84.3 * vit;

        this.taux_critiques    = 0.15;
        this.degat_critiques   = 1.30;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.05;
        this.taux_blocage      = 0.02;
        this.reduction_blocage = 0.02;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " incante un sort sombre sur " + cible.getNom() + " !");
        double degats = this.getAttaque() * 1.30;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new ReductionDefense(0.15, 2), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " declenche une onde magique sur tous les ennemis !");
        for (PersonnageBase cible : equipeEnnemie) {
            if (cible.estVivant()) {
                double degats = this.getAttaque() * 1.10;
                Combat.appliquerDegatsAvecLog(this, cible, degats, log);
            }
        }
        Combat.appliquerEffet(this, new BuffAttaque(0.10, 2), log);
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Projectile Magique", "Sort Sombre", "Onde Magique"};
    }
    @Override public void descriptionAttaqueBase() {
        System.out.println("Projectile Magique : attaque de base magique sur la cible.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Sort Sombre : inflige 130% ATK a la cible et reduit sa DEF de 15% pendant 2 tours.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Onde Magique : inflige 110% ATK a toute l'equipe ennemie et augmente sa propre ATK de 10% pendant 2 tours.");
    }
}