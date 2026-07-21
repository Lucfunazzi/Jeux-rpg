package lancement.Gestionnaires;

import lancement.Menus.MenuRecrutement;
import Joueur.Personnage_principale;
import Personnage.PersonnageBase;
import java.time.LocalDate;
import java.util.ArrayList;
import lancement.Quetes.QueteJournaliere;
import lancement.Quetes.QueteProgression;
import Personnage.FairyTail.*;
import java.util.HashMap;
import java.util.Map;

public class GestionnaireQuetes {
    

    private final ArrayList<QueteProgression> quetesProgression = new ArrayList<>();
    private QueteJournaliere queteJournaliere;
    private LocalDate dernierRenouvellement;
    private int indexQueteJournaliere = 0;
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
                QueteJournaliere.TypeObjectif.FORTIFIER, 3, 500, 2000),
        new QueteJournaliere("J2", "Accumulateur",
                "Gagnez 500 or en combat.",
                QueteJournaliere.TypeObjectif.GAGNER_OR, 500, 500, 1000),
        new QueteJournaliere("J3", "Mage assidu",
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
    initialiserRecompensesPersonnages();   // ← ajouter
    dernierRenouvellement = LocalDate.now().minusDays(1);
    verifierRenouvellement();
}

    // ── Initialisation des quêtes de progression ──────────────────────────
    private void initialiserQuetesProgression() {

        // ── Chapitre 1 normal (niv 1 → 10 | total ~6 200 XP) ────────────────
        // XP requise réelle niv 1→10 : 6 228 XP  |  distribution progressive
        quetesProgression.add(new QueteProgression("C1S1",
                "Prologue", "Terminez le stage 1 du Chapitre 1.",
                1, 1, false, 450, 500, 0));
        quetesProgression.add(new QueteProgression("C1S2",
                "Bora le charmeur", "Terminez le stage 2 du Chapitre 1.",
                1, 2, false, 500, 700, 0));
        quetesProgression.add(new QueteProgression("C1S3",
                "Chemin vers fairy tail", "Terminez le stage 3 du Chapitre 1.",
                1, 3, false, 550, 1000, 0));
        quetesProgression.add(new QueteProgression("C1S4",
                "L'arrivée de la reine des fées", "Terminez le stage 4 du Chapitre 1.",
                1, 4, false, 600, 1200, 0));
        quetesProgression.add(new QueteProgression("C1S5",
                "Premier mission pour Lucy", "Terminez le stage 5 du Chapitre 1.",
                1, 5, false, 600, 1500, 0));
        quetesProgression.add(new QueteProgression("C1S6",
                "Le duc evarlo", "Terminez le stage 6 du Chapitre 1.",
                1, 6, false, 700, 1800, 0));
        quetesProgression.add(new QueteProgression("C1S7",
                "Retour a fairy tail", "Terminez le stage 7 du Chapitre 1.",
                1, 7, false, 700, 2000, 0));
        quetesProgression.add(new QueteProgression("C1S8",
                "Eisen Wald", "Terminez le stage 8 du Chapitre 1.",
                1, 8, false, 750, 2200, 0));
        quetesProgression.add(new QueteProgression("C1S9",
                "Eligor le mage de vent", "Terminez le stage 9 du Chapitre 1.",
                1, 9, false, 750, 2500, 0));
        quetesProgression.add(new QueteProgression("C1S10",
                "La flute maudite", "Terminez le stage 10 du Chapitre 1.",
                1, 10, false, 600, 3000, 100));

        // ── Chapitre 1 Elite (niv 10 → 20 | total ~40 000 XP) ───────────────
        // XP requise réelle niv 10→20 : 40 024 XP  |  distribution progressive
        quetesProgression.add(new QueteProgression("C1E1",
                "Prologue Elite", "Terminez le stage 1 du Chapitre 1 Elite.",
                1, 1, true, 2800, 3000, 0));
        quetesProgression.add(new QueteProgression("C1E2",
                "Bora le charmeur Elite", "Terminez le stage 2 du Chapitre 1 Elite.",
                1, 2, true, 3200, 3500, 0));
        quetesProgression.add(new QueteProgression("C1E3",
                "Chemin vers Fairy Tail Elite", "Terminez le stage 3 du Chapitre 1 Elite.",
                1, 3, true, 3600, 4000, 0));
        quetesProgression.add(new QueteProgression("C1E4",
                "L'arrivée de la reine des fées Elite", "Terminez le stage 4 du Chapitre 1 Elite.",//vrai combat avec erza cette fois-ci
                1, 4, true, 4000, 4500, 0));
        quetesProgression.add(new QueteProgression("C1E5",
                "Premier mission pour Lucy Elite", "Terminez le stage 5 du Chapitre 1 Elite.",
                1, 5, true, 4000, 5000, 0));
        quetesProgression.add(new QueteProgression("C1E6",
                "Le duc evarlo Elite", "Terminez le stage 6 du Chapitre 1 Elite.",
                1, 6, true, 4400, 5500, 0));
        quetesProgression.add(new QueteProgression("C1E7",
                "Retour a fairy tail Elite", "Terminez le stage 7 du Chapitre 1 Elite.",
                1, 7, true, 4400, 6000, 0));
        quetesProgression.add(new QueteProgression("C1E8",
                "Eisen Wald Elite", "Terminez le stage 8 du Chapitre 1 Elite.",
                1, 8, true, 4800, 6500, 0));
        quetesProgression.add(new QueteProgression("C1E9",
                "Eligor le mage de vent Elite", "Terminez le stage 9 du Chapitre 1 Elite.",
                1, 9, true, 4800, 7000, 0));
        quetesProgression.add(new QueteProgression("C1E10",
                "La flute maudite Elite", "Terminez le stage 10 du Chapitre 1 Elite.", // vrai combat avec natsu gray et erza
                1, 10, true, 4000, 9000, 0));

        // ── Chapitre 2 normal (niv 20 → 25 | total ~71 000 XP) ──────────────
        // XP requise réelle niv 20→25 : 71 010 XP  |  distribution progressive
        quetesProgression.add(new QueteProgression("C2S1",
                "Prologue Chapitre 2", "Terminez le stage 1 du Chapitre 2.",
                2, 1, false, 4950, 1500, 0));
        quetesProgression.add(new QueteProgression("C2S2",
                "Arrivée a l'ile de galuna", "Terminez le stage 2 du Chapitre 2.",
                2, 2, false, 5700, 2000, 0));
        quetesProgression.add(new QueteProgression("C2S3",
                "Lucy VS Cherry", "Terminez le stage 3 du Chapitre 2.",
                2, 3, false, 6400, 2500, 0));
        quetesProgression.add(new QueteProgression("C2S4",
                "Yuka contre Natsu", "Terminez le stage 4 du Chapitre 2.",
                2, 4, false, 7100, 3000, 0));
        quetesProgression.add(new QueteProgression("C2S5",
                "Tobi contre Natsu", "Terminez le stage 5 du Chapitre 2.",
                2, 5, false, 7100, 3500, 0));
        quetesProgression.add(new QueteProgression("C2S6",
                "Gray vs Leon 1", "Terminez le stage 6 du Chapitre 2.",
                2, 6, false, 7800, 4000, 0));
        quetesProgression.add(new QueteProgression("C2S7",
                "Natsu contre l'homme mysterieux", "Terminez le stage 7 du Chapitre 2.",
                2, 7, false, 7800, 4500, 0));
        quetesProgression.add(new QueteProgression("C2S8",
                "Gray vs Leon part 2", "Terminez le stage 8 du Chapitre 2.",
                2, 8, false, 8500, 5000, 0));
        quetesProgression.add(new QueteProgression("C2S9",
                "Le passé de Gray", "Terminez le stage 9 du Chapitre 2.",
                2, 9, false, 8500, 5500, 0));
        quetesProgression.add(new QueteProgression("C2S10",
                "Deliora le demon ", "Terminez le stage 10 du Chapitre 2.",
                2, 10, false, 7100, 6000, 150));

        // ── Chapitre 2 Elite (niv 25 → 30 | total ~176 650 XP) ──────────────
        // XP requise réelle niv 25→30 : 176 680 XP  |  distribution progressive
        quetesProgression.add(new QueteProgression("C2E1",
                "Prologue Chapitre 2 Elite", "Terminez le stage 1 du Chapitre 2 Elite.",
                2, 1, true, 12350, 3000, 0));
        quetesProgression.add(new QueteProgression("C2E2",
                "Arrivée a l'ile de galuna Elite", "Terminez le stage 2 du Chapitre 2 Elite.",
                2, 2, true, 14150, 3500, 0));
        quetesProgression.add(new QueteProgression("C2E3",
                "Lucy VS Cherry Elite", "Terminez le stage 3 du Chapitre 2 Elite.",
                2, 3, true, 15900, 4000, 0));
        quetesProgression.add(new QueteProgression("C2E4",
                "Yuka contre Natsu Elite", "Terminez le stage 4 du Chapitre 2 Elite.",
                2, 4, true, 17650, 4500, 0));
        quetesProgression.add(new QueteProgression("C2E5",
                "Tobi contre Natsu Elite", "Terminez le stage 5 du Chapitre 2 Elite.",
                2, 5, true, 17650, 5000, 0));
        quetesProgression.add(new QueteProgression("C2E6",
                "Gray vs Leon 1 Elite", "Terminez le stage 6 du Chapitre 2 Elite.",
                2, 6, true, 19450, 5500, 0));
        quetesProgression.add(new QueteProgression("C2E7",
                "Natsu contre l'homme mysterieux Elite", "Terminez le stage 7 du Chapitre 2 Elite.",
                2, 7, true, 19450, 6000, 0));
        quetesProgression.add(new QueteProgression("C2E8",
                "Gray vs Leon part 2 Elite", "Terminez le stage 8 du Chapitre 2 Elite.",
                2, 8, true, 21200, 6500, 0));
        quetesProgression.add(new QueteProgression("C2E9",
                "Le passé de Gray Elite", "Terminez le stage 9 du Chapitre 2 Elite.",
                2, 9, true, 21200, 7000, 0));
        quetesProgression.add(new QueteProgression("C2E10",
                "Deliora le demon Elite", "Terminez le stage 10 du Chapitre 2 Elite.",
                2, 10, true, 17650, 10000, 200));

        // ── Chapitre 3 normal (niv 30 → 35 | total ~439 500 XP) ─────────────
        // XP requise réelle niv 30→35 : 439 623 XP  |  distribution progressive
        quetesProgression.add(new QueteProgression("C3S1",
                "Le tournoi commence", "Terminez le stage 1 du Chapitre 3.",
                3, 1, false, 30750, 2000, 0));
        quetesProgression.add(new QueteProgression("C3S2",
                "L'equipe de Chiaotzu", "Terminez le stage 2 du Chapitre 3.",
                3, 2, false, 35150, 2500, 0));
        quetesProgression.add(new QueteProgression("C3S3",
                "Les Saiyans arrivent", "Terminez le stage 3 du Chapitre 3.",
                3, 3, false, 39550, 3000, 0));
        quetesProgression.add(new QueteProgression("C3S4",
                "La tyrannie de Freezer", "Terminez le stage 4 du Chapitre 3.",
                3, 4, false, 43950, 3500, 0));
        quetesProgression.add(new QueteProgression("C3S5",
                "Les guerriers de la Terre", "Terminez le stage 5 du Chapitre 3.",
                3, 5, false, 43950, 4000, 0));
        quetesProgression.add(new QueteProgression("C3S6",
                "La menace Android", "Terminez le stage 6 du Chapitre 3.",
                3, 6, false, 48350, 4500, 0));
        quetesProgression.add(new QueteProgression("C3S7",
                "L'orgueil Saiyan", "Terminez le stage 7 du Chapitre 3.",
                3, 7, false, 48350, 5000, 0));
        quetesProgression.add(new QueteProgression("C3S8",
                "Le demon et l'enfant prodige", "Terminez le stage 8 du Chapitre 3.",
                3, 8, false, 52750, 5500, 0));
        quetesProgression.add(new QueteProgression("C3S9",
                "L'armee inarretable", "Terminez le stage 9 du Chapitre 3.",
                3, 9, false, 52750, 6000, 0));
        quetesProgression.add(new QueteProgression("C3S10",
                "Cell vaincu", "Terminez le stage 10 du Chapitre 3.",
                3, 10, false, 43950, 8000, 200));

        quetesProgression.add(new QueteProgression("C3E1",
                "Qualifications d'Elite", "Terminez le stage 1 du Chapitre 3 Elite.",
                3, 1, true, 320000, 4000, 0));
        quetesProgression.add(new QueteProgression("C3E2",
                "La Garde de Chiaotzu", "Terminez le stage 2 du Chapitre 3 Elite.",
                3, 2, true, 325000, 4500, 0));
        quetesProgression.add(new QueteProgression("C3E3",
                "Saiyans d'Elite", "Terminez le stage 3 du Chapitre 3 Elite.",
                3, 3, true, 328000, 5000, 0));
        quetesProgression.add(new QueteProgression("C3E4",
                "Freezer Transcende", "Terminez le stage 4 du Chapitre 3 Elite.",
                3, 4, true, 330000, 5500, 0));
        quetesProgression.add(new QueteProgression("C3E5",
                "Les Guerriers de Fairy Tail", "Terminez le stage 5 du Chapitre 3 Elite.",
                3, 5, true, 330000, 6000, 0));
        quetesProgression.add(new QueteProgression("C3E6",
                "Les Cyborgs Reprogrammes", "Terminez le stage 6 du Chapitre 3 Elite.",
                3, 6, true, 333000, 6500, 0));
        quetesProgression.add(new QueteProgression("C3E7",
                "L'Armee Saiyan", "Terminez le stage 7 du Chapitre 3 Elite.",
                3, 7, true, 333000, 7000, 0));
        quetesProgression.add(new QueteProgression("C3E8",
                "Piccolo et ses Disciples", "Terminez le stage 8 du Chapitre 3 Elite.",
                3, 8, true, 335000, 7500, 0));
        quetesProgression.add(new QueteProgression("C3E9",
                "L'Armee Inarretable", "Terminez le stage 9 du Chapitre 3 Elite.",
                3, 9, true, 335000, 8000, 0));
        quetesProgression.add(new QueteProgression("C3E10",
                "Cell Parfait Vaincu", "Terminez le stage 10 du Chapitre 3 Elite.",
                3, 10, true, 340000, 12000, 0));
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