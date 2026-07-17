package Effets;
import Personnage.PersonnageBase;
import java.util.List;

public class Resurrection implements Effet {
    private double pourcentagePv; // ex: 0.30 = revient à 30% PV max
    private boolean disponible;   // se consomme une seule fois

    public Resurrection(double pourcentagePv) {
        this.pourcentagePv = pourcentagePv;
        this.disponible = true;
    }

    @Override
    public void appliquer(PersonnageBase cible) {
        System.out.println(cible.getNom() + " est protégé par une Résurrection ("
                + (int)(pourcentagePv * 100) + "% PV) !");
    }

    @Override
    public void tick(PersonnageBase cible) {
        // Passif — aucune action par tour
    }

    @Override
    public boolean estTermine() { return !disponible; } // retiré après usage

    @Override
    public String getNom() { return "Resurrection"; }

    // Appelée dans subirDegats() de PersonnageBase quand PV tombent à 0
    public void tenterResurrection(PersonnageBase cible) {
        if (disponible) {
            double pvRestaures = cible.getVieMax() * pourcentagePv;
            cible.restaurerPv(pvRestaures);
            disponible = false;
            System.out.println("✦ " + cible.getNom() + " se releve avec "
                    + String.format("%.1f", pvRestaures) + " PV !");
        }
    }

    // Appelée depuis retirerVie() (chemins DoT) avec log de combat
    public void tenterResurrection(PersonnageBase cible, List<String> log) {
        if (disponible) {
            double pvRestaures = cible.getVieMax() * pourcentagePv;
            cible.restaurerPv(pvRestaures);
            disponible = false;
            log.add("✦ " + cible.getNom() + " se releve avec "
                    + String.format("%.1f", pvRestaures) + " PV !");
        }
    }

    public boolean isDisponible() { return disponible; }
    public double getPourcentagePv() { return pourcentagePv; }

    @Override
    public void appliquer(PersonnageBase cible, List<String> log) {
        log.add(cible.getNom() + " est protege par une Resurrection ("
                + (int)(pourcentagePv * 100) + "% PV) !");
    }

}
