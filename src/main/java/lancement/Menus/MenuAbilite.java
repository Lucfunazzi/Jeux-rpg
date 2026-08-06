package lancement.Menus;

import Joueur.ArbreCompetences;
import Joueur.NoeudArbre;
import Joueur.Personnage_principale;
import java.util.Scanner;
import lancement.GameContext;
import lancement.RangJoueur;

public class MenuAbilite {

    public void afficher(GameContext ctx, Scanner scanner) {
        Personnage_principale joueur = ctx.joueur;
        ArbreCompetences arbre = joueur.getArbreCompetences();
        boolean retour = false;

        while (!retour) {
            System.out.println("\n========================================");
            System.out.println("            ARBRE DE COMPÉTENCES");
            System.out.println("========================================");
            System.out.println("Points disponibles : " + arbre.getPointsDisponibles());
            System.out.println();

            // Afficher les compétences actives
            String[] noms = joueur.getNomsAttaques();
            System.out.println("  Spéciale active : " + noms[1]);
            System.out.println("  Ultime  active  : " + noms[2]);
            System.out.println();

            for (int numArbre = 1; numArbre <= 8; numArbre++) {
                System.out.println(numArbre + ". " + ligneArbre(arbre, numArbre, joueur.getChoixClasses()));
            }
            System.out.println("0. Retour");
            System.out.print("Votre choix : ");

            String choix = scanner.nextLine().trim();
            if (choix.equals("0")) {
                retour = true;
                continue;
            }
            int numArbre;
            try {
                numArbre = Integer.parseInt(choix);
            } catch (NumberFormatException e) {
                System.out.println("Choix invalide.");
                continue;
            }
            if (numArbre < 1 || numArbre > 8) {
                System.out.println("Choix invalide.");
            } else if (!estArbreDebloque(arbre, numArbre)) {
                System.out.println("L'arbre " + numArbre + " se débloque en terminant l'Arbre " + (numArbre - 1) + ".");
            } else {
                afficherArbre(ctx, arbre, numArbre, scanner);
            }
        }
    }

    private static boolean estArbreDebloque(ArbreCompetences arbre, int numArbre) {
        return switch (numArbre) {
            case 1  -> true;
            case 2  -> arbre.isArbre2Debloque();
            case 3  -> arbre.isArbre3Debloque();
            case 4  -> arbre.isArbre4Debloque();
            case 5  -> arbre.isArbre5Debloque();
            case 6  -> arbre.isArbre6Debloque();
            case 7  -> arbre.isArbre7Debloque();
            default -> arbre.isArbre8Debloque();
        };
    }

    private static boolean estArbreComplete(ArbreCompetences arbre, int numArbre) {
        return arbre.getNoeud(numArbre, 10).isDebloque();
    }

    private static String ligneArbre(ArbreCompetences arbre, int numArbre, String classe) {
        boolean ultime = numArbre == 4 || numArbre == 8;
        String titre = "Arbre " + numArbre + " — Nouvelle " + (ultime ? "Ultime" : "Spéciale");
        if (!estArbreDebloque(arbre, numArbre)) {
            return titre + " [VERROUILLÉ — terminez l'Arbre " + (numArbre - 1) + "]";
        }
        if (estArbreComplete(arbre, numArbre)) {
            String nom = ultime ? Personnage_principale.getNomUltimeArbre(classe, numArbre) : getNomCompetence(classe, numArbre);
            return titre + " [DÉBLOQUÉ : " + nom + "]" + (numArbre == 3 ? " — Rang B !" : "");
        }
        return titre;
    }

    private void afficherArbre(GameContext ctx,
                                ArbreCompetences arbre,
                                int numArbre,
                                Scanner scanner) {
        Personnage_principale joueur = ctx.joueur;
        boolean retour = false;

        while (!retour) {
            String nomArbre = switch (numArbre) {
                case 1 -> " — Voie du Combattant";
                case 2 -> " — Voie du Maître";
                case 3 -> " — Voie de l'Ascension";
                case 4 -> " — Voie de l'Ultime (à définir)";
                default -> numArbre == 8 ? " — Voie de l'Ultime supreme (à définir)" : " — Voie de la Maitrise (à définir)";
            };
            System.out.println("\n========================================");
            System.out.println("  ARBRE " + numArbre + nomArbre);
            System.out.println("========================================");
            System.out.println("Points disponibles : " + arbre.getPointsDisponibles());
            System.out.println();

            for (int i = 1; i <= 10; i++) {
                NoeudArbre n = getNoeud(arbre, numArbre, i);
                String etat = n.isDebloque() ? "[OK]" : "[  ]";
                String cout = n.isDebloque() ? "      " : "(" + n.getCoutPoints() + " pts)";
                System.out.println(etat + " Nœud " + i + " — " + n.getDescription() + "  " + cout);
            }

            System.out.println();
            System.out.println("Entrez le numéro du nœud à débloquer (0 pour revenir) :");
            int choix;
            try {
                choix = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Entrée invalide.");
                continue;
            }

            if (choix == 0) {
                retour = true;
            } else if (choix < 1 || choix > 10) {
                System.out.println("Nœud invalide.");
            } else {
                NoeudArbre n = getNoeud(arbre, numArbre, choix);

                if (n.getTypeBonus() == NoeudArbre.TypeBonus.COMPETENCE_SPECIALE
                        || n.getTypeBonus() == NoeudArbre.TypeBonus.COMPETENCE_ULTIME) {
                    System.out.println(debloquerNoeudCompetence(ctx, arbre, numArbre, choix));
                } else {
                    String resultat = arbre.tenterDebloquer(numArbre, choix);
                    if (resultat.equals("OK")) {
                        System.out.println(">> Nœud " + choix + " débloqué : " + n.getDescription() + " !");
                        System.out.println("   Points restants : " + arbre.getPointsDisponibles());
                    } else {
                        System.out.println(resultat);
                    }
                }
            }
        }
    }

    public static NoeudArbre getNoeud(ArbreCompetences arbre, int numArbre, int index) {
        return arbre.getNoeud(numArbre, index);
    }

    /** true pour les arbres 4 et 8 (ultime), false pour les arbres a speciale (1/2/3/5/6/7). */
    private static boolean estArbreUltime(int numArbre) {
        return numArbre == 4 || numArbre == 8;
    }

    /** Debloque un noeud de type competence speciale/ultime (avec activation automatique). Retourne le message resultat. */
    public static String debloquerNoeudCompetence(GameContext ctx,
                                           ArbreCompetences arbre,
                                           int numArbre, int indexNoeud) {
        Personnage_principale joueur = ctx.joueur;
        NoeudArbre n = getNoeud(arbre, numArbre, indexNoeud);
        boolean ultime = estArbreUltime(numArbre);
        String nomComp = ultime
                ? Personnage_principale.getNomUltimeArbre(joueur.getChoixClasses(), numArbre)
                : getNomCompetence(joueur.getChoixClasses(), numArbre);
        String typeMot = ultime ? "ultime" : "speciale";

        if (n.isDebloque()) {
            return nomComp + " est deja debloquee et active.";
        }

        String resultat = arbre.tenterDebloquer(numArbre, indexNoeud);
        if (!resultat.equals("OK")) return resultat;

        // Ne pas equiper automatiquement la nouvelle competence : le joueur choisit quand
        // l'activer depuis le menu Formation (bouton "Changer la competence").
        StringBuilder sb = new StringBuilder();
        sb.append("Nouvelle ").append(typeMot).append(" debloquee : ").append(nomComp).append(" !\n");
        sb.append("Retrouvez-la dans le menu Formation pour l'equiper.");

        if (numArbre == 3) {
            RangJoueur rangJoueur = ctx.rangJoueur;
            if (rangJoueur.getRang() == RangJoueur.Rang.C) {
                String resultatRang = rangJoueur.tenterMonteeRang(
                        joueur.getNiveau(), joueur.getArbreCompetences(), ctx.gestionnaireExamenS);
                if (resultatRang.equals("OK")) {
                    sb.append("\nFelicitations ! Vous passez Rang B !\n");
                    sb.append("Multiplicateur de stats : x").append(String.format("%.2f", rangJoueur.getMultiplicateur()));
                }
            }
        }
        sb.append("\nPoints restants : ").append(arbre.getPointsDisponibles());
        return sb.toString();
    }

    // ── Noms des compétences d'arbre par classe ───────────────────────────
    public static String getNomCompetence(String classe, int arbre) {
        return Joueur.Personnage_principale.getNomCompetenceArbre(classe, arbre);
    }
}
