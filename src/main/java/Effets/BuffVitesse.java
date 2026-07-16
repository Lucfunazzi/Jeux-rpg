package Effets;
import Personnage.PersonnageBase;
import java.util.List;

public class BuffVitesse implements Effet {
    private int toursRestants;
    private double pourcentage;

    /**
     * @param pourcentage Pourcentage de vitesse de base ajouté (ex: 0.20 pour 20%).
     * @param tours       Nombre de tours.
     */
    public BuffVitesse(double pourcentage, int tours) {
        this.pourcentage = pourcentage;
        this.toursRestants = tours;
    }

    @Override
    public void appliquer(PersonnageBase cible) {
        System.out.println(cible.getNom() + " gagne " + (int)(pourcentage * 100)
                + "% de vitesse pendant " + toursRestants + " tour(s) !");
    }

    @Override
    public void tick(PersonnageBase cible) {
        toursRestants--;
        if (toursRestants <= 0) {
            System.out.println("Le buff de vitesse de " + cible.getNom() + " se termine !");
        }
    }

    @Override
    public boolean estTermine() { return toursRestants <= 0; }

    @Override
    public String getNom() { return "BuffVitesse"; }

    public double getPourcentage() { return pourcentage; }
    public int getToursRestants() { return toursRestants; }

    @Override
    public void appliquer(PersonnageBase cible, List<String> log) {
        log.add(cible.getNom() + " gagne " + (int)(pourcentage * 100)
                + "% de vitesse pendant " + toursRestants + " tour(s) !");
    }

    @Override
    public void tick(PersonnageBase cible, List<String> log) {
        toursRestants--;
        if (toursRestants <= 0) {
            log.add("Le buff de vitesse de " + cible.getNom() + " se termine !");
        }
    }

}
