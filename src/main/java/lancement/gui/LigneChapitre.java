package lancement.gui;

import java.util.function.BiFunction;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import lancement.GameContext;
import lancement.Stage;

/** Decrit une ligne de la liste des chapitres (normal ou elite), independamment de la classe Chapitre* concernee. */
public record LigneChapitre(
        String label,
        boolean deverrouille,
        String messageVerrouille,
        int numeroChapitre,
        boolean elite,
        Supplier<boolean[]> stagesReussis,
        Supplier<boolean[]> stagesDebloques,
        IntFunction<String> titreStage,
        BiFunction<GameContext, Integer, Stage.ResultatStage> lancerStage
) {}
