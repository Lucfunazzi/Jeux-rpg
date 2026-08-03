package lancement.Gestionnaires;

import java.util.ArrayList;
import java.util.List;
import lancement.Quetes.QueteProgression;

/** Definition en dur de toutes les quetes de progression (une par stage, normal + elite, tous chapitres). */
public class QuetesProgressionData {

    public static List<QueteProgression> creer() {
        List<QueteProgression> liste = new ArrayList<>();

        // ── Chapitre 1 normal (niv 1 → 10 | total ~6 200 XP) ────────────────
        // XP requise réelle niv 1→10 : 6 228 XP  |  distribution progressive
        liste.add(new QueteProgression("C1S1",
                "Prologue", "Terminez le stage 1 du Chapitre 1.",
                1, 1, false, 450, 500, 0));
        liste.add(new QueteProgression("C1S2",
                "Bora le charmeur", "Terminez le stage 2 du Chapitre 1.",
                1, 2, false, 500, 700, 0));
        liste.add(new QueteProgression("C1S3",
                "Chemin vers fairy tail", "Terminez le stage 3 du Chapitre 1.",
                1, 3, false, 550, 1000, 0));
        liste.add(new QueteProgression("C1S4",
                "L'arrivée de la reine des fées", "Terminez le stage 4 du Chapitre 1.",
                1, 4, false, 600, 1200, 0));
        liste.add(new QueteProgression("C1S5",
                "Premier mission pour Lucy", "Terminez le stage 5 du Chapitre 1.",
                1, 5, false, 600, 1500, 0));
        liste.add(new QueteProgression("C1S6",
                "Le duc evarlo", "Terminez le stage 6 du Chapitre 1.",
                1, 6, false, 700, 1800, 0));
        liste.add(new QueteProgression("C1S7",
                "Retour a fairy tail", "Terminez le stage 7 du Chapitre 1.",
                1, 7, false, 700, 2000, 0));
        liste.add(new QueteProgression("C1S8",
                "Eisen Wald", "Terminez le stage 8 du Chapitre 1.",
                1, 8, false, 750, 2200, 0));
        liste.add(new QueteProgression("C1S9",
                "Eligor le mage de vent", "Terminez le stage 9 du Chapitre 1.",
                1, 9, false, 750, 2500, 0));
        liste.add(new QueteProgression("C1S10",
                "La flute maudite", "Terminez le stage 10 du Chapitre 1.",
                1, 10, false, 600, 3000, 100));

        // ── Chapitre 1 Elite (niv 10 → 20 | total ~40 000 XP) ───────────────
        // XP requise réelle niv 10→20 : 40 024 XP  |  distribution progressive
        liste.add(new QueteProgression("C1E1",
                "Prologue Elite", "Terminez le stage 1 du Chapitre 1 Elite.",
                1, 1, true, 2800, 3000, 0));
        liste.add(new QueteProgression("C1E2",
                "Bora le charmeur Elite", "Terminez le stage 2 du Chapitre 1 Elite.",
                1, 2, true, 3200, 3500, 0));
        liste.add(new QueteProgression("C1E3",
                "Chemin vers Fairy Tail Elite", "Terminez le stage 3 du Chapitre 1 Elite.",
                1, 3, true, 3600, 4000, 0));
        liste.add(new QueteProgression("C1E4",
                "L'arrivée de la reine des fées Elite", "Terminez le stage 4 du Chapitre 1 Elite.",//vrai combat avec erza cette fois-ci
                1, 4, true, 4000, 4500, 0));
        liste.add(new QueteProgression("C1E5",
                "Premier mission pour Lucy Elite", "Terminez le stage 5 du Chapitre 1 Elite.",
                1, 5, true, 4000, 5000, 0));
        liste.add(new QueteProgression("C1E6",
                "Le duc evarlo Elite", "Terminez le stage 6 du Chapitre 1 Elite.",
                1, 6, true, 4400, 5500, 0));
        liste.add(new QueteProgression("C1E7",
                "Retour a fairy tail Elite", "Terminez le stage 7 du Chapitre 1 Elite.",
                1, 7, true, 4400, 6000, 0));
        liste.add(new QueteProgression("C1E8",
                "Eisen Wald Elite", "Terminez le stage 8 du Chapitre 1 Elite.",
                1, 8, true, 4800, 6500, 0));
        liste.add(new QueteProgression("C1E9",
                "Eligor le mage de vent Elite", "Terminez le stage 9 du Chapitre 1 Elite.",
                1, 9, true, 4800, 7000, 0));
        liste.add(new QueteProgression("C1E10",
                "La flute maudite Elite", "Terminez le stage 10 du Chapitre 1 Elite.", // vrai combat avec natsu gray et erza
                1, 10, true, 4000, 9000, 0));

        // ── Chapitre 2 normal (niv 20 → 25 | total ~71 000 XP) ──────────────
        // XP requise réelle niv 20→25 : 71 010 XP  |  distribution progressive
        liste.add(new QueteProgression("C2S1",
                "Prologue Chapitre 2", "Terminez le stage 1 du Chapitre 2.",
                2, 1, false, 4950, 1500, 0));
        liste.add(new QueteProgression("C2S2",
                "Arrivée a l'ile de galuna", "Terminez le stage 2 du Chapitre 2.",
                2, 2, false, 5700, 2000, 0));
        liste.add(new QueteProgression("C2S3",
                "Lucy VS Cherry", "Terminez le stage 3 du Chapitre 2.",
                2, 3, false, 6400, 2500, 0));
        liste.add(new QueteProgression("C2S4",
                "Yuka contre Natsu", "Terminez le stage 4 du Chapitre 2.",
                2, 4, false, 7100, 3000, 0));
        liste.add(new QueteProgression("C2S5",
                "Tobi contre Natsu", "Terminez le stage 5 du Chapitre 2.",
                2, 5, false, 7100, 3500, 0));
        liste.add(new QueteProgression("C2S6",
                "Gray vs Leon 1", "Terminez le stage 6 du Chapitre 2.",
                2, 6, false, 7800, 4000, 0));
        liste.add(new QueteProgression("C2S7",
                "Natsu contre l'homme mysterieux", "Terminez le stage 7 du Chapitre 2.",
                2, 7, false, 7800, 4500, 0));
        liste.add(new QueteProgression("C2S8",
                "Gray vs Leon part 2", "Terminez le stage 8 du Chapitre 2.",
                2, 8, false, 8500, 5000, 0));
        liste.add(new QueteProgression("C2S9",
                "Le passé de Gray", "Terminez le stage 9 du Chapitre 2.",
                2, 9, false, 8500, 5500, 0));
        liste.add(new QueteProgression("C2S10",
                "Deliora le demon ", "Terminez le stage 10 du Chapitre 2.",
                2, 10, false, 7100, 6000, 150));

        // ── Chapitre 2 Elite (niv 25 → 30 | total ~176 650 XP) ──────────────
        // XP requise réelle niv 25→30 : 176 680 XP  |  distribution progressive
        liste.add(new QueteProgression("C2E1",
                "Prologue Chapitre 2 Elite", "Terminez le stage 1 du Chapitre 2 Elite.",
                2, 1, true, 12350, 3000, 0));
        liste.add(new QueteProgression("C2E2",
                "Arrivée a l'ile de galuna Elite", "Terminez le stage 2 du Chapitre 2 Elite.",
                2, 2, true, 14150, 3500, 0));
        liste.add(new QueteProgression("C2E3",
                "Lucy VS Cherry Elite", "Terminez le stage 3 du Chapitre 2 Elite.",
                2, 3, true, 15900, 4000, 0));
        liste.add(new QueteProgression("C2E4",
                "Yuka contre Natsu Elite", "Terminez le stage 4 du Chapitre 2 Elite.",
                2, 4, true, 17650, 4500, 0));
        liste.add(new QueteProgression("C2E5",
                "Tobi contre Natsu Elite", "Terminez le stage 5 du Chapitre 2 Elite.",
                2, 5, true, 17650, 5000, 0));
        liste.add(new QueteProgression("C2E6",
                "Gray vs Leon 1 Elite", "Terminez le stage 6 du Chapitre 2 Elite.",
                2, 6, true, 19450, 5500, 0));
        liste.add(new QueteProgression("C2E7",
                "Natsu contre l'homme mysterieux Elite", "Terminez le stage 7 du Chapitre 2 Elite.",
                2, 7, true, 19450, 6000, 0));
        liste.add(new QueteProgression("C2E8",
                "Gray vs Leon part 2 Elite", "Terminez le stage 8 du Chapitre 2 Elite.",
                2, 8, true, 21200, 6500, 0));
        liste.add(new QueteProgression("C2E9",
                "Le passé de Gray Elite", "Terminez le stage 9 du Chapitre 2 Elite.",
                2, 9, true, 21200, 7000, 0));
        liste.add(new QueteProgression("C2E10",
                "Deliora le demon Elite", "Terminez le stage 10 du Chapitre 2 Elite.",
                2, 10, true, 17650, 10000, 200));

        // ── Chapitre 3 normal (niv 30 → 35 | total ~439 500 XP) ─────────────
        // XP requise réelle niv 30→35 : 439 623 XP  |  distribution progressive
        liste.add(new QueteProgression("C3S1",
                "L'assaut de Phantom Lord", "Terminez le stage 1 du Chapitre 3.",
                3, 1, false, 30750, 2000, 0));
        liste.add(new QueteProgression("C3S2",
                "Totomaru — Sept Flammes", "Terminez le stage 2 du Chapitre 3.",
                3, 2, false, 35150, 2500, 0));
        liste.add(new QueteProgression("C3S3",
                "Sol — L'Impenetrable", "Terminez le stage 3 du Chapitre 3.",
                3, 3, false, 39550, 3000, 0));
        liste.add(new QueteProgression("C3S4",
                "L'Element 4 se deploie", "Terminez le stage 4 du Chapitre 3.",
                3, 4, false, 43950, 3500, 0));
        liste.add(new QueteProgression("C3S5",
                "Jubia — L'Eau qui emprisonne", "Terminez le stage 5 du Chapitre 3.",
                3, 5, false, 43950, 4000, 0));
        liste.add(new QueteProgression("C3S6",
                "L'Element 4 au complet", "Terminez le stage 6 du Chapitre 3.",
                3, 6, false, 48350, 4500, 0));
        liste.add(new QueteProgression("C3S7",
                "Aria — Magie du Ciel Vide", "Terminez le stage 7 du Chapitre 3.",
                3, 7, false, 48350, 5000, 0));
        liste.add(new QueteProgression("C3S8",
                "L'Element 4 — Derniere resistance", "Terminez le stage 8 du Chapitre 3.",
                3, 8, false, 52750, 5500, 0));
        liste.add(new QueteProgression("C3S9",
                "Jose — L'Ombre s'eveille", "Terminez le stage 9 du Chapitre 3.",
                3, 9, false, 52750, 6000, 0));
        liste.add(new QueteProgression("C3S10",
                "Jose Porla — Maitre de Phantom Lord", "Terminez le stage 10 du Chapitre 3.",
                3, 10, false, 43950, 8000, 200));

        // ── Chapitre 3 Elite (niv 35 → 41 | total ~1 461 000 XP) ────────────
        liste.add(new QueteProgression("C3E1",
                "L'assaut de Phantom Lord Renforce", "Terminez le stage 1 du Chapitre 3 Elite.",
                3, 1, true, 141000, 4000, 0));
        liste.add(new QueteProgression("C3E2",
                "Totomaru — Sept Flammes d'Elite", "Terminez le stage 2 du Chapitre 3 Elite.",
                3, 2, true, 143000, 4500, 0));
        liste.add(new QueteProgression("C3E3",
                "Sol — L'Impenetrable d'Elite", "Terminez le stage 3 du Chapitre 3 Elite.",
                3, 3, true, 145000, 5000, 0));
        liste.add(new QueteProgression("C3E4",
                "L'Element 4 Renforce", "Terminez le stage 4 du Chapitre 3 Elite.",
                3, 4, true, 146000, 5500, 0));
        liste.add(new QueteProgression("C3E5",
                "Jubia — L'Eau qui Brise d'Elite", "Terminez le stage 5 du Chapitre 3 Elite.",
                3, 5, true, 146000, 6000, 0));
        liste.add(new QueteProgression("C3E6",
                "L'Element 4 Complet d'Elite", "Terminez le stage 6 du Chapitre 3 Elite.",
                3, 6, true, 147000, 6500, 0));
        liste.add(new QueteProgression("C3E7",
                "Aria — Magie du Ciel Vide Transcendee", "Terminez le stage 7 du Chapitre 3 Elite.",
                3, 7, true, 147000, 7000, 0));
        liste.add(new QueteProgression("C3E8",
                "L'Element 4 — Ultime Resistance", "Terminez le stage 8 du Chapitre 3 Elite.",
                3, 8, true, 148000, 7500, 0));
        liste.add(new QueteProgression("C3E9",
                "Jose — L'Ombre Transcendee", "Terminez le stage 9 du Chapitre 3 Elite.",
                3, 9, true, 148000, 8000, 0));
        liste.add(new QueteProgression("C3E10",
                "Jose Porla — Forme Spectrale Supreme", "Terminez le stage 10 du Chapitre 3 Elite.",
                3, 10, true, 150000, 12000, 0));

        // ── Chapitre 4 normal (niv 41 → 47 | total ~4 360 000 XP) ───────────
        liste.add(new QueteProgression("C4S1",
                "Embuscade dans le casino", "Terminez le stage 1 du Chapitre 4.",
                4, 1, false, 305000, 6500, 0));
        liste.add(new QueteProgression("C4S2",
                "Infiltration dans la tour du paradis", "Terminez le stage 2 du Chapitre 4.",
                4, 2, false, 349000, 7000, 0));
        liste.add(new QueteProgression("C4S3",
                "Miaou, Il faut sauver Happy", "Terminez le stage 3 du Chapitre 4.",
                4, 3, false, 392000, 7500, 0));
        liste.add(new QueteProgression("C4S4",
                "Libération d'erza", "Terminez le stage 4 du Chapitre 4.",
                4, 4, false, 436000, 8000, 0));
        liste.add(new QueteProgression("C4S5",
                "Les esprits et l'eau", "Terminez le stage 5 du Chapitre 4.",
                4, 5, false, 436000, 8500, 0));
        liste.add(new QueteProgression("C4S6",
                "Le hiboux assasin", "Terminez le stage 6 du Chapitre 4.",
                4, 6, false, 480000, 9000, 0));
        liste.add(new QueteProgression("C4S7",
                "Epée contre Epée", "Terminez le stage 7 du Chapitre 4.",
                4, 7, false, 480000, 9500, 0));
        liste.add(new QueteProgression("C4S8",
                "Erza contre Jellal — Le Passe Ressurgit", "Terminez le stage 8 du Chapitre 4.",
                4, 8, false, 523000, 10000, 0));
        liste.add(new QueteProgression("C4S9",
                "Simon Revient — L'Assaut sur Jellal", "Terminez le stage 9 du Chapitre 4.",
                4, 9, false, 523000, 10500, 0));
        liste.add(new QueteProgression("C4S10",
                "Jellal — L'Effondrement de la Tour du Paradis", "Terminez le stage 10 du Chapitre 4.",
                4, 10, false, 436000, 14000, 250));

        // ── Chapitre 5 normal (niv 50 → 54 | total ~12 157 500 XP) ──────────
        liste.add(new QueteProgression("C5S1",
                "Stage 1 — [Titre a definir]", "Terminez le stage 1 du Chapitre 5.",
                5, 1, false, 850000, 12500, 0));
        liste.add(new QueteProgression("C5S2",
                "Stage 2 — [Titre a definir]", "Terminez le stage 2 du Chapitre 5.",
                5, 2, false, 970000, 13000, 0));
        liste.add(new QueteProgression("C5S3",
                "Stage 3 — [Titre a definir]", "Terminez le stage 3 du Chapitre 5.",
                5, 3, false, 1090000, 13500, 0));
        liste.add(new QueteProgression("C5S4",
                "Stage 4 — [Titre a definir]", "Terminez le stage 4 du Chapitre 5.",
                5, 4, false, 1220000, 14000, 0));
        liste.add(new QueteProgression("C5S5",
                "Stage 5 — [Titre a definir]", "Terminez le stage 5 du Chapitre 5.",
                5, 5, false, 1220000, 14500, 0));
        liste.add(new QueteProgression("C5S6",
                "Stage 6 — [Titre a definir]", "Terminez le stage 6 du Chapitre 5.",
                5, 6, false, 1340000, 15000, 0));
        liste.add(new QueteProgression("C5S7",
                "Stage 7 — [Titre a definir]", "Terminez le stage 7 du Chapitre 5.",
                5, 7, false, 1340000, 15500, 0));
        liste.add(new QueteProgression("C5S8",
                "Stage 8 — [Titre a definir]", "Terminez le stage 8 du Chapitre 5.",
                5, 8, false, 1460000, 16000, 0));
        liste.add(new QueteProgression("C5S9",
                "Stage 9 — [Titre a definir]", "Terminez le stage 9 du Chapitre 5.",
                5, 9, false, 1460000, 16500, 0));
        liste.add(new QueteProgression("C5S10",
                "Stage 10 — [Titre a definir]", "Terminez le stage 10 du Chapitre 5.",
                5, 10, false, 1220000, 20000, 300));

        // ── Chapitre 6 normal (niv 59 → 61 | total ~25 709 000 XP) ──────────
        liste.add(new QueteProgression("C6S1",
                "Stage 1 — [Titre a definir]", "Terminez le stage 1 du Chapitre 6.",
                6, 1, false, 1800000, 18500, 0));
        liste.add(new QueteProgression("C6S2",
                "Stage 2 — [Titre a definir]", "Terminez le stage 2 du Chapitre 6.",
                6, 2, false, 2060000, 19000, 0));
        liste.add(new QueteProgression("C6S3",
                "Stage 3 — [Titre a definir]", "Terminez le stage 3 du Chapitre 6.",
                6, 3, false, 2310000, 19500, 0));
        liste.add(new QueteProgression("C6S4",
                "Stage 4 — [Titre a definir]", "Terminez le stage 4 du Chapitre 6.",
                6, 4, false, 2570000, 20000, 0));
        liste.add(new QueteProgression("C6S5",
                "Stage 5 — [Titre a definir]", "Terminez le stage 5 du Chapitre 6.",
                6, 5, false, 2570000, 20500, 0));
        liste.add(new QueteProgression("C6S6",
                "Stage 6 — [Titre a definir]", "Terminez le stage 6 du Chapitre 6.",
                6, 6, false, 2830000, 21000, 0));
        liste.add(new QueteProgression("C6S7",
                "Stage 7 — [Titre a definir]", "Terminez le stage 7 du Chapitre 6.",
                6, 7, false, 2830000, 21500, 0));
        liste.add(new QueteProgression("C6S8",
                "Stage 8 — [Titre a definir]", "Terminez le stage 8 du Chapitre 6.",
                6, 8, false, 3090000, 22000, 0));
        liste.add(new QueteProgression("C6S9",
                "Stage 9 — [Titre a definir]", "Terminez le stage 9 du Chapitre 6.",
                6, 9, false, 3090000, 22500, 0));
        liste.add(new QueteProgression("C6S10",
                "Stage 10 — [Titre a definir]", "Terminez le stage 10 du Chapitre 6.",
                6, 10, false, 2570000, 26000, 350));

        // ── Chapitre 7 normal (niv 66 → 68 | total ~92 120 000 XP) ──────────
        liste.add(new QueteProgression("C7S1",
                "Stage 1 — [Titre a definir]", "Terminez le stage 1 du Chapitre 7.",
                7, 1, false, 6400000, 24500, 0));
        liste.add(new QueteProgression("C7S2",
                "Stage 2 — [Titre a definir]", "Terminez le stage 2 du Chapitre 7.",
                7, 2, false, 7400000, 25000, 0));
        liste.add(new QueteProgression("C7S3",
                "Stage 3 — [Titre a definir]", "Terminez le stage 3 du Chapitre 7.",
                7, 3, false, 8300000, 25500, 0));
        liste.add(new QueteProgression("C7S4",
                "Stage 4 — [Titre a definir]", "Terminez le stage 4 du Chapitre 7.",
                7, 4, false, 9200000, 26000, 0));
        liste.add(new QueteProgression("C7S5",
                "Stage 5 — [Titre a definir]", "Terminez le stage 5 du Chapitre 7.",
                7, 5, false, 9200000, 26500, 0));
        liste.add(new QueteProgression("C7S6",
                "Stage 6 — [Titre a definir]", "Terminez le stage 6 du Chapitre 7.",
                7, 6, false, 10100000, 27000, 0));
        liste.add(new QueteProgression("C7S7",
                "Stage 7 — [Titre a definir]", "Terminez le stage 7 du Chapitre 7.",
                7, 7, false, 10100000, 27500, 0));
        liste.add(new QueteProgression("C7S8",
                "Stage 8 — [Titre a definir]", "Terminez le stage 8 du Chapitre 7.",
                7, 8, false, 11100000, 28000, 0));
        liste.add(new QueteProgression("C7S9",
                "Stage 9 — [Titre a definir]", "Terminez le stage 9 du Chapitre 7.",
                7, 9, false, 11100000, 28500, 0));
        liste.add(new QueteProgression("C7S10",
                "Stage 10 — [Titre a definir]", "Terminez le stage 10 du Chapitre 7.",
                7, 10, false, 9200000, 32000, 400));

        // ── Chapitre 8 normal (niv 72 → 73 | total ~125 032 000 XP) ─────────
        liste.add(new QueteProgression("C8S1",
                "Stage 1 — [Titre a definir]", "Terminez le stage 1 du Chapitre 8.",
                8, 1, false, 8800000, 30500, 0));
        liste.add(new QueteProgression("C8S2",
                "Stage 2 — [Titre a definir]", "Terminez le stage 2 du Chapitre 8.",
                8, 2, false, 10000000, 31000, 0));
        liste.add(new QueteProgression("C8S3",
                "Stage 3 — [Titre a definir]", "Terminez le stage 3 du Chapitre 8.",
                8, 3, false, 11300000, 31500, 0));
        liste.add(new QueteProgression("C8S4",
                "Stage 4 — [Titre a definir]", "Terminez le stage 4 du Chapitre 8.",
                8, 4, false, 12500000, 32000, 0));
        liste.add(new QueteProgression("C8S5",
                "Stage 5 — [Titre a definir]", "Terminez le stage 5 du Chapitre 8.",
                8, 5, false, 12500000, 32500, 0));
        liste.add(new QueteProgression("C8S6",
                "Stage 6 — [Titre a definir]", "Terminez le stage 6 du Chapitre 8.",
                8, 6, false, 13800000, 33000, 0));
        liste.add(new QueteProgression("C8S7",
                "Stage 7 — [Titre a definir]", "Terminez le stage 7 du Chapitre 8.",
                8, 7, false, 13800000, 33500, 0));
        liste.add(new QueteProgression("C8S8",
                "Stage 8 — [Titre a definir]", "Terminez le stage 8 du Chapitre 8.",
                8, 8, false, 15000000, 34000, 0));
        liste.add(new QueteProgression("C8S9",
                "Stage 9 — [Titre a definir]", "Terminez le stage 9 du Chapitre 8.",
                8, 9, false, 15000000, 34500, 0));
        liste.add(new QueteProgression("C8S10",
                "Stage 10 — [Titre a definir]", "Terminez le stage 10 du Chapitre 8.",
                8, 10, false, 12500000, 38000, 450));

        // ── Chapitre 9 normal (niv 75 → 77 | total ~475 320 000 XP) ─────────
        liste.add(new QueteProgression("C9S1",
                "Stage 1 — [Titre a definir]", "Terminez le stage 1 du Chapitre 9.",
                9, 1, false, 33300000, 36500, 0));
        liste.add(new QueteProgression("C9S2",
                "Stage 2 — [Titre a definir]", "Terminez le stage 2 du Chapitre 9.",
                9, 2, false, 38000000, 37000, 0));
        liste.add(new QueteProgression("C9S3",
                "Stage 3 — [Titre a definir]", "Terminez le stage 3 du Chapitre 9.",
                9, 3, false, 42800000, 37500, 0));
        liste.add(new QueteProgression("C9S4",
                "Stage 4 — [Titre a definir]", "Terminez le stage 4 du Chapitre 9.",
                9, 4, false, 47500000, 38000, 0));
        liste.add(new QueteProgression("C9S5",
                "Stage 5 — [Titre a definir]", "Terminez le stage 5 du Chapitre 9.",
                9, 5, false, 47500000, 38500, 0));
        liste.add(new QueteProgression("C9S6",
                "Stage 6 — [Titre a definir]", "Terminez le stage 6 du Chapitre 9.",
                9, 6, false, 52300000, 39000, 0));
        liste.add(new QueteProgression("C9S7",
                "Stage 7 — [Titre a definir]", "Terminez le stage 7 du Chapitre 9.",
                9, 7, false, 52300000, 39500, 0));
        liste.add(new QueteProgression("C9S8",
                "Stage 8 — [Titre a definir]", "Terminez le stage 8 du Chapitre 9.",
                9, 8, false, 57000000, 40000, 0));
        liste.add(new QueteProgression("C9S9",
                "Stage 9 — [Titre a definir]", "Terminez le stage 9 du Chapitre 9.",
                9, 9, false, 57000000, 40500, 0));
        liste.add(new QueteProgression("C9S10",
                "Stage 10 — [Titre a definir]", "Terminez le stage 10 du Chapitre 9.",
                9, 10, false, 47500000, 44000, 500));

        // ── Chapitre 10 normal (niv 80 → 82 | total ~1 182 750 000 XP) ──────
        liste.add(new QueteProgression("C10S1",
                "Stage 1 — [Titre a definir]", "Terminez le stage 1 du Chapitre 10.",
                10, 1, false, 83000000, 42500, 0));
        liste.add(new QueteProgression("C10S2",
                "Stage 2 — [Titre a definir]", "Terminez le stage 2 du Chapitre 10.",
                10, 2, false, 95000000, 43000, 0));
        liste.add(new QueteProgression("C10S3",
                "Stage 3 — [Titre a definir]", "Terminez le stage 3 du Chapitre 10.",
                10, 3, false, 106000000, 43500, 0));
        liste.add(new QueteProgression("C10S4",
                "Stage 4 — [Titre a definir]", "Terminez le stage 4 du Chapitre 10.",
                10, 4, false, 118000000, 44000, 0));
        liste.add(new QueteProgression("C10S5",
                "Stage 5 — [Titre a definir]", "Terminez le stage 5 du Chapitre 10.",
                10, 5, false, 118000000, 44500, 0));
        liste.add(new QueteProgression("C10S6",
                "Stage 6 — [Titre a definir]", "Terminez le stage 6 du Chapitre 10.",
                10, 6, false, 130000000, 45000, 0));
        liste.add(new QueteProgression("C10S7",
                "Stage 7 — [Titre a definir]", "Terminez le stage 7 du Chapitre 10.",
                10, 7, false, 130000000, 45500, 0));
        liste.add(new QueteProgression("C10S8",
                "Stage 8 — [Titre a definir]", "Terminez le stage 8 du Chapitre 10.",
                10, 8, false, 142000000, 46000, 0));
        liste.add(new QueteProgression("C10S9",
                "Stage 9 — [Titre a definir]", "Terminez le stage 9 du Chapitre 10.",
                10, 9, false, 142000000, 46500, 0));
        liste.add(new QueteProgression("C10S10",
                "Stage 10 — [Titre a definir]", "Terminez le stage 10 du Chapitre 10.",
                10, 10, false, 118000000, 50000, 550));

        // ── Chapitre 11 normal (TODO : niveaux / XP totale a definir) ───────
        liste.add(new QueteProgression("C11S1",
                "Stage 1 — [Titre a definir]", "Terminez le stage 1 du Chapitre 11.",
                11, 1, false, 0, 0, 0));
        liste.add(new QueteProgression("C11S2",
                "Stage 2 — [Titre a definir]", "Terminez le stage 2 du Chapitre 11.",
                11, 2, false, 0, 0, 0));
        liste.add(new QueteProgression("C11S3",
                "Stage 3 — [Titre a definir]", "Terminez le stage 3 du Chapitre 11.",
                11, 3, false, 0, 0, 0));
        liste.add(new QueteProgression("C11S4",
                "Stage 4 — [Titre a definir]", "Terminez le stage 4 du Chapitre 11.",
                11, 4, false, 0, 0, 0));
        liste.add(new QueteProgression("C11S5",
                "Stage 5 — [Titre a definir]", "Terminez le stage 5 du Chapitre 11.",
                11, 5, false, 0, 0, 0));
        liste.add(new QueteProgression("C11S6",
                "Stage 6 — [Titre a definir]", "Terminez le stage 6 du Chapitre 11.",
                11, 6, false, 0, 0, 0));
        liste.add(new QueteProgression("C11S7",
                "Stage 7 — [Titre a definir]", "Terminez le stage 7 du Chapitre 11.",
                11, 7, false, 0, 0, 0));
        liste.add(new QueteProgression("C11S8",
                "Stage 8 — [Titre a definir]", "Terminez le stage 8 du Chapitre 11.",
                11, 8, false, 0, 0, 0));
        liste.add(new QueteProgression("C11S9",
                "Stage 9 — [Titre a definir]", "Terminez le stage 9 du Chapitre 11.",
                11, 9, false, 0, 0, 0));
        liste.add(new QueteProgression("C11S10",
                "Stage 10 — [Titre a definir]", "Terminez le stage 10 du Chapitre 11.",
                11, 10, false, 0, 0, 0));

        // ── Chapitre 12 normal (TODO : niveaux / XP totale a definir) ───────
        liste.add(new QueteProgression("C12S1",
                "Stage 1 — [Titre a definir]", "Terminez le stage 1 du Chapitre 12.",
                12, 1, false, 0, 0, 0));
        liste.add(new QueteProgression("C12S2",
                "Stage 2 — [Titre a definir]", "Terminez le stage 2 du Chapitre 12.",
                12, 2, false, 0, 0, 0));
        liste.add(new QueteProgression("C12S3",
                "Stage 3 — [Titre a definir]", "Terminez le stage 3 du Chapitre 12.",
                12, 3, false, 0, 0, 0));
        liste.add(new QueteProgression("C12S4",
                "Stage 4 — [Titre a definir]", "Terminez le stage 4 du Chapitre 12.",
                12, 4, false, 0, 0, 0));
        liste.add(new QueteProgression("C12S5",
                "Stage 5 — [Titre a definir]", "Terminez le stage 5 du Chapitre 12.",
                12, 5, false, 0, 0, 0));
        liste.add(new QueteProgression("C12S6",
                "Stage 6 — [Titre a definir]", "Terminez le stage 6 du Chapitre 12.",
                12, 6, false, 0, 0, 0));
        liste.add(new QueteProgression("C12S7",
                "Stage 7 — [Titre a definir]", "Terminez le stage 7 du Chapitre 12.",
                12, 7, false, 0, 0, 0));
        liste.add(new QueteProgression("C12S8",
                "Stage 8 — [Titre a definir]", "Terminez le stage 8 du Chapitre 12.",
                12, 8, false, 0, 0, 0));
        liste.add(new QueteProgression("C12S9",
                "Stage 9 — [Titre a definir]", "Terminez le stage 9 du Chapitre 12.",
                12, 9, false, 0, 0, 0));
        liste.add(new QueteProgression("C12S10",
                "Stage 10 — [Titre a definir]", "Terminez le stage 10 du Chapitre 12.",
                12, 10, false, 0, 0, 0));

        // ── Chapitre 13 normal (TODO : niveaux / XP totale a definir) ───────
        liste.add(new QueteProgression("C13S1",
                "Stage 1 — [Titre a definir]", "Terminez le stage 1 du Chapitre 13.",
                13, 1, false, 0, 0, 0));
        liste.add(new QueteProgression("C13S2",
                "Stage 2 — [Titre a definir]", "Terminez le stage 2 du Chapitre 13.",
                13, 2, false, 0, 0, 0));
        liste.add(new QueteProgression("C13S3",
                "Stage 3 — [Titre a definir]", "Terminez le stage 3 du Chapitre 13.",
                13, 3, false, 0, 0, 0));
        liste.add(new QueteProgression("C13S4",
                "Stage 4 — [Titre a definir]", "Terminez le stage 4 du Chapitre 13.",
                13, 4, false, 0, 0, 0));
        liste.add(new QueteProgression("C13S5",
                "Stage 5 — [Titre a definir]", "Terminez le stage 5 du Chapitre 13.",
                13, 5, false, 0, 0, 0));
        liste.add(new QueteProgression("C13S6",
                "Stage 6 — [Titre a definir]", "Terminez le stage 6 du Chapitre 13.",
                13, 6, false, 0, 0, 0));
        liste.add(new QueteProgression("C13S7",
                "Stage 7 — [Titre a definir]", "Terminez le stage 7 du Chapitre 13.",
                13, 7, false, 0, 0, 0));
        liste.add(new QueteProgression("C13S8",
                "Stage 8 — [Titre a definir]", "Terminez le stage 8 du Chapitre 13.",
                13, 8, false, 0, 0, 0));
        liste.add(new QueteProgression("C13S9",
                "Stage 9 — [Titre a definir]", "Terminez le stage 9 du Chapitre 13.",
                13, 9, false, 0, 0, 0));
        liste.add(new QueteProgression("C13S10",
                "Stage 10 — [Titre a definir]", "Terminez le stage 10 du Chapitre 13.",
                13, 10, false, 0, 0, 0));

        // ── Chapitre 4 Elite (niv 47 → 49 | total ~2 883 000 XP) ──
        liste.add(new QueteProgression("C4E1",
                "[ELITE] Stage 1 — [Titre a definir]", "Terminez le stage 1 du Chapitre 4 Elite.",
                4, 1, true, 202000, 9500, 0));
        liste.add(new QueteProgression("C4E2",
                "[ELITE] Stage 2 — [Titre a definir]", "Terminez le stage 2 du Chapitre 4 Elite.",
                4, 2, true, 231000, 10000, 0));
        liste.add(new QueteProgression("C4E3",
                "[ELITE] Stage 3 — [Titre a definir]", "Terminez le stage 3 du Chapitre 4 Elite.",
                4, 3, true, 260000, 10500, 0));
        liste.add(new QueteProgression("C4E4",
                "[ELITE] Stage 4 — [Titre a definir]", "Terminez le stage 4 du Chapitre 4 Elite.",
                4, 4, true, 288000, 11000, 0));
        liste.add(new QueteProgression("C4E5",
                "[ELITE] Stage 5 — [Titre a definir]", "Terminez le stage 5 du Chapitre 4 Elite.",
                4, 5, true, 288000, 11500, 0));
        liste.add(new QueteProgression("C4E6",
                "[ELITE] Stage 6 — [Titre a definir]", "Terminez le stage 6 du Chapitre 4 Elite.",
                4, 6, true, 317000, 12000, 0));
        liste.add(new QueteProgression("C4E7",
                "[ELITE] Stage 7 — [Titre a definir]", "Terminez le stage 7 du Chapitre 4 Elite.",
                4, 7, true, 317000, 12500, 0));
        liste.add(new QueteProgression("C4E8",
                "[ELITE] Stage 8 — [Titre a definir]", "Terminez le stage 8 du Chapitre 4 Elite.",
                4, 8, true, 346000, 13000, 0));
        liste.add(new QueteProgression("C4E9",
                "[ELITE] Stage 9 — [Titre a definir]", "Terminez le stage 9 du Chapitre 4 Elite.",
                4, 9, true, 346000, 13500, 0));
        liste.add(new QueteProgression("C4E10",
                "[ELITE] Stage 10 — [Titre a definir]", "Terminez le stage 10 du Chapitre 4 Elite.",
                4, 10, true, 288000, 17000, 0));

        // ── Chapitre 5 Elite (niv 54 → 56 | total ~10 332 000 XP) ──
        liste.add(new QueteProgression("C5E1",
                "[ELITE] Stage 1 — [Titre a definir]", "Terminez le stage 1 du Chapitre 5 Elite.",
                5, 1, true, 720000, 15500, 0));
        liste.add(new QueteProgression("C5E2",
                "[ELITE] Stage 2 — [Titre a definir]", "Terminez le stage 2 du Chapitre 5 Elite.",
                5, 2, true, 830000, 16000, 0));
        liste.add(new QueteProgression("C5E3",
                "[ELITE] Stage 3 — [Titre a definir]", "Terminez le stage 3 du Chapitre 5 Elite.",
                5, 3, true, 930000, 16500, 0));
        liste.add(new QueteProgression("C5E4",
                "[ELITE] Stage 4 — [Titre a definir]", "Terminez le stage 4 du Chapitre 5 Elite.",
                5, 4, true, 1030000, 17000, 0));
        liste.add(new QueteProgression("C5E5",
                "[ELITE] Stage 5 — [Titre a definir]", "Terminez le stage 5 du Chapitre 5 Elite.",
                5, 5, true, 1030000, 17500, 0));
        liste.add(new QueteProgression("C5E6",
                "[ELITE] Stage 6 — [Titre a definir]", "Terminez le stage 6 du Chapitre 5 Elite.",
                5, 6, true, 1140000, 18000, 0));
        liste.add(new QueteProgression("C5E7",
                "[ELITE] Stage 7 — [Titre a definir]", "Terminez le stage 7 du Chapitre 5 Elite.",
                5, 7, true, 1140000, 18500, 0));
        liste.add(new QueteProgression("C5E8",
                "[ELITE] Stage 8 — [Titre a definir]", "Terminez le stage 8 du Chapitre 5 Elite.",
                5, 8, true, 1240000, 19000, 0));
        liste.add(new QueteProgression("C5E9",
                "[ELITE] Stage 9 — [Titre a definir]", "Terminez le stage 9 du Chapitre 5 Elite.",
                5, 9, true, 1240000, 19500, 0));
        liste.add(new QueteProgression("C5E10",
                "[ELITE] Stage 10 — [Titre a definir]", "Terminez le stage 10 du Chapitre 5 Elite.",
                5, 10, true, 1030000, 23000, 0));

        // ── Chapitre 6 Elite (niv 61 → 63 | total ~37 021 000 XP) ──
        liste.add(new QueteProgression("C6E1",
                "[ELITE] Stage 1 — [Titre a definir]", "Terminez le stage 1 du Chapitre 6 Elite.",
                6, 1, true, 2590000, 21500, 0));
        liste.add(new QueteProgression("C6E2",
                "[ELITE] Stage 2 — [Titre a definir]", "Terminez le stage 2 du Chapitre 6 Elite.",
                6, 2, true, 2960000, 22000, 0));
        liste.add(new QueteProgression("C6E3",
                "[ELITE] Stage 3 — [Titre a definir]", "Terminez le stage 3 du Chapitre 6 Elite.",
                6, 3, true, 3330000, 22500, 0));
        liste.add(new QueteProgression("C6E4",
                "[ELITE] Stage 4 — [Titre a definir]", "Terminez le stage 4 du Chapitre 6 Elite.",
                6, 4, true, 3700000, 23000, 0));
        liste.add(new QueteProgression("C6E5",
                "[ELITE] Stage 5 — [Titre a definir]", "Terminez le stage 5 du Chapitre 6 Elite.",
                6, 5, true, 3700000, 23500, 0));
        liste.add(new QueteProgression("C6E6",
                "[ELITE] Stage 6 — [Titre a definir]", "Terminez le stage 6 du Chapitre 6 Elite.",
                6, 6, true, 4070000, 24000, 0));
        liste.add(new QueteProgression("C6E7",
                "[ELITE] Stage 7 — [Titre a definir]", "Terminez le stage 7 du Chapitre 6 Elite.",
                6, 7, true, 4070000, 24500, 0));
        liste.add(new QueteProgression("C6E8",
                "[ELITE] Stage 8 — [Titre a definir]", "Terminez le stage 8 du Chapitre 6 Elite.",
                6, 8, true, 4440000, 25000, 0));
        liste.add(new QueteProgression("C6E9",
                "[ELITE] Stage 9 — [Titre a definir]", "Terminez le stage 9 du Chapitre 6 Elite.",
                6, 9, true, 4440000, 25500, 0));
        liste.add(new QueteProgression("C6E10",
                "[ELITE] Stage 10 — [Titre a definir]", "Terminez le stage 10 du Chapitre 6 Elite.",
                6, 10, true, 3700000, 29000, 0));

        // ── Chapitre 7 Elite (niv 68 → 69 | total ~60 297 000 XP) ──
        liste.add(new QueteProgression("C7E1",
                "[ELITE] Stage 1 — [Titre a definir]", "Terminez le stage 1 du Chapitre 7 Elite.",
                7, 1, true, 4200000, 27500, 0));
        liste.add(new QueteProgression("C7E2",
                "[ELITE] Stage 2 — [Titre a definir]", "Terminez le stage 2 du Chapitre 7 Elite.",
                7, 2, true, 4800000, 28000, 0));
        liste.add(new QueteProgression("C7E3",
                "[ELITE] Stage 3 — [Titre a definir]", "Terminez le stage 3 du Chapitre 7 Elite.",
                7, 3, true, 5400000, 28500, 0));
        liste.add(new QueteProgression("C7E4",
                "[ELITE] Stage 4 — [Titre a definir]", "Terminez le stage 4 du Chapitre 7 Elite.",
                7, 4, true, 6000000, 29000, 0));
        liste.add(new QueteProgression("C7E5",
                "[ELITE] Stage 5 — [Titre a definir]", "Terminez le stage 5 du Chapitre 7 Elite.",
                7, 5, true, 6000000, 29500, 0));
        liste.add(new QueteProgression("C7E6",
                "[ELITE] Stage 6 — [Titre a definir]", "Terminez le stage 6 du Chapitre 7 Elite.",
                7, 6, true, 6600000, 30000, 0));
        liste.add(new QueteProgression("C7E7",
                "[ELITE] Stage 7 — [Titre a definir]", "Terminez le stage 7 du Chapitre 7 Elite.",
                7, 7, true, 6600000, 30500, 0));
        liste.add(new QueteProgression("C7E8",
                "[ELITE] Stage 8 — [Titre a definir]", "Terminez le stage 8 du Chapitre 7 Elite.",
                7, 8, true, 7200000, 31000, 0));
        liste.add(new QueteProgression("C7E9",
                "[ELITE] Stage 9 — [Titre a definir]", "Terminez le stage 9 du Chapitre 7 Elite.",
                7, 9, true, 7200000, 31500, 0));
        liste.add(new QueteProgression("C7E10",
                "[ELITE] Stage 10 — [Titre a definir]", "Terminez le stage 10 du Chapitre 7 Elite.",
                7, 10, true, 6000000, 35000, 0));

        // ── Chapitre 8 Elite (moitie du niveau 73 | total ~75 019 000 XP) ──
        liste.add(new QueteProgression("C8E1",
                "[ELITE] Stage 1 — [Titre a definir]", "Terminez le stage 1 du Chapitre 8 Elite.",
                8, 1, true, 5300000, 33500, 0));
        liste.add(new QueteProgression("C8E2",
                "[ELITE] Stage 2 — [Titre a definir]", "Terminez le stage 2 du Chapitre 8 Elite.",
                8, 2, true, 6000000, 34000, 0));
        liste.add(new QueteProgression("C8E3",
                "[ELITE] Stage 3 — [Titre a definir]", "Terminez le stage 3 du Chapitre 8 Elite.",
                8, 3, true, 6800000, 34500, 0));
        liste.add(new QueteProgression("C8E4",
                "[ELITE] Stage 4 — [Titre a definir]", "Terminez le stage 4 du Chapitre 8 Elite.",
                8, 4, true, 7500000, 35000, 0));
        liste.add(new QueteProgression("C8E5",
                "[ELITE] Stage 5 — [Titre a definir]", "Terminez le stage 5 du Chapitre 8 Elite.",
                8, 5, true, 7500000, 35500, 0));
        liste.add(new QueteProgression("C8E6",
                "[ELITE] Stage 6 — [Titre a definir]", "Terminez le stage 6 du Chapitre 8 Elite.",
                8, 6, true, 8300000, 36000, 0));
        liste.add(new QueteProgression("C8E7",
                "[ELITE] Stage 7 — [Titre a definir]", "Terminez le stage 7 du Chapitre 8 Elite.",
                8, 7, true, 8300000, 36500, 0));
        liste.add(new QueteProgression("C8E8",
                "[ELITE] Stage 8 — [Titre a definir]", "Terminez le stage 8 du Chapitre 8 Elite.",
                8, 8, true, 9000000, 37000, 0));
        liste.add(new QueteProgression("C8E9",
                "[ELITE] Stage 9 — [Titre a definir]", "Terminez le stage 9 du Chapitre 8 Elite.",
                8, 9, true, 9000000, 37500, 0));
        liste.add(new QueteProgression("C8E10",
                "[ELITE] Stage 10 — [Titre a definir]", "Terminez le stage 10 du Chapitre 8 Elite.",
                8, 10, true, 7500000, 41000, 0));

        // ── Chapitre 9 Elite (moitie du niveau 77 | total ~155 559 000 XP) ──
        liste.add(new QueteProgression("C9E1",
                "[ELITE] Stage 1 — [Titre a definir]", "Terminez le stage 1 du Chapitre 9 Elite.",
                9, 1, true, 10900000, 39500, 0));
        liste.add(new QueteProgression("C9E2",
                "[ELITE] Stage 2 — [Titre a definir]", "Terminez le stage 2 du Chapitre 9 Elite.",
                9, 2, true, 12400000, 40000, 0));
        liste.add(new QueteProgression("C9E3",
                "[ELITE] Stage 3 — [Titre a definir]", "Terminez le stage 3 du Chapitre 9 Elite.",
                9, 3, true, 14000000, 40500, 0));
        liste.add(new QueteProgression("C9E4",
                "[ELITE] Stage 4 — [Titre a definir]", "Terminez le stage 4 du Chapitre 9 Elite.",
                9, 4, true, 15600000, 41000, 0));
        liste.add(new QueteProgression("C9E5",
                "[ELITE] Stage 5 — [Titre a definir]", "Terminez le stage 5 du Chapitre 9 Elite.",
                9, 5, true, 15600000, 41500, 0));
        liste.add(new QueteProgression("C9E6",
                "[ELITE] Stage 6 — [Titre a definir]", "Terminez le stage 6 du Chapitre 9 Elite.",
                9, 6, true, 17100000, 42000, 0));
        liste.add(new QueteProgression("C9E7",
                "[ELITE] Stage 7 — [Titre a definir]", "Terminez le stage 7 du Chapitre 9 Elite.",
                9, 7, true, 17100000, 42500, 0));
        liste.add(new QueteProgression("C9E8",
                "[ELITE] Stage 8 — [Titre a definir]", "Terminez le stage 8 du Chapitre 9 Elite.",
                9, 8, true, 18700000, 43000, 0));
        liste.add(new QueteProgression("C9E9",
                "[ELITE] Stage 9 — [Titre a definir]", "Terminez le stage 9 du Chapitre 9 Elite.",
                9, 9, true, 18700000, 43500, 0));
        liste.add(new QueteProgression("C9E10",
                "[ELITE] Stage 10 — [Titre a definir]", "Terminez le stage 10 du Chapitre 9 Elite.",
                9, 10, true, 15600000, 47000, 0));

        // ── Chapitre 10 Elite (moitie du niveau 82 | total ~387 082 000 XP) ──
        liste.add(new QueteProgression("C10E1",
                "[ELITE] Stage 1 — [Titre a definir]", "Terminez le stage 1 du Chapitre 10 Elite.",
                10, 1, true, 27100000, 45500, 0));
        liste.add(new QueteProgression("C10E2",
                "[ELITE] Stage 2 — [Titre a definir]", "Terminez le stage 2 du Chapitre 10 Elite.",
                10, 2, true, 31000000, 46000, 0));
        liste.add(new QueteProgression("C10E3",
                "[ELITE] Stage 3 — [Titre a definir]", "Terminez le stage 3 du Chapitre 10 Elite.",
                10, 3, true, 34800000, 46500, 0));
        liste.add(new QueteProgression("C10E4",
                "[ELITE] Stage 4 — [Titre a definir]", "Terminez le stage 4 du Chapitre 10 Elite.",
                10, 4, true, 38700000, 47000, 0));
        liste.add(new QueteProgression("C10E5",
                "[ELITE] Stage 5 — [Titre a definir]", "Terminez le stage 5 du Chapitre 10 Elite.",
                10, 5, true, 38700000, 47500, 0));
        liste.add(new QueteProgression("C10E6",
                "[ELITE] Stage 6 — [Titre a definir]", "Terminez le stage 6 du Chapitre 10 Elite.",
                10, 6, true, 42600000, 48000, 0));
        liste.add(new QueteProgression("C10E7",
                "[ELITE] Stage 7 — [Titre a definir]", "Terminez le stage 7 du Chapitre 10 Elite.",
                10, 7, true, 42600000, 48500, 0));
        liste.add(new QueteProgression("C10E8",
                "[ELITE] Stage 8 — [Titre a definir]", "Terminez le stage 8 du Chapitre 10 Elite.",
                10, 8, true, 46400000, 49000, 0));
        liste.add(new QueteProgression("C10E9",
                "[ELITE] Stage 9 — [Titre a definir]", "Terminez le stage 9 du Chapitre 10 Elite.",
                10, 9, true, 46400000, 49500, 0));
        liste.add(new QueteProgression("C10E10",
                "[ELITE] Stage 10 — [Titre a definir]", "Terminez le stage 10 du Chapitre 10 Elite.",
                10, 10, true, 38700000, 53000, 0));

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
