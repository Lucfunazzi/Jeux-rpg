package Effets;
import Personnage.PersonnageBase;
import java.util.List;

public class Fragilite implements Effet {
    private int toursRestant;
    private double augmentationDegats; // ex: 0.25 = +25% dégâts reçus

    public Fragilite(int toursRestant, double augmentationDegats) {
        this.toursRestant = toursRestant;
        this.augmentationDegats = augmentationDegats;
    }

    @Override
    public void appliquer(PersonnageBase cible) {
        System.out.println(cible.getNom() + " est fragilisé ! Dégâts reçus augmentés de "
                + (int)(augmentationDegats * 100) + "% pendant " + toursRestant + " tours.");
    }

    @Override
    public void tick(PersonnageBase cible) {
        if (toursRestant > 0) toursRestant--;
    }

    @Override
    public boolean estTermine() { return toursRestant <= 0; }

    @Override
    public String getNom() { return "Fragilite"; }

    // Appelée dans subirDegats() de PersonnageBase — déjà en place
    public double appliquerSurDegats(double degatsEntrants) {
        return degatsEntrants * (1.0 + augmentationDegats);
    }

    public int getToursRestant() { return toursRestant; }
    public double getAugmentationDegats() { return augmentationDegats; }

    @Override
    public void appliquer(PersonnageBase cible, List<String> log) {
        log.add(cible.getNom() + " est fragilise ! Degats recus augmentes de "
                + (int)(augmentationDegats * 100) + "% pendant " + toursRestant + " tours.");
    }

}
