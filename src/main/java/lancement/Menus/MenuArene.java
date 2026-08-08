package lancement.Menus;

import Combat.Combat;
import lancement.GameContext;
import lancement.RangJoueur;
import Joueur.ArbreCompetences;
import Joueur.Personnage_principale;
import Joueur.Elementaliste;
import Joueur.Chevalier;
import Joueur.ChasseurDeDragon;
import Joueur.Invocateur;
import Joueur.Competences;
import Equipement.EquipementFactory;
import Personnage.PersonnageBase;
import lancement.Gestionnaires.AreneData;
import lancement.Gestionnaires.GestionnaireArene;
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
        gestionnaireArene.uploaderRangJoueur(joueurArene);

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
        if (ctx.rangJoueur.estCoffreRangDisponible())
            System.out.println("  [5] Coffre d'Arène — Rang " + ctx.rangJoueur.getRangNom() + "  🎁 DISPONIBLE !");
        else
            System.out.println("  [5] Coffre d'Arène — Rang " + ctx.rangJoueur.getRangNom() + "  (deja reclame)");
        System.out.println("  [0] Retour");
        System.out.print("  Choix : ");

        return switch (scanner.nextLine().trim()) {
            case "1" -> { afficherClassement();                                        yield true; }
            case "2" -> { choisirAdversaire();                                         yield true; }
            case "3" -> { new MenuBoutiqueArene(ctx, scanner, joueurArene, gestionnaireArene).afficher(); yield true; }
            case "4" -> { ouvrirCoffre();                                              yield true; }
            case "5" -> { ouvrirCoffreDeRang();                                        yield true; }
            case "0" -> false;
            default  -> { System.out.println("Choix invalide."); yield true; }
        };
    }

    // ── Coffre d'Arene selon le Rang Joueur (C -> UR) ──────────────────────

    private void ouvrirCoffreDeRang() {
        String resultat = ctx.rangJoueur.reclamerCoffreRang(ctx.inventaire);
        System.out.println("\n  " + resultat);
        ctx.sauvegarde.sauvegarder(ctx);
        System.out.print("\n  Appuie sur Entrée pour continuer...");
        scanner.nextLine();
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

        // Ajouter le personnage principal IA (5ème membre) au même niveau que l'équipe
        int niveauEquipeAdv = Math.max(1, adversaire.getNiveauMoyenEquipe());
        PersonnageBase principalAdverse = creerPersonnagePrincipalIA(
            adversaire.getPersonnagePrincipalNom(), niveauEquipeAdv, adversaire.getRang()
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

        lancement.Gestionnaires.GestionnaireArene.appliquerCompagnonAdversaire(
                equipeAdverse, rangJoueurPourRangArene(adversaire.getRang()));

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
     * Le marqueur est "PP_Chevalier", "PP_Chasseur de Dragon", "PP_Mage" ou "PP_Constellationniste".
     * Pour les vrais joueurs, on essaie creerPersonnage() normalement.
     */
    /** Crée le PP adverse IA au bon niveau et avec les compétences exclusives à sa classe.
     *  @param rangArene position au classement (1 = meilleur), utilisee pour donner au PP un
     *  rang de stats et un nombre d'arbres de competences debloques coherents avec son niveau
     *  de classement (sans quoi ce PP restait toujours plus faible que ses 4 coequipiers, qui
     *  eux sont geares et montes de niveau via AreneData.construireEquipe). */
    public static PersonnageBase creerPersonnagePrincipalIA(String marqueur, int niveauCible, int rangArene) {
        if (marqueur == null || !marqueur.startsWith("PP_")) return null;

        String classe = marqueur.replace("PP_", "");

        // Compétences liées à la classe — chaque classe a sa spéciale et ultime fixes
        Competences comp = switch (classe) {
            case "Mage"                -> new Elementaliste();
            case "Chasseur de Dragon"  -> new ChasseurDeDragon();
            case "Chevalier"           -> new Chevalier();
            case "Constellationniste"  -> new Invocateur();
            default                    -> new Chevalier();
        };

        Personnage_principale pp = new Personnage_principale("Combattant (" + classe + ")", 1);
        pp.setChoixClasses(classe);
        pp.setCompetencesChoisie(comp);

        // Rang IA independant du rang du vrai joueur : contexte minimal ne portant que le
        // rangJoueur necessaire au multiplicateur de stats (getMultiplicateurRang()).
        GameContext ctxIA = new GameContext();
        ctxIA.rangJoueur = new RangJoueur();
        pp.setGameContext(ctxIA);

        // Monte le PP niveau par niveau en faisant progresser son rang (donc son coefficient de
        // croissance) EN MEME TEMPS que le niveau, exactement comme un vrai joueur qui grimperait
        // C -> B -> A -> ... au fil de sa progression. Fixer d'emblee le coefficient du rang final
        // puis monter tous les niveaux avec cette valeur composerait le +5%/niveau avec un
        // multiplicateur bien trop eleve des le niveau 1 (ex: rang A avec pres de 300k PV), ce qui
        // ne correspond a aucun personnage reel de ce rang.
        ctxIA.rangJoueur.setRang(rangPourNiveauIA(1));
        while (pp.getNiveau() < niveauCible) {
            ctxIA.rangJoueur.setRang(rangPourNiveauIA(pp.getNiveau() + 1));
            pp.monterDeNiveauSilencieux();
        }

        // Une fois la courbe de niveaux montee, le rang "officiel" affiche (badge, couleur) et
        // utilise pour l'equipement/les arbres suit la position au classement d'arene. Ce
        // changement de rang ne s'applique qu'aux montees de niveau FUTURES (voir
        // multiplicateurCroissanceNiveau) : il ne reapplique donc rien retroactivement sur les
        // stats deja accumulees ci-dessus.
        RangJoueur.Rang rangFinal = rangJoueurPourRangArene(rangArene);
        ctxIA.rangJoueur.setRang(rangFinal);

        // Debloque completement les arbres de competences correspondant au rang final (comme un
        // vrai personnage qui aurait complete certains arbres a ce stade), puis choisit
        // aleatoirement l'attaque speciale et l'attaque ultime actives parmi celles debloquees.
        ArbreCompetences arbre = pp.getArbreCompetences();
        int[] arbresDebloques = arbresDeblo(rangFinal);
        for (int numArbre : arbresDebloques) {
            for (int i = 1; i <= 10; i++) arbre.getNoeud(numArbre, i).debloquer();
        }

        List<Integer> specialesDispo = new ArrayList<>();
        List<Integer> ultimesDispo   = new ArrayList<>();
        for (int numArbre : arbresDebloques) {
            if (numArbre == 4 || numArbre == 8) ultimesDispo.add(numArbre);
            else                                specialesDispo.add(numArbre);
        }
        Random rng = new Random();
        if (!specialesDispo.isEmpty())
            pp.setCompetenceSpecialeActive(specialesDispo.get(rng.nextInt(specialesDispo.size())));
        if (!ultimesDispo.isEmpty())
            pp.setCompetenceUltimeActive(ultimesDispo.get(rng.nextInt(ultimesDispo.size())));

        // Equipement fantome, comme les 4 coequipiers (AreneData.construireEquipe) : sans ca,
        // le PP adverse restait toujours nu face a des coequipiers geares.
        EquipementFactory.equiperSetStandard(pp, EquipementFactory.rareteEnnemiPourRangArene(rangArene));

        return pp;
    }

    /** Rang de classement d'arene (1 = meilleur, 100 = pire) -> rang C..UR du faux PP, utilise
     *  pour son rang affiche final, son equipement fantome et ses arbres de competences
     *  debloques. */
    public static RangJoueur.Rang rangJoueurPourRangArene(int rangArene) {
        if (rangArene >= 80) return RangJoueur.Rang.C;
        if (rangArene >= 65) return RangJoueur.Rang.B;
        if (rangArene >= 30) return RangJoueur.Rang.A;
        if (rangArene >= 20) return RangJoueur.Rang.S;
        if (rangArene >= 10) return RangJoueur.Rang.SS;
        if (rangArene >= 3)  return RangJoueur.Rang.SSS;
        return RangJoueur.Rang.UR;
    }

    /** Palier de rang C..UR selon le niveau, utilise UNIQUEMENT pour faire progresser le
     *  coefficient de croissance du faux PP au fil de sa montee de niveaux (voir
     *  creerPersonnagePrincipalIA) — distinct des seuils de RangJoueur (bases sur les arbres de
     *  competences + l'Examen S), puisque le PP IA ne passe pas par ces conditions. */
    private static RangJoueur.Rang rangPourNiveauIA(int niveau) {
        if (niveau <= 30)  return RangJoueur.Rang.C;
        if (niveau <= 60)  return RangJoueur.Rang.B;
        if (niveau <= 90)  return RangJoueur.Rang.A;
        if (niveau <= 120) return RangJoueur.Rang.S;
        if (niveau <= 150) return RangJoueur.Rang.SS;
        if (niveau <= 190) return RangJoueur.Rang.SSS;
        return RangJoueur.Rang.UR;
    }

    /** Arbres de competences a debloquer entierement pour un rang donne : le rang C debloque les
     *  2 premieres attaques speciales (arbres 1/2), B la 3e (arbre 3), A le 2e ultime (arbre 8 —
     *  les 3 autres specials restent le seul choix disponible), S les 3 dernieres specials
     *  (arbres 5/6/7), et SS/SSS/UR debloquent tous les arbres restants (dont l'arbre 4, le 1er
     *  ultime). */
    private static int[] arbresDeblo(RangJoueur.Rang rang) {
        return switch (rang) {
            case C  -> new int[]{1, 2};
            case B  -> new int[]{1, 2, 3};
            case A  -> new int[]{1, 2, 3, 8};
            case S  -> new int[]{1, 2, 3, 5, 6, 7, 8};
            default -> new int[]{1, 2, 3, 4, 5, 6, 7, 8}; // SS, SSS, UR
        };
    }

    // ── Coffre journalier ─────────────────────────────────────────────────

    private boolean isCoffreDisponible() {
        return gestionnaireArene.coffreJournalierDisponible(ctx);
    }

    private void ouvrirCoffre() {
        GestionnaireArene.RecompenseCoffreJournalier recompense = gestionnaireArene.reclamerCoffreJournalier(joueurArene, ctx);
        if (recompense == null) {
            System.out.println("\n  Le coffre sera disponible à 20h !");
            return;
        }

        System.out.println("\n  🎁 COFFRE JOURNALIER — " + recompense.tranche());
        System.out.println("  ─────────────────────────────────────");
        System.out.printf ("  + %,d points boutique%n", recompense.pointsBoutique());
        System.out.printf ("  + %,d or%n", recompense.or());
        if (recompense.coupons() > 0) System.out.println("  + " + recompense.coupons() + " coupons !");
        System.out.println("  ─────────────────────────────────────");

        System.out.print("\n  Appuie sur Entrée pour continuer...");
        scanner.nextLine();
    }

    // ── Factory personnages ───────────────────────────────────────────────

    private PersonnageBase creerPersonnage(String nom) {
        PersonnageBase p = creerPersonnageConnu(nom);
        // Repli sur la fabrique du Recrutement normal pour tout personnage
        // pas explicitement liste ci-dessous (evite les disparitions silencieuses
        // si un nom est ajoute a un pool de GestionnaireArene sans etre duplique ici).
        if (p == null) p = new MenuRecrutement().creerPersonnage(nom);
        return p;
    }

    private PersonnageBase creerPersonnageConnu(String nom) {
        return switch (nom) {
            // ── Rang C ──
            case "Alzack"           -> new perso_Arzak();
            case "Bisca"            -> new perso_Biska();
            case "Nab"              -> new perso_Nab();
            case "Duc Everlue"      -> new perso_DucEverlue();
            case "Yuka"             -> new perso_Yuka();
            case "Cherry"           -> new perso_Cherry();
            case "Bora"             -> new perso_Bora();
            case "Eligoal"          -> new perso_Eligor();
            case "Tobi"             -> new perso_Tobi();

            // ── Rang B ──
            case "Elfman"           -> new perso_Elfman();
            case "Sol"              -> new perso_Sol();
            case "Levy"             -> new perso_Levy();
            case "Lisanna"          -> new perso_Lisanna();
            case "Kana"             -> new perso_Kana();
            case "Bickslow"         -> new perso_Bixrow();
            case "Leon"             -> new perso_Leon();
            case "Totomaru"         -> new perso_Totomaru();

            // ── Rang A ──
            case "Natsu"            -> new perso_Natsu();
            case "Lucy"             -> new perso_Lucy();
            case "Gray"             -> new perso_Gray();
            case "Freed"            -> new perso_Freed();
            case "Gajeel"           -> new perso_Gajeel();
            case "Jubia"            -> new perso_Jubia_4elements();
            case "Wendy"            -> new perso_Wendy();
            case "Evergreen"        -> new perso_Evergreen();
            case "Angel"            -> new perso_Angel();
            case "Aria"             -> new perso_Aria();

            // ── Rang S ──
            case "Erza"             -> new perso_Erza();
            case "Mirajane"         -> new perso_Mirajane();
            case "Sting"            -> new perso_Sting();
            case "Rogue"            -> new perso_Rogue();
            case "Natsu Etherion"   -> new perso_Natsu_Etherion();
            case "Yukino"           -> new perso_Yukino();
            case "José Pora"        -> new perso_Jose();

            // ── Rang SS ──
            case "Lucas"            -> new perso_Lucas();
            case "Mirajane Halphas" -> new perso_Mirajane_Halphas();
            case "Ul Milkovich"     -> new perso_Ul();

            default -> null;
        };
    }

  
}