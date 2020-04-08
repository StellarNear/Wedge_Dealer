package stellarnear.wedge_companion.Activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stellarnear.wedge_companion.CustomAlertDialog;
import stellarnear.wedge_companion.Perso.Form;
import stellarnear.wedge_companion.Perso.FormCapacity;
import stellarnear.wedge_companion.Perso.FormCapacityLauncher;
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
    private Map<View, Form> mapViewForm = new HashMap<>();
    private List<ImageView> listImgCategories = new ArrayList<>();
    private Tools tools = Tools.getTools();
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
        mC = getContext();
        returnFragView = inflater.inflate(R.layout.fragment_main_form, container, false);
        flipper = returnFragView.findViewById(R.id.form_flipper);
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
                        if ((Math.abs(x2Gesture - x1Gesture) < 10) && (t2Gesture - t1Gesture) < 500) {
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
        ImageButton buttonMain = returnFragView.findViewById(R.id.button_frag_form_to_main);
        animate(buttonMain);
        buttonMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new MainActivityFragment();
                FragmentManager fragmentManager = getActivity().getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.animator.infadefrag, R.animator.outtobotfrag);
                fragmentTransaction.replace(R.id.fragment_main_frame_layout, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        returnFragView.findViewById(R.id.button_frag_form_unform).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pj.getAllForms().hasActiveForm()) {
                    new AlertDialog.Builder(getActivity())
                            .setIcon(R.drawable.ic_warning_black_24dp)
                            .setTitle("Changement de forme")
                            .setMessage("Veux tu annuler ta forme actuelle : " + pj.getAllForms().getCurrentForm().getName() + " ?")
                            .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    unform();
                                }
                            })
                            .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();
                } else {
                    unform();
                }
            }
        });

        listImgCategories.add((ImageView) returnFragView.findViewById(R.id.button_form_animal));
        listImgCategories.add((ImageView) returnFragView.findViewById(R.id.button_form_magical));
        listImgCategories.add((ImageView) returnFragView.findViewById(R.id.button_form_vegetal));
        listImgCategories.add((ImageView) returnFragView.findViewById(R.id.button_form_elemental));
        setFillButton(listImgCategories.get(0), "animal");
        setFillButton(listImgCategories.get(1), "magic");
        setFillButton(listImgCategories.get(2), "vegetal");
        setFillButton(listImgCategories.get(3), "elemental");
        refreshPageInfos();
        setupMainImagebackStartup();

        return returnFragView;
    }

    private void unform() {
        pj.getAllForms().unform();
        select(null);
        setFillFlipperWithFormType(""); //vide le flipper
        refreshPageInfos();
        setupMainImagebackStartup();
    }

    private void setupMainImagebackStartup() {
        ((TextView) returnFragView.findViewById(R.id.form_modif_carac)).setText(pj.getAllForms().getFormAbiModText());
        if (pj.getAllForms().hasActiveForm() && returnFragView.findViewById(R.id.form_current_form_background_image) != null) {
            try {
                switch (pj.getAllForms().getCurrentForm().getType()) {
                    case "animal":
                        select(listImgCategories.get(0));
                        break;
                    case "magic":
                        select(listImgCategories.get(1));
                        break;
                    case "vegetal":
                        select(listImgCategories.get(2));
                        break;
                    case "elemental":
                        select(listImgCategories.get(3));
                        break;
                }
                int imgId = getResources().getIdentifier(pj.getAllForms().getCurrentForm().getId(), "drawable", getContext().getPackageName());
                ((ImageView) returnFragView.findViewById(R.id.form_current_form_background_image)).setImageDrawable(getResources().getDrawable(imgId));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            ((ImageView) returnFragView.findViewById(R.id.form_current_form_background_image)).setImageDrawable(getResources().getDrawable(R.drawable.wedge_loaded));
            returnFragView.findViewById(R.id.form_current_form_background_image).setVisibility(View.VISIBLE);
        }
    }


    private void refreshPageInfos() {
        Form form = pj.getAllForms().getCurrentForm();
        String name = "Aucune";
        if (form != null) {
            name = form.getName();
        }
        ((TextView) returnFragView.findViewById(R.id.form_current_form)).setText(name);
        ((TextView) returnFragView.findViewById(R.id.form_remaning_text)).setText(String.valueOf(pj.getCurrentResourceValue("resource_animal_form")));
        addCurrentPassiveCapa((LinearLayout) returnFragView.findViewById(R.id.linear_form_passives));
        addCurrentActiveCapa((LinearLayout) returnFragView.findViewById(R.id.linear_form_actives));
        if (form != null && form.getAtkTxt().length() > 0) {
            returnFragView.findViewById(R.id.form_current_atks_lay).setVisibility(View.VISIBLE);
            ((TextView) returnFragView.findViewById(R.id.form_current_atks_text)).setText(form.getAtkTxt());
        } else {
            returnFragView.findViewById(R.id.form_current_atks_lay).setVisibility(View.GONE);
        }
    }

    private void setFillButton(final View viewById, final String type) {
        viewById.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFillFlipperWithFormType(type);
                refreshAbiModifText();
                select(viewById);
            }
        });
    }

    private void select(View viewById) {
        for (ImageView view : listImgCategories) {
            if (view != viewById && viewById != null) { //le different de null pour quand on select rien on réaffiche tout
                ColorMatrix matrix = new ColorMatrix();
                matrix.setSaturation(0);
                ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                view.setColorFilter(filter);
            } else {
                ColorMatrix matrix = new ColorMatrix();
                matrix.setSaturation(1);
                ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                view.setColorFilter(filter);
            }
        }
    }

    private void setFillFlipperWithFormType(String type) {
        flipper.removeAllViews();
        returnFragView.findViewById(R.id.form_current_form_background_image).setVisibility(View.GONE);
        mapViewForm = new HashMap<>();
        for (final Form form : pj.getAllForms().getFormOfType(type)) {
            View formView = getActivity().getLayoutInflater().inflate(R.layout.custom_form_info_flipper, flipper, false);
            ((TextView) formView.findViewById(R.id.form_info_textName)).setText(form.getName());
            ((TextView) formView.findViewById(R.id.form_info_textType)).setText("Type : " + form.getTypeTxt());
            ((TextView) formView.findViewById(R.id.form_info_textSize)).setText("Taille : " + form.getSize());
            if (form.getAtkTxt().length() > 0) {
                ((TextView) formView.findViewById(R.id.form_info_textAtk)).setText(form.getAtkTxt());
            } else {
                formView.findViewById(R.id.form_info_layAtk).setVisibility(View.GONE);
            }
            if (form.getResistsValueLongFormat().length() > 0) {
                ((TextView) formView.findViewById(R.id.form_info_textResi)).setText(form.getResistsValueLongFormat());
            } else {
                formView.findViewById(R.id.form_info_layResi).setVisibility(View.GONE);
            }
            if (form.getVulnerability().length() > 0) {
                ((TextView) formView.findViewById(R.id.form_info_textVuln)).setText(form.getVulnerability());
            } else {
                formView.findViewById(R.id.form_info_layVuln).setVisibility(View.GONE);
            }
            if (form.getListPassiveCapacities().size() > 0) {
                String allCapas = "";
                for (FormCapacity capa : form.getListPassiveCapacities()) {
                    if (!allCapas.equalsIgnoreCase("")) {
                        allCapas += ", ";
                    }
                    allCapas += capa.getName();
                }
                TextView textCapa = new TextView(mC);
                textCapa.setText(allCapas);
                textCapa.setTextSize(10);
                textCapa.setGravity(Gravity.CENTER);
                ((LinearLayout) formView.findViewById(R.id.form_info_passiveCapasLinear)).addView(textCapa);
            } else {
                formView.findViewById(R.id.form_info_passiveCapasLinear).setVisibility(View.GONE);
            }
            if (form.getListActivesCapacities().size() > 0) {
                String allCapas = "";
                for (FormCapacity capa : form.getListActivesCapacities()) {
                    if (!allCapas.equalsIgnoreCase("")) {
                        allCapas += ", ";
                    }
                    allCapas += capa.getName();
                }
                TextView textCapa = new TextView(mC);
                textCapa.setText(allCapas);
                textCapa.setTextSize(10);
                textCapa.setGravity(Gravity.CENTER);
                ((LinearLayout) formView.findViewById(R.id.form_info_activesCapasLinear)).addView(textCapa);
            } else {
                formView.findViewById(R.id.form_info_activesCapasLinear).setVisibility(View.GONE);
            }

            try {
                int imgId = getResources().getIdentifier(form.getId(), "drawable", getContext().getPackageName());
                ((ImageView) formView.findViewById(R.id.form_info_image)).setImageDrawable(mC.getDrawable(imgId));
            } catch (Exception e) {
                e.printStackTrace();
            }
            mapViewForm.put(formView, form);
            flipper.addView(formView);
        }
    }

    private void flipNext() {
        flipper.clearAnimation();
        Animation in = AnimationUtils.loadAnimation(mC, R.anim.infromright);
        Animation out = AnimationUtils.loadAnimation(mC, R.anim.outtoleft);
        flipper.setInAnimation(in);
        flipper.setOutAnimation(out);
        flipper.showNext();
        refreshAbiModifText();
    }

    private void flipPrevious() {
        flipper.clearAnimation();
        Animation in = AnimationUtils.loadAnimation(mC, R.anim.infromleft);
        Animation out = AnimationUtils.loadAnimation(mC, R.anim.outtoright);
        flipper.setInAnimation(in);
        flipper.setOutAnimation(out);
        flipper.showPrevious();
        refreshAbiModifText();
    }

    private void refreshAbiModifText() {
        ((TextView) returnFragView.findViewById(R.id.form_modif_carac)).setText(pj.getAllForms().getFormAbiModText(mapViewForm.get(flipper.getCurrentView())));
    }

    private void popUpchangeForm() {
        final Form form = mapViewForm.get(flipper.getCurrentView());
        if (form != null) {
            new AlertDialog.Builder(getActivity())
                    .setIcon(R.drawable.ic_warning_black_24dp)
                    .setTitle("Changement de forme")
                    .setMessage("Veux tu changer ta forme en " + form.getName() + " ?"
                            + "\n\nHors combat en utilisant l'orbe de pooka (1min)" +
                            "\nEn consomant un changement de forme (restant : " + pj.getCurrentResourceValue("resource_animal_form") + ")")
                    .setNeutralButton("Annuler", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setPositiveButton("Normal", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (pj.getCurrentResourceValue("resource_animal_form") > 0) {
                                pj.getAllResources().getResource("resource_animal_form").spend(1);
                                pj.getAllForms().changeFormTo(form);
                                pj.refresh();
                                refreshPageInfos();
                            } else {
                                tools.customToast(mC, "Tu n'as plus d'utilisation de forme animale disponible...");
                            }
                        }
                    })
                    .setNegativeButton("Pooka", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            pj.getAllForms().changeFormTo(form);
                            pj.refresh();
                            refreshPageInfos();
                        }
                    }).show();
        }
    }


    private void addCurrentPassiveCapa(LinearLayout linear) {
        linear.removeAllViews();
        Form currentForm = pj.getAllForms().getCurrentForm();
        if (currentForm == null) {
            linear.addView(textViewFormated("Aucune"));
        } else {
            for (final FormCapacity formCapacity : currentForm.getListPassiveCapacities()) {
                TextView text = textViewFormated(formCapacity.getName());
                text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupLongTooltip(formCapacity);
                    }
                });
                linear.addView(text);
            }
            if (currentForm.getResistsValueLongFormat().length() > 0) {
                TextView text = textViewFormated("");
                text.setText(currentForm.getResistsValueLongFormat()); //avec spannable pas string
                linear.addView(text);
            }
            if (currentForm.getVulnerability().length() > 0) {
                TextView text = textViewFormated("Vulnérabilité : " + currentForm.getVulnerability());
                linear.addView(text);
            }
        }
    }

    private void popupLongTooltip(final FormCapacity capa) {
        View tooltip = getActivity().getLayoutInflater().inflate(R.layout.custom_toast_info_form_capacity, null);
        final CustomAlertDialog tooltipAlert = new CustomAlertDialog(getActivity(), mC, tooltip);
        tooltipAlert.setPermanent(true);
        ((TextView) tooltip.findViewById(R.id.toast_textName)).setText(capa.getName());
        ((TextView) tooltip.findViewById(R.id.toast_textDescr)).setText(capa.getDescr());
        if (capa.getType().equalsIgnoreCase("active")) {
            tooltip.findViewById(R.id.button_use_capacity).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tooltipAlert.dismissAlert();
                    launchCapacity(capa);
                }
            });
        } else {
            tooltip.findViewById(R.id.button_use_capacity).setVisibility(View.GONE);
        }
        tooltipAlert.clickToHide(tooltip.findViewById(R.id.toast_LinearLayout));
        tooltipAlert.showAlert();
    }

    private void launchCapacity(FormCapacity capa) {
        new FormCapacityLauncher(getActivity(), mC, capa);
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
        if (pj.getAllForms().getCurrentForm() == null || pj.getAllForms().getCurrentForm().getListActivesCapacities().size() == 0) {
            linear.addView(textViewFormated("Aucune"));
        } else {
            for (final FormCapacity formCapacity : pj.getAllForms().getCurrentForm().getListActivesCapacities()) {
                TextView text = textViewFormated(formCapacity.getName());
                text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupLongTooltip(formCapacity);
                    }
                });
                linear.addView(text);
            }
        }
    }


    private void animate(final ImageButton buttonMain) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Animation anim = new ScaleAnimation(1f, 1.25f, 1f, 1.25f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                anim.setRepeatCount(1);
                anim.setRepeatMode(Animation.REVERSE);
                anim.setDuration(666);

                buttonMain.startAnimation(anim);
            }
        }, getResources().getInteger(R.integer.translationFragDuration));
    }

}
