package Personnage.pnj.Chapitre2;

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

    public EnnemiCherry() { this(12); }

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
        return new String[]{"Arbre Marionnette", "Contrôle des Esprits", "Marionnette de l'Amour"};
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
        log.add("Cherry tente de prendre le contrôle des invocations de " + cible.getNom() + " — sa magie est inhibée !");
        double degats = this.getAttaque() * 1.10;
        Combat.appliquerDegatsAvecLog(this, cible, degats, log);
        // Contrôle des esprits : Silence pour empêcher les invocations
       
        Combat.appliquerEffet(this, cible, new ReductionAttaque(0.15, 2), log);
    }

    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        log.add("Cherry libère la Marionnette de l'Amour — la forêt entière se dresse contre les ennemis !");
        for (PersonnageBase cible : equipeEnnemie) {
            if (cible.estVivant()) {
                double degats = this.getAttaque() * 0.85;
                Combat.appliquerDegatsAvecLog(this, cible, degats, log);
                Combat.appliquerEffet(this, cible, new Silence(2), log);
                
            }
        }
    }

    @Override public void descriptionAttaqueBase() {
        System.out.println("Arbre Marionnette — Inflige 100% ATK, réduit ATK de 10% pendant 2 tours.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        System.out.println("Contrôle des Esprits — Inflige 110% ATK, silence 2 tours (inhibe invocations), réduit ATK de 15%.");
    }
    @Override public void descriptionAttaqueUltime() {
        System.out.println("Marionnette de l'Amour — Inflige 85% ATK à tous, silence 2 tours et réduit ATK de 12%.");
    }
}
