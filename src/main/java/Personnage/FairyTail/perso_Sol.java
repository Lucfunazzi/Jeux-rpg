package Personnage.FairyTail;

import Combat.Combat;
import Effets.BuffDefense;
import Effets.Confusion;
import Effets.Etourdissement;
import Effets.ReductionAttaque;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Sol (Mister Sol) — Elementaliste, rang B.
 * Magie de la Terre : fusionne avec le sol, crée des roches, Merci la Vie (lecture de mémoire).
 * Sorts : Show Time (gravats), Merci la Vie (souvenirs douloureux), Sol Liquide, Sonate de Plâtre.
 * Parle avec un accent et des mots anglais/français. "Gentleman".
 */
public class perso_Sol extends PersonnageBase {

    public perso_Sol() {
        this.nom    = "Sol";
        this.type   = "Elementaliste";
        this.role   = "Tank";
        this.rarete = "B";
        this.niveau = 1;
        double mult = 1.30;
        this.vie     = 500 * mult;
        this.attaque = 130 * mult;
        this.defense = 155 * mult;
        this.vitesse =  90 * mult;
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
        log.add("Sol projette des gravats de pierre sur " + cible.getNom() + " — Show Time, non !");
        Combat.attaquer(this, cible, log);
        Combat.appliquerEffet(this, cible, new ReductionAttaque(0.12, 2), log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Sol plonge dans le sol et lit les souvenirs douloureux de " + cible.getNom() + " — Merci la Vie !");
        double degats = this.getAttaque() * 1.10;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
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
        log.add("Sol liquéfie le sol — la Sonate de Plâtre engloutit toute l'équipe ennemie, non !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) multiplicateurRage += (this.getRage() - 100) / 100.0;
        Combat.appliquerEffet(this, new BuffDefense(0.30, 3), log);
        for (PersonnageBase allie : equipeAlliee) {
            if (allie.estVivant() && allie != this)
                Combat.appliquerEffet(this, allie, new BuffDefense(0.15, 2), log);
        }
        for (PersonnageBase cible : equipeEnnemie) {
            if (cible.estVivant()) {
                double degats = (this.getAttaque() * 0.90) * multiplicateurRage;
                Combat.appliquerDegatsAvecLog(this, cible, degats, log);
                Combat.appliquerEffet(this, cible, new Etourdissement(1), log);
            }
        }
        double soin = this.getVieMax() * 0.10;
        this.recevoirSoin(soin, log);
    }

    @Override public void descriptionAttaqueBase() { System.out.println("Show Time — Gravats de Pierre : 100% ATK, réduit ATK de 12%."); }
    @Override public void descriptionAttaqueSpeciale() { System.out.println("Merci la Vie — Mémoire : 110% ATK, 50% confusion ou étourdissement, réduit ATK de 18%."); }
    @Override public void descriptionAttaqueUltime() { System.out.println("Sonate de Plâtre : +30% DEF, alliés +15% DEF, 90% ATK à tous (x rage), étourdissement, draine 10% PV."); }
}
