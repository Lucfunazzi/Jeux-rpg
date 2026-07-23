package lancement.ExamenS;

import Combat.Combat;
import Effets.BuffTitre;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.List;

/**
 * Un combat de l'Examen de Rang S. Recompense geree par MenuExamenS (boite de pierre),
 * pas de sauvegarde automatique ici (faite par l'appelant apres coup).
 */
public class StageExamenS {

    private final int numero;
    private final ArrayList<PersonnageBase> ennemis;

    private List<Combat.PersonnageSnapshot> etatInitial;
    private List<Combat.CombatEvent> evenements;

    public StageExamenS(int numero, ArrayList<PersonnageBase> ennemis) {
        this.numero  = numero;
        this.ennemis = ennemis;
    }

    public boolean lancer(ArrayList<PersonnageBase> equipeAlliee, double bonusTitre) {
        for (PersonnageBase p : equipeAlliee) p.reinitialiserPourCombat();
        for (PersonnageBase e : ennemis)       e.reinitialiserPourCombat();

        System.out.println("\n========================================");
        System.out.println("  EXAMEN DE RANG S — STAGE " + numero);
        System.out.println("========================================");
        System.out.println("Ennemis :");
        for (PersonnageBase e : ennemis)
            System.out.println("  - " + e.getNom() + " [" + e.getRarete() + "] Niv." + e.getNiveau() + " (" + e.getRole() + ")");
        System.out.println();

        etatInitial = Combat.snapshotEquipes(equipeAlliee, ennemis);
        Combat combat = new Combat(equipeAlliee, ennemis, bonusTitre);
        evenements = combat.lancerCombatEnregistre();

        for (PersonnageBase p : equipeAlliee) {
            p.getEffetsActifs().removeIf(e -> e instanceof BuffTitre);
        }

        boolean victoire = tousKO(ennemis);
        if (victoire) {
            for (PersonnageBase p : equipeAlliee) p.reinitialiserPourCombat();
        }
        return victoire;
    }

    private boolean tousKO(ArrayList<PersonnageBase> equipe) {
        for (PersonnageBase p : equipe) if (p.estVivant()) return false;
        return true;
    }

    public int getNumero() { return numero; }
    public List<Combat.PersonnageSnapshot> getEtatInitial() { return etatInitial; }
    public List<Combat.CombatEvent> getEvenements() { return evenements; }
}
