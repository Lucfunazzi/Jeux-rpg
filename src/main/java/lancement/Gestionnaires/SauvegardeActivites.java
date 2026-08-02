package lancement.Gestionnaires;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lancement.GameContext;
import lancement.SauvegardeData;

/** Sauvegarde/restauration des activites a compteur/reset : energie, donjon, chasse au tresor, examen de rang S. */
public class SauvegardeActivites {

    public static void sauvegarder(GameContext ctx, SauvegardeData data) {
        GestionnaireEnergie ge = ctx.gestionnaireEnergie;
        data.energie                 = ge.getEnergie();
        data.derniereRechargeEnergie = ge.getDerniereRecharge().toString();
        data.rechargesUtilisees      = ge.getRechargesUtilisees();
        data.dernierResetRecharge    = ge.getDernierResetRecharge().toString();
        data.runsEliteParStage       = ge.getRunsEliteParStage();
        data.dernierResetRunsElite   = ge.getDernierResetRunsElite().toString();

        data.donjonRuns         = ctx.gestionnaireDonjon.getRuns();
        data.donjonDernierReset = ctx.gestionnaireDonjon.getDernierReset().toString();

        data.chasseTresorFouilles     = ctx.gestionnaireChasseTresor.getFouillesUtilisees();
        data.chasseTresorDernierReset = ctx.gestionnaireChasseTresor.getDernierReset().toString();

        data.examenSDejaReussi     = ctx.gestionnaireExamenS.getDejaReussi();
        data.examenSFaitAujourdhui = ctx.gestionnaireExamenS.getFaitAujourdhui();
        data.examenSDernierReset   = ctx.gestionnaireExamenS.getDernierReset().toString();
    }

    public static void restaurerEnergie(GestionnaireEnergie ge, SauvegardeData data) {
        ge.setEnergie(data.energie);
        if (data.derniereRechargeEnergie != null)
            ge.setDerniereRecharge(LocalDateTime.parse(data.derniereRechargeEnergie));
        ge.setRechargesUtilisees(data.rechargesUtilisees);
        if (data.dernierResetRecharge != null)
            ge.setDernierResetRecharge(LocalDate.parse(data.dernierResetRecharge));
        if (data.runsEliteParStage != null)
            ge.setRunsEliteParStage(data.runsEliteParStage);
        if (data.dernierResetRunsElite != null)
            ge.setDernierResetRunsElite(LocalDate.parse(data.dernierResetRunsElite));
    }

    public static void restaurerDonjon(GestionnaireDonjon gd, SauvegardeData data) {
        if (data.donjonRuns != null)         gd.setRuns(data.donjonRuns);
        if (data.donjonDernierReset != null) gd.setDernierReset(LocalDate.parse(data.donjonDernierReset));
    }

    public static void restaurerChasseTresor(GestionnaireChasseTresor gc, SauvegardeData data) {
        gc.setFouillesUtilisees(data.chasseTresorFouilles);
        if (data.chasseTresorDernierReset != null) gc.setDernierReset(LocalDate.parse(data.chasseTresorDernierReset));
    }

    public static void restaurerExamenS(GestionnaireExamenS ge, SauvegardeData data) {
        if (data.examenSDejaReussi != null)     ge.setDejaReussi(data.examenSDejaReussi);
        if (data.examenSFaitAujourdhui != null) ge.setFaitAujourdhui(data.examenSFaitAujourdhui);
        if (data.examenSDernierReset != null)   ge.setDernierReset(LocalDate.parse(data.examenSDernierReset));
    }
}
