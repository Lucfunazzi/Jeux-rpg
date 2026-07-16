package lancement.Gestionnaires;

import java.util.ArrayList;
import java.util.List;
import lancement.Titre;

public class GestionnaireTitres {

    private final List<Titre> titresObtenus = new ArrayList<>();
    private Titre titreActif = null;

    // ── Débloquer un titre ────────────────────────────────────────────────
    public void debloquerTitre(String nomTitre) {
        if (possedeTitre(nomTitre)) return;
        Titre t = creerTitre(nomTitre);
        if (t != null) {
            titresObtenus.add(t);
            System.out.println(">> Nouveau titre obtenu : [" + nomTitre + "] !");
        }
    }

    // ── Équiper / déséquiper ──────────────────────────────────────────────
    public void equiperTitre(String nomTitre) {
        for (Titre t : titresObtenus) {
            if (t.getNom().equals(nomTitre)) {
                if (titreActif != null) titreActif.setEquipe(false);
                t.setEquipe(true);
                titreActif = t;
                System.out.println(">> Titre equipe : [" + nomTitre + "]");
                return;
            }
        }
        System.out.println("Titre introuvable : " + nomTitre);
    }

    public void desequiperTitre() {
        if (titreActif == null) {
            System.out.println("Aucun titre equipe.");
            return;
        }
        System.out.println(">> Titre [" + titreActif.getNom() + "] desequipe.");
        titreActif.setEquipe(false);
        titreActif = null;
    }

    // ── Bonus actif ───────────────────────────────────────────────────────
    public double getBonusActif() {
        if (titreActif == null) return 0.0;
        return titreActif.getBonusStatsPourcentage();
    }

    // ── Utilitaires ───────────────────────────────────────────────────────
    public boolean possedeTitre(String nom) {
        for (Titre t : titresObtenus)
            if (t.getNom().equals(nom)) return true;
        return false;
    }

    private Titre creerTitre(String nom) {
        return switch (nom) {
            case "Conquerant de l'Elite" ->
                new Titre("Conquerant de l'Elite",
                        "A triomphe du Chapitre 1 Elite dans toute sa gloire.",
                        0.03);
            default -> null;
        };
    }

    public List<Titre>  getTitresObtenus() { return titresObtenus; }
    public Titre        getTitreActif()    { return titreActif; }
    public void         setTitreActif(Titre t) { this.titreActif = t; }
}