package Personnage.pnj.chapitre1Elite;

import Effets.Etourdissement;
import Effets.ReductionAttaque;
import Personnage.PersonnageBase;
import Combat.Combat;
import java.util.ArrayList;
import java.util.List;

public class EnnemiSoigneur2Elite extends PersonnageBase {

    public EnnemiSoigneur2Elite() {
        this(19);
    }

    public EnnemiSoigneur2Elite(int niveau) {
        this.nom    = "Mage Perturbateur";
        this.niveau = niveau;
        this.type="Elementaliste";
        this.role   = "Support";
        this.rarete = "C";

        double niv = Math.pow(1.05, niveau - 1);
        double vit = Math.pow(1.03, niveau - 1);
        this.vie     = 332.4 * niv;
        this.attaque =  83.1 * niv;
        this.defense =  49.9 * niv;
        this.vitesse = 176.2 * vit;

        this.taux_critiques    = 0.15;
        this.degat_critiques   = 1.30;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.08;
        this.taux_blocage      = 0.08;
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
        PersonnageBase tank = null;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant() && ennemi.getRole().equals("Tank")) {
                tank = ennemi;
                break;
            }
        }
        if (tank == null) {
            for (PersonnageBase ennemi : equipeEnnemie) {
                if (ennemi.estVivant()) {
                    if (tank == null || ennemi.getVie() < tank.getVie())
                        tank = ennemi;
                }
            }
        }
        if (tank != null) {
            Combat.appliquerEffet(this, tank, new Etourdissement(1), log);
            Combat.appliquerEffet(this, tank, new ReductionAttaque(0.10, 2), log);
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " perturbe le champ de bataille !");
        for (PersonnageBase allie : equipeAlliee) {
            if (allie.estVivant()) {
                double soin = this.getAttaque() * 0.70;
                allie.recevoirSoin(soin, log);
            }
        }
        PersonnageBase tank = null;
        for (PersonnageBase ennemi : equipeEnnemie) {
            if (ennemi.estVivant() && ennemi.getRole().equals("Tank")) {
                tank = ennemi;
                break;
            }
        }
        if (tank != null) {
            Combat.appliquerEffet(this, tank, new Etourdissement(2), log);
        }
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Eclair", "Disruption", "Perturbation Totale"};
    }
    @Override public void descriptionAttaqueBase() {
        System.out.println("Eclair : attaque de base sur la cible.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Disruption : etourdit le tank ennemi 1 tour et reduit son ATK de 10% pendant 2 tours.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Perturbation Totale : soigne les allies a 70% ATK et etourdit le tank ennemi 2 tours.");
    }
}