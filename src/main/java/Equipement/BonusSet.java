package Equipement;

/**
 * Bonus de set d'equipement, sur le principe 4/5/6 pieces d'une meme rarete portees :
 *   4 pieces -> bonus PV, 5 pieces -> bonus % Vitesse, 6 pieces (set complet) -> bonus % Attaque.
 * SSS et UR recoivent en plus un bonus special au palier 6 pieces (voir {@link #special}).
 */
public final class BonusSet {
    private BonusSet() {}

    public static final int SEUIL_PV  = 4;
    public static final int SEUIL_VIT = 5;
    public static final int SEUIL_ATK = 6;
    public static final int SET_COMPLET = 6;

    public record Palier(double bonusPV, double bonusVitessePct, double bonusAttaquePct) {}

    /** Bonus supplementaire au palier 6/6, reserve aux raretes SSS et UR. */
    public record BonusSpecial(double bonusTauxCritique, double bonusDegatCritiquePct,
                                double bonusEsquive, double bonusVolDeVie) {}

    public static Palier palier(Equipement.Rarete rarete) {
        return switch (rarete) {
            case C   -> new Palier(200,    0.02, 0.02);
            case B   -> new Palier(900,    0.03, 0.03);
            case A   -> new Palier(1500,   0.04, 0.04);
            case S   -> new Palier(4000,   0.05, 0.05);
            case SS  -> new Palier(12000,  0.06, 0.06);
            case SSS -> new Palier(40000,  0.08, 0.08);
            case UR  -> new Palier(150000, 0.10, 0.10);
        };
    }

    /** {@code null} si la rarete n'a pas de bonus special (seuls SSS et UR en ont un). */
    public static BonusSpecial special(Equipement.Rarete rarete) {
        return switch (rarete) {
            case SSS -> new BonusSpecial(0.05, 0.10, 0.05, 0.0);
            case UR  -> new BonusSpecial(0.10, 0.15, 0.10, 0.10);
            default  -> null;
        };
    }
}
