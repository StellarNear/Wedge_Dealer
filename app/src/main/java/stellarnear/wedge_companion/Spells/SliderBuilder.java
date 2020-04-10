package stellarnear.wedge_companion.Spells;

import android.content.Context;
import android.widget.SeekBar;

import com.google.android.material.snackbar.Snackbar;

import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.PostData;
import stellarnear.wedge_companion.PostDataElement;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Tools;


public class SliderBuilder {
    private Perso pj = PersoManager.getCurrentPJ();
    private Spell spell;
    private Context mC;
    private boolean slided = false;
    private CalculationSpell calculationSpell;
    private OnCastEventListener mListener;
    private SeekBar seek;
    private Tools tools = Tools.getTools();

    public SliderBuilder(Context mC, Spell spell) {
        this.mC = mC;
        this.spell = spell;
        calculationSpell = new CalculationSpell();
    }

    public void setSlider(final SeekBar seek) {
        this.seek = seek;
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBar.getProgress() > 75) {
                    seekBar.setProgress(100);
                    if (spell.getRank() != 0 && pj.getCurrentResourceValue("spell_rank_" + calculationSpell.currentRank(spell)) < 1) {
                        seekBar.setProgress(1);
                        tools.customToast(mC, "Tu n'as pas d'emplacement de sort " + calculationSpell.currentRank(spell) + " de disponible...", "center");
                    } else if (!spell.isCast() && spell.isMyth() && pj.getCurrentResourceValue("resource_mythic_points") < 1) {
                        seekBar.setProgress(1);
                        tools.customToast(mC, "Il ne te reste aucun point mythique pour lancer ce sort Mythique", "center");
                    } else {
                        startCasting();
                    }
                } else {
                    seekBar.setProgress(1);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                if (progress > 75) {
                    seekBar.setThumb(mC.getDrawable(R.drawable.thumb_select));
                } else {
                    seekBar.setThumb(mC.getDrawable(R.drawable.thumb_unselect));
                }
            }
        });
    }

    private void startCasting() {
        mListener.onEvent(); //build les resultat à faire avant de spend le cast car ca PostEvent (et il faut avoir setDMg dans le spell
        spendCast();
        Snackbar.make(seek, "Lancement du sort : " + spell.getName(), Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    public void spendCast() {
        if (!spell.isCast()) {
            slided = true;

            pj.castSpell(spell);

            if (spell.isMyth()) {
                pj.getAllResources().getResource("resource_mythic_points").spend(1);
                new PostData(mC, new PostDataElement("Lancement sort mythique", "-1pt mythique"));
                tools.customToast(mC, "Sort Mythique\nIl te reste " + pj.getCurrentResourceValue("resource_mythic_points") + " point(s) mythique(s)", "center");
            }
        } else if (!slided) {
            slided = true;
            new PostData(mC, new PostDataElement(spell)); // pour les sous sorts on peut avoir un sous sort pas lancé mais pas posté
        }
    }

    public void setCastEventListener(OnCastEventListener eventListener) {
        mListener = eventListener;
    }

    public interface OnCastEventListener {
        void onEvent();
    }
}
