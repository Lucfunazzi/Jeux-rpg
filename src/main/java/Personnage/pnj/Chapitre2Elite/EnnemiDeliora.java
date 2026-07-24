package Personnage.pnj.Chapitre2Elite;

import Personnage.PersonnageBase;
import Combat.Combat;
import Effets.ReductionDefense;
import java.util.List;

/**
 * Deliora — boss de fin du Chapitre 2 Elite, combattu par l'equipe du joueur
 * (pas d'invite comme Natsu/Gray/Lucy, pas de flashback scripte comme dans
 * le Chapitre 2 normal). Version elite : meme trame que EnnemiDeliora du
 * Chapitre 2, destinee a etre ajustee (legerement plus forte).
 */
public class EnnemiDeliora extends PersonnageBase {

    public EnnemiDeliora() { this(30); }

    public EnnemiDeliora(int niveau) {
        this.nom    = "Deliora";
        this.niveau = niveau;
        this.type   = "Demon";
        this.role   = "Tank";
        this.rarete = "A";

        double niv = Math.pow(1.05, niveau - 1);
        double vit = Math.pow(1.03, niveau - 1);
        this.vie     = 1000.0 * niv;
        this.attaque =  150.0 * niv;
        this.defense =  50.0 * niv;
        this.vitesse =   110.0 * vit;

        this.taux_critiques    = 0.15;
        this.degat_critiques   = 1.40;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.05;
        this.taux_blocage      = 0.10;
        this.reduction_blocage = 0.15;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " frappe " + cible.getNom() + " de son poing devastateur !");
        Combat.attaquer(this, cible, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " libere un souffle destructeur sur " + cible.getNom() + " !");
        double degats = this.getAttaque() * 1.50;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new ReductionDefense(0.15, 2), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add(this.nom + " dechaine toute sa puissance destructrice !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) {
            multiplicateurRage += (this.getRage() - 100) / 100.0;
        }
        for (PersonnageBase cible : equipeEnnemie) {
            if (cible.estVivant()) {
                double degats = (this.getAttaque() * 1.20) * multiplicateurRage;
                Combat.appliquerDegatsAvecLog(this, cible, degats, log);
            }
        }
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Poing Devastateur", "Souffle Destructeur", "Rage du Demon"};
    }
    @Override public void descriptionAttaqueBase() {
        System.out.println("Poing Devastateur : attaque de base sur la cible.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Souffle Destructeur : inflige 150% ATK et reduit la defense de la cible de 15% pendant 2 tours.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Rage du Demon : inflige 120% ATK a tous les ennemis.");
    }
}
