package Personnage.pnj.Chapitre2Elite;

import Combat.Combat;
import Effets.ReductionAttaque;
import Effets.Silence;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Cherry (Sherry Brendy) — Mage des Marionnettes, rang C.
 * Magie des Poupées : contrôle tout objet, matière ou Esprit Céleste non-humain.
 * Peut renvoyer les Esprits Célestes dans leur monde et retourner des invocations contre leur maître.
 * Sort signature : Arbre Marionnette (contrôle des végétaux).
 */
public class EnnemiCherry extends PersonnageBase {

    public EnnemiCherry() { this(20); }

    public EnnemiCherry(int niveau) {
        this.nom    = "Cherry";
        this.niveau = niveau;
        this.type   = "Elementaliste";
        this.role   = "Support";
        this.rarete = "C";

        double niv = Math.pow(1.05, niveau - 1);
        double vit = Math.pow(1.03, niveau - 1);
        this.vie     = 260.0 * niv;
        this.attaque =  70.0 * niv;
        this.defense =  65.0 * niv;
        this.vitesse =  65.0 * vit;

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
        return new String[]{"Arbre Marionnette", "Marionnette de l'amour ", "Forêt de l'Amour"};
    }

    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Cherry anime un arbre géant qui attaque " + cible.getNom() + " de ses branches !");
        Combat.attaquer(this, cible, log);

    }

    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Cherry utilise marionnette de l'amour  ");
          PersonnageBase cibleSoin = null;
    for (PersonnageBase allie : equipeAlliee) {
        if (allie.estVivant()) {
            if (cibleSoin == null || allie.getVie() < cibleSoin.getVie())
                cibleSoin = allie;
        }
    }
    if (cibleSoin == null) return;
    double soin = this.getAttaque() * 0.80;
    cibleSoin.recevoirSoin(soin, log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Cherry libère la Forêt de l'amour — la forêt entière se dresse contre les ennemis !");
        for (PersonnageBase cible : equipeEnnemie) {
            if (cible.estVivant()) {
                double degats = this.getAttaque() * 0.50;
                Combat.appliquerDegatsAvecLog(this, cible, degats, log);



            }
            if (Math.random() < 0.30){
                 Combat.appliquerEffet(this, cible, new Silence(2), log);
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Arbre Marionnette — Inflige 100% ATK");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Contrôle des Esprits — Inflige 110% ATK et réduit l'ATK de 15% de la cible.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Marionnette de l'Amour — Inflige 50% ATK à tous, à 30% de silence les cibles pendants 2 tours.");
    }
}
