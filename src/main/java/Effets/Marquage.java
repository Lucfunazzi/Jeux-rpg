package Effets;
import Personnage.PersonnageBase;
import java.util.List;

public class Marquage implements Effet {
    private int toursRestant;
    private double augmentationDegats;

    /**
     * @param toursRestant      Nombre de tours pendant lesquels la cible est marquee.
     * @param augmentationDegats Pourcentage de degats supplementaires recus (ex: 0.30 pour +30%).
     */
    public Marquage(int toursRestant, double augmentationDegats) {
        this.toursRestant = toursRestant;
        this.augmentationDegats = augmentationDegats;
    }

    @Override
    public void appliquer(PersonnageBase cible) {
        System.out.println(cible.getNom() + " est marque ! Degats recus augmentes de "
                + (int)(augmentationDegats * 100) + "% pendant " + toursRestant + " tour(s).");
    }

    @Override
    public void tick(PersonnageBase cible) {
        toursRestant--;
        if (toursRestant <= 0) {
            System.out.println("Le marquage de " + cible.getNom() + " se termine !");
        }
    }

    @Override
    public boolean estTermine() { return toursRestant <= 0; }

    @Override
    public String getNom() { return "Marquage"; }

    public double appliquerSurDegats(double degatsEntrants) {
        return degatsEntrants * (1.0 + augmentationDegats);
    }

    public int getToursRestant() { return toursRestant; }
    public double getAugmentationDegats() { return augmentationDegats; }

    @Override
    public void appliquer(PersonnageBase cible, List<String> log) {
        log.add(cible.getNom() + " est marque ! Degats recus augmentes de "
                + (int)(augmentationDegats * 100) + "% pendant " + toursRestant + " tour(s).");
    }

    @Override
    public void tick(PersonnageBase cible, List<String> log) {
        toursRestant--;
        if (toursRestant <= 0) {
            log.add("Le marquage de " + cible.getNom() + " se termine !");
        }
    }

}
