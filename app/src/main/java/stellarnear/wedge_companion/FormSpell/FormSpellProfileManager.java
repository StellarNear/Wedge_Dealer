package stellarnear.wedge_companion.FormSpell;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import stellarnear.wedge_companion.CustomAlertDialog;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Tools;

public class FormSpellProfileManager {
    private Activity mA;
    private Context mC;
    private FormPower spell;
    private View profile;
    private ViewFlipper panel;
    private Boolean resultDisplayed =false; //pour aps revenir au panneau central si le sort a ses dégats affiché
    private CustomAlertDialog metaPopup;
    private Tools tools=new Tools();
    private FormSliderBuilder sliderBuild;
    private OnRefreshEventListener mListener;
    private String position="info";

    public FormSpellProfileManager(Activity mA, Context mC, FormPower spell, View profileView){
        this.mA=mA;
        this.mC=mC;
        this.spell=spell;
        profile = profileView;
        panel = ((ViewFlipper)profile.findViewById(R.id.view_flipper));
        buildProfileMechanisms();
        panel.setDisplayedChild(1);
    }

    private void buildProfileMechanisms(){


        if(spell.isCast()){
            ((LinearLayout)profile.findViewById(R.id.metamagic)).setEnabled(false);
            ((TextView)profile.findViewById(R.id.text_meta)).setTextColor(Color.GRAY);
            ((ImageView)profile.findViewById(R.id.symbol_meta)).getDrawable().mutate().setColorFilter(Color.GRAY,PorterDuff.Mode.SRC_IN);
        }

        //Slider
        if(sliderBuild==null) {
            sliderBuild = new FormSliderBuilder(mC, spell);
            sliderBuild.setSlider((SeekBar) profile.findViewById(R.id.slider));
            sliderBuild.setCastEventListener(new FormSliderBuilder.OnCastEventListener() {
                @Override
                public void onEvent() {
                    ((LinearLayout) profile.findViewById(R.id.fourth_panel)).removeAllViews();
                    new FormResultBuilder(mA, mC, spell).addResults((LinearLayout) profile.findViewById(R.id.fourth_panel));
                    resultDisplayed = true;
                    movePanelTo("dmg");
                    if (mListener != null) {
                        mListener.onEvent();
                    }
                }
            });
        }
    }

    private void movePanelTo(String toPosition) {
        if(!this.position.equalsIgnoreCase(toPosition)) {
            Animation in=null;Animation out=null;
            int indexChild=1;
            switch (this.position) {
                case "elem":
                    out = AnimationUtils.loadAnimation(mC, R.anim.outtoleft);
                    break;
                case "info":
                    if(toPosition.equalsIgnoreCase("elem")){
                        out = AnimationUtils.loadAnimation(mC, R.anim.outtoright);
                    } else {
                        out = AnimationUtils.loadAnimation(mC, R.anim.outtoleft);
                    }
                    break;
                case "arcane":
                    out = AnimationUtils.loadAnimation(mC, R.anim.outtoright);
                    break;
            }
            switch (toPosition) {
                case "elem":
                    in = AnimationUtils.loadAnimation(mC, R.anim.infromleft);
                    indexChild=0;
                    break;
                case "info":
                    if(this.position.equalsIgnoreCase("elem")){
                        in = AnimationUtils.loadAnimation(mC, R.anim.infromright);
                    } else {
                        in = AnimationUtils.loadAnimation(mC, R.anim.infromleft);
                    }
                    indexChild=1;
                    break;
                case  "arcane":
                    in = AnimationUtils.loadAnimation(mC, R.anim.infromright);
                    indexChild=2;
                    break;
                case "dmg":
                    in = AnimationUtils.loadAnimation(mC, R.anim.infromright);
                    indexChild=3;
                    break;
            }
            panel.clearAnimation();
            panel.setInAnimation(in);
            panel.setOutAnimation(out);
            panel.setDisplayedChild(indexChild);
            this.position = toPosition;
        }
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
