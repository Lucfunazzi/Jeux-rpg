package Equipement;

/**
 * Parchemin d'experience utilisable sur un personnage recrute.
 * L'XP accordee est plafonnee au niveau du joueur principal.
 */
public class ParcheminXP {

    public enum Rarete {
        C, B, A, S
    }

    private final Rarete rarete;
    private final int    xp;

    /**
     * Valeurs recalibrees sur la courbe d'XP actuelle (PersonnageBase.experienceCumuleePourNiveau) :
     * chaque palier correspond a peu pres au cout d'1 niveau dans sa tranche de jeu habituelle
     * (C ~niveau 1, B ~niveau 10-12, A ~niveau 33-35). Le palier S est deliberement moins
     * "efficace" par XP au-dela du mur de fin de contenu (niveau 59+, 20 000 XP/niveau fixe)
     * pour ne pas court-circuiter ce mur avec de grosses quantites de parchemins.
     */
    public ParcheminXP(Rarete rarete) {
        this.rarete = rarete;
        this.xp = switch (rarete) {
            case C -> 300;
            case B -> 1200;
            case A -> 3500;
            case S -> 8000;
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
