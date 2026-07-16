package Personnage.pnj.Chapitre2Elite;

import Combat.Combat;
import Effets.BuffAttaque;
import Effets.BuffPrecision;
import Effets.ReductionAttaque;
import Effets.ReductionVitesse;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;

public class EnnemiShikamaruElite extends PersonnageBase {

    public EnnemiShikamaruElite() {
        this(30);
    }

    public EnnemiShikamaruElite(int niveau) {
        this.nom    = "Shikamaru";
        this.niveau = niveau;
        this.type   = "Ninja";
        this.role   = "Support";
        this.rarete = "A";

        double mult = 1.60;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 237.4 * mult * niv;
        this.attaque =  57.4 * mult * niv;
        this.defense =  49.5 * mult * niv;
        this.vitesse =  79.8 * mult * vit;

        this.taux_critiques    = 0.05;
        this.degat_critiques   = 1.10;
        this.taux_precisions   = 110.00;
        this.taux_esquives     = 0.12;
        this.taux_blocage      = 0.10;
        this.reduction_blocage = 0.15;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Manipulation des Ombres", "Entrave par l'Ombre", "Strategie du Maitre"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Shikamaru utilise Manipulation des Ombres !");
        Combat.attaquer(this, cible, log);
        Combat.appliquerEffet(this, cible, new ReductionVitesse(0.05, 1), log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Shikamaru utilise Entrave par l'Ombre !");
        double degats = this.getAttaque() * 0.90;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new ReductionAttaque(0.15, 2), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Shikamaru utilise Strategie du Maitre !");
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant()) {
                double degats = this.getAttaque() * 0.80;
                Combat.appliquerDegatsAvecLog(this, ennemi, degats, log);
                Combat.appliquerEffet(this, ennemi, new ReductionAttaque(0.15, 2), log);
            }
        }
        for (PersonnageBase allie : equipeAlliee) {
            if (allie.estVivant()) {
                Combat.appliquerEffet(this, allie, new BuffAttaque(0.15, 2), log);
                Combat.appliquerEffet(this, allie, new BuffPrecision(15.00, 2), log);
            }
        }
    }

    @Override
    public void descriptionAttaqueBase() {
        System.out.println("Manipulation des Ombres — inflige 100% ATK a une cible et reduit sa vitesse de 5% pendant 1 tour.");
    }

    @Override
    public void descriptionAttaqueSpeciale() {
        System.out.println("Entrave par l'Ombre — inflige 90% ATK a une cible et reduit son ATK de 15% pendant 2 tours.");
    }

    @Override
    public void descriptionAttaqueUltime() {
        System.out.println("Strategie du Maitre — inflige 80% ATK a tous les ennemis et reduit leur ATK de 15% pendant 2 tours. Augmente l'ATK et la precision de tous les allies de 15% pendant 2 tours.");
    }
}