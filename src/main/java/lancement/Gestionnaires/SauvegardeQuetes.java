package lancement.Gestionnaires;

import lancement.GameContext;
import lancement.SauvegardeData;
import lancement.Quetes.QueteJournaliere;
import lancement.Quetes.QueteProgression;
import java.util.ArrayList;
import java.util.List;

/** Sauvegarde/restauration des quetes journalieres et de progression. */
public class SauvegardeQuetes {

    public static void sauvegarder(GameContext ctx, SauvegardeData data) {
        GestionnaireQuetes gq = ctx.gestionnaireQuetes;
        data.dernierRenouvellementQuete = gq.getDernierRenouvellement().toString();
        data.indexQueteJournaliere      = gq.getIndexQueteJournaliere();
        data.quetesJournalieresActives  = new ArrayList<>();
        for (QueteJournaliere qj : gq.getQuetesJournalieres()) {
            SauvegardeData.QueteJournaliereData qjd = new SauvegardeData.QueteJournaliereData();
            qjd.id            = qj.getId();
            qjd.titre         = qj.getTitre();
            qjd.description   = qj.getDescription();
            qjd.typeObjectif  = qj.getTypeObjectif().name();
            qjd.objectifCible = qj.getObjectifCible();
            qjd.recompenseXP  = qj.getRecompenseXP();
            qjd.recompenseOr  = qj.getRecompenseOr();
            qjd.progression   = qj.getProgressionValeur();
            qjd.completee     = qj.isCompletee();
            qjd.reclamee      = qj.isReclamee();
            data.quetesJournalieresActives.add(qjd);
        }
        data.pointsJournaliers       = gq.getPointsJournaliers();
        data.barreJournaliereReclame = gq.getBarreJournaliereReclame();
        for (QueteProgression q : gq.getToutesQuetesProgression()) {
            if (q.isReclamee())  data.quetesProgressionReclamees.add(q.getId());
            if (q.isCompletee()) data.quetesProgressionCompletees.add(q.getId());
            if (q.isAcceptee())  data.quetesProgressionAcceptees.add(q.getId());
        }
    }

    public static void restaurer(GameContext ctx, SauvegardeData data) {
        GestionnaireQuetes gq = ctx.gestionnaireQuetes;
        if (data.dernierRenouvellementQuete != null)
            gq.setDernierRenouvellement(java.time.LocalDate.parse(data.dernierRenouvellementQuete));
        gq.setIndexQueteJournaliere(data.indexQueteJournaliere);

        if (data.quetesJournalieresActives != null && !data.quetesJournalieresActives.isEmpty()) {
            List<QueteJournaliere> restaurees = new ArrayList<>();
            for (SauvegardeData.QueteJournaliereData qjd : data.quetesJournalieresActives) {
                QueteJournaliere qj = new QueteJournaliere(
                    qjd.id, qjd.titre, qjd.description,
                    QueteJournaliere.TypeObjectif.valueOf(qjd.typeObjectif),
                    qjd.objectifCible, qjd.recompenseXP, qjd.recompenseOr,
                    GestionnaireQuetes.recompensesItemsPourId(qjd.id)
                );
                qj.ajouterProgression(qjd.progression);
                qj.setCompletee(qjd.completee);
                qj.setReclamee(qjd.reclamee);
                restaurees.add(qj);
            }
            gq.setQuetesJournalieres(restaurees);
        }
        gq.setPointsJournaliers(data.pointsJournaliers);
        gq.setBarreJournaliereReclame(data.barreJournaliereReclame);

        for (QueteProgression q : gq.getToutesQuetesProgression()) {
            if (data.quetesProgressionReclamees.contains(q.getId())) {
                q.setCompletee(true); q.setReclamee(true); q.setAcceptee(true);
            } else if (data.quetesProgressionCompletees.contains(q.getId())) {
                q.setCompletee(true); q.setAcceptee(true);
            } else if (data.quetesProgressionAcceptees.contains(q.getId())) {
                q.setAcceptee(true);
            } else if (gq.stageDebloque(ctx, q.getChapitreRequis(), q.getStageRequis(), q.isElite())) {
                // Compatibilite retroactive : ce verrou de quete n'existait pas avant cette mise
                // a jour, donc un stage deja debloque dans une sauvegarde existante reste jouable
                // (la quete associee est consideree comme deja acceptee).
                q.setAcceptee(true);
            }
        }
    }
}
