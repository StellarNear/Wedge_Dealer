package stellarnear.wedge_companion.FormSpell;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.ViewFlipper;

import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Tools;

public class FormSpellProfileManager {
    private Activity mA;
    private Context mC;
    private FormPower spell;
    private View profile;
    private ViewFlipper panel;
    private Boolean resultDisplayed =false; //pour aps revenir au panneau central si le sort a ses dégats affiché
    private Tools tools=Tools.getTools();
    private FormSliderBuilder sliderBuild;
    private OnRefreshEventListener mListener;

    public FormSpellProfileManager(Activity mA, Context mC, FormPower spell, View profileView){
        this.mA=mA;
        this.mC=mC;
        this.spell=spell;
        profile = profileView;
        panel = ((ViewFlipper)profile.findViewById(R.id.view_flipper));
        buildProfileMechanisms();
        panel.setDisplayedChild(0);
    }

    private void buildProfileMechanisms(){
        //Slider
        if(sliderBuild==null) {
            sliderBuild = new FormSliderBuilder(mC, spell);
            sliderBuild.setSlider((SeekBar) profile.findViewById(R.id.slider));
            sliderBuild.setCastEventListener(new FormSliderBuilder.OnCastEventListener() {
                @Override
                public void onEvent() {
                    ((LinearLayout) profile.findViewById(R.id.result_panel)).removeAllViews();
                    new FormResultBuilder(mA, mC, spell).addResults((LinearLayout) profile.findViewById(R.id.result_panel));
                    resultDisplayed = true;
                    movePanelToDmg();
                    if (mListener != null) {
                        mListener.onEvent();
                    }
                }
            });
        }
    }

    private void movePanelToDmg() {
        Animation out=null;
        Animation in = AnimationUtils.loadAnimation(mC, R.anim.infromright);
        panel.clearAnimation();
        panel.setInAnimation(in);
        panel.setOutAnimation(out);
        panel.setDisplayedChild(1);
    }

    public void refreshProfileMechanisms() {
        buildProfileMechanisms();
    }

    public boolean isDone() {
        return resultDisplayed;
    }

    public interface OnRefreshEventListener {
        void onEvent();
    }

    public void setRefreshEventListener(OnRefreshEventListener eventListener) {
        mListener = eventListener;
    }
}
