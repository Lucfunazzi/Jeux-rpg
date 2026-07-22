package Personnage.pnj.Chapitre2;

import Combat.Combat;
import Effets.BuffDefense;
import Effets.Gel;
import Effets.ReductionAttaque;
import Effets.ReductionVitesse;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Leon Bastia — Chef des mages de l'île Galuna, rang B.
 * Magie Ice-Make STATIQUE (crée des créatures de glace animées),
 * contrairement à Gray qui utilise Ice-Make dynamique.
 * Sort signature : Ice-Make : Lion / Ice-Make : Oiseau de glace.
 * Dans le wiki : type Chevalier (combattant élite).
 */
public class EnnemiLeon extends PersonnageBase {

    public EnnemiLeon() { this(18); }

    public EnnemiLeon(int niveau) {
        this.nom    = "Leon";
        this.niveau = niveau;
        this.type   = "Chevalier";
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
        return new String[]{"Ice-Make : Oiseau de Glace", "Ice-Make : Lion de Glace", "Ice-Make : Tigre Polaire"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Leon façonne un oiseau de glace qui fond sur " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
        
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Leon invoque un lion de glace qui lacère " + cible.getNom() + " !");
        double degats = this.getAttaque() * 1.50;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        
        if (Math.random() < 0.35) {
            Combat.appliquerEffet(this, cible, new Gel(1), log);
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Leon libère un tigre polaire colossal — la créature de glace s'abat sur toute l'équipe ennemie !");
        for (PersonnageBase cible : equipeEnnemie) {
            if (cible.estVivant()) {
                double degats = this.getAttaque() * 1.10;
                Combat.appliquerDegatsAvecLog(this, cible, degats, log);
                Combat.appliquerEffet(this, cible, new ReductionVitesse(0.10, 2), log);
                if (Math.random() < 0.25) {
                    Combat.appliquerEffet(this, cible, new Gel(1), log);
                }
            }
        }
       
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Ice-Make : Oiseau de Glace — Inflige 100% ATK et réduit la VIT de 15% pendant 2 tours.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Ice-Make : Lion de Glace — Inflige 150% ATK, réduit ATK de 20% pendant 2 tours, 35% de gel 1 tour.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Ice-Make : Tigre Polaire — Inflige 110% ATK à tous, réduit VIT de 25%.");
    }
}
