package Personnage.pnj.chapitre1Elite;

import Personnage.PersonnageBase;
import Combat.Combat;
import Effets.Confusion;
import Effets.ReductionAttaque;
import java.util.ArrayList;
import java.util.List;

public class EnnemiGuerrier2Elite extends PersonnageBase {

    public EnnemiGuerrier2Elite() {
        this(10);
    }

    public EnnemiGuerrier2Elite(int niveau) {
        this.nom    = "Guerrier de Fer";
        this.niveau = niveau;
        this.type="Elementaliste";
        this.role   = "Tank";
        this.rarete = "C";

        double niv = Math.pow(1.05, niveau - 1);
        double vit = Math.pow(1.03, niveau - 1);
        this.vie     = 709.1 * niv;
        this.attaque = 128.9 * niv;
        this.defense = 116.0 * niv;
        this.vitesse =  46.0 * vit;

        this.taux_critiques    = 0.05;
        this.degat_critiques   = 1.10;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.05;
        this.taux_blocage      = 0.22;
        this.reduction_blocage = 0.25;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " frappe violemment " + cible.getNom() + " avec son arme !");
        double degats = this.getAttaque() * 1.40;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        if (Math.random() < 0.25) {
            Combat.appliquerEffet(this, cible, new Confusion(1), log);
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " effectue une charge devastatrice sur tous les ennemis !");
        for (PersonnageBase cible : equipeEnnemie) {
            if (cible.estVivant()) {
                double degats = this.getAttaque() * 1.20;
                Combat.appliquerDegatsAvecLog(this, cible, degats, log);
                Combat.appliquerEffet(this, cible, new ReductionAttaque(0.10, 2), log);
            }
        }
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Coup de Bouclier", "Frappe Brutale", "Charge Devastatrice"};
    }
    @Override public void descriptionAttaqueBase() {
        System.out.println("Coup de Bouclier : attaque de base sur la cible.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Frappe Brutale : inflige 140% ATK a la cible. 25% de chance de Confusion 1 tour.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Charge Devastatrice : inflige 120% ATK a toute l'equipe ennemie et reduit leur ATK de 10% pendant 2 tours.");
    }
}