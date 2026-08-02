package Effets;
import Personnage.PersonnageBase;
import java.util.List;

/**
 * Marquage : effet fixe (30% de degats recus en plus pendant 2 tours), contrairement a
 * Fragilite qui a le meme mecanisme mais avec pourcentage/duree choisis par l'appelant.
 */
public class Marquage implements Effet {
    private static final int TOURS_FIXES = 2;
    private static final double AUGMENTATION_FIXE = 0.30;

    private int toursRestant;
    private final double augmentationDegats;

    public Marquage() {
        this.toursRestant = TOURS_FIXES;
        this.augmentationDegats = AUGMENTATION_FIXE;
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
