package Personnage.pnj.Chapitre2;

import Combat.Combat;
import Effets.Gel;
import Effets.ReductionAttaque;
import Effets.ReductionVitesse;
import Effets.BuffDefense;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Leon Bastia — Mage de glace (version île Galuna), rang B.
 * Chef des mages de l'île, magie de glace puissante et contrôle de terrain.
 * Multiplicateur rang B : 1.30
 */
public class EnnemiLeon extends PersonnageBase {

    public EnnemiLeon() { this(18); }

    public EnnemiLeon(int niveau) {
        this.nom    = "Leon";
        this.niveau = niveau;
        this.type="Elementaliste";
        this.role   = "DPS";
        this.rarete = "B";

        double mult = 1.30;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 230.0 * mult * niv;
        this.attaque =  95.0 * mult * niv;
        this.defense =  65.0 * mult * niv;
        this.vitesse =  88.0 * mult * vit;

        this.taux_critiques    = 0.18;
        this.degat_critiques   = 1.35;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.10;
        this.taux_blocage      = 0.06;
        this.reduction_blocage = 0.08;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Ice-Make : Lances", "Ice-Make : Flèches de glace", "Ice-Make : Golem de glace"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Leon façonne des lances de glace et les projette sur " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
        Combat.appliquerEffet(this, cible, new ReductionVitesse(0.12, 1), log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Leon tire une rafale de flèches de glace sur " + cible.getNom() + " !");
        double degats = this.getAttaque() * 1.45;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new ReductionAttaque(0.18, 2), log);
        if (Math.random() < 0.30) {
            Combat.appliquerEffet(this, cible, new Gel(1), log);
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Leon invoque un colosse de glace qui écrase toute l'équipe ennemie !");
        for (PersonnageBase cible : equipeEnnemie) {
            if (cible.estVivant()) {
                double degats = this.getAttaque() * 1.10;
                Combat.appliquerDegatsAvecLog(this, cible, degats, log);
                Combat.appliquerEffet(this, cible, new ReductionVitesse(0.20, 2), log);
            }
        }
        Combat.appliquerEffet(this, new BuffDefense(0.15, 2), log);
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Ice-Make : Lances — Inflige 100% ATK et réduit la Vitesse de 12% pendant 1 tour.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Ice-Make : Flèches — Inflige 145% ATK, réduit ATK de 18% pendant 2 tours, 30% de gel 1 tour.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Ice-Make : Golem — Inflige 110% ATK à tous, réduit leur Vitesse de 20% pendant 2 tours, gagne 15% DEF.");
    }
}
