package Effets;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Marque explosive : ne fait aucun degat pendant son compte a rebours, puis
 * detone au dernier tour en infligeant des degats bases sur les PV max de la cible.
 */
public class MarqueExplosive implements Effet, EffetNegatif {
    private int toursRestant;
    private final double pourcentageDegats;

    /**
     * @param toursRestant      Nombre de tours avant l'explosion.
     * @param pourcentageDegats Pourcentage des PV max de la cible inflige a l'explosion (ex: 0.25 pour 25%).
     */
    public MarqueExplosive(int toursRestant, double pourcentageDegats) {
        this.toursRestant = toursRestant;
        this.pourcentageDegats = pourcentageDegats;
    }

    @Override
    public void appliquer(PersonnageBase cible) {
        System.out.println(cible.getNom() + " est marque d'une charge explosive ! Explosion dans "
                + toursRestant + " tour(s).");
    }

    @Override
    public void tick(PersonnageBase cible) {
        toursRestant--;
        if (toursRestant <= 0) {
            double degats = cible.getVieMax() * pourcentageDegats;
            cible.retirerVie(degats);
            System.out.println("💥 La marque explosive sur " + cible.getNom() + " detone et inflige "
                    + String.format("%.0f", degats) + " PV de degats !");
        }
    }

    @Override
    public boolean estTermine() { return toursRestant <= 0; }

    @Override
    public String getNom() { return "Marque explosive"; }

    public int getToursRestant() { return toursRestant; }
    public double getPourcentageDegats() { return pourcentageDegats; }

    @Override
    public void appliquer(PersonnageBase cible, List<String> log) {
        log.add(cible.getNom() + " est marque d'une charge explosive ! Explosion dans "
                + toursRestant + " tour(s).");
    }

    @Override
    public void tick(PersonnageBase cible, List<String> log) {
        toursRestant--;
        if (toursRestant <= 0) {
            double degats = cible.getVieMax() * pourcentageDegats;
            cible.retirerVie(degats, log);
            log.add("💥 La marque explosive sur " + cible.getNom() + " detone et inflige "
                    + String.format("%.0f", degats) + " PV de degats !");
        }
    }
}
