package lancement;

import Joueur.Personnage_principale;
import java.util.Random;
import java.util.Scanner;

public class MiniJeuPFC {

    private static final int COUT_PARTIEC = 50;
    private static final int PARCHEMINS_MANCHE1C = 3;
    private static final int PARCHEMINS_MANCHE2C = 5;
    private static final int PARCHEMINS_MANCHE3C = 10;
    private static final int COUT_PARTIEB = 150;
    private static final int PARCHEMINS_MANCHE1B = 2;
    private static final int PARCHEMINS_MANCHE2B = 4;
    private static final int PARCHEMINS_MANCHE3B = 7;
    private static final int COUT_PARTIEA = 400;
    private static final int PARCHEMINS_MANCHE1A = 2;
    private static final int PARCHEMINS_MANCHE2A = 3;
    private static final int PARCHEMINS_MANCHE3A = 5;
    private static final double REMBOURSEMENT_MANCHE1 = 0.80;
    private static final double REMBOURSEMENT_MANCHE2 = 0.60;
    private static final double REMBOURSEMENT_MANCHE3 = 0.30;

    private final Random random = new Random();

    /**
     * Lance une session PFC complete (3 manches).
     * @return le nombre de parchemins C gagne (0 si echec manche 1)
     */
    public int jouer(Personnage_principale joueur, Scanner scanner) {
        if (joueur.getOr() < COUT_PARTIEC) {
            System.out.println("Pas assez d'or ! Il faut " + COUT_PARTIEC + " or pour jouer.");
            return 0;
        }

        // Deduire le cout
        joueur.ajouterOr(-COUT_PARTIEC);
        System.out.println("\n--- Debut de la partie (" + COUT_PARTIEC + " or depenses) ---");

        int parcheminsTotaux = 0;

        // === MANCHE 1 ===
        System.out.println("\n[ Manche 1 ] Gain si victoire : " + PARCHEMINS_MANCHE1C + " parchemins");
        boolean gagneM1 = jouerManche(scanner);

        if (!gagneM1) {
            int remboursement = (int)(COUT_PARTIEC * REMBOURSEMENT_MANCHE1);
            joueur.ajouterOr(remboursement);
            System.out.println("Defaite a la manche 1. Remboursement : " + remboursement + " or.");
            return 0;
        }
        parcheminsTotaux += PARCHEMINS_MANCHE1C;
        System.out.println("Victoire ! +" + PARCHEMINS_MANCHE1C + " parchemins C.");

        // === MANCHE 2 ===
        System.out.println("\n[ Manche 2 ] Gain si victoire : " + PARCHEMINS_MANCHE2C + " parchemins supplementaires");
        boolean gagneM2 = jouerManche(scanner);

        if (!gagneM2) {
            int remboursement = (int)(COUT_PARTIEC * REMBOURSEMENT_MANCHE2);
            joueur.ajouterOr(remboursement);
            System.out.println("Defaite a la manche 2. Remboursement : " + remboursement + " or.");
            System.out.println("Parchemins conserves : " + parcheminsTotaux);
            return parcheminsTotaux;
        }
        parcheminsTotaux += PARCHEMINS_MANCHE2C;
        System.out.println("Victoire ! +" + PARCHEMINS_MANCHE2C + " parchemins C.");

        // === MANCHE 3 ===
        System.out.println("\n[ Manche 3 ] Gain si victoire : " + PARCHEMINS_MANCHE3C + " parchemins supplementaires");
        boolean gagneM3 = jouerManche(scanner);

        if (!gagneM3) {
            int remboursement = (int)(COUT_PARTIEC * REMBOURSEMENT_MANCHE3);
            joueur.ajouterOr(remboursement);
            System.out.println("Defaite a la manche 3. Remboursement : " + remboursement + " or.");
            System.out.println("Parchemins conserves : " + parcheminsTotaux);
            return parcheminsTotaux;
        }
        parcheminsTotaux += PARCHEMINS_MANCHE3C;
        System.out.println("Victoire ! +" + PARCHEMINS_MANCHE3C + " parchemins C.");
        System.out.println("Partie parfaite ! Total : " + parcheminsTotaux + " parchemins C gagnes !");

        return parcheminsTotaux;
    }
    
    public int jouerB(Personnage_principale joueur, Scanner scanner) {
        if (joueur.getOr() < COUT_PARTIEB) {
            System.out.println("Pas assez d'or ! Il faut " + COUT_PARTIEB + " or pour jouer.");
            return 0;
        }

        // Deduire le cout
        joueur.ajouterOr(-COUT_PARTIEB);
        System.out.println("\n--- Debut de la partie (" + COUT_PARTIEB + " or depenses) ---");

        int parcheminsTotaux = 0;

        // === MANCHE 1 ===
        System.out.println("\n[ Manche 1 ] Gain si victoire : " + PARCHEMINS_MANCHE1B+ " parchemins");
        boolean gagneM1 = jouerManche(scanner);

        if (!gagneM1) {
            int remboursement = (int)(COUT_PARTIEB * REMBOURSEMENT_MANCHE1);
            joueur.ajouterOr(remboursement);
            System.out.println("Defaite a la manche 1. Remboursement : " + remboursement + " or.");
            return 0;
        }
        parcheminsTotaux += PARCHEMINS_MANCHE1B;
        System.out.println("Victoire ! +" + PARCHEMINS_MANCHE1B + " parchemins B.");

        // === MANCHE 2 ===
        System.out.println("\n[ Manche 2 ] Gain si victoire : " + PARCHEMINS_MANCHE2B + " parchemins supplementaires");
        boolean gagneM2 = jouerManche(scanner);

        if (!gagneM2) {
            int remboursement = (int)(COUT_PARTIEB * REMBOURSEMENT_MANCHE2);
            joueur.ajouterOr(remboursement);
            System.out.println("Defaite a la manche 2. Remboursement : " + remboursement + " or.");
            System.out.println("Parchemins conserves : " + parcheminsTotaux);
            return parcheminsTotaux;
        }
        parcheminsTotaux += PARCHEMINS_MANCHE2B;
        System.out.println("Victoire ! +" + PARCHEMINS_MANCHE2B+ " parchemins B.");

        // === MANCHE 3 ===
        System.out.println("\n[ Manche 3 ] Gain si victoire : " + PARCHEMINS_MANCHE3B + " parchemins supplementaires");
        boolean gagneM3 = jouerManche(scanner);

        if (!gagneM3) {
            int remboursement = (int)(COUT_PARTIEB * REMBOURSEMENT_MANCHE3);
            joueur.ajouterOr(remboursement);
            System.out.println("Defaite a la manche 3. Remboursement : " + remboursement + " or.");
            System.out.println("Parchemins conserves : " + parcheminsTotaux);
            return parcheminsTotaux;
        }
        parcheminsTotaux += PARCHEMINS_MANCHE3B;
        System.out.println("Victoire ! +" + PARCHEMINS_MANCHE3B + " parchemins B.");
        System.out.println("Partie parfaite ! Total : " + parcheminsTotaux + " parchemins B gagnes !");

        return parcheminsTotaux;
    }

    public int jouerA(Personnage_principale joueur, Scanner scanner) {
        if (joueur.getOr() < COUT_PARTIEA) {
            System.out.println("Pas assez d'or ! Il faut " + COUT_PARTIEA + " or pour jouer.");
            return 0;
        }

        // Deduire le cout
        joueur.ajouterOr(-COUT_PARTIEA);
        System.out.println("\n--- Debut de la partie (" + COUT_PARTIEA + " or depenses) ---");

        int parcheminsTotaux = 0;

        // === MANCHE 1 ===
        System.out.println("\n[ Manche 1 ] Gain si victoire : " + PARCHEMINS_MANCHE1A + " parchemins");
        boolean gagneM1 = jouerManche(scanner);

        if (!gagneM1) {
            int remboursement = (int)(COUT_PARTIEA * REMBOURSEMENT_MANCHE1);
            joueur.ajouterOr(remboursement);
            System.out.println("Defaite a la manche 1. Remboursement : " + remboursement + " or.");
            return 0;
        }
        parcheminsTotaux += PARCHEMINS_MANCHE1A;
        System.out.println("Victoire ! +" + PARCHEMINS_MANCHE1A + " parchemins A.");

        // === MANCHE 2 ===
        System.out.println("\n[ Manche 2 ] Gain si victoire : " + PARCHEMINS_MANCHE2A + " parchemins supplementaires");
        boolean gagneM2 = jouerManche(scanner);

        if (!gagneM2) {
            int remboursement = (int)(COUT_PARTIEA * REMBOURSEMENT_MANCHE2);
            joueur.ajouterOr(remboursement);
            System.out.println("Defaite a la manche 2. Remboursement : " + remboursement + " or.");
            System.out.println("Parchemins conserves : " + parcheminsTotaux);
            return parcheminsTotaux;
        }
        parcheminsTotaux += PARCHEMINS_MANCHE2A;
        System.out.println("Victoire ! +" + PARCHEMINS_MANCHE2A + " parchemins A.");

        // === MANCHE 3 ===
        System.out.println("\n[ Manche 3 ] Gain si victoire : " + PARCHEMINS_MANCHE3A + " parchemins supplementaires");
        boolean gagneM3 = jouerManche(scanner);

        if (!gagneM3) {
            int remboursement = (int)(COUT_PARTIEA * REMBOURSEMENT_MANCHE3);
            joueur.ajouterOr(remboursement);
            System.out.println("Defaite a la manche 3. Remboursement : " + remboursement + " or.");
            System.out.println("Parchemins conserves : " + parcheminsTotaux);
            return parcheminsTotaux;
        }
        parcheminsTotaux += PARCHEMINS_MANCHE3A;
        System.out.println("Victoire ! +" + PARCHEMINS_MANCHE3A + " parchemins A.");
        System.out.println("Partie parfaite ! Total : " + parcheminsTotaux + " parchemins A gagnes !");

        return parcheminsTotaux;
    }

    /**
     * Joue une manche de PFC.
     * @return true si le joueur gagne, false sinon (egalite = rejouer)
     */
    private boolean jouerManche(Scanner scanner) {
        while (true) {
            System.out.println("Choisissez : 1. Pierre  2. Feuille  3. Ciseaux");
            System.out.print("Votre choix : ");

            int choixJoueur;
            try {
                choixJoueur = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Entree invalide, recommencez.");
                continue;
            }

            if (choixJoueur < 1 || choixJoueur > 3) {
                System.out.println("Choix invalide, recommencez.");
                continue;
            }

            int choixIA = random.nextInt(3) + 1;

            String nomJoueur = getNom(choixJoueur);
            String nomIA     = getNom(choixIA);

            System.out.println("Vous : " + nomJoueur + " | Adversaire : " + nomIA);

            int resultat = evaluer(choixJoueur, choixIA);
            if (resultat == 1) {
                System.out.println("Vous gagnez cette manche !");
                return true;
            } else if (resultat == -1) {
                System.out.println("Vous perdez cette manche !");
                return false;
            } else {
                System.out.println("Egalite ! On rejoue la manche.");
            }
        }
    }
    
    public int jouerAutoC(Personnage_principale joueur) {
    if (joueur.getOr() < COUT_PARTIEC) {
        System.out.println("Pas assez d'or ! Il faut " + COUT_PARTIEC + " or pour jouer.");
        return 0;
    }
    
    

    joueur.ajouterOr(-COUT_PARTIEC);
    int parcheminsTotaux = 0;

    // Manche 1
    boolean gagneM1 = jouerMancheAuto();
    if (!gagneM1) {
        int remboursement = (int)(COUT_PARTIEC * REMBOURSEMENT_MANCHE1);
        joueur.ajouterOr(remboursement);
        return 0;
    }
    parcheminsTotaux += PARCHEMINS_MANCHE1C;

    // Manche 2
    boolean gagneM2 = jouerMancheAuto();
    if (!gagneM2) {
        int remboursement = (int)(COUT_PARTIEC * REMBOURSEMENT_MANCHE2);
        joueur.ajouterOr(remboursement);
        return parcheminsTotaux;
    }
    parcheminsTotaux += PARCHEMINS_MANCHE2C;

    // Manche 3
    boolean gagneM3 = jouerMancheAuto();
    if (!gagneM3) {
        int remboursement = (int)(COUT_PARTIEC * REMBOURSEMENT_MANCHE3);
        joueur.ajouterOr(remboursement);
        return parcheminsTotaux;
    }
    parcheminsTotaux += PARCHEMINS_MANCHE3C;

    return parcheminsTotaux;
}
    
    public int jouerAutoB(Personnage_principale joueur) {
    if (joueur.getOr() < COUT_PARTIEB) {
        System.out.println("Pas assez d'or ! Il faut " + COUT_PARTIEB + " or pour jouer.");
        return 0;
    }
    
     joueur.ajouterOr(-COUT_PARTIEB);
    int parcheminsTotaux = 0;

    // Manche 1
    boolean gagneM1 = jouerMancheAuto();
    if (!gagneM1) {
        int remboursement = (int)(COUT_PARTIEB * REMBOURSEMENT_MANCHE1);
        joueur.ajouterOr(remboursement);
        return 0;
    }
    parcheminsTotaux += PARCHEMINS_MANCHE1B;

    // Manche 2
    boolean gagneM2 = jouerMancheAuto();
    if (!gagneM2) {
        int remboursement = (int)(COUT_PARTIEB * REMBOURSEMENT_MANCHE2);
        joueur.ajouterOr(remboursement);
        return parcheminsTotaux;
    }
    parcheminsTotaux += PARCHEMINS_MANCHE2B;

    // Manche 3
    boolean gagneM3 = jouerMancheAuto();
    if (!gagneM3) {
        int remboursement = (int)(COUT_PARTIEB * REMBOURSEMENT_MANCHE3);
        joueur.ajouterOr(remboursement);
        return parcheminsTotaux;
    }
    parcheminsTotaux += PARCHEMINS_MANCHE3B;

    return parcheminsTotaux;

    }

    public int jouerAutoA(Personnage_principale joueur) {
    if (joueur.getOr() < COUT_PARTIEA) {
        System.out.println("Pas assez d'or ! Il faut " + COUT_PARTIEA + " or pour jouer.");
        return 0;
    }

    joueur.ajouterOr(-COUT_PARTIEA);
    int parcheminsTotaux = 0;

    // Manche 1
    boolean gagneM1 = jouerMancheAuto();
    if (!gagneM1) {
        int remboursement = (int)(COUT_PARTIEA * REMBOURSEMENT_MANCHE1);
        joueur.ajouterOr(remboursement);
        return 0;
    }
    parcheminsTotaux += PARCHEMINS_MANCHE1A;

    // Manche 2
    boolean gagneM2 = jouerMancheAuto();
    if (!gagneM2) {
        int remboursement = (int)(COUT_PARTIEA * REMBOURSEMENT_MANCHE2);
        joueur.ajouterOr(remboursement);
        return parcheminsTotaux;
    }
    parcheminsTotaux += PARCHEMINS_MANCHE2A;

    // Manche 3
    boolean gagneM3 = jouerMancheAuto();
    if (!gagneM3) {
        int remboursement = (int)(COUT_PARTIEA * REMBOURSEMENT_MANCHE3);
        joueur.ajouterOr(remboursement);
        return parcheminsTotaux;
    }
    parcheminsTotaux += PARCHEMINS_MANCHE3A;

    return parcheminsTotaux;
    }

private boolean jouerMancheAuto() {
    int choixJoueur = random.nextInt(3) + 1;
    int choixIA     = random.nextInt(3) + 1;
    int resultat    = evaluer(choixJoueur, choixIA);
    if (resultat == 0) return jouerMancheAuto(); // egalite, on relance
    return resultat == 1;
}

    /**
     * Evalue le resultat : 1 = joueur gagne, -1 = joueur perd, 0 = egalite
     */
    private int evaluer(int joueur, int ia) {
        if (joueur == ia) return 0;
        if ((joueur == 1 && ia == 3) ||
            (joueur == 2 && ia == 1) ||
            (joueur == 3 && ia == 2)) return 1;
        return -1;
    }

    private String getNom(int choix) {
        return switch (choix) {
            case 1 -> "Pierre";
            case 2 -> "Feuille";
            case 3 -> "Ciseaux";
            default -> "?";
        };
    }

    public int getCoutPartieC() { return COUT_PARTIEC; }
    public int getCoutPartieB(){return COUT_PARTIEB;}
    public int getCoutPartieA(){return COUT_PARTIEA;}
}


