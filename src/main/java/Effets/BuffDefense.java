package Effets;
import Personnage.PersonnageBase;
import java.util.List;

public class BuffDefense implements Effet {
    private int toursRestants;
    private double pourcentage;
    private int bonusAbsolu; // ✅ stocke le montant réel retiré plus tard

    public BuffDefense(double pourcentage, int tours) {
        this.pourcentage = pourcentage;
        this.toursRestants = tours;
    }

   @Override
public void appliquer(PersonnageBase cible) {
    System.out.println(cible.getNom() + " gagne " + (int)(pourcentage * 100)
            + "% de défense pendant " + toursRestants + " tour(s) !");
}

    @Override
public void tick(PersonnageBase cible) {
    toursRestants--;
    if (toursRestants <= 0)
        System.out.println("Le buff de défense de " + cible.getNom() + " se termine !");
}

    @Override
    public boolean estTermine() { return toursRestants <= 0; }

    @Override
    public String getNom() { return "BuffDefense"; }

    public double getPourcentage() { return pourcentage; }
    public int getToursRestants() { return toursRestants; }

    @Override
    public void appliquer(PersonnageBase cible, List<String> log) {
        log.add(cible.getNom() + " gagne " + (int)(pourcentage * 100)
                + "% de defense pendant " + toursRestants + " tour(s) !");
    }

    @Override
    public void tick(PersonnageBase cible, List<String> log) {
        toursRestants--;
        if (toursRestants <= 0)
            log.add("Le buff de defense de " + cible.getNom() + " se termine !");
    }

}
