package Personnage.pnj.Chapitre3Elite;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

public class EnnemiSoigneurDBZElite extends PersonnageBase {

    public EnnemiSoigneurDBZElite() { this(40); }

    public EnnemiSoigneurDBZElite(int niveau) {
        this.nom    = "Moine d'Elite";
        this.niveau = niveau;
        this.type   = "Guerrier";
        this.role   = "Support";
        this.rarete = "B";

        double mult = 1.3;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 260.0 * mult * niv;
        this.attaque = 80.0 * mult * niv;
        this.defense = 55.0 * mult * niv;
        this.vitesse = 90.0 * mult * vit;

        this.taux_critiques    = 0.05;
        this.degat_critiques   = 1.1;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.06;
        this.taux_blocage      = 0.08;
        this.reduction_blocage = 0.15;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Frappe", "Soin d'Elite", "Benediction du Tournoi"};
    }
    @Override public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.getNom() + " attaque " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
    }
    @Override public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.getNom() + " soigne son equipe !");
        for (PersonnageBase a : equipeAlliee)
            if (a.estVivant()) {
                a.recevoirSoin(this.getAttaque() * 0.50, log);
                Combat.appliquerEffet(this, a, new BuffDefense(0.10, 2), log);
            }
    }
    @Override public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.getNom() + " — Benediction du Tournoi !");
        for (PersonnageBase a : equipeAlliee)
            if (a.estVivant()) {
                a.recevoirSoin(this.getAttaque() * 0.60, log);
                Combat.appliquerEffet(this, a, new Resurrection(0.25), log);
            }
    }
    @Override public void descriptionAttaqueBase()    { System.out.println("Frappe : attaque de base."); }
    @Override public void descriptionAttaqueSpeciale(){ System.out.println("Soin d'Elite : attaque speciale."); }
    @Override public void descriptionAttaqueUltime()  { System.out.println("Benediction du Tournoi : attaque ultime."); }
}
