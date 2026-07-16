package Effets;
import Personnage.PersonnageBase;
import java.util.List;

public class Invincibilite implements Effet {
    private int toursRestant;

    public Invincibilite(int toursRestant) {
        this.toursRestant = toursRestant;
    }

    @Override
    public void appliquer(PersonnageBase cible) {
        System.out.println(cible.getNom() + " est invincible pendant "
                + toursRestant + " tour(s) !");
    }

    @Override
    public void tick(PersonnageBase cible) {
        if (toursRestant > 0) toursRestant--;
    }

    @Override
    public boolean estTermine() { return toursRestant <= 0; }

    @Override
    public String getNom() { return "Invincibilite"; }

    public int getToursRestant() { return toursRestant; }

    @Override
    public void appliquer(PersonnageBase cible, List<String> log) {
        log.add(cible.getNom() + " est invincible pendant " + toursRestant + " tour(s) !");
    }

}
