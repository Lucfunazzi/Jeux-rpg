package Personnage.pnj.Chapitre1;

import Combat.Combat;
import Effets.Brulure;
import Effets.ReductionDefense;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Bora — Mage de la Flamme Écarlate, rang C.
 * Arc Prologue : ancien mage véreux qui vendait des esclaves.
 */
public class EnnemiBora extends PersonnageBase {

    public EnnemiBora() { this(4); }

    public EnnemiBora(int niveau) {
        this.nom    = "Bora";
        this.niveau = niveau;
        this.type="Elementaliste";
        this.role   = "DPS";
        this.rarete = "C";

        double niv = Math.pow(1.05, niveau - 1);
        double vit = Math.pow(1.03, niveau - 1);
        this.vie     = 200.0 * niv;
        this.attaque =  85.0 * niv;
        this.defense =  45.0 * niv;
        this.vitesse =  75.0 * vit;

        this.taux_critiques    = 0.12;
        this.degat_critiques   = 1.20;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.08;
        this.taux_blocage      = 0.03;
        this.reduction_blocage = 0.05;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Flamme Écarlate", "Tornade de Feu", "Pluie d'Étincelles"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Bora invoque une flamme écarlate sur " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
        Combat.appliquerEffet(this, cible, new Brulure(1, 0.05), log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Bora déchaîne une tornade de feu sur " + cible.getNom() + " !");
        double degats = this.getAttaque() * 1.30;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new ReductionDefense(0.10, 2), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Bora libère une pluie d'étincelles sur tous les ennemis !");
        for (PersonnageBase cible : equipeEnnemie) {
            if (cible.estVivant()) {
                double degats = this.getAttaque() * 0.70;
                Combat.appliquerDegatsAvecLog(this, cible, degats, log);
                Combat.appliquerEffet(this, cible, new Brulure(1, 0.04), log);
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Flamme Écarlate — Inflige 100% ATK et brûle la cible 1 tour (5% PV/tour).");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Tornade de Feu — Inflige 130% ATK et réduit la Défense de 10% pendant 2 tours.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Pluie d'Étincelles — Inflige 70% ATK à tous les ennemis et les brûle 1 tour.");
    }
}
