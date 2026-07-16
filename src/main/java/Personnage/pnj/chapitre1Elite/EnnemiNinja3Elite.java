package Personnage.pnj.chapitre1Elite;

import Personnage.PersonnageBase;
import Combat.Combat;
import Effets.Marquage;
import Effets.ReductionAttaque;
import java.util.ArrayList;
import java.util.List;

public class EnnemiNinja3Elite extends PersonnageBase {

    public EnnemiNinja3Elite() {
        this(16);
    }

    public EnnemiNinja3Elite(int niveau) {
        this.nom    = "Executeur";
        this.niveau = niveau;
        this.type   = "Ninja";
        this.role   = "DPS";
        this.rarete = "C";

        double niv = Math.pow(1.05, niveau - 1);
        double vit = Math.pow(1.03, niveau - 1);
        this.vie     = 216.5 * niv;
        this.attaque = 105.8 * niv;
        this.defense =  38.5 * niv;
        this.vitesse =  77.0 * vit;

        this.taux_critiques    = 0.30;
        this.degat_critiques   = 1.55;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.12;
        this.taux_blocage      = 0.02;
        this.reduction_blocage = 0.02;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " frappe " + cible.getNom() + " avec brutalite !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " execute " + cible.getNom() + " !");
        double degats = this.getAttaque() * 1.80;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new Marquage(2, 0.20), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " seme la destruction sur toute l'equipe !");
        for (PersonnageBase cible : equipeEnnemie) {
            if (cible.estVivant()) {
                double degats = this.getAttaque() * 1.40;
                Combat.appliquerDegatsAvecLog(this, cible, degats, log);
                Combat.appliquerEffet(this, cible, new ReductionAttaque(0.15, 2), log);
            }
        }
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Frappe Brutale", "Execution", "Destruction Totale"};
    }
    @Override public void descriptionAttaqueBase() {
        System.out.println("Frappe Brutale : attaque de base sur la cible.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Execution : inflige 180% ATK a la cible et applique Marquage 2 tours.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Destruction Totale : inflige 140% ATK a toute l'equipe ennemie et reduit leur ATK de 15% pendant 2 tours.");
    }
}