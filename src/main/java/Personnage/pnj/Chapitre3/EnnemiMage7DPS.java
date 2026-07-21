package Personnage.pnj.Chapitre3;


import Personnage.pnj.Chapitre2.*;
import Personnage.PersonnageBase;
import Combat.Combat;
import java.util.ArrayList;
import java.util.List;

public class EnnemiMage7DPS extends PersonnageBase {

    public EnnemiMage7DPS() {
        this(19);
    }

    public EnnemiMage7DPS(int niveau) {
        this.nom    = "Mage Executeur";
        this.niveau = niveau;
        this.type="Elementaliste";
        this.role   = "DPS";
        this.rarete = "C";

        double niv = Math.pow(1.05, niveau - 1);
        double vit = Math.pow(1.03, niveau - 1);
        this.vie     = 265.0 * niv;
        this.attaque = 108.0 * niv;
        this.defense =  42.0 * niv;
        this.vitesse = 105.0 * vit;

        this.taux_critiques    = 0.20;
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
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " Lumier sacré " + cible.getNom() + " !");
        double degats = this.getAttaque() * 1.80;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " torrent de lumière sur toute l'equipe !");
        for (PersonnageBase cible : equipeEnnemie) {
            if (cible.estVivant()) {
                double degats = this.getAttaque() *1.00;
                Combat.appliquerDegatsAvecLog(this, cible, degats, log);
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
        System.out.println("Execution : inflige 180% ATK a la cible.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Destruction Totale : inflige 140% ATK a toute l'equipe ennemie.");
    }
}
