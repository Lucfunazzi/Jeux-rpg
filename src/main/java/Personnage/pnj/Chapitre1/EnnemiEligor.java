package Personnage.pnj.Chapitre1;

import Combat.Combat;
import Effets.BuffDefense;
import Effets.ReductionAttaque;
import Effets.ReductionVitesse;
import Effets.Silence;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Eligoal — Leader de la Guilde Eisenwald, surnommé "Shinigami", rang C.
 * Magie du Vent : attaques de vent tranchantes invisibles.
 * Sort signature : Mur de Vent (barrière de tornades qui emprisonne une zone entière).
 * Mur Tempête : bouclier de vent capable de bloquer les attaques ennemies.
 */
public class EnnemiEligor extends PersonnageBase {

    public EnnemiEligor() { this(8); }

    public EnnemiEligor(int niveau) {
        this.nom    = "Eligoal";
        this.niveau = niveau;
        this.type   = "Elementaliste";
        this.role   = "DPS";
        this.rarete = "C";

        double mult = 1.20;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 270.0 * mult * niv;
        this.attaque = 100.0 * mult * niv;
        this.defense =  65.0 * mult * niv;
        this.vitesse =  90.0 * mult * vit;

        this.taux_critiques    = 0.15;
        this.degat_critiques   = 1.30;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.14;
        this.taux_blocage      = 0.08;
        this.reduction_blocage = 0.10;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Lame de Vent Tranchante", "Mur Tempête", "Mur de Vent — Prison de Tornades"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Eligoal déchaîne des lames de vent invisibles et tranchantes sur " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
      
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Eligoal déplace sa main gauche et crée un bouclier de vent dévastateur !");
       
        double degats = this.getAttaque() * 1.20;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new ReductionAttaque(0.10, 2), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Eligoal érige un Mur de Vent colossal — une prison de tornades tranchantes emprisonne l'équipe ennemie !");
        for (PersonnageBase cible : equipeEnnemie) {
            if (cible.estVivant()) {
                double degats = this.getAttaque() * 0.50;
                Combat.appliquerDegatsAvecLog(this, cible, degats, log);
                
                Combat.appliquerEffet(this, cible, new ReductionVitesse(0.10, 2), log);
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Lame de Vent Tranchante — Inflige 100% ATK, réduit la VIT de 15% pendant 2 tours.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Mur Tempête — +25% DEF, inflige 120% ATK, réduit ATK cible de 20% pendant 2 tours.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Mur de Vent — Inflige 85% ATK à tous, silence 2 tours, réduit VIT de 25%.");
    }
}
