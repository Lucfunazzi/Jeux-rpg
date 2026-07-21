package Personnage.pnj.Chapitre2Elite;

import Personnage.pnj.Chapitre1.*;
import Personnage.PersonnageBase;
import Combat.Combat;
import java.util.ArrayList;
import java.util.List;

public class EnnemiTank1Elite extends PersonnageBase {

    public EnnemiTank1Elite() {
        this(17);
    }

    public EnnemiTank1Elite(int niveau) {
        this.nom    = "Colosse de Pierre";
        this.niveau = niveau;
        this.type="Elementaliste";
        this.role   = "Tank";
        this.rarete = "C";

        double niv = Math.pow(1.05, niveau - 1);
        double vit = Math.pow(1.03, niveau - 1);
        this.vie     = 500.0 * niv;
        this.attaque =  60.0 * niv;
        this.defense = 110.0 * niv;
        this.vitesse =  45.0 * vit;

        this.taux_critiques    = 0.05;
        this.degat_critiques   = 1.10;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.04;
        this.taux_blocage      = 0.25;
        this.reduction_blocage = 0.30;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " percute " + cible.getNom() + " avec son bouclier !");
        double degats = this.getAttaque() * 1.40;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " provoque un tremblement de terre sur tous les ennemis !");
        for (PersonnageBase cible : equipeEnnemie) {
            if (cible.estVivant()) {
                double degats = this.getAttaque() * 1.20;
                Combat.appliquerDegatsAvecLog(this, cible, degats, log);
            }
        }
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Poing Colossal", "Charge de Bouclier", "Tremblement de Terre"};
    }
    @Override public void descriptionAttaqueBase() {
        System.out.println("Poing Colossal : attaque de base sur la cible.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Charge de Bouclier : inflige 140% ATK a la cible.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Tremblement de Terre : inflige 120% ATK a toute l'equipe ennemie.");
    }
}
