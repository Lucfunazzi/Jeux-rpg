package Personnage.pnj.Chapitre3;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

public class EnnemiProtecteurDBZ extends PersonnageBase {

    public EnnemiProtecteurDBZ() { this(28); }

    public EnnemiProtecteurDBZ(int niveau) {
        this.nom    = "Protecteur du Tournoi";
        this.niveau = niveau;
        this.type   = "Guerrier";
        this.role   = "Tank";
        this.rarete = "B";

        double mult = 1.30;
        double niv = Math.pow(1.05, niveau - 1);
        double vit = Math.pow(1.03, niveau - 1);
        this.vie     = 520.0 * mult * niv;
        this.attaque =  65.0 * mult * niv;
        this.defense = 120.0 * mult * niv;
        this.vitesse =  55.0 * mult * vit;

        this.taux_critiques    = 0.05;
        this.degat_critiques   = 1.10;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.03;
        this.taux_blocage      = 0.25;
        this.reduction_blocage = 0.30;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Charge brutale", "Mur de chair", "Tremblement de Terre"};
    }
    @Override public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " charge " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
        Combat.appliquerEffet(this, new BuffDefense(0.10, 1), log);
    }
    @Override public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " se transforme en Mur de chair !");
        double bouclier = this.getVieMax() * 0.20;
        for (PersonnageBase a : equipeAlliee)
            if (a.estVivant()) Combat.appliquerEffet(this, a, new Bouclier(bouclier), log);
    }
    @Override public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " provoque un Tremblement de Terre !");
        for (PersonnageBase c : equipeEnnemie)
            if (c.estVivant()) {
                Combat.appliquerDegatsAvecLog(this, c, this.getAttaque() * 1.10, log);
                Combat.appliquerEffet(this, c, new Etourdissement(1), log);
            }
    }
    @Override public void descriptionAttaqueBase()    { System.out.println("Charge brutale : 100% ATK + +10% DEF 1 tour."); }
    @Override public void descriptionAttaqueSpeciale(){ System.out.println("Mur de chair : bouclier 20% PV max a toute l'equipe."); }
    @Override public void descriptionAttaqueUltime()  { System.out.println("Tremblement de Terre : 110% ATK tous + etourdissement 1 tour."); }
}
