package Effets;
import Personnage.PersonnageBase;
import java.util.List;

public class Sommeil implements Effet {
    private int toursRestant;

    /**
     * @param toursRestant Nombre de tours pendant lesquels la cible dort.
     */
    public Sommeil(int toursRestant) {
        this.toursRestant = toursRestant;
    }

    @Override
    public void appliquer(PersonnageBase cible) {
        System.out.println(cible.getNom() + " s'endort pendant "
                + toursRestant + " tour(s) !");
    }

    @Override
    public void tick(PersonnageBase cible) {
        toursRestant--;
        if (toursRestant <= 0) {
            System.out.println(cible.getNom() + " se reveille !");
        }
    }

    @Override
    public boolean estTermine() { return toursRestant <= 0; }

    @Override
    public String getNom() { return "Sommeil"; }

    public void reveillerSiDegats() {
        if (toursRestant > 0) {
            toursRestant = 0;
            System.out.println("Le personnage se reveille sous l'impact !");
        }
    }

    public int getToursRestant() { return toursRestant; }

    @Override
    public void appliquer(PersonnageBase cible, List<String> log) {
        log.add(cible.getNom() + " s'endort pendant " + toursRestant + " tour(s) !");
    }

    @Override
    public void tick(PersonnageBase cible, List<String> log) {
        toursRestant--;
        if (toursRestant <= 0) {
            log.add(cible.getNom() + " se reveille !");
        }
    }

}
