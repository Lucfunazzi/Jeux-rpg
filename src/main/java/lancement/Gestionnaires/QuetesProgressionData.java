package lancement.Gestionnaires;

import java.util.ArrayList;
import java.util.List;
import lancement.Chapitres.CourbeChapitres;
import lancement.Quetes.QueteProgression;

/** Definition en dur de toutes les quetes de progression (une par stage, normal + elite, tous chapitres). */
public class QuetesProgressionData {

    public static List<QueteProgression> creer() {
        List<QueteProgression> liste = new ArrayList<>();

        // ── Chapitre 1 normal (niv 1 → 10 | total 6 300 XP) ─────────────────
        liste.add(new QueteProgression("C1S1",
                "Prologue", "Terminez le stage 1 du Chapitre 1.",
                1, 1, false, CourbeChapitres.xpQuete(1, 1), 500, 0));
        liste.add(new QueteProgression("C1S2",
                "Bora le charmeur", "Terminez le stage 2 du Chapitre 1.",
                1, 2, false, CourbeChapitres.xpQuete(1, 2), 700, 0));
        liste.add(new QueteProgression("C1S3",
                "Chemin vers fairy tail", "Terminez le stage 3 du Chapitre 1.",
                1, 3, false, CourbeChapitres.xpQuete(1, 3), 1000, 0));
        liste.add(new QueteProgression("C1S4",
                "L'arrivée de la reine des fées", "Terminez le stage 4 du Chapitre 1.",
                1, 4, false, CourbeChapitres.xpQuete(1, 4), 1200, 0));
        liste.add(new QueteProgression("C1S5",
                "Premier mission pour Lucy", "Terminez le stage 5 du Chapitre 1.",
                1, 5, false, CourbeChapitres.xpQuete(1, 5), 1500, 0));
        liste.add(new QueteProgression("C1S6",
                "Le duc evarlo", "Terminez le stage 6 du Chapitre 1.",
                1, 6, false, CourbeChapitres.xpQuete(1, 6), 1800, 0));
        liste.add(new QueteProgression("C1S7",
                "Retour a fairy tail", "Terminez le stage 7 du Chapitre 1.",
                1, 7, false, CourbeChapitres.xpQuete(1, 7), 2000, 0));
        liste.add(new QueteProgression("C1S8",
                "Eisen Wald", "Terminez le stage 8 du Chapitre 1.",
                1, 8, false, CourbeChapitres.xpQuete(1, 8), 2200, 0));
        liste.add(new QueteProgression("C1S9",
                "Eligor le mage de vent", "Terminez le stage 9 du Chapitre 1.",
                1, 9, false, CourbeChapitres.xpQuete(1, 9), 2500, 0));
        liste.add(new QueteProgression("C1S10",
                "La flute maudite", "Terminez le stage 10 du Chapitre 1.",
                1, 10, false, CourbeChapitres.xpQuete(1, 10), 3000, 100));

        // ── Chapitre 1 Elite (XP desactivee — recompenses Or/materiaux inchangees) ──
        liste.add(new QueteProgression("C1E1",
                "Prologue Elite", "Terminez le stage 1 du Chapitre 1 Elite.",
                1, 1, true, 0, 3000, 0));
        liste.add(new QueteProgression("C1E2",
                "Bora le charmeur Elite", "Terminez le stage 2 du Chapitre 1 Elite.",
                1, 2, true, 0, 3500, 0));
        liste.add(new QueteProgression("C1E3",
                "Chemin vers Fairy Tail Elite", "Terminez le stage 3 du Chapitre 1 Elite.",
                1, 3, true, 0, 4000, 0));
        liste.add(new QueteProgression("C1E4",
                "L'arrivée de la reine des fées Elite", "Terminez le stage 4 du Chapitre 1 Elite.",//vrai combat avec erza cette fois-ci
                1, 4, true, 0, 4500, 0));
        liste.add(new QueteProgression("C1E5",
                "Premier mission pour Lucy Elite", "Terminez le stage 5 du Chapitre 1 Elite.",
                1, 5, true, 0, 5000, 0));
        liste.add(new QueteProgression("C1E6",
                "Le duc evarlo Elite", "Terminez le stage 6 du Chapitre 1 Elite.",
                1, 6, true, 0, 5500, 0));
        liste.add(new QueteProgression("C1E7",
                "Retour a fairy tail Elite", "Terminez le stage 7 du Chapitre 1 Elite.",
                1, 7, true, 0, 6000, 0));
        liste.add(new QueteProgression("C1E8",
                "Eisen Wald Elite", "Terminez le stage 8 du Chapitre 1 Elite.",
                1, 8, true, 0, 6500, 0));
        liste.add(new QueteProgression("C1E9",
                "Eligor le mage de vent Elite", "Terminez le stage 9 du Chapitre 1 Elite.",
                1, 9, true, 0, 7000, 0));
        liste.add(new QueteProgression("C1E10",
                "La flute maudite Elite", "Terminez le stage 10 du Chapitre 1 Elite.", // vrai combat avec natsu gray et erza
                1, 10, true, 0, 9000, 0));

        // ── Chapitre 2 normal (niv 10 → 15 | total 7 000 XP) ────────────────
        liste.add(new QueteProgression("C2S1",
                "Prologue Chapitre 2", "Terminez le stage 1 du Chapitre 2.",
                2, 1, false, CourbeChapitres.xpQuete(2, 1), 1500, 0));
        liste.add(new QueteProgression("C2S2",
                "Arrivée a l'ile de galuna", "Terminez le stage 2 du Chapitre 2.",
                2, 2, false, CourbeChapitres.xpQuete(2, 2), 2000, 0));
        liste.add(new QueteProgression("C2S3",
                "Lucy VS Cherry", "Terminez le stage 3 du Chapitre 2.",
                2, 3, false, CourbeChapitres.xpQuete(2, 3), 2500, 0));
        liste.add(new QueteProgression("C2S4",
                "Yuka contre Natsu", "Terminez le stage 4 du Chapitre 2.",
                2, 4, false, CourbeChapitres.xpQuete(2, 4), 3000, 0));
        liste.add(new QueteProgression("C2S5",
                "Tobi contre Natsu", "Terminez le stage 5 du Chapitre 2.",
                2, 5, false, CourbeChapitres.xpQuete(2, 5), 3500, 0));
        liste.add(new QueteProgression("C2S6",
                "Gray vs Leon 1", "Terminez le stage 6 du Chapitre 2.",
                2, 6, false, CourbeChapitres.xpQuete(2, 6), 4000, 0));
        liste.add(new QueteProgression("C2S7",
                "Natsu contre l'homme mysterieux", "Terminez le stage 7 du Chapitre 2.",
                2, 7, false, CourbeChapitres.xpQuete(2, 7), 4500, 0));
        liste.add(new QueteProgression("C2S8",
                "Gray vs Leon part 2", "Terminez le stage 8 du Chapitre 2.",
                2, 8, false, CourbeChapitres.xpQuete(2, 8), 5000, 0));
        liste.add(new QueteProgression("C2S9",
                "Le passé de Gray", "Terminez le stage 9 du Chapitre 2.",
                2, 9, false, CourbeChapitres.xpQuete(2, 9), 5500, 0));
        liste.add(new QueteProgression("C2S10",
                "Deliora le demon ", "Terminez le stage 10 du Chapitre 2.",
                2, 10, false, CourbeChapitres.xpQuete(2, 10), 6000, 150));

        // ── Chapitre 2 Elite (XP desactivee — recompenses Or/materiaux inchangees) ──
        liste.add(new QueteProgression("C2E1",
                "Prologue Chapitre 2 Elite", "Terminez le stage 1 du Chapitre 2 Elite.",
                2, 1, true, 0, 3000, 0));
        liste.add(new QueteProgression("C2E2",
                "Arrivée a l'ile de galuna Elite", "Terminez le stage 2 du Chapitre 2 Elite.",
                2, 2, true, 0, 3500, 0));
        liste.add(new QueteProgression("C2E3",
                "Lucy VS Cherry Elite", "Terminez le stage 3 du Chapitre 2 Elite.",
                2, 3, true, 0, 4000, 0));
        liste.add(new QueteProgression("C2E4",
                "Yuka contre Natsu Elite", "Terminez le stage 4 du Chapitre 2 Elite.",
                2, 4, true, 0, 4500, 0));
        liste.add(new QueteProgression("C2E5",
                "Tobi contre Natsu Elite", "Terminez le stage 5 du Chapitre 2 Elite.",
                2, 5, true, 0, 5000, 0));
        liste.add(new QueteProgression("C2E6",
                "Gray vs Leon 1 Elite", "Terminez le stage 6 du Chapitre 2 Elite.",
                2, 6, true, 0, 5500, 0));
        liste.add(new QueteProgression("C2E7",
                "Natsu contre l'homme mysterieux Elite", "Terminez le stage 7 du Chapitre 2 Elite.",
                2, 7, true, 0, 6000, 0));
        liste.add(new QueteProgression("C2E8",
                "Gray vs Leon part 2 Elite", "Terminez le stage 8 du Chapitre 2 Elite.",
                2, 8, true, 0, 6500, 0));
        liste.add(new QueteProgression("C2E9",
                "Le passé de Gray Elite", "Terminez le stage 9 du Chapitre 2 Elite.",
                2, 9, true, 0, 7000, 0));
        liste.add(new QueteProgression("C2E10",
                "Deliora le demon Elite", "Terminez le stage 10 du Chapitre 2 Elite.",
                2, 10, true, 0, 10000, 0));

        // ── Chapitre 3 normal (niv 15 → 22 | total 14 000 XP) ───────────────
        liste.add(new QueteProgression("C3S1",
                "L'assaut de Phantom Lord", "Terminez le stage 1 du Chapitre 3.",
                3, 1, false, CourbeChapitres.xpQuete(3, 1), 2000, 0));
        liste.add(new QueteProgression("C3S2",
                "Totomaru — Sept Flammes", "Terminez le stage 2 du Chapitre 3.",
                3, 2, false, CourbeChapitres.xpQuete(3, 2), 2500, 0));
        liste.add(new QueteProgression("C3S3",
                "Sol — L'Impenetrable", "Terminez le stage 3 du Chapitre 3.",
                3, 3, false, CourbeChapitres.xpQuete(3, 3), 3000, 0));
        liste.add(new QueteProgression("C3S4",
                "L'Element 4 se deploie", "Terminez le stage 4 du Chapitre 3.",
                3, 4, false, CourbeChapitres.xpQuete(3, 4), 3500, 0));
        liste.add(new QueteProgression("C3S5",
                "Jubia — L'Eau qui emprisonne", "Terminez le stage 5 du Chapitre 3.",
                3, 5, false, CourbeChapitres.xpQuete(3, 5), 4000, 0));
        liste.add(new QueteProgression("C3S6",
                "L'Element 4 au complet", "Terminez le stage 6 du Chapitre 3.",
                3, 6, false, CourbeChapitres.xpQuete(3, 6), 4500, 0));
        liste.add(new QueteProgression("C3S7",
                "Aria — Magie du Ciel Vide", "Terminez le stage 7 du Chapitre 3.",
                3, 7, false, CourbeChapitres.xpQuete(3, 7), 5000, 0));
        liste.add(new QueteProgression("C3S8",
                "L'Element 4 — Derniere resistance", "Terminez le stage 8 du Chapitre 3.",
                3, 8, false, CourbeChapitres.xpQuete(3, 8), 5500, 0));
        liste.add(new QueteProgression("C3S9",
                "Jose — L'Ombre s'eveille", "Terminez le stage 9 du Chapitre 3.",
                3, 9, false, CourbeChapitres.xpQuete(3, 9), 6000, 0));
        liste.add(new QueteProgression("C3S10",
                "Jose Porla — Maitre de Phantom Lord", "Terminez le stage 10 du Chapitre 3.",
                3, 10, false, CourbeChapitres.xpQuete(3, 10), 8000, 200));

        // ── Chapitre 3 Elite (XP desactivee — recompenses Or/materiaux inchangees) ──
        liste.add(new QueteProgression("C3E1",
                "L'assaut de Phantom Lord Renforce", "Terminez le stage 1 du Chapitre 3 Elite.",
                3, 1, true, 0, 4000, 0));
        liste.add(new QueteProgression("C3E2",
                "Totomaru — Sept Flammes d'Elite", "Terminez le stage 2 du Chapitre 3 Elite.",
                3, 2, true, 0, 4500, 0));
        liste.add(new QueteProgression("C3E3",
                "Sol — L'Impenetrable d'Elite", "Terminez le stage 3 du Chapitre 3 Elite.",
                3, 3, true, 0, 5000, 0));
        liste.add(new QueteProgression("C3E4",
                "L'Element 4 Renforce", "Terminez le stage 4 du Chapitre 3 Elite.",
                3, 4, true, 0, 5500, 0));
        liste.add(new QueteProgression("C3E5",
                "Jubia — L'Eau qui Brise d'Elite", "Terminez le stage 5 du Chapitre 3 Elite.",
                3, 5, true, 0, 6000, 0));
        liste.add(new QueteProgression("C3E6",
                "L'Element 4 Complet d'Elite", "Terminez le stage 6 du Chapitre 3 Elite.",
                3, 6, true, 0, 6500, 0));
        liste.add(new QueteProgression("C3E7",
                "Aria — Magie du Ciel Vide Transcendee", "Terminez le stage 7 du Chapitre 3 Elite.",
                3, 7, true, 0, 7000, 0));
        liste.add(new QueteProgression("C3E8",
                "L'Element 4 — Ultime Resistance", "Terminez le stage 8 du Chapitre 3 Elite.",
                3, 8, true, 0, 7500, 0));
        liste.add(new QueteProgression("C3E9",
                "Jose — L'Ombre Transcendee", "Terminez le stage 9 du Chapitre 3 Elite.",
                3, 9, true, 0, 8000, 0));
        liste.add(new QueteProgression("C3E10",
                "Jose Porla — Forme Spectrale Supreme", "Terminez le stage 10 du Chapitre 3 Elite.",
                3, 10, true, 0, 12000, 0));

        // ── Chapitre 4 normal (niv 24 → 30 | total 17 100 XP) ───────────────
        liste.add(new QueteProgression("C4S1",
                "Embuscade dans le casino", "Terminez le stage 1 du Chapitre 4.",
                4, 1, false, CourbeChapitres.xpQuete(4, 1), 6500, 0));
        liste.add(new QueteProgression("C4S2",
                "Infiltration dans la tour du paradis", "Terminez le stage 2 du Chapitre 4.",
                4, 2, false, CourbeChapitres.xpQuete(4, 2), 7000, 0));
        liste.add(new QueteProgression("C4S3",
                "Miaou, Il faut sauver Happy", "Terminez le stage 3 du Chapitre 4.",
                4, 3, false, CourbeChapitres.xpQuete(4, 3), 7500, 0));
        liste.add(new QueteProgression("C4S4",
                "Libération d'erza", "Terminez le stage 4 du Chapitre 4.",
                4, 4, false, CourbeChapitres.xpQuete(4, 4), 8000, 0));
        liste.add(new QueteProgression("C4S5",
                "Les esprits et l'eau", "Terminez le stage 5 du Chapitre 4.",
                4, 5, false, CourbeChapitres.xpQuete(4, 5), 8500, 0));
        liste.add(new QueteProgression("C4S6",
                "Le hiboux assasin", "Terminez le stage 6 du Chapitre 4.",
                4, 6, false, CourbeChapitres.xpQuete(4, 6), 9000, 0));
        liste.add(new QueteProgression("C4S7",
                "Epée contre Epée", "Terminez le stage 7 du Chapitre 4.",
                4, 7, false, CourbeChapitres.xpQuete(4, 7), 9500, 0));
        liste.add(new QueteProgression("C4S8",
                "Erza contre Jellal — Le Passe Ressurgit", "Terminez le stage 8 du Chapitre 4.",
                4, 8, false, CourbeChapitres.xpQuete(4, 8), 10000, 0));
        liste.add(new QueteProgression("C4S9",
                "Simon Revient — L'Assaut sur Jellal", "Terminez le stage 9 du Chapitre 4.",
                4, 9, false, CourbeChapitres.xpQuete(4, 9), 10500, 0));
        liste.add(new QueteProgression("C4S10",
                "Jellal — L'Effondrement de la Tour du Paradis", "Terminez le stage 10 du Chapitre 4.",
                4, 10, false, CourbeChapitres.xpQuete(4, 10), 14000, 250));

        // ── Chapitre 5 normal (niv 30 → 35 | total 17 000 XP) ───────────────
        liste.add(new QueteProgression("C5S1",
                "Stage 1 — [Titre a definir]", "Terminez le stage 1 du Chapitre 5.",
                5, 1, false, CourbeChapitres.xpQuete(5, 1), 12500, 0));
        liste.add(new QueteProgression("C5S2",
                "Stage 2 — [Titre a definir]", "Terminez le stage 2 du Chapitre 5.",
                5, 2, false, CourbeChapitres.xpQuete(5, 2), 13000, 0));
        liste.add(new QueteProgression("C5S3",
                "Stage 3 — [Titre a definir]", "Terminez le stage 3 du Chapitre 5.",
                5, 3, false, CourbeChapitres.xpQuete(5, 3), 13500, 0));
        liste.add(new QueteProgression("C5S4",
                "Stage 4 — [Titre a definir]", "Terminez le stage 4 du Chapitre 5.",
                5, 4, false, CourbeChapitres.xpQuete(5, 4), 14000, 0));
        liste.add(new QueteProgression("C5S5",
                "Stage 5 — [Titre a definir]", "Terminez le stage 5 du Chapitre 5.",
                5, 5, false, CourbeChapitres.xpQuete(5, 5), 14500, 0));
        liste.add(new QueteProgression("C5S6",
                "Stage 6 — [Titre a definir]", "Terminez le stage 6 du Chapitre 5.",
                5, 6, false, CourbeChapitres.xpQuete(5, 6), 15000, 0));
        liste.add(new QueteProgression("C5S7",
                "Stage 7 — [Titre a definir]", "Terminez le stage 7 du Chapitre 5.",
                5, 7, false, CourbeChapitres.xpQuete(5, 7), 15500, 0));
        liste.add(new QueteProgression("C5S8",
                "Stage 8 — [Titre a definir]", "Terminez le stage 8 du Chapitre 5.",
                5, 8, false, CourbeChapitres.xpQuete(5, 8), 16000, 0));
        liste.add(new QueteProgression("C5S9",
                "Stage 9 — [Titre a definir]", "Terminez le stage 9 du Chapitre 5.",
                5, 9, false, CourbeChapitres.xpQuete(5, 9), 16500, 0));
        liste.add(new QueteProgression("C5S10",
                "Stage 10 — [Titre a definir]", "Terminez le stage 10 du Chapitre 5.",
                5, 10, false, CourbeChapitres.xpQuete(5, 10), 20000, 300));

        // ── Chapitre 6 normal (niv 35 → 40 | total 19 500 XP) ───────────────
        liste.add(new QueteProgression("C6S1",
                "Stage 1 — [Titre a definir]", "Terminez le stage 1 du Chapitre 6.",
                6, 1, false, CourbeChapitres.xpQuete(6, 1), 18500, 0));
        liste.add(new QueteProgression("C6S2",
                "Stage 2 — [Titre a definir]", "Terminez le stage 2 du Chapitre 6.",
                6, 2, false, CourbeChapitres.xpQuete(6, 2), 19000, 0));
        liste.add(new QueteProgression("C6S3",
                "Stage 3 — [Titre a definir]", "Terminez le stage 3 du Chapitre 6.",
                6, 3, false, CourbeChapitres.xpQuete(6, 3), 19500, 0));
        liste.add(new QueteProgression("C6S4",
                "Stage 4 — [Titre a definir]", "Terminez le stage 4 du Chapitre 6.",
                6, 4, false, CourbeChapitres.xpQuete(6, 4), 20000, 0));
        liste.add(new QueteProgression("C6S5",
                "Stage 5 — [Titre a definir]", "Terminez le stage 5 du Chapitre 6.",
                6, 5, false, CourbeChapitres.xpQuete(6, 5), 20500, 0));
        liste.add(new QueteProgression("C6S6",
                "Stage 6 — [Titre a definir]", "Terminez le stage 6 du Chapitre 6.",
                6, 6, false, CourbeChapitres.xpQuete(6, 6), 21000, 0));
        liste.add(new QueteProgression("C6S7",
                "Stage 7 — [Titre a definir]", "Terminez le stage 7 du Chapitre 6.",
                6, 7, false, CourbeChapitres.xpQuete(6, 7), 21500, 0));
        liste.add(new QueteProgression("C6S8",
                "Stage 8 — [Titre a definir]", "Terminez le stage 8 du Chapitre 6.",
                6, 8, false, CourbeChapitres.xpQuete(6, 8), 22000, 0));
        liste.add(new QueteProgression("C6S9",
                "Stage 9 — [Titre a definir]", "Terminez le stage 9 du Chapitre 6.",
                6, 9, false, CourbeChapitres.xpQuete(6, 9), 22500, 0));
        liste.add(new QueteProgression("C6S10",
                "Stage 10 — [Titre a definir]", "Terminez le stage 10 du Chapitre 6.",
                6, 10, false, CourbeChapitres.xpQuete(6, 10), 26000, 350));

        // ── Chapitre 7 normal (niv 40 → 45 | total 22 000 XP) ───────────────
        liste.add(new QueteProgression("C7S1",
                "Stage 1 — [Titre a definir]", "Terminez le stage 1 du Chapitre 7.",
                7, 1, false, CourbeChapitres.xpQuete(7, 1), 24500, 0));
        liste.add(new QueteProgression("C7S2",
                "Stage 2 — [Titre a definir]", "Terminez le stage 2 du Chapitre 7.",
                7, 2, false, CourbeChapitres.xpQuete(7, 2), 25000, 0));
        liste.add(new QueteProgression("C7S3",
                "Stage 3 — [Titre a definir]", "Terminez le stage 3 du Chapitre 7.",
                7, 3, false, CourbeChapitres.xpQuete(7, 3), 25500, 0));
        liste.add(new QueteProgression("C7S4",
                "Stage 4 — [Titre a definir]", "Terminez le stage 4 du Chapitre 7.",
                7, 4, false, CourbeChapitres.xpQuete(7, 4), 26000, 0));
        liste.add(new QueteProgression("C7S5",
                "Stage 5 — [Titre a definir]", "Terminez le stage 5 du Chapitre 7.",
                7, 5, false, CourbeChapitres.xpQuete(7, 5), 26500, 0));
        liste.add(new QueteProgression("C7S6",
                "Stage 6 — [Titre a definir]", "Terminez le stage 6 du Chapitre 7.",
                7, 6, false, CourbeChapitres.xpQuete(7, 6), 27000, 0));
        liste.add(new QueteProgression("C7S7",
                "Stage 7 — [Titre a definir]", "Terminez le stage 7 du Chapitre 7.",
                7, 7, false, CourbeChapitres.xpQuete(7, 7), 27500, 0));
        liste.add(new QueteProgression("C7S8",
                "Stage 8 — [Titre a definir]", "Terminez le stage 8 du Chapitre 7.",
                7, 8, false, CourbeChapitres.xpQuete(7, 8), 28000, 0));
        liste.add(new QueteProgression("C7S9",
                "Stage 9 — [Titre a definir]", "Terminez le stage 9 du Chapitre 7.",
                7, 9, false, CourbeChapitres.xpQuete(7, 9), 28500, 0));
        liste.add(new QueteProgression("C7S10",
                "Stage 10 — [Titre a definir]", "Terminez le stage 10 du Chapitre 7.",
                7, 10, false, CourbeChapitres.xpQuete(7, 10), 32000, 400));

        // ── Chapitre 8 normal (niv 45 → 48 | total 14 400 XP) ───────────────
        liste.add(new QueteProgression("C8S1",
                "Stage 1 — [Titre a definir]", "Terminez le stage 1 du Chapitre 8.",
                8, 1, false, CourbeChapitres.xpQuete(8, 1), 30500, 0));
        liste.add(new QueteProgression("C8S2",
                "Stage 2 — [Titre a definir]", "Terminez le stage 2 du Chapitre 8.",
                8, 2, false, CourbeChapitres.xpQuete(8, 2), 31000, 0));
        liste.add(new QueteProgression("C8S3",
                "Stage 3 — [Titre a definir]", "Terminez le stage 3 du Chapitre 8.",
                8, 3, false, CourbeChapitres.xpQuete(8, 3), 31500, 0));
        liste.add(new QueteProgression("C8S4",
                "Stage 4 — [Titre a definir]", "Terminez le stage 4 du Chapitre 8.",
                8, 4, false, CourbeChapitres.xpQuete(8, 4), 32000, 0));
        liste.add(new QueteProgression("C8S5",
                "Stage 5 — [Titre a definir]", "Terminez le stage 5 du Chapitre 8.",
                8, 5, false, CourbeChapitres.xpQuete(8, 5), 32500, 0));
        liste.add(new QueteProgression("C8S6",
                "Stage 6 — [Titre a definir]", "Terminez le stage 6 du Chapitre 8.",
                8, 6, false, CourbeChapitres.xpQuete(8, 6), 33000, 0));
        liste.add(new QueteProgression("C8S7",
                "Stage 7 — [Titre a definir]", "Terminez le stage 7 du Chapitre 8.",
                8, 7, false, CourbeChapitres.xpQuete(8, 7), 33500, 0));
        liste.add(new QueteProgression("C8S8",
                "Stage 8 — [Titre a definir]", "Terminez le stage 8 du Chapitre 8.",
                8, 8, false, CourbeChapitres.xpQuete(8, 8), 34000, 0));
        liste.add(new QueteProgression("C8S9",
                "Stage 9 — [Titre a definir]", "Terminez le stage 9 du Chapitre 8.",
                8, 9, false, CourbeChapitres.xpQuete(8, 9), 34500, 0));
        liste.add(new QueteProgression("C8S10",
                "Stage 10 — [Titre a definir]", "Terminez le stage 10 du Chapitre 8.",
                8, 10, false, CourbeChapitres.xpQuete(8, 10), 38000, 450));

        // ── Chapitre 9 normal (niv 48 → 51 | total 15 300 XP) ───────────────
        liste.add(new QueteProgression("C9S1",
                "Stage 1 — [Titre a definir]", "Terminez le stage 1 du Chapitre 9.",
                9, 1, false, CourbeChapitres.xpQuete(9, 1), 36500, 0));
        liste.add(new QueteProgression("C9S2",
                "Stage 2 — [Titre a definir]", "Terminez le stage 2 du Chapitre 9.",
                9, 2, false, CourbeChapitres.xpQuete(9, 2), 37000, 0));
        liste.add(new QueteProgression("C9S3",
                "Stage 3 — [Titre a definir]", "Terminez le stage 3 du Chapitre 9.",
                9, 3, false, CourbeChapitres.xpQuete(9, 3), 37500, 0));
        liste.add(new QueteProgression("C9S4",
                "Stage 4 — [Titre a definir]", "Terminez le stage 4 du Chapitre 9.",
                9, 4, false, CourbeChapitres.xpQuete(9, 4), 38000, 0));
        liste.add(new QueteProgression("C9S5",
                "Stage 5 — [Titre a definir]", "Terminez le stage 5 du Chapitre 9.",
                9, 5, false, CourbeChapitres.xpQuete(9, 5), 38500, 0));
        liste.add(new QueteProgression("C9S6",
                "Stage 6 — [Titre a definir]", "Terminez le stage 6 du Chapitre 9.",
                9, 6, false, CourbeChapitres.xpQuete(9, 6), 39000, 0));
        liste.add(new QueteProgression("C9S7",
                "Stage 7 — [Titre a definir]", "Terminez le stage 7 du Chapitre 9.",
                9, 7, false, CourbeChapitres.xpQuete(9, 7), 39500, 0));
        liste.add(new QueteProgression("C9S8",
                "Stage 8 — [Titre a definir]", "Terminez le stage 8 du Chapitre 9.",
                9, 8, false, CourbeChapitres.xpQuete(9, 8), 40000, 0));
        liste.add(new QueteProgression("C9S9",
                "Stage 9 — [Titre a definir]", "Terminez le stage 9 du Chapitre 9.",
                9, 9, false, CourbeChapitres.xpQuete(9, 9), 40500, 0));
        liste.add(new QueteProgression("C9S10",
                "Stage 10 — [Titre a definir]", "Terminez le stage 10 du Chapitre 9.",
                9, 10, false, CourbeChapitres.xpQuete(9, 10), 44000, 500));

        // ── Chapitre 10 normal (niv 52 → 54 | total 10 900 XP) ──────────────
        liste.add(new QueteProgression("C10S1",
                "Stage 1 — [Titre a definir]", "Terminez le stage 1 du Chapitre 10.",
                10, 1, false, CourbeChapitres.xpQuete(10, 1), 42500, 0));
        liste.add(new QueteProgression("C10S2",
                "Stage 2 — [Titre a definir]", "Terminez le stage 2 du Chapitre 10.",
                10, 2, false, CourbeChapitres.xpQuete(10, 2), 43000, 0));
        liste.add(new QueteProgression("C10S3",
                "Stage 3 — [Titre a definir]", "Terminez le stage 3 du Chapitre 10.",
                10, 3, false, CourbeChapitres.xpQuete(10, 3), 43500, 0));
        liste.add(new QueteProgression("C10S4",
                "Stage 4 — [Titre a definir]", "Terminez le stage 4 du Chapitre 10.",
                10, 4, false, CourbeChapitres.xpQuete(10, 4), 44000, 0));
        liste.add(new QueteProgression("C10S5",
                "Stage 5 — [Titre a definir]", "Terminez le stage 5 du Chapitre 10.",
                10, 5, false, CourbeChapitres.xpQuete(10, 5), 44500, 0));
        liste.add(new QueteProgression("C10S6",
                "Stage 6 — [Titre a definir]", "Terminez le stage 6 du Chapitre 10.",
                10, 6, false, CourbeChapitres.xpQuete(10, 6), 45000, 0));
        liste.add(new QueteProgression("C10S7",
                "Stage 7 — [Titre a definir]", "Terminez le stage 7 du Chapitre 10.",
                10, 7, false, CourbeChapitres.xpQuete(10, 7), 45500, 0));
        liste.add(new QueteProgression("C10S8",
                "Stage 8 — [Titre a definir]", "Terminez le stage 8 du Chapitre 10.",
                10, 8, false, CourbeChapitres.xpQuete(10, 8), 46000, 0));
        liste.add(new QueteProgression("C10S9",
                "Stage 9 — [Titre a definir]", "Terminez le stage 9 du Chapitre 10.",
                10, 9, false, CourbeChapitres.xpQuete(10, 9), 46500, 0));
        liste.add(new QueteProgression("C10S10",
                "Stage 10 — [Titre a definir]", "Terminez le stage 10 du Chapitre 10.",
                10, 10, false, CourbeChapitres.xpQuete(10, 10), 50000, 550));

        // ── Chapitre 11 normal (niv 54 → 57 | total 17 100 XP | Or/materiaux TODO) ──
        liste.add(new QueteProgression("C11S1",
                "Stage 1 — [Titre a definir]", "Terminez le stage 1 du Chapitre 11.",
                11, 1, false, CourbeChapitres.xpQuete(11, 1), 0, 0));
        liste.add(new QueteProgression("C11S2",
                "Stage 2 — [Titre a definir]", "Terminez le stage 2 du Chapitre 11.",
                11, 2, false, CourbeChapitres.xpQuete(11, 2), 0, 0));
        liste.add(new QueteProgression("C11S3",
                "Stage 3 — [Titre a definir]", "Terminez le stage 3 du Chapitre 11.",
                11, 3, false, CourbeChapitres.xpQuete(11, 3), 0, 0));
        liste.add(new QueteProgression("C11S4",
                "Stage 4 — [Titre a definir]", "Terminez le stage 4 du Chapitre 11.",
                11, 4, false, CourbeChapitres.xpQuete(11, 4), 0, 0));
        liste.add(new QueteProgression("C11S5",
                "Stage 5 — [Titre a definir]", "Terminez le stage 5 du Chapitre 11.",
                11, 5, false, CourbeChapitres.xpQuete(11, 5), 0, 0));
        liste.add(new QueteProgression("C11S6",
                "Stage 6 — [Titre a definir]", "Terminez le stage 6 du Chapitre 11.",
                11, 6, false, CourbeChapitres.xpQuete(11, 6), 0, 0));
        liste.add(new QueteProgression("C11S7",
                "Stage 7 — [Titre a definir]", "Terminez le stage 7 du Chapitre 11.",
                11, 7, false, CourbeChapitres.xpQuete(11, 7), 0, 0));
        liste.add(new QueteProgression("C11S8",
                "Stage 8 — [Titre a definir]", "Terminez le stage 8 du Chapitre 11.",
                11, 8, false, CourbeChapitres.xpQuete(11, 8), 0, 0));
        liste.add(new QueteProgression("C11S9",
                "Stage 9 — [Titre a definir]", "Terminez le stage 9 du Chapitre 11.",
                11, 9, false, CourbeChapitres.xpQuete(11, 9), 0, 0));
        liste.add(new QueteProgression("C11S10",
                "Stage 10 — [Titre a definir]", "Terminez le stage 10 du Chapitre 11.",
                11, 10, false, CourbeChapitres.xpQuete(11, 10), 0, 0));

        // ── Chapitre 12 normal (niv 57 → 59 | total 11 900 XP | Or/materiaux TODO) ──
        liste.add(new QueteProgression("C12S1",
                "Stage 1 — [Titre a definir]", "Terminez le stage 1 du Chapitre 12.",
                12, 1, false, CourbeChapitres.xpQuete(12, 1), 0, 0));
        liste.add(new QueteProgression("C12S2",
                "Stage 2 — [Titre a definir]", "Terminez le stage 2 du Chapitre 12.",
                12, 2, false, CourbeChapitres.xpQuete(12, 2), 0, 0));
        liste.add(new QueteProgression("C12S3",
                "Stage 3 — [Titre a definir]", "Terminez le stage 3 du Chapitre 12.",
                12, 3, false, CourbeChapitres.xpQuete(12, 3), 0, 0));
        liste.add(new QueteProgression("C12S4",
                "Stage 4 — [Titre a definir]", "Terminez le stage 4 du Chapitre 12.",
                12, 4, false, CourbeChapitres.xpQuete(12, 4), 0, 0));
        liste.add(new QueteProgression("C12S5",
                "Stage 5 — [Titre a definir]", "Terminez le stage 5 du Chapitre 12.",
                12, 5, false, CourbeChapitres.xpQuete(12, 5), 0, 0));
        liste.add(new QueteProgression("C12S6",
                "Stage 6 — [Titre a definir]", "Terminez le stage 6 du Chapitre 12.",
                12, 6, false, CourbeChapitres.xpQuete(12, 6), 0, 0));
        liste.add(new QueteProgression("C12S7",
                "Stage 7 — [Titre a definir]", "Terminez le stage 7 du Chapitre 12.",
                12, 7, false, CourbeChapitres.xpQuete(12, 7), 0, 0));
        liste.add(new QueteProgression("C12S8",
                "Stage 8 — [Titre a definir]", "Terminez le stage 8 du Chapitre 12.",
                12, 8, false, CourbeChapitres.xpQuete(12, 8), 0, 0));
        liste.add(new QueteProgression("C12S9",
                "Stage 9 — [Titre a definir]", "Terminez le stage 9 du Chapitre 12.",
                12, 9, false, CourbeChapitres.xpQuete(12, 9), 0, 0));
        liste.add(new QueteProgression("C12S10",
                "Stage 10 — [Titre a definir]", "Terminez le stage 10 du Chapitre 12.",
                12, 10, false, CourbeChapitres.xpQuete(12, 10), 0, 0));

        // ── Chapitre 13 normal (niv 61 → 63 | total 40 000 XP | palier endgame) ──
        liste.add(new QueteProgression("C13S1",
                "Stage 1 — [Titre a definir]", "Terminez le stage 1 du Chapitre 13.",
                13, 1, false, CourbeChapitres.xpQuete(13, 1), 0, 0));
        liste.add(new QueteProgression("C13S2",
                "Stage 2 — [Titre a definir]", "Terminez le stage 2 du Chapitre 13.",
                13, 2, false, CourbeChapitres.xpQuete(13, 2), 0, 0));
        liste.add(new QueteProgression("C13S3",
                "Stage 3 — [Titre a definir]", "Terminez le stage 3 du Chapitre 13.",
                13, 3, false, CourbeChapitres.xpQuete(13, 3), 0, 0));
        liste.add(new QueteProgression("C13S4",
                "Stage 4 — [Titre a definir]", "Terminez le stage 4 du Chapitre 13.",
                13, 4, false, CourbeChapitres.xpQuete(13, 4), 0, 0));
        liste.add(new QueteProgression("C13S5",
                "Stage 5 — [Titre a definir]", "Terminez le stage 5 du Chapitre 13.",
                13, 5, false, CourbeChapitres.xpQuete(13, 5), 0, 0));
        liste.add(new QueteProgression("C13S6",
                "Stage 6 — [Titre a definir]", "Terminez le stage 6 du Chapitre 13.",
                13, 6, false, CourbeChapitres.xpQuete(13, 6), 0, 0));
        liste.add(new QueteProgression("C13S7",
                "Stage 7 — [Titre a definir]", "Terminez le stage 7 du Chapitre 13.",
                13, 7, false, CourbeChapitres.xpQuete(13, 7), 0, 0));
        liste.add(new QueteProgression("C13S8",
                "Stage 8 — [Titre a definir]", "Terminez le stage 8 du Chapitre 13.",
                13, 8, false, CourbeChapitres.xpQuete(13, 8), 0, 0));
        liste.add(new QueteProgression("C13S9",
                "Stage 9 — [Titre a definir]", "Terminez le stage 9 du Chapitre 13.",
                13, 9, false, CourbeChapitres.xpQuete(13, 9), 0, 0));
        liste.add(new QueteProgression("C13S10",
                "Stage 10 — [Titre a definir]", "Terminez le stage 10 du Chapitre 13.",
                13, 10, false, CourbeChapitres.xpQuete(13, 10), 0, 0));

        // ── Chapitre 4 Elite (XP desactivee — recompenses Or/materiaux inchangees) ──
        liste.add(new QueteProgression("C4E1",
                "[ELITE] Stage 1 — [Titre a definir]", "Terminez le stage 1 du Chapitre 4 Elite.",
                4, 1, true, 0, 9500, 0));
        liste.add(new QueteProgression("C4E2",
                "[ELITE] Stage 2 — [Titre a definir]", "Terminez le stage 2 du Chapitre 4 Elite.",
                4, 2, true, 0, 10000, 0));
        liste.add(new QueteProgression("C4E3",
                "[ELITE] Stage 3 — [Titre a definir]", "Terminez le stage 3 du Chapitre 4 Elite.",
                4, 3, true, 0, 10500, 0));
        liste.add(new QueteProgression("C4E4",
                "[ELITE] Stage 4 — [Titre a definir]", "Terminez le stage 4 du Chapitre 4 Elite.",
                4, 4, true, 0, 11000, 0));
        liste.add(new QueteProgression("C4E5",
                "[ELITE] Stage 5 — [Titre a definir]", "Terminez le stage 5 du Chapitre 4 Elite.",
                4, 5, true, 0, 11500, 0));
        liste.add(new QueteProgression("C4E6",
                "[ELITE] Stage 6 — [Titre a definir]", "Terminez le stage 6 du Chapitre 4 Elite.",
                4, 6, true, 0, 12000, 0));
        liste.add(new QueteProgression("C4E7",
                "[ELITE] Stage 7 — [Titre a definir]", "Terminez le stage 7 du Chapitre 4 Elite.",
                4, 7, true, 0, 12500, 0));
        liste.add(new QueteProgression("C4E8",
                "[ELITE] Stage 8 — [Titre a definir]", "Terminez le stage 8 du Chapitre 4 Elite.",
                4, 8, true, 0, 13000, 0));
        liste.add(new QueteProgression("C4E9",
                "[ELITE] Stage 9 — [Titre a definir]", "Terminez le stage 9 du Chapitre 4 Elite.",
                4, 9, true, 0, 13500, 0));
        liste.add(new QueteProgression("C4E10",
                "[ELITE] Stage 10 — [Titre a definir]", "Terminez le stage 10 du Chapitre 4 Elite.",
                4, 10, true, 0, 17000, 0));

        // ── Chapitre 5 Elite (XP desactivee — recompenses Or/materiaux inchangees) ──
        liste.add(new QueteProgression("C5E1",
                "[ELITE] Stage 1 — [Titre a definir]", "Terminez le stage 1 du Chapitre 5 Elite.",
                5, 1, true, 0, 15500, 0));
        liste.add(new QueteProgression("C5E2",
                "[ELITE] Stage 2 — [Titre a definir]", "Terminez le stage 2 du Chapitre 5 Elite.",
                5, 2, true, 0, 16000, 0));
        liste.add(new QueteProgression("C5E3",
                "[ELITE] Stage 3 — [Titre a definir]", "Terminez le stage 3 du Chapitre 5 Elite.",
                5, 3, true, 0, 16500, 0));
        liste.add(new QueteProgression("C5E4",
                "[ELITE] Stage 4 — [Titre a definir]", "Terminez le stage 4 du Chapitre 5 Elite.",
                5, 4, true, 0, 17000, 0));
        liste.add(new QueteProgression("C5E5",
                "[ELITE] Stage 5 — [Titre a definir]", "Terminez le stage 5 du Chapitre 5 Elite.",
                5, 5, true, 0, 17500, 0));
        liste.add(new QueteProgression("C5E6",
                "[ELITE] Stage 6 — [Titre a definir]", "Terminez le stage 6 du Chapitre 5 Elite.",
                5, 6, true, 0, 18000, 0));
        liste.add(new QueteProgression("C5E7",
                "[ELITE] Stage 7 — [Titre a definir]", "Terminez le stage 7 du Chapitre 5 Elite.",
                5, 7, true, 0, 18500, 0));
        liste.add(new QueteProgression("C5E8",
                "[ELITE] Stage 8 — [Titre a definir]", "Terminez le stage 8 du Chapitre 5 Elite.",
                5, 8, true, 0, 19000, 0));
        liste.add(new QueteProgression("C5E9",
                "[ELITE] Stage 9 — [Titre a definir]", "Terminez le stage 9 du Chapitre 5 Elite.",
                5, 9, true, 0, 19500, 0));
        liste.add(new QueteProgression("C5E10",
                "[ELITE] Stage 10 — [Titre a definir]", "Terminez le stage 10 du Chapitre 5 Elite.",
                5, 10, true, 0, 23000, 0));

        // ── Chapitre 6 Elite (XP desactivee — recompenses Or/materiaux inchangees) ──
        liste.add(new QueteProgression("C6E1",
                "[ELITE] Stage 1 — [Titre a definir]", "Terminez le stage 1 du Chapitre 6 Elite.",
                6, 1, true, 0, 21500, 0));
        liste.add(new QueteProgression("C6E2",
                "[ELITE] Stage 2 — [Titre a definir]", "Terminez le stage 2 du Chapitre 6 Elite.",
                6, 2, true, 0, 22000, 0));
        liste.add(new QueteProgression("C6E3",
                "[ELITE] Stage 3 — [Titre a definir]", "Terminez le stage 3 du Chapitre 6 Elite.",
                6, 3, true, 0, 22500, 0));
        liste.add(new QueteProgression("C6E4",
                "[ELITE] Stage 4 — [Titre a definir]", "Terminez le stage 4 du Chapitre 6 Elite.",
                6, 4, true, 0, 23000, 0));
        liste.add(new QueteProgression("C6E5",
                "[ELITE] Stage 5 — [Titre a definir]", "Terminez le stage 5 du Chapitre 6 Elite.",
                6, 5, true, 0, 23500, 0));
        liste.add(new QueteProgression("C6E6",
                "[ELITE] Stage 6 — [Titre a definir]", "Terminez le stage 6 du Chapitre 6 Elite.",
                6, 6, true, 0, 24000, 0));
        liste.add(new QueteProgression("C6E7",
                "[ELITE] Stage 7 — [Titre a definir]", "Terminez le stage 7 du Chapitre 6 Elite.",
                6, 7, true, 0, 24500, 0));
        liste.add(new QueteProgression("C6E8",
                "[ELITE] Stage 8 — [Titre a definir]", "Terminez le stage 8 du Chapitre 6 Elite.",
                6, 8, true, 0, 25000, 0));
        liste.add(new QueteProgression("C6E9",
                "[ELITE] Stage 9 — [Titre a definir]", "Terminez le stage 9 du Chapitre 6 Elite.",
                6, 9, true, 0, 25500, 0));
        liste.add(new QueteProgression("C6E10",
                "[ELITE] Stage 10 — [Titre a definir]", "Terminez le stage 10 du Chapitre 6 Elite.",
                6, 10, true, 0, 29000, 0));

        // ── Chapitre 7 Elite (XP desactivee — recompenses Or/materiaux inchangees) ──
        liste.add(new QueteProgression("C7E1",
                "[ELITE] Stage 1 — [Titre a definir]", "Terminez le stage 1 du Chapitre 7 Elite.",
                7, 1, true, 0, 27500, 0));
        liste.add(new QueteProgression("C7E2",
                "[ELITE] Stage 2 — [Titre a definir]", "Terminez le stage 2 du Chapitre 7 Elite.",
                7, 2, true, 0, 28000, 0));
        liste.add(new QueteProgression("C7E3",
                "[ELITE] Stage 3 — [Titre a definir]", "Terminez le stage 3 du Chapitre 7 Elite.",
                7, 3, true, 0, 28500, 0));
        liste.add(new QueteProgression("C7E4",
                "[ELITE] Stage 4 — [Titre a definir]", "Terminez le stage 4 du Chapitre 7 Elite.",
                7, 4, true, 0, 29000, 0));
        liste.add(new QueteProgression("C7E5",
                "[ELITE] Stage 5 — [Titre a definir]", "Terminez le stage 5 du Chapitre 7 Elite.",
                7, 5, true, 0, 29500, 0));
        liste.add(new QueteProgression("C7E6",
                "[ELITE] Stage 6 — [Titre a definir]", "Terminez le stage 6 du Chapitre 7 Elite.",
                7, 6, true, 0, 30000, 0));
        liste.add(new QueteProgression("C7E7",
                "[ELITE] Stage 7 — [Titre a definir]", "Terminez le stage 7 du Chapitre 7 Elite.",
                7, 7, true, 0, 30500, 0));
        liste.add(new QueteProgression("C7E8",
                "[ELITE] Stage 8 — [Titre a definir]", "Terminez le stage 8 du Chapitre 7 Elite.",
                7, 8, true, 0, 31000, 0));
        liste.add(new QueteProgression("C7E9",
                "[ELITE] Stage 9 — [Titre a definir]", "Terminez le stage 9 du Chapitre 7 Elite.",
                7, 9, true, 0, 31500, 0));
        liste.add(new QueteProgression("C7E10",
                "[ELITE] Stage 10 — [Titre a definir]", "Terminez le stage 10 du Chapitre 7 Elite.",
                7, 10, true, 0, 35000, 0));

        // ── Chapitre 8 Elite (XP desactivee — recompenses Or/materiaux inchangees) ──
        liste.add(new QueteProgression("C8E1",
                "[ELITE] Stage 1 — [Titre a definir]", "Terminez le stage 1 du Chapitre 8 Elite.",
                8, 1, true, 0, 33500, 0));
        liste.add(new QueteProgression("C8E2",
                "[ELITE] Stage 2 — [Titre a definir]", "Terminez le stage 2 du Chapitre 8 Elite.",
                8, 2, true, 0, 34000, 0));
        liste.add(new QueteProgression("C8E3",
                "[ELITE] Stage 3 — [Titre a definir]", "Terminez le stage 3 du Chapitre 8 Elite.",
                8, 3, true, 0, 34500, 0));
        liste.add(new QueteProgression("C8E4",
                "[ELITE] Stage 4 — [Titre a definir]", "Terminez le stage 4 du Chapitre 8 Elite.",
                8, 4, true, 0, 35000, 0));
        liste.add(new QueteProgression("C8E5",
                "[ELITE] Stage 5 — [Titre a definir]", "Terminez le stage 5 du Chapitre 8 Elite.",
                8, 5, true, 0, 35500, 0));
        liste.add(new QueteProgression("C8E6",
                "[ELITE] Stage 6 — [Titre a definir]", "Terminez le stage 6 du Chapitre 8 Elite.",
                8, 6, true, 0, 36000, 0));
        liste.add(new QueteProgression("C8E7",
                "[ELITE] Stage 7 — [Titre a definir]", "Terminez le stage 7 du Chapitre 8 Elite.",
                8, 7, true, 0, 36500, 0));
        liste.add(new QueteProgression("C8E8",
                "[ELITE] Stage 8 — [Titre a definir]", "Terminez le stage 8 du Chapitre 8 Elite.",
                8, 8, true, 0, 37000, 0));
        liste.add(new QueteProgression("C8E9",
                "[ELITE] Stage 9 — [Titre a definir]", "Terminez le stage 9 du Chapitre 8 Elite.",
                8, 9, true, 0, 37500, 0));
        liste.add(new QueteProgression("C8E10",
                "[ELITE] Stage 10 — [Titre a definir]", "Terminez le stage 10 du Chapitre 8 Elite.",
                8, 10, true, 0, 41000, 0));

        // ── Chapitre 9 Elite (XP desactivee — recompenses Or/materiaux inchangees) ──
        liste.add(new QueteProgression("C9E1",
                "[ELITE] Stage 1 — [Titre a definir]", "Terminez le stage 1 du Chapitre 9 Elite.",
                9, 1, true, 0, 39500, 0));
        liste.add(new QueteProgression("C9E2",
                "[ELITE] Stage 2 — [Titre a definir]", "Terminez le stage 2 du Chapitre 9 Elite.",
                9, 2, true, 0, 40000, 0));
        liste.add(new QueteProgression("C9E3",
                "[ELITE] Stage 3 — [Titre a definir]", "Terminez le stage 3 du Chapitre 9 Elite.",
                9, 3, true, 0, 40500, 0));
        liste.add(new QueteProgression("C9E4",
                "[ELITE] Stage 4 — [Titre a definir]", "Terminez le stage 4 du Chapitre 9 Elite.",
                9, 4, true, 0, 41000, 0));
        liste.add(new QueteProgression("C9E5",
                "[ELITE] Stage 5 — [Titre a definir]", "Terminez le stage 5 du Chapitre 9 Elite.",
                9, 5, true, 0, 41500, 0));
        liste.add(new QueteProgression("C9E6",
                "[ELITE] Stage 6 — [Titre a definir]", "Terminez le stage 6 du Chapitre 9 Elite.",
                9, 6, true, 0, 42000, 0));
        liste.add(new QueteProgression("C9E7",
                "[ELITE] Stage 7 — [Titre a definir]", "Terminez le stage 7 du Chapitre 9 Elite.",
                9, 7, true, 0, 42500, 0));
        liste.add(new QueteProgression("C9E8",
                "[ELITE] Stage 8 — [Titre a definir]", "Terminez le stage 8 du Chapitre 9 Elite.",
                9, 8, true, 0, 43000, 0));
        liste.add(new QueteProgression("C9E9",
                "[ELITE] Stage 9 — [Titre a definir]", "Terminez le stage 9 du Chapitre 9 Elite.",
                9, 9, true, 0, 43500, 0));
        liste.add(new QueteProgression("C9E10",
                "[ELITE] Stage 10 — [Titre a definir]", "Terminez le stage 10 du Chapitre 9 Elite.",
                9, 10, true, 0, 47000, 0));

        // ── Chapitre 10 Elite (XP desactivee — recompenses Or/materiaux inchangees) ──
        liste.add(new QueteProgression("C10E1",
                "[ELITE] Stage 1 — [Titre a definir]", "Terminez le stage 1 du Chapitre 10 Elite.",
                10, 1, true, 0, 45500, 0));
        liste.add(new QueteProgression("C10E2",
                "[ELITE] Stage 2 — [Titre a definir]", "Terminez le stage 2 du Chapitre 10 Elite.",
                10, 2, true, 0, 46000, 0));
        liste.add(new QueteProgression("C10E3",
                "[ELITE] Stage 3 — [Titre a definir]", "Terminez le stage 3 du Chapitre 10 Elite.",
                10, 3, true, 0, 46500, 0));
        liste.add(new QueteProgression("C10E4",
                "[ELITE] Stage 4 — [Titre a definir]", "Terminez le stage 4 du Chapitre 10 Elite.",
                10, 4, true, 0, 47000, 0));
        liste.add(new QueteProgression("C10E5",
                "[ELITE] Stage 5 — [Titre a definir]", "Terminez le stage 5 du Chapitre 10 Elite.",
                10, 5, true, 0, 47500, 0));
        liste.add(new QueteProgression("C10E6",
                "[ELITE] Stage 6 — [Titre a definir]", "Terminez le stage 6 du Chapitre 10 Elite.",
                10, 6, true, 0, 48000, 0));
        liste.add(new QueteProgression("C10E7",
                "[ELITE] Stage 7 — [Titre a definir]", "Terminez le stage 7 du Chapitre 10 Elite.",
                10, 7, true, 0, 48500, 0));
        liste.add(new QueteProgression("C10E8",
                "[ELITE] Stage 8 — [Titre a definir]", "Terminez le stage 8 du Chapitre 10 Elite.",
                10, 8, true, 0, 49000, 0));
        liste.add(new QueteProgression("C10E9",
                "[ELITE] Stage 9 — [Titre a definir]", "Terminez le stage 9 du Chapitre 10 Elite.",
                10, 9, true, 0, 49500, 0));
        liste.add(new QueteProgression("C10E10",
                "[ELITE] Stage 10 — [Titre a definir]", "Terminez le stage 10 du Chapitre 10 Elite.",
                10, 10, true, 0, 53000, 0));

        // ── Chapitre 11 Elite (TODO : niveaux / XP totale a definir) ──
        liste.add(new QueteProgression("C11E1",
                "[ELITE] Stage 1 — [Titre a definir]", "Terminez le stage 1 du Chapitre 11 Elite.",
                11, 1, true, 0, 0, 0));
        liste.add(new QueteProgression("C11E2",
                "[ELITE] Stage 2 — [Titre a definir]", "Terminez le stage 2 du Chapitre 11 Elite.",
                11, 2, true, 0, 0, 0));
        liste.add(new QueteProgression("C11E3",
                "[ELITE] Stage 3 — [Titre a definir]", "Terminez le stage 3 du Chapitre 11 Elite.",
                11, 3, true, 0, 0, 0));
        liste.add(new QueteProgression("C11E4",
                "[ELITE] Stage 4 — [Titre a definir]", "Terminez le stage 4 du Chapitre 11 Elite.",
                11, 4, true, 0, 0, 0));
        liste.add(new QueteProgression("C11E5",
                "[ELITE] Stage 5 — [Titre a definir]", "Terminez le stage 5 du Chapitre 11 Elite.",
                11, 5, true, 0, 0, 0));
        liste.add(new QueteProgression("C11E6",
                "[ELITE] Stage 6 — [Titre a definir]", "Terminez le stage 6 du Chapitre 11 Elite.",
                11, 6, true, 0, 0, 0));
        liste.add(new QueteProgression("C11E7",
                "[ELITE] Stage 7 — [Titre a definir]", "Terminez le stage 7 du Chapitre 11 Elite.",
                11, 7, true, 0, 0, 0));
        liste.add(new QueteProgression("C11E8",
                "[ELITE] Stage 8 — [Titre a definir]", "Terminez le stage 8 du Chapitre 11 Elite.",
                11, 8, true, 0, 0, 0));
        liste.add(new QueteProgression("C11E9",
                "[ELITE] Stage 9 — [Titre a definir]", "Terminez le stage 9 du Chapitre 11 Elite.",
                11, 9, true, 0, 0, 0));
        liste.add(new QueteProgression("C11E10",
                "[ELITE] Stage 10 — [Titre a definir]", "Terminez le stage 10 du Chapitre 11 Elite.",
                11, 10, true, 0, 0, 0));

        // ── Chapitre 12 Elite (TODO : niveaux / XP totale a definir) ──
        liste.add(new QueteProgression("C12E1",
                "[ELITE] Stage 1 — [Titre a definir]", "Terminez le stage 1 du Chapitre 12 Elite.",
                12, 1, true, 0, 0, 0));
        liste.add(new QueteProgression("C12E2",
                "[ELITE] Stage 2 — [Titre a definir]", "Terminez le stage 2 du Chapitre 12 Elite.",
                12, 2, true, 0, 0, 0));
        liste.add(new QueteProgression("C12E3",
                "[ELITE] Stage 3 — [Titre a definir]", "Terminez le stage 3 du Chapitre 12 Elite.",
                12, 3, true, 0, 0, 0));
        liste.add(new QueteProgression("C12E4",
                "[ELITE] Stage 4 — [Titre a definir]", "Terminez le stage 4 du Chapitre 12 Elite.",
                12, 4, true, 0, 0, 0));
        liste.add(new QueteProgression("C12E5",
                "[ELITE] Stage 5 — [Titre a definir]", "Terminez le stage 5 du Chapitre 12 Elite.",
                12, 5, true, 0, 0, 0));
        liste.add(new QueteProgression("C12E6",
                "[ELITE] Stage 6 — [Titre a definir]", "Terminez le stage 6 du Chapitre 12 Elite.",
                12, 6, true, 0, 0, 0));
        liste.add(new QueteProgression("C12E7",
                "[ELITE] Stage 7 — [Titre a definir]", "Terminez le stage 7 du Chapitre 12 Elite.",
                12, 7, true, 0, 0, 0));
        liste.add(new QueteProgression("C12E8",
                "[ELITE] Stage 8 — [Titre a definir]", "Terminez le stage 8 du Chapitre 12 Elite.",
                12, 8, true, 0, 0, 0));
        liste.add(new QueteProgression("C12E9",
                "[ELITE] Stage 9 — [Titre a definir]", "Terminez le stage 9 du Chapitre 12 Elite.",
                12, 9, true, 0, 0, 0));
        liste.add(new QueteProgression("C12E10",
                "[ELITE] Stage 10 — [Titre a definir]", "Terminez le stage 10 du Chapitre 12 Elite.",
                12, 10, true, 0, 0, 0));

        // ── Chapitre 13 Elite (TODO : niveaux / XP totale a definir) ──
        liste.add(new QueteProgression("C13E1",
                "[ELITE] Stage 1 — [Titre a definir]", "Terminez le stage 1 du Chapitre 13 Elite.",
                13, 1, true, 0, 0, 0));
        liste.add(new QueteProgression("C13E2",
                "[ELITE] Stage 2 — [Titre a definir]", "Terminez le stage 2 du Chapitre 13 Elite.",
                13, 2, true, 0, 0, 0));
        liste.add(new QueteProgression("C13E3",
                "[ELITE] Stage 3 — [Titre a definir]", "Terminez le stage 3 du Chapitre 13 Elite.",
                13, 3, true, 0, 0, 0));
        liste.add(new QueteProgression("C13E4",
                "[ELITE] Stage 4 — [Titre a definir]", "Terminez le stage 4 du Chapitre 13 Elite.",
                13, 4, true, 0, 0, 0));
        liste.add(new QueteProgression("C13E5",
                "[ELITE] Stage 5 — [Titre a definir]", "Terminez le stage 5 du Chapitre 13 Elite.",
                13, 5, true, 0, 0, 0));
        liste.add(new QueteProgression("C13E6",
                "[ELITE] Stage 6 — [Titre a definir]", "Terminez le stage 6 du Chapitre 13 Elite.",
                13, 6, true, 0, 0, 0));
        liste.add(new QueteProgression("C13E7",
                "[ELITE] Stage 7 — [Titre a definir]", "Terminez le stage 7 du Chapitre 13 Elite.",
                13, 7, true, 0, 0, 0));
        liste.add(new QueteProgression("C13E8",
                "[ELITE] Stage 8 — [Titre a definir]", "Terminez le stage 8 du Chapitre 13 Elite.",
                13, 8, true, 0, 0, 0));
        liste.add(new QueteProgression("C13E9",
                "[ELITE] Stage 9 — [Titre a definir]", "Terminez le stage 9 du Chapitre 13 Elite.",
                13, 9, true, 0, 0, 0));
        liste.add(new QueteProgression("C13E10",
                "[ELITE] Stage 10 — [Titre a definir]", "Terminez le stage 10 du Chapitre 13 Elite.",
                13, 10, true, 0, 0, 0));

        return liste;
    }
}
