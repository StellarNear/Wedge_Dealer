package stellarnear.wedge_companion.Spells;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import stellarnear.wedge_companion.ContactAlertDialog;
import stellarnear.wedge_companion.CustomAlertDialog;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.TestRMAlertDialog;
import stellarnear.wedge_companion.Tools;

public class SpellProfileManager {
    private Activity mA;
    private Context mC;
    private Spell spell;
    private View profile;
    private ViewFlipper panel;
    private Boolean resultDisplayed =false; //pour aps revenir au panneau central si le sort a ses dégats affiché
    private CustomAlertDialog metaPopup;
    private Tools tools=new Tools();
    private SliderBuilder sliderBuild;
    private OnRefreshEventListener mListener;

    public SpellProfileManager(Activity mA, Context mC, Spell spell,View profileView){
        this.mA=mA;
        this.mC=mC;
        this.spell=spell;
        profile = profileView;
        panel = ((ViewFlipper)profile.findViewById(R.id.view_flipper));
        panel.setDisplayedChild(0);
        buildProfileMechanisms();
        if(spell.isCast()){
            displayResult();
        }
    }

    private void buildProfileMechanisms(){
        if(spell.isFailed() || spell.contactFailed()){
            triggerFail();
        }

        if(spell.hasPassedRM() || !spell.hasRM()){
            profile.findViewById(R.id.sr_test_img).setVisibility(View.GONE);
            profile.findViewById(R.id.sr_test_sepa).setVisibility(View.GONE);
        } else {
            ((ImageView) profile.findViewById(R.id.sr_test_img)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TestRMAlertDialog testAlert = new TestRMAlertDialog(mA, mC, spell);
                    testAlert.setRefreshEventListener(new TestRMAlertDialog.OnRefreshEventListener() {
                        @Override
                        public void onEvent() {
                            buildProfileMechanisms();
                            if (mListener != null) { mListener.onEvent();  }
                        }
                    });
                    testAlert.showAlertDialog();
                }
            });
        }

        //metamagie
        ((LinearLayout)profile.findViewById(R.id.metamagic)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(metaPopup==null){makeMetaPopup(spell);}
                showMetaPopup();
            }
        });
        if(spell.isCast()){
            ((LinearLayout)profile.findViewById(R.id.metamagic)).setEnabled(false);
            ((TextView)profile.findViewById(R.id.text_meta)).setTextColor(Color.GRAY);
            ((ImageView)profile.findViewById(R.id.symbol_meta)).getDrawable().mutate().setColorFilter(Color.GRAY,PorterDuff.Mode.SRC_IN);
        }

        //Slider
        if(sliderBuild==null) {
            sliderBuild = new SliderBuilder(mC, spell);
            sliderBuild.setSlider((SeekBar) profile.findViewById(R.id.slider));
            sliderBuild.setCastEventListener(new SliderBuilder.OnCastEventListener() {
                @Override
                public void onEvent() {
                    displayResult();
                    if (mListener != null) {
                        mListener.onEvent();
                    }
                }
            });
        }

        //sort contact
        if(spell.getContact().equalsIgnoreCase("")){
            ((LinearLayout)profile.findViewById(R.id.contact)).setVisibility(View.GONE);
        } else {
            ((LinearLayout)profile.findViewById(R.id.contact)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ContactAlertDialog contactDialog = new ContactAlertDialog(mA, mC,spell);
                    contactDialog.showAlertDialog();
                    contactDialog.setRefreshEventListener(new ContactAlertDialog.OnRefreshEventListener() {
                        @Override
                        public void onEvent() {
                            buildProfileMechanisms();
                            ((LinearLayout)profile.findViewById(R.id.contact)).setVisibility(View.GONE);
                            if(mListener!=null){mListener.onEvent();}
                        }
                    });
                }
            });
        }
    }

    private void displayResult() {
        ((LinearLayout) profile.findViewById(R.id.result_panel)).removeAllViews();
        new ResultBuilder(mA, mC, spell).addResults((LinearLayout) profile.findViewById(R.id.result_panel));
        resultDisplayed = true;
        movePanelToDmg();
    }

    public void triggerFail() {
        ((LinearLayout)profile.findViewById(R.id.result_panel)).removeAllViews();
        TextView txt_view = new TextView(mC);
        txt_view.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        String message = "Le sort a raté..." ;
        txt_view.setText(message);
        txt_view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        ((LinearLayout)profile.findViewById(R.id.result_panel)).addView(txt_view);
        sliderBuild.spendCast();
        resultDisplayed =true;
        movePanelToDmg();
    }

    private void movePanelToDmg() {
            Animation in=AnimationUtils.loadAnimation(mC, R.anim.infromright);Animation out=AnimationUtils.loadAnimation(mC, R.anim.outtoleft);
            panel.clearAnimation();
            panel.setInAnimation(in);
            panel.setOutAnimation(out);
            panel.setDisplayedChild(1);
    }

    private void makeMetaPopup(Spell spell) {
        LayoutInflater inflate = mA.getLayoutInflater();
        final View mainView = inflate.inflate(R.layout.metamagie_dialog, null);
        LinearLayout mainLin = mainView.findViewById(R.id.metamagie_main_linear);
        mainLin.removeAllViews();
        for (final Metamagic meta : spell.getMetaList().asList()) {
            LinearLayout metaLin = new LinearLayout(mC);
            metaLin.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            metaLin.setGravity(Gravity.CENTER);
            CheckBox check = spell.getCheckboxeForMetaId(mA,mC,meta.getId());

            check.setTextColor(mC.getColor(R.color.darker_gray));
            ViewGroup parent = (ViewGroup) check.getParent();
            if (parent != null) {
                parent.removeView(check);
            }
            meta.setRefreshEventListener(new Metamagic.OnRefreshEventListener() {
                @Override
                public void onEvent() {
                    buildProfileMechanisms();
                    if(mListener!=null){mListener.onEvent();}
                }
            });
            metaLin.addView(check);

            ImageButton image = new ImageButton(mC);
            image.setImageDrawable(mC.getDrawable(R.drawable.ic_add_circle_outline_black_24dp));
            LinearLayout.LayoutParams para = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            image.setLayoutParams(para);
            image.setForegroundGravity(Gravity.CENTER);
            image.setPadding(15, 0, 0, 0);
            image.setColorFilter(Color.GRAY);
            image.setBackgroundColor(mC.getColor(R.color.transparent));
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tools.customToast(mC,meta.getDescription(),"center");
                }
            });

            ViewGroup parentImg = (ViewGroup) image.getParent();
            if (parentImg != null) {
                parentImg.removeView(image);
            }
            metaLin.addView(image);
            mainLin.addView(metaLin);
        }
        metaPopup = new CustomAlertDialog(mA, mC, mainView);
        metaPopup.setPermanent(true);
        metaPopup.clickToHide(mainView.findViewById(R.id.metamagie_back));
    }

    private void showMetaPopup(){
        metaPopup.showAlert();
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
