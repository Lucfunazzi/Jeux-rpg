package Personnage.FairyTail;

import Combat.Combat;
import Effets.BuffAttaque;
import Effets.Etourdissement;
import Effets.ReductionAttaque;
import Effets.ReductionDefense;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * José Pora — Elementaliste, rang S.
 * Magie des Ténèbres (Shade) : soldats fantomatiques, Méduse, Vague Fantomatique (Dead Wave).
 * Maître de Phantom Lord, l'un des 10 Mages Sacrés.
 */
public class perso_Jose extends PersonnageBase {

    public perso_Jose() {
        this.nom    = "José Pora";
        this.type   = "Elementaliste";
        this.role   = "DPS";
        this.rarete = "S";
        this.niveau = 1;
        double mult = 1.50;
        this.vie     = 500 * mult;
        this.attaque = 200 * mult;
        this.defense = 140 * mult;
        this.vitesse = 150 * mult;
        this.taux_critiques    = 0.22;
        this.degat_critiques   = 1.50;
        this.taux_precisions   = 105.00;
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
        log.add("José invoque des soldats fantomatiques qui s'abattent sur " + cible.getNom() + " !");
        Combat.attaquer(this, cible, log);
        Combat.appliquerEffet(this, cible, new ReductionAttaque(0.15, 2), log);
        double soin = this.getAttaque() * 0.15;
        this.recevoirSoin(soin, log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Les Shades de José fusionnent en une méduse colossale — ses poings massifs fracassent " + cible.getNom() + " !");
        double degats = this.getAttaque() * 1.70;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new ReductionDefense(0.25, 3), log);
        if (Math.random() < 0.35) Combat.appliquerEffet(this, cible, new Etourdissement(1), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("José libère la Vague Fantomatique — Dead Wave ! Une spirale de Shades déchire tout sur son passage !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) multiplicateurRage += (this.getRage() - 100) / 100.0;
        Combat.appliquerEffet(this, new BuffAttaque(0.30, 3), log);
        for (PersonnageBase cible : equipeEnnemie) {
            if (cible.estVivant()) {
                double degats = (this.getAttaque() * 1.40) * multiplicateurRage;
                Combat.appliquerDegatsAvecLog(this, cible, degats, log);
                Combat.appliquerEffet(this, cible, new ReductionDefense(0.20, 3), log);
                Combat.appliquerEffet(this, cible, new ReductionAttaque(0.20, 3), log);
            }
        }
        double soin = this.getVieMax() * 0.12;
        this.recevoirSoin(soin, log);
    }

    @Override public void descriptionAttaqueBase() { System.out.println("Shades — Soldats Fantomatiques : 100% ATK, réduit ATK de 15%, se soigne."); }
    @Override public void descriptionAttaqueSpeciale() { System.out.println("Méduse — Fantôme Colossal : 170% ATK, réduit DEF de 25%, 35% étourdissement."); }
    @Override public void descriptionAttaqueUltime() { System.out.println("Dead Wave : +30% ATK, 140% ATK à tous (x rage), -20% ATK/DEF 3 tours, draine 12% PV."); }
}
