package Equipement;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Pieces d'equipement portees par un personnage (une par emplacement) et bonus qui en
 * decoulent : bonus bruts par piece, bonus des pierres inserees, et bonus de set (BonusSet)
 * une fois un seuil de pieces de meme rarete atteint. Extrait de PersonnageBase, qui reste
 * responsable d'appliquer l'effet PV lors de equiper()/desequiper() (getVieMax() incluant deja
 * dynamiquement le bonus PV courant).
 */
public class PiecesEquipees {

    private final HashMap<Equipement.Slot, Equipement> equipements = new HashMap<>();

    public void equiper(Equipement e) {
        equipements.put(e.getSlot(), e);
    }

    public Equipement desequiper(Equipement.Slot slot) {
        return equipements.remove(slot);
    }

    public Equipement get(Equipement.Slot slot) {
        return equipements.get(slot);
    }

    public ArrayList<Equipement> versListe() {
        return new ArrayList<>(equipements.values());
    }

    public double bonusATK() {
        return equipements.values().stream().mapToDouble(Equipement::getBonusATK).sum();
    }

    public double bonusDEF() {
        return equipements.values().stream().mapToDouble(Equipement::getBonusDEF).sum();
    }

    public double bonusPV() {
        return equipements.values().stream().mapToDouble(Equipement::getBonusPV).sum();
    }

    public double bonusVIT() {
        return equipements.values().stream().mapToDouble(Equipement::getBonusVIT).sum();
    }

    // ── Bonus des pierres inserees dans les pieces equipees ────────────────
    /** Somme brute des pierres du type donne (ex : 1.5 pour +1.5%), pour les stats deja exprimees en points 100. */
    public double bonusPierrePoints(Pierre.Type type) {
        double total = 0;
        for (Equipement e : equipements.values()) total += e.getBonusPierre(type);
        return total;
    }

    /** Meme somme convertie en fraction (0.015 pour +1.5%), pour les stats exprimees en 0-1. */
    public double bonusPierreFraction(Pierre.Type type) {
        return bonusPierrePoints(type) / 100.0;
    }

    /** Nombre de pieces equipees de la rarete donnee (max 6, une par emplacement). */
    public int compterPieces(Equipement.Rarete rarete) {
        int count = 0;
        for (Equipement e : equipements.values()) {
            if (e.getRarete() == rarete) count++;
        }
        return count;
    }

    /**
     * Rarete du set actuellement porte en plus grand nombre (pour l'affichage de la progression
     * du bonus de set), ou {@code null} si aucune piece n'est equipee.
     */
    public Equipement.Rarete rareteSetDominante() {
        Equipement.Rarete dominante = null;
        int max = 0;
        for (Equipement.Rarete r : Equipement.Rarete.values()) {
            int n = compterPieces(r);
            if (n > max) { max = n; dominante = r; }
        }
        return dominante;
    }

    public double bonusSetPV() {
        double total = 0;
        for (Equipement.Rarete r : Equipement.Rarete.values()) {
            if (compterPieces(r) >= BonusSet.SEUIL_PV) total += BonusSet.palier(r).bonusPV();
        }
        return total;
    }

    public double bonusSetVitessePct() {
        double total = 0;
        for (Equipement.Rarete r : Equipement.Rarete.values()) {
            if (compterPieces(r) >= BonusSet.SEUIL_VIT) total += BonusSet.palier(r).bonusVitessePct();
        }
        return total;
    }

    public double bonusSetAttaquePct() {
        double total = 0;
        for (Equipement.Rarete r : Equipement.Rarete.values()) {
            if (compterPieces(r) >= BonusSet.SEUIL_ATK) total += BonusSet.palier(r).bonusAttaquePct();
        }
        return total;
    }

    /** Bonus special de set complet (6/6) actif, uniquement pour SSS/UR ; {@code null} sinon. */
    public BonusSet.BonusSpecial bonusSetSpecialActif() {
        for (Equipement.Rarete r : Equipement.Rarete.values()) {
            if (compterPieces(r) >= BonusSet.SET_COMPLET) {
                BonusSet.BonusSpecial special = BonusSet.special(r);
                if (special != null) return special;
            }
        }
        return null;
    }
}
