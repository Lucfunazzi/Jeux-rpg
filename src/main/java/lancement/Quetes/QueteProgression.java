/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lancement.Quetes;

/**
 *
 * @author Lucas
 */


public class QueteProgression extends Quete {
    private final int chapitreReqis;
    private final int stageRequis;
    private final boolean estElite;

    public QueteProgression(String id, String titre, String description,
                            int chapitreRequis, int stageRequis, boolean estElite,
                            int recompenseXP, int recompenseOr, int recompenseParcheminC) {
        super(id, titre, description, recompenseXP, recompenseOr, recompenseParcheminC);
        this.chapitreReqis = chapitreRequis;
        this.stageRequis   = stageRequis;
        this.estElite      = estElite;
    }

    public int getChapitreRequis() { return chapitreReqis; }
    public int getStageRequis()    { return stageRequis; }
    public boolean isElite()       { return estElite; }

    @Override
    public String getProgression() {
        return completee ? "Termine" : "En cours";
    }
}
