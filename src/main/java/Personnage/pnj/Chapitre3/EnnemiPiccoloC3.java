package Personnage.pnj.Chapitre3;

import Combat.Combat;
import Effets.*;
import Personnage.PersonnageBase;
import java.util.List;

public class EnnemiPiccoloC3 extends PersonnageBase {

    public EnnemiPiccoloC3() { this(34); }

    public EnnemiPiccoloC3(int niveau) {
        this.nom    = "Piccolo";
        this.niveau = niveau;
        this.type   = "Guerrier";
        this.role   = "Support";
        this.rarete = "A";

        double mult = 1.4;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 560.0 * mult * niv;
        this.attaque = 180.0 * mult * niv;
        this.defense = 130.0 * mult * niv;
        this.vitesse = 115.0 * mult * vit;

        this.taux_critiques    = 0.12;
        this.degat_critiques   = 1.3;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.08;
        this.taux_blocage      = 0.1;
        this.reduction_blocage = 0.15;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override public String[] getNomsAttaques() {
        return new String[]{"Makankosappo", "Bouclier Namek", "Special Beam Cannon"};
    }
    @Override public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Piccolo tire un rayon sur " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
        Combat.appliquerEffet(this, cible, new ReductionAttaque(0.10, 2), log);
    }
    @Override public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Piccolo protege son equipe avec un Bouclier Namek !");
        double bouclier = this.getVieMax() * 0.25;
        for (PersonnageBase a : equipeAlliee)
            if (a.estVivant()) Combat.appliquerEffet(this, a, new Bouclier(bouclier), log);
    }
    @Override public void attaqueUltime(List<PersonnageBase> equipeAlliee, List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Piccolo perfore tout avec son Special Beam Cannon !");
        // Cible l'ennemi avec le plus d'ATK
        PersonnageBase cibleUltime = null;
        for (PersonnageBase c : equipeEnnemie)
            if (c.estVivant() && (cibleUltime == null || c.getAttaque() > cibleUltime.getAttaque()))
                cibleUltime = c;
        if (cibleUltime != null) {
            Combat.appliquerDegatsAvecLog(this, cibleUltime, this.getAttaque() * 2.20, log);
            Combat.appliquerEffet(this, cibleUltime, new Paralysie(2,0.20), log);
        }
    }
    @Override public void descriptionAttaqueBase()    { System.out.println("Makankosappo : attaque de base."); }
    @Override public void descriptionAttaqueSpeciale(){ System.out.println("Bouclier Namek : attaque speciale."); }
    @Override public void descriptionAttaqueUltime()  { System.out.println("Special Beam Cannon : attaque ultime."); }
}