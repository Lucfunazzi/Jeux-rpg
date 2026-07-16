package Effets;
import Personnage.PersonnageBase;
import java.util.List;

public class Petrification implements Effet {
    private int toursRestants;
    private static final double MULTIPLICATEUR_DEGATS = 1.20;

    /**
     * @param toursRestants Nombre de tours pendant lesquels la cible est petrifiee.
     */
    public Petrification(int toursRestants) {
        this.toursRestants = toursRestants;
    }

    @Override
    public void appliquer(PersonnageBase cible) {
        System.out.println(cible.getNom() + " est petrifiee ! Elle ne peut pas agir "
                + "et recoit 20% de degats supplementaires !");
    }

    @Override
    public void tick(PersonnageBase cible) {
        toursRestants--;
        if (toursRestants <= 0) {
            System.out.println(cible.getNom() + " n'est plus petrifiee.");
        }
    }

    /**
     * Applique le multiplicateur de degats sur une cible petrifiee.
     * A appeler dans Combat.appliquerDegatsAvecLog si la cible a cet effet.
     */
    public double appliquerSurDegats(double degats) {
        return degats * MULTIPLICATEUR_DEGATS;
    }

    @Override
    public boolean estTermine() { return toursRestants <= 0; }

    @Override
    public String getNom() { return "Petrification"; }

    public int getToursRestants() { return toursRestants; }

    @Override
    public void appliquer(PersonnageBase cible, List<String> log) {
        log.add(cible.getNom() + " est petrifiee ! Elle ne peut pas agir "
                + "et recoit 20% de degats supplementaires !");
    }

    @Override
    public void tick(PersonnageBase cible, List<String> log) {
        toursRestants--;
        if (toursRestants <= 0) {
            log.add(cible.getNom() + " n'est plus petrifiee.");
        }
    }

}
