package Personnage.pnj.chapitre1Elite;



import Personnage.PersonnageBase;
import Effets.*;
import Combat.Combat;
import java.util.ArrayList;
import java.util.List;

public class EnnemiMage6Debuff extends PersonnageBase {

    public EnnemiMage6Debuff() {
        this(17);
    }

    public EnnemiMage6Debuff(int niveau) {
        this.nom    = "Invocateur cartessien";
        this.niveau = niveau;
        this.type="Invocateur";
        this.role   = "Support";
        this.rarete = "C";

        double niv = Math.pow(1.05, niveau - 1);
        double vit = Math.pow(1.03, niveau - 1);
        this.vie     = 210.0 * niv;
        this.attaque =  82.0 * niv;
        this.defense =  28.0 * niv;
        this.vitesse = 110.0 * vit;

        this.taux_critiques    = 0.20;
        this.degat_critiques   = 1.50;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.15;
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
        log.add(this.nom + " invoque briseur de défense" + cible.getNom() + " !");
        double degats = this.getAttaque() * 0.70;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(cible, new ReductionDefense(0.10,2), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " Invoque Vitesse Reduite !");
        for (PersonnageBase cible : equipeEnnemie) {
            if (cible.estVivant()) {
                double degats = this.getAttaque() * 0.40;
                Combat.appliquerDegatsAvecLog(this, cible, degats, log);
                Combat.appliquerEffet(this,cible ,new ReductionVitesse(0.10,2), log);
            }
        }
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Frappe de l'Ombre", "Surgissement", "Disparition"};
    }
    @Override public void descriptionAttaqueBase() {
        System.out.println("Frappe de l'Ombre : attaque de base sur la cible.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Surgissement : inflige 170% ATK a la cible.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Disparition : inflige 120% ATK a toute l'equipe ennemie.");
    }
}
