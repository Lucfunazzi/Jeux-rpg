package Personnage.pnj.Chapitre3;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

public class EnnemiSoigneurDBZ extends PersonnageBase {

    public EnnemiSoigneurDBZ() { this(27); }

    public EnnemiSoigneurDBZ(int niveau) {
        this.nom    = "Moine du Tournoi";
        this.niveau = niveau;
        this.type   = "Guerrier";
        this.role   = "Support";
        this.rarete = "B";

        double mult = 1.30;
        double niv = Math.pow(1.05, niveau - 1);
        double vit = Math.pow(1.03, niveau - 1);
        this.vie     = 260.0 * mult * niv;
        this.attaque =  80.0 * mult * niv;
        this.defense =  55.0 * mult * niv;
        this.vitesse =  90.0 * mult * vit;

        this.taux_critiques    = 0.05;
        this.degat_critiques   = 1.10;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.06;
        this.taux_blocage      = 0.03;
        this.reduction_blocage = 0.05;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Frappe sacrée", "Soin d'urgence", "Benediction du Tournoi"};
    }
    @Override public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " frappe " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
    }
    @Override public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        PersonnageBase plusBas = null;
        for (PersonnageBase a : equipeAlliee)
            if (a.estVivant() && (plusBas == null || a.getVie() < plusBas.getVie())) plusBas = a;
        if (plusBas != null) {
            double soin = this.getAttaque() * 1.20;
            plusBas.recevoirSoin(soin, log);
            log.add(this.nom + " soigne " + plusBas.getNom() + " de " + String.format("%.1f", soin) + " PV !");
        }
    }
    @Override public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " benit toute son equipe !");
        for (PersonnageBase a : equipeAlliee)
            if (a.estVivant()) {
                double soin = this.getAttaque() * 0.80;
                a.recevoirSoin(soin, log);
                Combat.appliquerEffet(this, a, new BuffDefense(0.10, 2), log);
            }
    }
    @Override public void descriptionAttaqueBase()    { System.out.println("Frappe sacrée : 100% ATK."); }
    @Override public void descriptionAttaqueSpeciale(){ System.out.println("Soin d'urgence : soigne l'allie le plus bas de 120% ATK."); }
    @Override public void descriptionAttaqueUltime()  { System.out.println("Benediction : soin 80% ATK + +10% DEF 2 tours a toute l'equipe."); }
}
