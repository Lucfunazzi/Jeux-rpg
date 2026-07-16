package Effets;
import Personnage.PersonnageBase;
import java.util.List;

public class Trempe implements Effet {
    private int toursRestants;

    /**
     * @param toursRestants Nombre de tours pendant lesquels la cible est trempee.
     */
    public Trempe(int toursRestants) {
        this.toursRestants = toursRestants;
    }

    @Override
    public void appliquer(PersonnageBase cible) {
        System.out.println(cible.getNom() + " est trempe ! Plus vulnerable au gel et a la paralysie.");
    }

    @Override
    public void tick(PersonnageBase cible) {
        toursRestants--;
        if (toursRestants <= 0) {
            System.out.println(cible.getNom() + " n'est plus trempe.");
        }
    }

    @Override
    public boolean estTermine() { return toursRestants <= 0; }

    @Override
    public String getNom() { return "Trempe"; }

    public int getToursRestants() { return toursRestants; }

    @Override
    public void appliquer(PersonnageBase cible, List<String> log) {
        log.add(cible.getNom() + " est trempe ! Plus vulnerable au gel et a la paralysie.");
    }

    @Override
    public void tick(PersonnageBase cible, List<String> log) {
        toursRestants--;
        if (toursRestants <= 0) {
            log.add(cible.getNom() + " n'est plus trempe.");
        }
    }

}
