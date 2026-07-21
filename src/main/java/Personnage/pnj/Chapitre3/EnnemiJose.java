package Personnage.pnj.Chapitre3;

import Combat.Combat;
import Effets.BuffAttaque;
import Effets.Etourdissement;
import Effets.ReductionAttaque;
import Effets.ReductionDefense;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * José Pora — Maître de la guilde Phantom Lord, l'un des 10 Mages Sacrés, rang S.
 * Magie des Ténèbres (Shade) : invoque des soldats fantomatiques (Shades).
 * Sorts : Shade Troopers, Méduse (forme de méduse géante), Vague Fantomatique (Dead Wave).
 * Sa magie dégage une sensation de mal et de froid intense.
 */
public class EnnemiJose extends PersonnageBase {

    public EnnemiJose() { this(35); }

    public EnnemiJose(int niveau) {
        this.nom    = "José Pora";
        this.niveau = niveau;
        this.type   = "Elementaliste";
        this.role   = "DPS";
        this.rarete = "S";

        double mult = 1.50;
        double niv  = Math.pow(1.05, niveau - 1);
        double vit  = Math.pow(1.03, niveau - 1);
        this.vie     = 320.0 * mult * niv;
        this.attaque = 130.0 * mult * niv;
        this.defense =  90.0 * mult * niv;
        this.vitesse = 110.0 * mult * vit;

        this.taux_critiques    = 0.22;
        this.degat_critiques   = 1.50;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.14;
        this.taux_blocage      = 0.08;
        this.reduction_blocage = 0.12;
        this.degats_renvoi     = 0.80;

        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Shades — Soldats Fantomatiques", "Méduse — Fantôme Colossal", "Vague Fantomatique — Dead Wave"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("José invoque des soldats fantomatiques armés qui s'abattent sur " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
        double soin = this.getAttaque() * 0.15;
        this.recevoirSoin(soin, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Les Shades de José fusionnent en une méduse colossale qui écrase " + cible.getNom() + " de ses poings massifs !");
        double degats = this.getAttaque() * 1.70;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new ReductionDefense(0.25, 3), log);
        if (Math.random() < 0.35) {
            Combat.appliquerEffet(this, cible, new Etourdissement(1), log);
        }
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("José étend son bras et génère une spirale de Shades — la Vague Fantomatique déchire tout sur son passage !");
        Combat.appliquerEffet(this, new BuffAttaque(0.30, 3), log);
        for (PersonnageBase cible : equipeEnnemie) {
            if (cible.estVivant()) {
                double degats = this.getAttaque() * 1.40;
                Combat.appliquerDegatsAvecLog(this, cible, degats, log);
                
                Combat.appliquerEffet(this, cible, new ReductionAttaque(0.15, 2), log);
            }
        }
        double soin = this.getVieMax() * 0.12;
        this.recevoirSoin(soin, log);
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Shades — Soldats Fantomatiques : Inflige 100% ATK, réduit ATK de 15%, se soigne de 15% ATK.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Méduse — Fantôme Colossal : Inflige 170% ATK, réduit DEF de 25% 3 tours, 35% étourdissement.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Vague Fantomatique — Dead Wave : +30% ATK, 140% ATK à tous, -20% ATK/DEF 3 tours, draine 12% PV.");
    }
}
