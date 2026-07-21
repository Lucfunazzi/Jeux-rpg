package Personnage.FairyTail;

import Combat.Combat;
import Effets.ReductionAttaque;
import Effets.Silence;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Cherry (Sherry Brendy) — Elementaliste, rang C.
 * Magie des Poupées (Marionnettes) : contrôle objets, végétaux et esprits célestes.
 * Lamia Scale. Amoureuse de Lyon. Sort contre Lucy : contrôle de Taurus.
 */
public class perso_Cherry extends PersonnageBase {

    public perso_Cherry() {
        this.nom    = "Cherry";
        this.type   = "Elementaliste";
        this.role   = "Support";
        this.rarete = "C";
        this.niveau = 1;
        double mult = 1.00;
        this.vie     = 330 * mult;
        this.attaque =  85 * mult;
        this.defense =  80 * mult;
        this.vitesse =  80 * mult;
        this.taux_critiques    = 0.06;
        this.degat_critiques   = 1.10;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.06;
        this.taux_blocage      = 0.10;
        this.reduction_blocage = 0.12;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    @Override
    public String[] getNomsAttaques() {
        return new String[]{"Arbre Marionnette", "Contrôle des Esprits", "Marionnette de l'Amour"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Cherry anime un arbre géant qui attaque " + cible.getNom() + " de ses branches !");
        Combat.attaquer(this, cible, log);
        Combat.appliquerEffet(this, cible, new ReductionAttaque(0.10, 2), log);
    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Cherry tente de prendre le contrôle des invocations de " + cible.getNom() + " !");
        double degats = this.getAttaque() * 1.10;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        Combat.appliquerEffet(this, cible, new Silence(2), log);
        Combat.appliquerEffet(this, cible, new ReductionAttaque(0.15, 2), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Cherry déclenche la Marionnette de l'Amour — toute la forêt se dresse contre les ennemis !");
        double multiplicateurRage = 1.0;
        if (this.getRage() > 100) multiplicateurRage += (this.getRage() - 100) / 100.0;
        for (PersonnageBase cible : equipeEnnemie) {
            if (cible.estVivant()) {
                double degats = (this.getAttaque() * 0.85) * multiplicateurRage;
                Combat.appliquerDegatsAvecLog(this, cible, degats, log);
                Combat.appliquerEffet(this, cible, new Silence(2), log);
                Combat.appliquerEffet(this, cible, new ReductionAttaque(0.12, 2), log);
            }
        }
    }

    @Override public void descriptionAttaqueBase() { System.out.println("Arbre Marionnette — 100% ATK, réduit ATK de 10%."); }
    @Override public void descriptionAttaqueSpeciale() { System.out.println("Contrôle des Esprits — 110% ATK, silence 2 tours, réduit ATK de 15%."); }
    @Override public void descriptionAttaqueUltime() { System.out.println("Marionnette de l'Amour — 85% ATK à tous (x rage), silence 2 tours, réduit ATK de 12%."); }
}
