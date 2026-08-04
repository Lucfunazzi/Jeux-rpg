package lancement.Gestionnaires;

import Equipement.BoiteGuilde;
import Equipement.CarteOr;
import Equipement.CleCoffreGuilde;
import Equipement.Inventaire;
import Equipement.PotionEnergie;
import lancement.Menus.MenuExamenS;
import lancement.Menus.MenuRecrutement;
import Joueur.Personnage_principale;
import Personnage.PersonnageBase;
import java.time.LocalDate;
import java.util.ArrayList;
import lancement.Quetes.Quete;
import lancement.Quetes.QueteJournaliere;
import lancement.Quetes.QueteProgression;
import Personnage.FairyTail.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestionnaireQuetes {

    /** Niveau joueur requis pour acceder au menu des quetes journalieres. */
    public static final int NIVEAU_DEBLOCAGE_JOURNALIERES = 30;

    private final ArrayList<QueteProgression> quetesProgression = new ArrayList<>();
    private List<QueteJournaliere> quetesJournalieres = new ArrayList<>();
    private LocalDate dernierRenouvellement;
    private int indexQueteJournaliere = 0; // conserve pour compatibilite de sauvegarde uniquement

    // ── Barre de points quotidienne (5 quetes journalieres actives = 100 pts max) ──
    public static final int[] PALIERS_BARRE_JOURNALIERE = {20, 40, 60, 80, 100};
    private int       pointsJournaliers      = 0;
    private boolean[] barreJournaliereReclame = new boolean[PALIERS_BARRE_JOURNALIERE.length];

    // Map : id de quête → personnage à offrir (null = pas de perso)
private final Map<String, PersonnageBase> recompensesPersonnages = new HashMap<>();

private void initialiserRecompensesPersonnages() {
    recompensesPersonnages.put("C1S3",  new perso_Bora());    // Stage 3 chapitre 1  → Bora
    recompensesPersonnages.put("C1S5", new perso_Nab());   // Stage 5 chapitre 1 → Nab
  
}

    // Pool de quêtes journalières
    // XP journalière calibrée pour être un petit bonus, pas une source principale
    private static final QueteJournaliere[] POOL_JOURNALIER = {
        new QueteJournaliere("J1", "Forgeron du jour",
                "Fortifiez votre equipement 3 fois.",
                QueteJournaliere.TypeObjectif.FORTIFIER, 3, 500, 2000,
                List.of(new Quete.RecompenseItem(CleCoffreGuilde.NOM, 1))),
        new QueteJournaliere("J2", "Accumulateur",
                "Gagnez 500 or en combat.",
                QueteJournaliere.TypeObjectif.GAGNER_OR, 500, 500, 1000,
                List.of(new Quete.RecompenseItem(BoiteGuilde.NOM, 1))),
        new QueteJournaliere("J3", "Mage assidu",
                "Terminez 3 stages.",
                QueteJournaliere.TypeObjectif.TERMINER_STAGE, 3, 800, 1500,
                List.of(new Quete.RecompenseItem(CleCoffreGuilde.NOM, 1))),
        new QueteJournaliere("J4", "Grande forge",
                "Fortifiez votre equipement 5 fois.",
                QueteJournaliere.TypeObjectif.FORTIFIER, 5, 800, 2500,
                List.of(new Quete.RecompenseItem(BoiteGuilde.NOM, 1))),
        new QueteJournaliere("J5", "Chasseur de richesses",
                "Gagnez 1000 or en combat.",
                QueteJournaliere.TypeObjectif.GAGNER_OR, 1000, 1000, 2000,
                List.of(new Quete.RecompenseItem(CleCoffreGuilde.NOM, 1),
                        new Quete.RecompenseItem(BoiteGuilde.NOM, 1))),
    };

  public GestionnaireQuetes() {
    initialiserQuetesProgression();
    initialiserRecompensesPersonnages();   // ← ajouter
    dernierRenouvellement = LocalDate.now().minusDays(1);
    verifierRenouvellement();
    // Le tout premier stage du jeu est jouable des le lancement d'une nouvelle partie,
    // sans etape d'acceptation manuelle (rien a "debloquer" avant lui).
    QueteProgression c1s1 = trouverQueteProgression("C1S1");
    if (c1s1 != null) c1s1.setAcceptee(true);
}

    // ── Initialisation des quêtes de progression (definitions dans QuetesProgressionData) ──
    private void initialiserQuetesProgression() {
        quetesProgression.addAll(QuetesProgressionData.creer());
    }

    // ── Renouvellement automatique à minuit ───────────────────────────────
    // Les 5 quetes du pool sont toutes actives simultanement chaque jour
    // (plutot qu'une seule qui tourne), pour permettre d'atteindre les 100
    // points de la barre journaliere en une seule journee.
    public void verifierRenouvellement() {
        LocalDate aujourdhui = LocalDate.now();
        if (quetesJournalieres.isEmpty() || !aujourdhui.equals(dernierRenouvellement)) {
            quetesJournalieres = new ArrayList<>();
            for (QueteJournaliere modele : POOL_JOURNALIER) {
                quetesJournalieres.add(new QueteJournaliere(
                    modele.getId(), modele.getTitre(), modele.getDescription(),
                    modele.getTypeObjectif(), modele.getObjectifCible(),
                    modele.getRecompenseXP(), modele.getRecompenseOr(),
                    modele.getRecompensesItems()
                ));
            }
            pointsJournaliers       = 0;
            barreJournaliereReclame = new boolean[PALIERS_BARRE_JOURNALIERE.length];
            dernierRenouvellement   = aujourdhui;
        }
    }

    /** Retrouve les recompenses d'items associees au modele de quete portant cet id (pour la restauration de sauvegarde). */
    public static List<Quete.RecompenseItem> recompensesItemsPourId(String id) {
        for (QueteJournaliere modele : POOL_JOURNALIER) {
            if (modele.getId().equals(id)) return modele.getRecompensesItems();
        }
        return List.of();
    }

    // ── Déclencheurs appelés depuis l'extérieur ───────────────────────────
    public void notifierStageFini(int chapitre, int stage, boolean estElite,
                                   Personnage_principale joueur,
                                   MenuRecrutement menuRecrutement,
                                   ArrayList<PersonnageBase> personnagesRecruites) {
        verifierRenouvellement();

        for (QueteProgression q : quetesProgression) {
    if (!q.isCompletee()
            && q.getChapitreRequis() == chapitre
            && q.getStageRequis()    == stage
            && q.isElite()           == estElite) {
        q.setCompletee(true);
        System.out.println("\n>> Quete accomplie : " + q.getTitre() + " !");

        // ── Récompense personnage ─────────────────────────────────────
        PersonnageBase persoRecompense = recompensesPersonnages.get(q.getId());
        if (persoRecompense != null) {
            boolean dejaPresent = personnagesRecruites.stream()
                    .anyMatch(p -> p.getClass().equals(persoRecompense.getClass()));
            if (dejaPresent) {
                System.out.println(">> Vous possedez deja " + persoRecompense.getNom()
                        + " — recompense convertie en fragments !");
                
            } else {
                personnagesRecruites.add(persoRecompense);
                System.out.println(">> " + persoRecompense.getNom()
                        + " a rejoint votre equipe !");
            }
        }
    }
}

        avancerQuetesJournalieres(QueteJournaliere.TypeObjectif.TERMINER_STAGE, 1);
    }

    public void notifierFortification() {
        verifierRenouvellement();
        avancerQuetesJournalieres(QueteJournaliere.TypeObjectif.FORTIFIER, 1);
    }

    public void notifierOrGagne(int montant) {
        verifierRenouvellement();
        avancerQuetesJournalieres(QueteJournaliere.TypeObjectif.GAGNER_OR, montant);
    }

    /** Fait avancer toutes les quetes journalieres actives du type donne ; +20 pts de barre par quete qui se termine. */
    private void avancerQuetesJournalieres(QueteJournaliere.TypeObjectif type, int montant) {
        for (QueteJournaliere qj : quetesJournalieres) {
            if (qj.isReclamee() || qj.getTypeObjectif() != type) continue;

            boolean etaitCompletee = qj.isCompletee();
            qj.ajouterProgression(montant);
            if (!etaitCompletee && qj.isCompletee()) {
                pointsJournaliers = Math.min(100, pointsJournaliers + 20);
                System.out.println("\n>> Quete journaliere accomplie : " + qj.getTitre() + " !");
            }
        }
    }

    // ── Quêtes visibles ─────────────────────────────────────────────────
    // Chaque piste (chapitre normal / chapitre élite) avance indépendamment :
    // une seule quête à la fois par piste, révélée quand le stage correspondant
    // se débloque réellement en jeu. Les pistes normale et élite d'un même
    // chapitre sont donc simultanées. Une quête déjà réclamée disparaît de la
    // liste pour éviter qu'elle ne s'allonge indéfiniment.
    public ArrayList<QueteProgression> getQuetesVisibles(lancement.GameContext ctx) {
        ArrayList<QueteProgression> visibles = new ArrayList<>();

        for (QueteProgression q : quetesProgression) {
            if (q.isCompletee() && q.isReclamee()) continue;
            if (stageDebloque(ctx, q.getChapitreRequis(), q.getStageRequis(), q.isElite())) {
                visibles.add(q);
            }
        }
        return visibles;
    }

    // Reflète les vraies conditions d'accès de MenuHistoire : un chapitre/élite
    // n'est accessible qu'une fois son prérequis terminé, et le stage doit en
    // plus être débloqué à l'intérieur de ce chapitre.
    // Visibilite package (et non private) : reutilise par GestionnaireSauvegarde pour la
    // compatibilite retroactive des sauvegardes lors de l'introduction du verrou de quete.
    boolean stageDebloque(lancement.GameContext ctx, int chapitre, int stage, boolean estElite) {
        boolean accessible;
        boolean[] stagesDebloques;

        if (chapitre < 1 || chapitre > ctx.chapitres.size()) {
            accessible      = false;
            stagesDebloques = null;
        } else if (!estElite) {
            accessible      = (chapitre == 1) || ctx.chapitres.get(chapitre - 2).getStagesReussis()[10];
            stagesDebloques = ctx.chapitres.get(chapitre - 1).getStagesDebloques();
        } else {
            lancement.ChapitreElite.ChapitreElite ce = ctx.chapitresElite.get(chapitre - 1);
            accessible      = ce != null && ce.estDebloque();
            stagesDebloques = ce != null ? ce.getStagesDebloques() : null;
        }

        return accessible && stagesDebloques != null
                && stage < stagesDebloques.length && stagesDebloques[stage];
    }

    public List<QueteJournaliere> getQuetesJournalieres() { return quetesJournalieres; }
    public void setQuetesJournalieres(List<QueteJournaliere> liste) { this.quetesJournalieres = liste; }
    public ArrayList<QueteProgression> getToutesQuetesProgression() { return quetesProgression; }

    // ── Acceptation des quetes de chapitre : c'est ce qui debloque le stage associe ──
    private QueteProgression trouverQueteProgression(String id) {
        for (QueteProgression q : quetesProgression) if (q.getId().equals(id)) return q;
        return null;
    }

    private QueteProgression trouverQueteProgressionPourStage(int chapitre, int stage, boolean estElite) {
        for (QueteProgression q : quetesProgression) {
            if (q.getChapitreRequis() == chapitre && q.getStageRequis() == stage && q.isElite() == estElite) {
                return q;
            }
        }
        return null;
    }

    /** Vrai si le stage est a la fois debloque sequentiellement ET si sa quete a ete acceptee. */
    public boolean estStageJouable(int chapitre, int stage, boolean estElite) {
        QueteProgression q = trouverQueteProgressionPourStage(chapitre, stage, estElite);
        return q != null && q.isAcceptee();
    }

    /** Accepte une quete de chapitre : debloque le stage associe. Retourne un message resultat. */
    public String accepterQueteProgression(lancement.GameContext ctx, String id) {
        QueteProgression q = trouverQueteProgression(id);
        if (q == null) return "Quete introuvable.";
        if (q.isAcceptee()) return "Quete deja acceptee.";
        if (!stageDebloque(ctx, q.getChapitreRequis(), q.getStageRequis(), q.isElite())) {
            return "Cette quete n'est pas encore disponible.";
        }
        q.setAcceptee(true);
        return "Quete acceptee : " + q.getTitre() + " ! Stage " + q.getStageRequis()
                + " du Chapitre " + q.getChapitreRequis() + (q.isElite() ? " Elite" : "") + " debloque.";
    }

    /** Recompenses effectivement accordees par {@link #reclamerRecompense}, pour affichage console/GUI. */
    public record ResultatRecompense(int xp, int or, int parcheminC, boolean potionEnergie,
                                      List<Quete.RecompenseItem> items) {}

    /**
     * Accorde les recompenses d'une quete completee (XP, or, parchemins, potion journaliere,
     * items) et sauvegarde. Logique partagee entre MenuQuetes (console) et EcranQuetesController
     * (GUI), qui ne different que par la mise en forme du message renvoye au joueur.
     */
    public ResultatRecompense reclamerRecompense(lancement.GameContext ctx, Quete q) {
        q.setReclamee(true);

        int xp = q.getRecompenseXP();
        if (xp > 0)
            for (PersonnageBase p : ctx.formation.getEquipe()) p.gagnerExperience(xp);

        int or = q.getRecompenseOr();
        if (or > 0) ctx.joueur.ajouterOr(or);

        int parcheminC = q.getRecompenseParcheminC();
        if (parcheminC > 0) ctx.menuRecrutement.ajouterParcheminC(parcheminC);

        boolean potionEnergie = q instanceof QueteJournaliere;
        if (potionEnergie) ctx.inventaire.ajouterMateriau(PotionEnergie.MOYENNE.nom, 1);

        for (Quete.RecompenseItem item : q.getRecompensesItems())
            ctx.inventaire.ajouterMateriau(item.nom(), item.quantite());

        ctx.sauvegarde.sauvegarder(ctx);
        return new ResultatRecompense(xp, or, parcheminC, potionEnergie, q.getRecompensesItems());
    }

    // ── Barre de points quotidienne ────────────────────────────────────────
    public int getPointsJournaliers() { return pointsJournaliers; }

    public boolean estBarreDisponible(int index) {
        return pointsJournaliers >= PALIERS_BARRE_JOURNALIERE[index] && !barreJournaliereReclame[index];
    }

    public boolean isBarreReclamee(int index) { return barreJournaliereReclame[index]; }

    public String afficherRecompenseBarre(int index) {
        return switch (index) {
            case 0  -> "10x " + CarteOr.NIVEAU_1.nom;
            case 1  -> "10x " + CarteOr.NIVEAU_2.nom;
            case 2  -> "2x " + PotionEnergie.GRANDE.nom;
            case 3  -> "2x " + MenuExamenS.nomBoite(1);
            default -> "10x " + CarteOr.NIVEAU_3.nom + ", 2x " + PotionEnergie.MOYENNE.nom
                    + ", 5x " + BoiteGuilde.NOM + ", 1x " + CleCoffreGuilde.NOM;
        };
    }

    public String reclamerBarre(int index, Inventaire inventaire) {
        if (!estBarreDisponible(index)) return "Palier non disponible.";

        switch (index) {
            case 0  -> inventaire.ajouterCartesOr(CarteOr.NIVEAU_1, 10);
            case 1  -> inventaire.ajouterCartesOr(CarteOr.NIVEAU_2, 10);
            case 2  -> inventaire.ajouterMateriau(PotionEnergie.GRANDE.nom, 2);
            case 3  -> inventaire.ajouterMateriau(MenuExamenS.nomBoite(1), 2);
            default -> {
                inventaire.ajouterCartesOr(CarteOr.NIVEAU_3, 10);
                inventaire.ajouterMateriau(PotionEnergie.MOYENNE.nom, 2);
                inventaire.ajouterMateriau(BoiteGuilde.NOM, 5);
                inventaire.ajouterMateriau(CleCoffreGuilde.NOM, 1);
            }
        }
        barreJournaliereReclame[index] = true;
        return "Palier " + PALIERS_BARRE_JOURNALIERE[index] + " points reclame ! " + afficherRecompenseBarre(index);
    }

    // ── Getters/setters pour la sauvegarde ────────────────────────────────
    public LocalDate getDernierRenouvellement() { return dernierRenouvellement; }
    public void setDernierRenouvellement(LocalDate date) { this.dernierRenouvellement = date; }
    public int getIndexQueteJournaliere() { return indexQueteJournaliere; }
    public void setIndexQueteJournaliere(int index) { this.indexQueteJournaliere = index; }
    public void setPointsJournaliers(int p) { this.pointsJournaliers = p; }
    public boolean[] getBarreJournaliereReclame() { return barreJournaliereReclame; }
    public void setBarreJournaliereReclame(boolean[] v) {
        boolean[] resultat = new boolean[PALIERS_BARRE_JOURNALIERE.length];
        if (v != null) System.arraycopy(v, 0, resultat, 0, Math.min(v.length, resultat.length));
        this.barreJournaliereReclame = resultat;
    }
}