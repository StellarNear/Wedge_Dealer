package stellarnear.wedge_companion.FormSpell;

import android.content.Context;
import android.widget.SeekBar;

import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.PostData;
import stellarnear.wedge_companion.PostDataElement;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Tools;


public class FormSliderBuilder {
    private Perso pj = PersoManager.getCurrentPJ();
    private FormPower spell;
    private Context mC;
    private OnCastEventListener mListener;
    private SeekBar seek;
    private Tools tools=new Tools();

    public FormSliderBuilder(Context mC, FormPower spell){
        this.mC=mC;
        this.spell=spell;
    }

    public void setSlider(final SeekBar seek){
        this.seek=seek;
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBar.getProgress() > 75) {
                    seekBar.setProgress(100);
                        startCasting();
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
        spendCast();
        mListener.onEvent();
    }

    public void spendCast() {
        if(!spell.isCast()) {
            spell.cast();
            new PostData(mC,new PostDataElement(spell)); // pour les sous sorts on peut avoir un sous sort pas lancé mais pas posté
        }
    }

    public interface OnCastEventListener {
        void onEvent();
    }

    public void setCastEventListener(OnCastEventListener eventListener) {
        mListener = eventListener;
    }
}
