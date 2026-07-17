package Effets;
import Personnage.PersonnageBase;
import java.util.List;

public class Absorption implements Effet {
    private int toursRestant;
    private double pourcentageVol; // ex: 0.20 = vole 20% des dégâts infligés en PV

    public Absorption(int toursRestant, double pourcentageVol) {
        this.toursRestant = toursRestant;
        this.pourcentageVol = pourcentageVol;
    }

    @Override
    public void appliquer(PersonnageBase cible) {
        System.out.println(cible.getNom() + " gagne l'absorption de vie ("
                + (int)(pourcentageVol * 100) + "% des dégâts infligés) pendant "
                + toursRestant + " tour(s) !");
    }

    @Override
    public void tick(PersonnageBase cible) {
        if (toursRestant > 0) toursRestant--;
    }

    @Override
    public boolean estTermine() { return toursRestant <= 0; }

    @Override
    public String getNom() { return "Absorption"; }

    // Appelée dans Combat.attaquer() après avoir infligé des dégâts — déjà en place
    public void volerVie(PersonnageBase soi, double degatsInfliges, java.util.List<String> log) {
        double pvVoles = degatsInfliges * pourcentageVol;
        soi.recevoirSoin(pvVoles, log);
    }

    public int getToursRestant() { return toursRestant; }
    public double getPourcentageVol() { return pourcentageVol; }

    @Override
    public void appliquer(PersonnageBase cible, List<String> log) {
        log.add(cible.getNom() + " gagne l'absorption de vie ("
                + (int)(pourcentageVol * 100) + "% des degats infliges) pendant "
                + toursRestant + " tour(s) !");
    }

}
