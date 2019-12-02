package stellarnear.wedge_companion.Activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.util.HashMap;
import java.util.Map;

import stellarnear.wedge_companion.Perso.Form;
import stellarnear.wedge_companion.Perso.FormCapacity;
import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Tools;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragmentForm extends Fragment {
    private Perso pj = PersoManager.getCurrentPJ();
    private Context mC;
    private View returnFragView;
    private ViewFlipper flipper;
    private Map<View,Form> mapViewForm=new HashMap<>();
    private Tools tools=new Tools();
    private float x1Gesture, x2Gesture;
    private long t1Gesture, t2Gesture;
    public MainActivityFragmentForm() {
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (container != null) {
            container.removeAllViews();
        }
        mC=getContext();
        returnFragView = inflater.inflate(R.layout.fragment_main_form, container, false);
        flipper = (ViewFlipper)returnFragView.findViewById(R.id.form_flipper);
        flipper.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x1Gesture = event.getX();
                        t1Gesture = System.currentTimeMillis();
                        return true;
                    case MotionEvent.ACTION_UP:
                        x2Gesture = event.getX();
                        t2Gesture = System.currentTimeMillis();
                        if (x1Gesture == x2Gesture && (t2Gesture - t1Gesture) < 1000) {
                            popUpchangeForm();
                        } else if (x1Gesture > x2Gesture) {
                            flipNext();
                        } else if (x2Gesture > x1Gesture) {
                            flipPrevious();
                        }
                        return true;
                }
                return false;
            }
        });
        ImageButton buttonMain = (ImageButton) returnFragView.findViewById(R.id.button_frag_form_to_main);
        animate(buttonMain);
        buttonMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unlockOrient();
                Fragment fragment = new MainActivityFragment();
                FragmentManager fragmentManager = getActivity().getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.animator.infadefrag,R.animator.outtobotfrag);
                fragmentTransaction.replace(R.id.fragment_main_frame_layout, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        setFillButton(returnFragView.findViewById(R.id.button_form_animal),"animal");
        setFillButton(returnFragView.findViewById(R.id.button_form_magical),"magic");
        setFillButton(returnFragView.findViewById(R.id.button_form_vegetal),"vegetal");
        setFillButton(returnFragView.findViewById(R.id.button_form_elemental),"elemental");

        refreshPageInfos();

        if(pj.getAllForms().hasActiveForm()){
            try {
                int imgId = getResources().getIdentifier(pj.getAllForms().getCurrentForm().getId(), "drawable", getContext().getPackageName());
                ((ImageView)returnFragView.findViewById(R.id.form_current_form_background_image)).setImageDrawable(getResources().getDrawable(imgId));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return returnFragView;
    }


    private void refreshPageInfos() {
        Form form = pj.getAllForms().getCurrentForm();
        String name="Aucune"; if(form!=null){name=form.getName();}
        ((TextView)returnFragView.findViewById(R.id.form_current_form)).setText(name);
        ((TextView)returnFragView.findViewById(R.id.form_remaning_text)).setText(String.valueOf(pj.getResourceValue("resource_animal_form")));
        addCurrentPassiveCapa((LinearLayout)returnFragView.findViewById(R.id.linear_form_passives));
        addCurrentActiveCapa((LinearLayout)returnFragView.findViewById(R.id.linear_form_actives));
    }

    private void setFillButton(View viewById, final String type) {
        viewById.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFillFlipperWithFormType(type);
            }
        });
    }

    private void setFillFlipperWithFormType(String type) {
        flipper.removeAllViews();
        mapViewForm=new HashMap<>();
        for(final Form form :pj.getAllForms().getFormOfType(type)){
            View formView = getActivity().getLayoutInflater().inflate(R.layout.custom_form_info_flipper,flipper,false);
            ((TextView)formView.findViewById(R.id.form_info_textName)).setText(form.getName());
            ((TextView)formView.findViewById(R.id.form_info_textType)).setText("Type :\n"+form.getType());
            ((TextView)formView.findViewById(R.id.form_info_textSize)).setText("Taille :\n"+form.getSize());
            //((TextView)view.findViewById(R.id.form_info_textResi)).setText("Résistance : "+form.getResistanceTxt());
            //TODO ((TextView)view.findViewById(R.id.form_info_textVuln)).setText("Vunérabilité : "+form.getVulnerabilityTxt());
            try {
                int imgId = getResources().getIdentifier(form.getId(), "drawable", getContext().getPackageName());
                ((ImageView)formView.findViewById(R.id.form_info_image)).setImageDrawable(mC.getDrawable(imgId));
            } catch (Exception e) {
                e.printStackTrace();
            }
            mapViewForm.put(formView,form);
            flipper.addView(formView);
        }
    }

    private void flipNext() {
        flipper.clearAnimation();
        Animation in = AnimationUtils.loadAnimation(mC,R.anim.infromright);
        Animation out =  AnimationUtils.loadAnimation(mC,R.anim.outtoleft);
        flipper.setInAnimation(in);  flipper.setOutAnimation(out);
        flipper.showNext();
    }
    private void flipPrevious() {
        flipper.clearAnimation();
        Animation in = AnimationUtils.loadAnimation(mC,R.anim.infromleft);
        Animation out =  AnimationUtils.loadAnimation(mC,R.anim.outtoright);
        flipper.setInAnimation(in);  flipper.setOutAnimation(out);
        flipper.showPrevious();
    }

    private void popUpchangeForm() {
        final Form form = mapViewForm.get(flipper.getCurrentView());
        if(form!=null) {
            new AlertDialog.Builder(getActivity())
                    .setIcon(R.drawable.ic_warning_black_24dp)
                    .setTitle("Changement de forme")
                    .setMessage("Veux tu changer ta forme en " + form.getName() + " ?"
                            + "\n\nHors combat en utilisant l'orbe de pooka (1min)" +
                            "\nEn consomant un changement de forme (restant : " + pj.getResourceValue("resource_animal_form") + ")")
                    .setNeutralButton("Annuler", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setPositiveButton("Normal", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(pj.getResourceValue("resource_animal_form")>0) {
                                pj.getAllResources().getResource("resource_animal_form").spend(1);
                                pj.getAllForms().changeFormTo(form);
                                refreshPageInfos();
                            } else {
                                tools.customToast(mC,"Tu n'as plus d'utilisation de forme animale disponible...");
                            }
                        }
                    })
                    .setNegativeButton("Pooka", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            pj.getAllForms().changeFormTo(form);
                            refreshPageInfos();
                        }
                    }).show();
        }
    }


    private void addCurrentPassiveCapa(LinearLayout linear) {
        linear.removeAllViews();
        if(pj.getAllForms().getCurrentForm()==null){
            linear.addView(textViewFormated("Aucune"));
        } else {
            for (final FormCapacity formCapacity : pj.getAllForms().getCurrentForm().getListPassiveCapacities()) {
                TextView text = textViewFormated(formCapacity.getName());
                text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tools.customToast(getContext(), formCapacity.getDescr(), "center");
                    }
                });
                linear.addView(text);
            }
        }
    }

    private TextView textViewFormated(String textTxt) {
        TextView text = new TextView(getContext());
        text.setText(textTxt);
        text.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        text.setGravity(Gravity.CENTER);
        text.setTypeface(null, Typeface.BOLD);
        text.setTextSize(16);
        text.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        return text;
    }

    private void addCurrentActiveCapa(LinearLayout linear) {
        linear.removeAllViews();
        if(pj.getAllForms().getCurrentForm()==null){
            linear.addView(textViewFormated("Aucune"));
        } else {
            for (final FormCapacity formCapacity : pj.getAllForms().getCurrentForm().getListActivesCapacities()) {
                TextView text = textViewFormated(formCapacity.getName());
                text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tools.customToast(getContext(), formCapacity.getDescr(), "center");
                    }
                });
                linear.addView(text);
            }
        }
    }

    private void unlockOrient() {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    }

    private void animate(final ImageButton buttonMain) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Animation anim = new ScaleAnimation(1f,1.25f,1f,1.25f,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                anim.setRepeatCount(1);
                anim.setRepeatMode(Animation.REVERSE);
                anim.setDuration(666);

                buttonMain.startAnimation(anim);
            }
        }, getResources().getInteger(R.integer.translationFragDuration));
    }

}
