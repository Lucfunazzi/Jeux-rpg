package Personnage.pnj.Chapitre3;

import Combat.Combat;
import Effets.BuffDefense;
import Effets.Confusion;
import Effets.Etourdissement;
import Effets.ReductionAttaque;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Sol (Mister Sol) — Element 4 de Phantom Lord, Magie de la Terre, rang B.
 * Parle avec un accent anglais. Fusionner avec le sol pour se déplacer.
 * Sort spécial : Merci la Vie — illusion par les mauvais souvenirs (confusion/étourdissement).
 * Show Time (Rosshu Konseruto) : envoie des gravats de pierre.
 * Sol Liquide : rend la terre molle/liquide qui ensevelit les ennemis.
 * Sonate de Plâtre (Plâtre Sonata) : attaque de terre puissante.
 */
public class EnnemiSol extends PersonnageBase {

    public EnnemiSol() { this(26); }

    public EnnemiSol(int niveau) {
        this.nom    = "Sol";
        this.niveau = niveau;
        this.type   = "Elementaliste";
        this.role   = "Tank";
        this.rarete = "B";

        double mult = 1.30;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 320.0 * mult * niv;
        this.attaque =  85.0 * mult * niv;
        this.defense = 100.0 * mult * niv;
        this.vitesse =  70.0 * mult * vit;

        this.taux_critiques    = 0.08;
        this.degat_critiques   = 1.15;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.10;
        this.taux_blocage      = 0.18;
        this.reduction_blocage = 0.20;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Show Time — Gravats de Pierre", "Merci la Vie — Illusion de Mémoire", "Sol Liquide — Sonate de Plâtre"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Sol manie les gravats de pierre et les projette sur " + cible.getNom() + " — Show Time !");
        Combat.attaquer(this, cible, log);
        
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Sol plonge dans le sol et lit les souvenirs douloureux de " + cible.getNom() + " — Merci la Vie !");
        double degats = this.getAttaque() * 1.10;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        // Merci la Vie : illusion des mauvais souvenirs = confusion ou étourdissement
        if (Math.random() < 0.50) {
            Combat.appliquerEffet(this, cible, new Confusion(2), log);
        } else {
            Combat.appliquerEffet(this, cible, new Etourdissement(1), log);
        }
        Combat.appliquerEffet(this, cible, new ReductionAttaque(0.18, 2), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Sol liquéfie le sol sous les pieds ennemis — la Sonate de Plâtre les engloutit !");
        Combat.appliquerEffet(this, new BuffDefense(0.15, 3), log);
        
      for (PersonnageBase cibleTank : equipeEnnemie) {
            if (cibleTank.estVivant() && cibleTank.getRole().equals("Tank")) {
                double degats = this.getAttaque() * 0.80;
                Combat.appliquerDegatsAvecLog(this, cibleTank, degats, log);
                
                Combat.appliquerEffet(this, cibleTank, new Etourdissement(2), log);
            }
      }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Show Time — Gravats de Pierre : Inflige 100% ATK, réduit ATK de 12% pendant 2 tours.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Merci la Vie — Illusion de Mémoire : Inflige 110% ATK, 50% confusion ou étourdissement, réduit ATK de 18%.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Sol Liquide — Sonate de Plâtre : +30% DEF, alliés +15% DEF, 90% ATK à tous + étourdissement");
    }
}
