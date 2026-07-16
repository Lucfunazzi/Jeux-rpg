package lancement.Menus;

import Combat.Combat;
import lancement.GameContext;
import Joueur.Personnage_principale;
import Joueur.Mage;
import Joueur.Ninja;
import Joueur.Guerrier;
import Joueur.Competences;
import Personnage.PersonnageBase;
import lancement.Gestionnaires.AreneData;
import lancement.Gestionnaires.GestionnaireArene;
import Personnage.Naruto_Shippuden.*;
import Personnage.DragonBallZ.*;
import Personnage.FairyTail.*;
import java.util.*;
import java.util.stream.Collectors;

public class MenuArene {

    private final GameContext        ctx;
    private final Scanner            scanner;
    private final GestionnaireArene  gestionnaireArene;
    private AreneData                joueurArene;

    public MenuArene(GameContext ctx, Scanner scanner) {
        this.ctx               = ctx;
        this.scanner           = scanner;
        this.gestionnaireArene = new GestionnaireArene(this::creerPersonnage);
    }

    // ── Point d'entrée ────────────────────────────────────────────────────

    public void afficher() {
        System.out.println("\n╔══════════════════════════════╗");
        System.out.println("║          ⚔  ARÈNE  ⚔         ║");
        System.out.println("╚══════════════════════════════╝");
        System.out.println("Chargement du classement...");

        gestionnaireArene.chargerDepuisFirebase();
        initialiserJoueur();

        boolean continuer = true;
        while (continuer) continuer = afficherMenuPrincipal();
    }

    // ── Initialisation du joueur dans l'arène ─────────────────────────────

    private void initialiserJoueur() {
        List<String> equipeNoms = ctx.formation.getEquipe().stream()
                .filter(p -> p != null)
                .map(PersonnageBase::getNom)
                .collect(Collectors.toList());

        String userId = ctx.joueur.getNom().trim().toLowerCase().replace(" ", "_");
        String pseudo = ctx.joueur.getNom();

        joueurArene = gestionnaireArene.getOuCreerJoueur(
            userId, pseudo, equipeNoms, ctx.joueur.getNom()
        );
    }

    // ── Menu principal ────────────────────────────────────────────────────

    private boolean afficherMenuPrincipal() {
        boolean coffreDisponible = isCoffreDisponible();

        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("  Rang        : #" + joueurArene.getRang());
        System.out.println("  Pts classmt : " + joueurArene.getPointsArene());
        System.out.println("  Pts boutique: " + joueurArene.getPointsBoutique());
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("  [1] Voir le classement");
        System.out.println("  [2] Choisir un adversaire");
        System.out.println("  [3] Boutique");
        if (coffreDisponible)
            System.out.println("  [4] Coffre journalier  🎁 DISPONIBLE !");
        else
            System.out.println("  [4] Coffre journalier  (disponible à 20h)");
        System.out.println("  [0] Retour");
        System.out.print("  Choix : ");

        return switch (scanner.nextLine().trim()) {
            case "1" -> { afficherClassement();                                        yield true; }
            case "2" -> { choisirAdversaire();                                         yield true; }
            case "3" -> { new MenuBoutiqueArene(ctx, scanner, joueurArene).afficher(); yield true; }
            case "4" -> { ouvrirCoffre();                                              yield true; }
            case "0" -> false;
            default  -> { System.out.println("Choix invalide."); yield true; }
        };
    }

    // ── Classement ────────────────────────────────────────────────────────

    private void afficherClassement() {
        List<AreneData> adversaires = gestionnaireArene.getAdversairesVisibles(joueurArene.getRang());

        System.out.println("\n┌──────┬──────────────────────┬───────────────────┐");
        System.out.println("│ Rang │ Joueur               │ Points            │");
        System.out.println("├──────┼──────────────────────┼───────────────────┤");
        System.out.printf( "│ #%-4d│ %-20s │ %-17d │%n",
            joueurArene.getRang(), "► " + joueurArene.getPseudo(), joueurArene.getPointsArene());
        System.out.println("├──────┼──────────────────────┼───────────────────┤");

        for (AreneData a : adversaires) {
            System.out.printf("│ #%-4d│ %-20s │ %-17d │%n",
                a.getRang(),
                a.getPseudo() + (a.isEstFauxJoueur() ? "" : " ★"),
                a.getPointsArene());
        }

        System.out.println("└──────┴──────────────────────┴───────────────────┘");
        System.out.println("  ★ = vrai joueur");
        System.out.print("\n  Appuie sur Entrée pour continuer...");
        scanner.nextLine();
    }

    // ── Choix adversaire ──────────────────────────────────────────────────

    private void choisirAdversaire() {
        List<AreneData> adversaires = gestionnaireArene.getAdversairesVisibles(joueurArene.getRang());

        System.out.println("\n  Adversaires disponibles :");
        System.out.println("  ──────────────────────────────────────────────────");
        for (int i = 0; i < adversaires.size(); i++) {
            AreneData a = adversaires.get(i);
            // Afficher l'équipe en remplaçant le marqueur PP_ par un nom lisible
            List<String> nomsEquipe = new ArrayList<>(a.getEquipeDefensiveNoms());
            String nomPrincipal = a.getPersonnagePrincipalNom();
            if (nomPrincipal != null && nomPrincipal.startsWith("PP_")) {
                nomsEquipe.add("Combattant (" + nomPrincipal.replace("PP_", "") + ")");
            } else if (nomPrincipal != null) {
                nomsEquipe.add(nomPrincipal);
            }
            System.out.printf("  [%d] Rang #%-4d │ %-20s │ %s%n",
                i + 1, a.getRang(), a.getPseudo(),
                String.join(", ", nomsEquipe));
        }
        System.out.println("  [0] Retour");
        System.out.print("  Choix : ");

        int choix;
        try { choix = Integer.parseInt(scanner.nextLine().trim()); }
        catch (NumberFormatException e) { System.out.println("Choix invalide."); return; }

        if (choix == 0) return;
        if (choix < 1 || choix > adversaires.size()) { System.out.println("Choix invalide."); return; }

        lancerCombat(adversaires.get(choix - 1));
    }

    // ── Combat ────────────────────────────────────────────────────────────

    private void lancerCombat(AreneData adversaire) {
        System.out.println("\n  ⚔ Combat contre " + adversaire.getPseudo()
                         + " (Rang #" + adversaire.getRang() + ") !");

        List<PersonnageBase> equipeJoueur = ctx.formation.getEquipe().stream()
                .filter(p -> p != null)
                .collect(Collectors.toList());

        if (equipeJoueur.isEmpty()) {
            System.out.println("  Ton équipe est vide ! Configure ta formation d'abord.");
            return;
        }

        // Construire les 4 membres normaux de l'équipe adverse
        List<PersonnageBase> equipeAdverse = adversaire.construireEquipe(this::creerPersonnage);

        // Ajouter le personnage principal IA (5ème membre)
        PersonnageBase principalAdverse = creerPersonnagePrincipalIA(
            adversaire.getPersonnagePrincipalNom()
        );
        if (principalAdverse != null) {
            equipeAdverse.add(principalAdverse);
        } else if (!equipeAdverse.isEmpty()) {
            principalAdverse = equipeAdverse.get(0);
        }

        if (equipeAdverse.isEmpty()) {
            System.out.println("  Erreur : impossible de construire l'équipe adverse.");
            return;
        }

        // Réinitialiser les deux équipes avant le combat
        for (PersonnageBase p : equipeJoueur)  p.reinitialiserPourCombat();
        for (PersonnageBase p : equipeAdverse) p.reinitialiserPourCombat();

        boolean victoire = Combat.lancerCombatArene(
            equipeJoueur, ctx.joueur,
            equipeAdverse, principalAdverse,
            scanner
        );

        if (victoire) gestionnaireArene.appliquerVictoire(joueurArene, adversaire);
        else          gestionnaireArene.appliquerDefaite(joueurArene);

        gestionnaireArene.uploaderRangJoueur(joueurArene);
        ctx.sauvegarde.sauvegarder(ctx);
    }

    // ── Personnage principal IA ───────────────────────────────────────────

    /**
     * Crée un Personnage_principale IA pour les faux joueurs.
     * Le marqueur est "PP_Mage", "PP_Ninja" ou "PP_Guerrier".
     * Pour les vrais joueurs, on essaie creerPersonnage() normalement.
     */
    private PersonnageBase creerPersonnagePrincipalIA(String marqueur) {
        if (marqueur == null) return null;

        // Vrai joueur — le nom stocké est son pseudo, pas un perso recrutaable
        // On retourne null et lancerCombat() utilisera le premier de l'équipe
        if (!marqueur.startsWith("PP_")) return null;

        String classe = marqueur.replace("PP_", "");
        Personnage_principale pp = new Personnage_principale("Combattant", 30);
        Competences comp = switch (classe) {
            case "Mage"     -> new Mage();
            case "Ninja"    -> new Ninja();
            default         -> new Guerrier();
        };
        pp.setChoixClasses(classe);
        pp.setChoixComp(1 + new Random(classe.hashCode()).nextInt(3));
        pp.setCompetencesChoisie(comp);
        return pp;
    }

    // ── Coffre journalier ─────────────────────────────────────────────────

    private boolean isCoffreDisponible() {
        if (java.time.LocalDateTime.now().getHour() < 20) return false;
        return !getCleJour().equals(getDerniereCleCoffre());
    }

    private String getCleJour() {
        java.time.LocalDate aujourd = java.time.LocalDate.now();
        if (java.time.LocalDateTime.now().getHour() < 20)
            aujourd = aujourd.minusDays(1);
        return aujourd.toString();
    }

    private String getDerniereCleCoffre() {
        return ctx.dernierCoffreArene != null ? ctx.dernierCoffreArene : "";
    }

    private void ouvrirCoffre() {
        if (!isCoffreDisponible()) {
            System.out.println("\n  Le coffre sera disponible à 20h !");
            return;
        }

        int rang = joueurArene.getRang();
        int pointsBoutique;
        int or;
        int coupons = 0;
        String tranche;

        if (rang == 1) {
            pointsBoutique = 15_000; or = 110_000; coupons = 50;
            tranche = "Rang #1 — Légendaire";
        } else if (rang <= 4) {
            pointsBoutique = 11_000; or = 100_000;
            tranche = "Rang #2–4 — Élite";
        } else if (rang <= 9) {
            pointsBoutique = 10_000; or = 90_000;
            tranche = "Rang #5–9 — Maître";
        } else if (rang <= 24) {
            pointsBoutique = 7_000; or = 60_000;
            tranche = "Rang #10–24 — Expert";
        } else if (rang <= 49) {
            pointsBoutique = 5_000; or = 40_000;
            tranche = "Rang #25–49 — Avancé";
        } else if (rang <= 74) {
            pointsBoutique = 2_500; or = 20_000;
            tranche = "Rang #50–74 — Intermédiaire";
        } else {
            pointsBoutique = 1_500; or = 10_000;
            tranche = "Rang #75–100 — Débutant";
        }

        System.out.println("\n  🎁 COFFRE JOURNALIER — " + tranche);
        System.out.println("  ─────────────────────────────────────");
        System.out.printf ("  + %,d points boutique%n", pointsBoutique);
        System.out.printf ("  + %,d or%n", or);
        if (coupons > 0) System.out.println("  + " + coupons + " coupons !");
        System.out.println("  ─────────────────────────────────────");

        joueurArene.ajouterPointsBoutique(pointsBoutique);
        ctx.joueur.ajouterOr(or);
        if (coupons > 0) ctx.joueur.setCoupons(ctx.joueur.getCoupons() + coupons);

        ctx.dernierCoffreArene = getCleJour();
        gestionnaireArene.uploaderRangJoueur(joueurArene);
        ctx.sauvegarde.sauvegarder(ctx);

        System.out.print("\n  Appuie sur Entrée pour continuer...");
        scanner.nextLine();
    }

    // ── Factory personnages ───────────────────────────────────────────────

    private PersonnageBase creerPersonnage(String nom) {
        return switch (nom) {
            // ── Rang C ──
            case "Kiba"             -> new perso_Kiba();
            case "Shino"            -> new perso_Shino();
            case "Tenten"           -> new perso_Tenten();
            case "Iruka"            -> new perso_Iruka();
            case "Yamcha"           -> new perso_Yamcha();
            case "Tien"             -> new perso_Tien();
            case "Chiaotzu"         -> new perso_Chiaotzu();
            case "Elfman"           -> new perso_Elfman();
            case "Alzack"           -> new perso_Alzack();
            case "Bisca"            -> new perso_Bisca();
            // ── Rang B ──
            case "Hinata"           -> new perso_Hinata();
            case "Choji"            -> new perso_Choji();
            case "Ino"              -> new perso_Ino();
            case "Lee"              -> new perso_Lee();
            case "Kankuro"          -> new perso_Kankuro();
            case "Temari"           -> new perso_Temari();
            case "Kabuto"           -> new perso_Kabuto();
            case "Haku"             -> new perso_Haku();
            case "Zabuza"           -> new perso_Zabuza();
            case "Krillin"          -> new perso_Krillin();
            case "Raditz"           -> new perso_Raditz();
            case "Cana"             -> new perso_Kana();
            case "Loke"             -> new perso_Loke();
            case "Bickslow"         -> new perso_Bickslow();
            case "Evergreen"        -> new perso_Evergreen();
            case "Nappa"            -> new perso_Nappa();
            case "Gohan (enfant)"   -> new perso_Gohan_Enfant();
            // ── Rang A ──
            case "Naruto"           -> new perso_Naruto();
            case "Sasuke"           -> new perso_Sasuke();
            case "Sakura"           -> new perso_Sakura();
            case "Kakashi"          -> new perso_Kakashi();
            case "Neji"             -> new perso_Neji();
            case "Shikamaru"        -> new perso_Shikamaru();
            case "Kurenai"          -> new perso_Kurenai();
            case "Asuma"            -> new perso_Asuma();
            case "Gai"              -> new perso_Gai();
            case "Hidan"            -> new perso_Hidan();
            case "Deidara"          -> new perso_Deidara();
            case "Sangoku"          -> new perso_Sangoku();
            case "Natsu"            -> new perso_Natsu();
            case "Lucy"             -> new perso_Lucy();
            case "Gray"             -> new perso_Gray();
            case "Freed"            -> new perso_Freed();
            case "Gajeel"           -> new perso_Gajeel();
            case "Jubia"            -> new perso_Jubia();
            case "Wendy"            -> new perso_Wendy();
            case "Vegeta"           -> new perso_Vegeta_detecteur();
            case "Piccolo"          -> new perso_Piccolo();
            // ── Rang S ──
            case "Itachi"           -> new perso_Itachi();
            case "Orochimaru"       -> new perso_Orochimaru();
            case "Gaara"            -> new perso_Gaara();
            case "Erza"             -> new perso_Erza();
            case "Mirajane"         -> new perso_Mirajane();
            case "Sting"            -> new perso_Sting();
            case "Rogue"            -> new perso_Rogue();
            case "C-17"             -> new perso_C17();
            case "C-18"             -> new perso_C18();
            case "Freezer"          -> new perso_Freezer();
            case "Cell"             -> new perso_Cell();
            case "Natsu Etherion"   -> new perso_Natsu_Etherion();
            case "Mirajane Halphas" -> new perso_Mirajane_Halphas();
            case "Sasori" -> new perso_Sasori();
            // ── Rang SS ──
            case "Lucas"            -> new perso_Lucas();
            default                 -> null;
        };
    }
}