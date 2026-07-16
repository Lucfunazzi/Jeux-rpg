package Effets;
import Personnage.PersonnageBase;
import java.util.List;

public class BuffAttaque implements Effet {
    private int toursRestants;
    private double pourcentage;

    /**
     * @param pourcentage Pourcentage d'attaque de base ajouté (ex: 0.15 pour 15%).
     * @param tours       Nombre de tours.
     */
    public BuffAttaque(double pourcentage, int tours) {
        this.pourcentage = pourcentage;
        this.toursRestants = tours;
    }

    @Override
    public void appliquer(PersonnageBase cible) {
        // Aucune modification de stat ici — getAttaque() calculera le bonus dynamiquement
        System.out.println(cible.getNom() + " gagne " + (int)(pourcentage * 100)
                + "% d'attaque pendant " + toursRestants + " tour(s) !");
    }

    @Override
    public void tick(PersonnageBase cible) {
        toursRestants--;
        if (toursRestants <= 0) {
            System.out.println("Le buff d'attaque de " + cible.getNom() + " se termine !");
        }
    }

    @Override
    public boolean estTermine() { return toursRestants <= 0; }

    @Override
    public String getNom() { return "BuffAttaque"; }

    public double getPourcentage() { return pourcentage; }
    public int getToursRestants() { return toursRestants; }

    @Override
    public void appliquer(PersonnageBase cible, List<String> log) {
        log.add(cible.getNom() + " gagne " + (int)(pourcentage * 100)
                + "% d'attaque pendant " + toursRestants + " tour(s) !");
    }

    @Override
    public void tick(PersonnageBase cible, List<String> log) {
        toursRestants--;
        if (toursRestants <= 0) {
            log.add("Le buff d'attaque de " + cible.getNom() + " se termine !");
        }
    }

}
