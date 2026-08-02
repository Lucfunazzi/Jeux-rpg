package lancement.Gestionnaires;

import lancement.Chapitres.Chapitre;
import lancement.ChapitreElite.Chapitre3Elite;
import lancement.ChapitreElite.ChapitreElite;
import lancement.GameContext;
import lancement.SauvegardeData;
import java.util.List;
import java.util.Map;

/** Sauvegarde/restauration de la progression des chapitres normaux et elite (1-13). */
public class SauvegardeChapitres {

    public static void sauvegarder(GameContext ctx, SauvegardeData data) {
        for (int i = 1; i <= ctx.chapitres.size(); i++) {
            Chapitre c = ctx.chapitres.get(i - 1);
            if (c == null) continue;
            data.chapitresDebloques.put("c" + i, c.getStagesDebloques().clone());
            data.chapitresReussis.put("c" + i, c.getStagesReussis().clone());
        }
        for (int i = 1; i <= ctx.chapitresElite.size(); i++) {
            ChapitreElite c = ctx.chapitresElite.get(i - 1);
            if (c == null) continue;
            data.chapitresEliteDebloques.put("c" + i, c.getStagesDebloques().clone());
            data.chapitresEliteReussis.put("c" + i, c.getStagesReussis().clone());
        }
        if (ctx.chapitre3Elite != null) {
            copierTableaux(ctx.chapitre3Elite.getPremiereVictoire(), data.chapitre3ElitePremiereVictoire);
        }
    }

    /** Restaure les 13 chapitres normaux depuis les maps generiques (cle = "c" + numero de chapitre). */
    public static void restaurerChapitres(List<Chapitre> chapitres,
                                           Map<String, boolean[]> debloquesMap,
                                           Map<String, boolean[]> reussisMap) {
        for (int i = 1; i <= chapitres.size(); i++) {
            Chapitre c = chapitres.get(i - 1);
            boolean[] d = debloquesMap.get("c" + i);
            boolean[] r = reussisMap.get("c" + i);
            if (d != null) c.setStagesDebloques(d);
            if (r != null) c.setStagesReussis(r);
        }
    }

    /** Restaure les 13 chapitres elite depuis les maps generiques (cle = "c" + numero de chapitre). */
    public static void restaurerChapitresElite(List<ChapitreElite> chapitresElite,
                                                Map<String, boolean[]> debloquesMap,
                                                Map<String, boolean[]> reussisMap) {
        for (int i = 1; i <= chapitresElite.size(); i++) {
            ChapitreElite c = chapitresElite.get(i - 1);
            boolean[] d = debloquesMap.get("c" + i);
            boolean[] r = reussisMap.get("c" + i);
            if (d != null) c.setStagesDebloques(d);
            if (r != null) c.setStagesReussis(r);
        }
    }

    /** Specifique au Chapitre 3 Elite : restaure le suivi de premiere victoire (drop de fragments). */
    public static void restaurerChapitre3ElitePremiereVictoire(Chapitre3Elite c, SauvegardeData data) {
        if (data.chapitre3ElitePremiereVictoire != null) c.setPremiereVictoire(data.chapitre3ElitePremiereVictoire);
    }

    private static void copierTableaux(boolean[] src, boolean[] dst) {
        for (int i = 0; i < src.length && i < dst.length; i++) dst[i] = src[i];
    }
}
