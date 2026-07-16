package lancement.Gestionnaires;

import lancement.Menus.MenuRecrutement;
import Joueur.Personnage_principale;
import Personnage.PersonnageBase;
import java.time.LocalDate;
import java.util.ArrayList;
import lancement.Quetes.QueteJournaliere;
import lancement.Quetes.QueteProgression;

public class GestionnaireQuetes {

    private final ArrayList<QueteProgression> quetesProgression = new ArrayList<>();
    private QueteJournaliere queteJournaliere;
    private LocalDate dernierRenouvellement;
    private int indexQueteJournaliere = 0;

    // Pool de quêtes journalières
    // XP journalière calibrée pour être un petit bonus, pas une source principale
    private static final QueteJournaliere[] POOL_JOURNALIER = {
        new QueteJournaliere("J1", "Forgeron du jour",
                "Fortifiez votre equipement 3 fois.",
                QueteJournaliere.TypeObjectif.FORTIFIER, 3, 500, 2000),
        new QueteJournaliere("J2", "Accumulateur",
                "Gagnez 500 or en combat.",
                QueteJournaliere.TypeObjectif.GAGNER_OR, 500, 500, 1000),
        new QueteJournaliere("J3", "Guerrier assidu",
                "Terminez 3 stages.",
                QueteJournaliere.TypeObjectif.TERMINER_STAGE, 3, 800, 1500),
        new QueteJournaliere("J4", "Grande forge",
                "Fortifiez votre equipement 5 fois.",
                QueteJournaliere.TypeObjectif.FORTIFIER, 5, 800, 2500),
        new QueteJournaliere("J5", "Chasseur de richesses",
                "Gagnez 1000 or en combat.",
                QueteJournaliere.TypeObjectif.GAGNER_OR, 1000, 1000, 2000),
    };

    public GestionnaireQuetes() {
        initialiserQuetesProgression();
        dernierRenouvellement = LocalDate.now().minusDays(1); // force init
        verifierRenouvellement();
    }

    // ── Initialisation des quêtes de progression ──────────────────────────
    private void initialiserQuetesProgression() {

        // ── Chapitre 1 normal (niv 1 → 10 | total ~6 250 XP) ─────────────
        // XP progressive : 100 / 250 / 350 / 450 / 550 / 700 / 800 / 900 / 1000 / 1150
        quetesProgression.add(new QueteProgression("C1S1",
                "Les premiers pas", "Terminez le stage 1 du Chapitre 1.",
                1, 1, false, 100, 500, 0));
        quetesProgression.add(new QueteProgression("C1S2",
                "Sur la bonne voie", "Terminez le stage 2 du Chapitre 1.",
                1, 2, false, 250, 700, 0));
        quetesProgression.add(new QueteProgression("C1S3",
                "Le chemin s'ouvre", "Terminez le stage 3 du Chapitre 1.",
                1, 3, false, 350, 1000, 0));
        quetesProgression.add(new QueteProgression("C1S4",
                "Milieu du chemin", "Terminez le stage 4 du Chapitre 1.",
                1, 4, false, 450, 1200, 0));
        quetesProgression.add(new QueteProgression("C1S5",
                "A mi-parcours", "Terminez le stage 5 du Chapitre 1.",
                1, 5, false, 550, 1500, 0));
        quetesProgression.add(new QueteProgression("C1S6",
                "Pas en arriere", "Terminez le stage 6 du Chapitre 1.",
                1, 6, false, 700, 1800, 0));
        quetesProgression.add(new QueteProgression("C1S7",
                "La ligne droite", "Terminez le stage 7 du Chapitre 1.",
                1, 7, false, 800, 2000, 0));
        quetesProgression.add(new QueteProgression("C1S8",
                "Presque au bout", "Terminez le stage 8 du Chapitre 1.",
                1, 8, false, 900, 2200, 0));
        quetesProgression.add(new QueteProgression("C1S9",
                "Dernier obstacle", "Terminez le stage 9 du Chapitre 1.",
                1, 9, false, 1000, 2500, 0));
        quetesProgression.add(new QueteProgression("C1S10",
                "Conquerant du Chapitre 1", "Terminez le stage 10 du Chapitre 1.",
                1, 10, false, 1150, 3000, 100));

        // ── Chapitre 2 normal (niv 10 → 20 | total ~32 100 XP) ───────────
        // XP progressive : 600 / 1150 / 1750 / 2350 / 2900 / 3500 / 4100 / 4650 / 5250 / 5850
        quetesProgression.add(new QueteProgression("C2S1",
                "L'Examen commence", "Terminez le stage 1 du Chapitre 2.",
                2, 1, false, 600, 1500, 0));
        quetesProgression.add(new QueteProgression("C2S2",
                "Face a l'Equipe 8", "Terminez le stage 2 du Chapitre 2.",
                2, 2, false, 1150, 2000, 0));
        quetesProgression.add(new QueteProgression("C2S3",
                "L'Ombre et la Strategie", "Terminez le stage 3 du Chapitre 2.",
                2, 3, false, 1750, 2500, 0));
        quetesProgression.add(new QueteProgression("C2S4",
                "L'Equipe 7 se dresse", "Terminez le stage 4 du Chapitre 2.",
                2, 4, false, 2350, 3000, 0));
        quetesProgression.add(new QueteProgression("C2S5",
                "Le Brouillard de Zabuza", "Terminez le stage 5 du Chapitre 2.",
                2, 5, false, 2900, 3500, 0));
        quetesProgression.add(new QueteProgression("C2S6",
                "Sables de Suna", "Terminez le stage 6 du Chapitre 2.",
                2, 6, false, 3500, 4000, 0));
        quetesProgression.add(new QueteProgression("C2S7",
                "La Revanche de Gai", "Terminez le stage 7 du Chapitre 2.",
                2, 7, false, 4100, 4500, 0));
        quetesProgression.add(new QueteProgression("C2S8",
                "Le Feu d'Asuma", "Terminez le stage 8 du Chapitre 2.",
                2, 8, false, 4650, 5000, 0));
        quetesProgression.add(new QueteProgression("C2S9",
                "L'Oeil de Kurenai", "Terminez le stage 9 du Chapitre 2.",
                2, 9, false, 5250, 5500, 0));
        quetesProgression.add(new QueteProgression("C2S10",
                "Orochimaru vaincu", "Terminez le stage 10 du Chapitre 2.",
                2, 10, false, 5850, 6000, 150));

        // ── Chapitre 1 Elite (niv 20 → 25 | total ~59 250 XP) ────────────
        // XP progressive : 1100 / 2150 / 3250 / 4300 / 5400 / 6450 / 7550 / 8600 / 9700 / 10750
        quetesProgression.add(new QueteProgression("C1E1",
                "L'eveil de l'elite", "Terminez le stage 1 du Chapitre 1 Elite.",
                1, 1, true, 200, 3000, 0));
        quetesProgression.add(new QueteProgression("C1E2",
                "Elite confirmee", "Terminez le stage 2 du Chapitre 1 Elite.",
                1, 2, true, 400, 3500, 0));
        quetesProgression.add(new QueteProgression("C1E3",
                "Rang superieur", "Terminez le stage 3 du Chapitre 1 Elite.",
                1, 3, true, 600, 4000, 0));
        quetesProgression.add(new QueteProgression("C1E4",
                "Force brute", "Terminez le stage 4 du Chapitre 1 Elite.",
                1, 4, true, 800, 4500, 0));
        quetesProgression.add(new QueteProgression("C1E5",
                "Demi-chemin elite", "Terminez le stage 5 du Chapitre 1 Elite.",
                1, 5, true, 900, 5000, 0));
        quetesProgression.add(new QueteProgression("C1E6",
                "Survivant de l'ombre", "Terminez le stage 6 du Chapitre 1 Elite.",
                1, 6, true, 1000, 5500, 0));
        quetesProgression.add(new QueteProgression("C1E7",
                "Predateur redoutable", "Terminez le stage 7 du Chapitre 1 Elite.",
                1, 7, true, 900, 6000, 0));
        quetesProgression.add(new QueteProgression("C1E8",
                "Ombre parmi les ombres", "Terminez le stage 8 du Chapitre 1 Elite.",
                1, 8, true, 1100, 6500, 0));
        quetesProgression.add(new QueteProgression("C1E9",
                "General des tenebres", "Terminez le stage 9 du Chapitre 1 Elite.",
                1, 9, true, 1100, 7000, 0));
        quetesProgression.add(new QueteProgression("C1E10",
                "Dragon vaincu", "Terminez le stage 10 du Chapitre 1 Elite.",
                1, 10, true, 1000, 9000, 0));

        // ── Chapitre 2 Elite (niv 20 → 25 | total ~63 200 XP) ────────────
        // XP progressive : 1200 / 2300 / 3500 / 4600 / 5700 / 6900 / 8000 / 9200 / 10300 / 11500
        quetesProgression.add(new QueteProgression("C2E1",
                "L'elite de l'Examen", "Terminez le stage 1 du Chapitre 2 Elite.",
                2, 1, true, 400, 3000, 0));
        quetesProgression.add(new QueteProgression("C2E2",
                "Equipe 8 redoutable", "Terminez le stage 2 du Chapitre 2 Elite.",
                2, 2, true, 700, 3500, 0));
        quetesProgression.add(new QueteProgression("C2E3",
                "L'Ombre elite", "Terminez le stage 3 du Chapitre 2 Elite.",
                2, 3, true, 900, 4000, 0));
        quetesProgression.add(new QueteProgression("C2E4",
                "Equipe 7 au sommet", "Terminez le stage 4 du Chapitre 2 Elite.",
                2, 4, true, 1100, 4500, 0));
        quetesProgression.add(new QueteProgression("C2E5",
                "Brouillard d'acier", "Terminez le stage 5 du Chapitre 2 Elite.",
                2, 5, true, 1300, 5000, 0));
        quetesProgression.add(new QueteProgression("C2E6",
                "Sables implacables", "Terminez le stage 6 du Chapitre 2 Elite.",
                2, 6, true, 1500, 5500, 0));
        quetesProgression.add(new QueteProgression("C2E7",
                "La Fureur de Gai", "Terminez le stage 7 du Chapitre 2 Elite.",
                2, 7, true, 1600, 6000, 0));
        quetesProgression.add(new QueteProgression("C2E8",
                "Le Feu ne s'eteint pas", "Terminez le stage 8 du Chapitre 2 Elite.",
                2, 8, true, 1700, 6500, 0));
        quetesProgression.add(new QueteProgression("C2E9",
                "L'Oeil acere de Kurenai", "Terminez le stage 9 du Chapitre 2 Elite.",
                2, 9, true, 1400, 7000, 0));
        quetesProgression.add(new QueteProgression("C2E10",
                "Orochimaru — Forme Finale", "Terminez le stage 10 du Chapitre 2 Elite.",
                2, 10, true, 1400, 10000, 200));

        // ── Chapitre 3 normal (niv 25 → 35 | total ~82 500 XP) ───────────
        quetesProgression.add(new QueteProgression("C3S1",
                "Le tournoi commence", "Terminez le stage 1 du Chapitre 3.",
                3, 1, false, 1500, 2000, 0));
        quetesProgression.add(new QueteProgression("C3S2",
                "L'equipe de Chiaotzu", "Terminez le stage 2 du Chapitre 3.",
                3, 2, false, 3000, 2500, 0));
        quetesProgression.add(new QueteProgression("C3S3",
                "Les Saiyans arrivent", "Terminez le stage 3 du Chapitre 3.",
                3, 3, false, 4500, 3000, 0));
        quetesProgression.add(new QueteProgression("C3S4",
                "La tyrannie de Freezer", "Terminez le stage 4 du Chapitre 3.",
                3, 4, false, 6000, 3500, 0));
        quetesProgression.add(new QueteProgression("C3S5",
                "Les guerriers de la Terre", "Terminez le stage 5 du Chapitre 3.",
                3, 5, false, 7500, 4000, 0));
        quetesProgression.add(new QueteProgression("C3S6",
                "La menace Android", "Terminez le stage 6 du Chapitre 3.",
                3, 6, false, 9000, 4500, 0));
        quetesProgression.add(new QueteProgression("C3S7",
                "L'orgueil Saiyan", "Terminez le stage 7 du Chapitre 3.",
                3, 7, false, 10500, 5000, 0));
        quetesProgression.add(new QueteProgression("C3S8",
                "Le demon et l'enfant prodige", "Terminez le stage 8 du Chapitre 3.",
                3, 8, false, 12000, 5500, 0));
        quetesProgression.add(new QueteProgression("C3S9",
                "L'armee inarretable", "Terminez le stage 9 du Chapitre 3.",
                3, 9, false, 13500, 6000, 0));
        quetesProgression.add(new QueteProgression("C3S10",
                "Cell vaincu", "Terminez le stage 10 du Chapitre 3.",
                3, 10, false, 15000, 8000, 200));
    }

    // ── Renouvellement automatique à minuit ───────────────────────────────
    public void verifierRenouvellement() {
        LocalDate aujourdhui = LocalDate.now();
        if (queteJournaliere == null || !aujourdhui.equals(dernierRenouvellement)) {
            QueteJournaliere modele = POOL_JOURNALIER[indexQueteJournaliere % POOL_JOURNALIER.length];
            queteJournaliere = new QueteJournaliere(
                modele.getId(), modele.getTitre(), modele.getDescription(),
                modele.getTypeObjectif(), modele.getObjectifCible(),
                modele.getRecompenseXP(), modele.getRecompenseOr()
            );
            dernierRenouvellement = aujourdhui;
            indexQueteJournaliere++;
        }
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
            }
        }

        if (!queteJournaliere.isReclamee()
                && queteJournaliere.getTypeObjectif()
                   == QueteJournaliere.TypeObjectif.TERMINER_STAGE) {
            queteJournaliere.ajouterProgression(1);
            if (queteJournaliere.isCompletee()) {
                System.out.println("\n>> Quete journaliere accomplie : "
                        + queteJournaliere.getTitre() + " !");
            }
        }
    }

    public void notifierFortification() {
        verifierRenouvellement();
        if (!queteJournaliere.isReclamee()
                && queteJournaliere.getTypeObjectif()
                   == QueteJournaliere.TypeObjectif.FORTIFIER) {
            queteJournaliere.ajouterProgression(1);
            if (queteJournaliere.isCompletee()) {
                System.out.println("\n>> Quete journaliere accomplie : "
                        + queteJournaliere.getTitre() + " !");
            }
        }
    }

    public void notifierOrGagne(int montant) {
        verifierRenouvellement();
        if (!queteJournaliere.isReclamee()
                && queteJournaliere.getTypeObjectif()
                   == QueteJournaliere.TypeObjectif.GAGNER_OR) {
            queteJournaliere.ajouterProgression(montant);
            if (queteJournaliere.isCompletee()) {
                System.out.println("\n>> Quete journaliere accomplie : "
                        + queteJournaliere.getTitre() + " !");
            }
        }
    }

    // ── Quêtes visibles (dévoilées progressivement dans chaque groupe) ────
    public ArrayList<QueteProgression> getQuetesVisibles() {
        ArrayList<QueteProgression> visibles = new ArrayList<>();

        for (int i = 0; i < quetesProgression.size(); i++) {
            QueteProgression q = quetesProgression.get(i);
            boolean visible;
            if (i == 0) {
                visible = true;
            } else {
                QueteProgression precedente = quetesProgression.get(i - 1);
                boolean memeGroupe = precedente.getChapitreRequis() == q.getChapitreRequis()
                        && precedente.isElite() == q.isElite();
                if (memeGroupe) {
                    visible = precedente.isCompletee() || precedente.isReclamee();
                } else {
                    visible = true; // première d'un nouveau groupe toujours visible
                }
            }
            if (visible) visibles.add(q);
        }
        return visibles;
    }

    public QueteJournaliere getQueteJournaliere() { return queteJournaliere; }
    public ArrayList<QueteProgression> getToutesQuetesProgression() { return quetesProgression; }

    // ── Getters/setters pour la sauvegarde ────────────────────────────────
    public LocalDate getDernierRenouvellement() { return dernierRenouvellement; }
    public void setDernierRenouvellement(LocalDate date) { this.dernierRenouvellement = date; }
    public int getIndexQueteJournaliere() { return indexQueteJournaliere; }
    public void setIndexQueteJournaliere(int index) { this.indexQueteJournaliere = index; }
    public void setQueteJournaliere(QueteJournaliere q) { this.queteJournaliere = q; }
}