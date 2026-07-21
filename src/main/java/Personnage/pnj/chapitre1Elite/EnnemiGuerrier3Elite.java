package Personnage.pnj.chapitre1Elite;

import Personnage.PersonnageBase;
import Combat.Combat;
import Effets.BuffAttaque;
import Effets.Brulure;
import java.util.ArrayList;
import java.util.List;

public class EnnemiGuerrier3Elite extends PersonnageBase {

    public EnnemiGuerrier3Elite() {
        this(14);
    }

    public EnnemiGuerrier3Elite(int niveau) {
        this.nom    = "Gladiateur Fou";
        this.niveau = niveau;
        this.type="Elementaliste";
        this.role   = "DPS";
        this.rarete = "C";

        double niv = Math.pow(1.05, niveau - 1);
        double vit = Math.pow(1.03, niveau - 1);
        this.vie     = 212.1 * niv;
        this.attaque = 132.6 * niv;
        this.defense =  79.5 * niv;
        this.vitesse =  57.9 * vit;

        this.taux_critiques    = 0.19;
        this.degat_critiques   = 1.40;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.06;
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
        log.add(this.nom + " frappe " + cible.getNom() + " avec une rage incontrolee !");
        double degats = this.getAttaque() * 1.60;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, new BuffAttaque(0.20, 2), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " tourbillonne sur tous les ennemis !");
        for (PersonnageBase cible : equipeEnnemie) {
            if (cible.estVivant()) {
                double degats = this.getAttaque() * 1.30;
                Combat.appliquerDegatsAvecLog(this, cible, degats, log);
                Combat.appliquerEffet(this, cible, new Brulure(2, 0.05), log);
            }
        }
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Taillade", "Rage Incontrolee", "Tourbillon"};
    }
    @Override public void descriptionAttaqueBase() {
        System.out.println("Taillade : attaque de base sur la cible.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Rage Incontrolee : inflige 160% ATK a la cible et augmente sa propre ATK de 20% pendant 2 tours.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Tourbillon : inflige 130% ATK a toute l'equipe ennemie et applique Brulure (5% PV/tour) 2 tours.");
    }
}