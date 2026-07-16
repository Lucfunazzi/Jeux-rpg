package lancement.Menus;

import Joueur.ArbreCompetences;
import Joueur.Personnage_principale;
import java.util.Scanner;
import lancement.GameContext;
import lancement.Gestionnaires.GestionnaireTitres;
import lancement.RangJoueur;
import lancement.Titre;

public class MenuRang {

    public void afficher(GameContext ctx, Scanner scanner) {
        Personnage_principale joueur             = ctx.joueur;
        RangJoueur            rangJoueur         = ctx.rangJoueur;
        GestionnaireTitres    gestionnaireTitres = ctx.gestionnaireTitres;
        boolean retour = false;

        while (!retour) {
            System.out.println("\n========================================");
            System.out.println("           RANG & TITRES");
            System.out.println("========================================");
            System.out.println(rangJoueur.afficherRang());

            // Afficher l'état des arbres si rang C
            if (rangJoueur.getRang() == RangJoueur.Rang.C) {
                ArbreCompetences arbre = joueur.getArbreCompetences();
                System.out.println();
                System.out.println("  Arbre 1 : " + (arbre.isNoeud10Debloque()
                        ? "[COMPLETE]" : "[" + progressionArbre(arbre, 1) + "/10 noeuds]"));
                System.out.println("  Arbre 2 : " + (!arbre.isArbre2Debloque()
                        ? "[VERROUILLE]"
                        : arbre.isNoeud10Arbre2Debloque()
                            ? "[COMPLETE]"
                            : "[" + progressionArbre(arbre, 2) + "/10 noeuds]"));
            }

            System.out.println();
            if (gestionnaireTitres.getTitreActif() != null)
                System.out.println("Titre equipe : " + gestionnaireTitres.getTitreActif());
            else
                System.out.println("Titre equipe : aucun");

            System.out.println();
            System.out.println("1. Tenter une montee de rang");
            System.out.println("2. Gerer mes titres");
            System.out.println("0. Retour");
            System.out.print("Votre choix : ");

            switch (scanner.nextLine().trim()) {
                case "1" -> {
                    String resultat = rangJoueur.tenterMonteeRang(
                            joueur.getNiveau(),
                            joueur.getArbreCompetences());
                    if (resultat.equals("OK")) {
                        System.out.println(">> Felicitations ! Vous etes maintenant rang "
                                + rangJoueur.getRangNom() + " !");
                        System.out.println("   Multiplicateur de stats : x"
                                + String.format("%.2f", rangJoueur.getMultiplicateur()));
                        // Le multiplicateur est lu dynamiquement via GameContext
                        // dans getAttaque/getDefense/getVieMax/getVitesse — rien à setter ici
                    } else {
                        System.out.println(resultat);
                    }
                }
                case "2" -> afficherMenuTitres(gestionnaireTitres, scanner);
                case "0" -> retour = true;
                default  -> System.out.println("Choix invalide.");
            }
        }
    }

    // ── Compte les noeuds débloqués dans un arbre ─────────────────────────
    private int progressionArbre(ArbreCompetences arbre, int numArbre) {
        int count = 0;
        for (int i = 1; i <= 10; i++) {
            boolean debloque = numArbre == 1
                    ? arbre.getNoeud(i).isDebloque()
                    : arbre.getNoeudArbre2(i).isDebloque();
            if (debloque) count++;
        }
        return count;
    }

    private void afficherMenuTitres(GestionnaireTitres gestionnaireTitres,
                                     Scanner scanner) {
        boolean retour = false;
        while (!retour) {
            System.out.println("\n--- Mes titres ---");
            if (gestionnaireTitres.getTitresObtenus().isEmpty()) {
                System.out.println("Aucun titre obtenu pour l'instant.");
                return;
            }
            int i = 1;
            for (Titre t : gestionnaireTitres.getTitresObtenus()) {
                System.out.println(i + ". " + t);
                i++;
            }
            System.out.println("0. Desequiper le titre actif et retour");
            System.out.print("Choisir un titre a equiper (0 pour desequiper) : ");

            int choix;
            try {
                choix = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Entree invalide.");
                continue;
            }

            if (choix == 0) {
                gestionnaireTitres.desequiperTitre();
                retour = true;
            } else if (choix >= 1 && choix <= gestionnaireTitres.getTitresObtenus().size()) {
                Titre t = gestionnaireTitres.getTitresObtenus().get(choix - 1);
                gestionnaireTitres.equiperTitre(t.getNom());
            } else {
                System.out.println("Choix invalide.");
            }
        }
    }
}