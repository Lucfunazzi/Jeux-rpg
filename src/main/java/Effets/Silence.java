package Effets;
import Personnage.PersonnageBase;
import java.util.List;

public class Silence implements Effet {
    private int toursRestant;

    /**
     * @param toursRestant Nombre de tours pendant lesquels la cible est reduite au silence.
     */
    public Silence(int toursRestant) {
        this.toursRestant = toursRestant;
    }

    @Override
    public void appliquer(PersonnageBase cible) {
        System.out.println(cible.getNom() + " est reduit au silence pendant "
                + toursRestant + " tours ! Impossible d'utiliser la speciale.");
    }

    @Override
    public void tick(PersonnageBase cible) {
        toursRestant--;
        if (toursRestant <= 0) {
            System.out.println("Le silence de " + cible.getNom() + " se termine !");
        }
    }

    @Override
    public boolean estTermine() { return toursRestant <= 0; }

    @Override
    public String getNom() { return "Silence"; }

    public boolean empecheSpeciale() { return toursRestant > 0; }
    public int getToursRestant() { return toursRestant; }

    @Override
    public void appliquer(PersonnageBase cible, List<String> log) {
        log.add(cible.getNom() + " est reduit au silence pendant "
                + toursRestant + " tours ! Impossible d'utiliser la speciale.");
    }

    @Override
    public void tick(PersonnageBase cible, List<String> log) {
        toursRestant--;
        if (toursRestant <= 0) {
            log.add("Le silence de " + cible.getNom() + " se termine !");
        }
    }

}
