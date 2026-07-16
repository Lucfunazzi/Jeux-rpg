package Equipement;

/**
 * Parchemin d'experience utilisable sur un personnage recrute.
 * L'XP accordee est plafonnee au niveau du joueur principal.
 */
public class ParcheminXP {

    public enum Rarete {
        C, B, A
    }

    private final Rarete rarete;
    private final int    xp;

    public ParcheminXP(Rarete rarete) {
        this.rarete = rarete;
        this.xp = switch (rarete) {
            case C -> 500;
            case B -> 1500;
            case A -> 5000;
        };
    }

    public Rarete getRarete() { return rarete; }
    public int    getXP()     { return xp; }

    public String getNom() {
        return "Parchemin d'XP [" + rarete.name() + "]";
    }

    @Override
    public String toString() {
        return getNom() + " (+" + xp + " XP)";
    }
}
