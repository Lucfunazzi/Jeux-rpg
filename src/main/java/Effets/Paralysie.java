package Effets;
import Personnage.PersonnageBase;
import java.util.List;
import java.util.Random;

public class Paralysie implements Effet {
    private int toursRestant;
    private double chanceLiberation;
    private Random random = new Random();

    /**
     * @param toursRestant    Nombre de tours pendant lesquels la cible est paralysee.
     * @param chanceLiberation Chance de se liberer a chaque tour (ex: 0.33 pour 33%).
     */
    public Paralysie(int toursRestant, double chanceLiberation) {
        this.toursRestant = toursRestant;
        this.chanceLiberation = chanceLiberation;
    }

    @Override
    public void appliquer(PersonnageBase cible) {
        System.out.println(cible.getNom() + " est paralyse pendant "
                + toursRestant + " tour(s) ! ("
                + (int)(chanceLiberation * 100) + "% de chance de se liberer)");
    }

    @Override
    public void tick(PersonnageBase cible) {
        toursRestant--;
        if (toursRestant <= 0) {
            System.out.println("La paralysie de " + cible.getNom() + " se termine !");
        }
    }

    @Override
    public boolean estTermine() { return toursRestant <= 0; }

    @Override
    public String getNom() { return "Paralysie"; }

    public boolean peutAgir() {
        if (toursRestant <= 0) return true;
        if (random.nextDouble() < chanceLiberation) {
            toursRestant = 0;
            System.out.println("La paralysie est dissipee !");
            return true;
        }
        System.out.println("Le personnage est paralyse et ne peut pas agir !");
        return false;
    }

    public int getToursRestant() { return toursRestant; }
    public double getChanceLiberation() { return chanceLiberation; }

    @Override
    public void appliquer(PersonnageBase cible, List<String> log) {
        log.add(cible.getNom() + " est paralyse pendant " + toursRestant + " tour(s) ! ("
                + (int)(chanceLiberation * 100) + "% de chance de se liberer)");
    }

    @Override
    public void tick(PersonnageBase cible, List<String> log) {
        toursRestant--;
        if (toursRestant <= 0) {
            log.add("La paralysie de " + cible.getNom() + " se termine !");
        }
    }

}
