package Personnage.pnj.EnnemisGeneriques;

import Personnage.PersonnageBase;
import Combat.Combat;
import Effets.*;
import java.util.List;

public class EnnemiMage5Tank extends PersonnageBase {

    public EnnemiMage5Tank(Variante variante, int niveau) {
        this.nom    = "Chevalier magique";
        this.niveau = niveau;
        this.type="Chevalier";
        this.role   = "Tank";
        this.rarete = Variante.raretePourNiveau(niveau);

        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        double mult = variante.getMultiplicateur() * Variante.multiplicateurRang(niveau);
        // Ecart historique entre variantes, preserve tel quel : le Chapitre 1 est plus
        // fragile/esquive moins (def 50, esquive/blocage 0.10) que toutes les autres
        // variantes (def 45, esquive 0.12, blocage 0.18), sauf Chapitre2/2Elite plus tanky
        // (def 60).
        boolean estChapitre1 = variante == Variante.CHAPITRE_1;
        double defenseBase = estChapitre1 ? 50.0
                : (variante == Variante.CHAPITRE_2 || variante == Variante.CHAPITRE_2_ELITE) ? 60.0
                : 45.0;
        this.vie     = 450.0 * niv * mult;
        this.attaque =  55.0 * niv * mult;
        this.defense = defenseBase * niv * mult;
        this.vitesse =  75.0 * vit * mult;

        this.taux_critiques    = 0.05;
        this.degat_critiques   = 1.10;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = estChapitre1 ? 0.10 : 0.12;
        this.taux_blocage      = estChapitre1 ? 0.10 : 0.18;
        this.reduction_blocage = 0.20;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " frappe " + cible.getNom() + " avec sa lame du chevalier!");
        double degats = this.getAttaque() * 1.10;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, new BuffDefense(0.15,2), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " Se protége avec son bouclier !");
        Combat.appliquerEffet(this, new Bouclier(this.getVieMax() * 0.30), log);
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Frappe de la Lame", "Lame du Chevalier", "Bouclier Absolu"};
    }
    @Override public void descriptionAttaqueBase() {
        System.out.println("Frappe de la Lame : attaque de base sur la cible.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Lame du Chevalier : inflige 110% ATK a la cible et augmente sa DEF de 15% pendant 2 tours.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Bouclier Absolu : se protège avec un bouclier equivalent a 30% de ses PV max.");
    }
}
