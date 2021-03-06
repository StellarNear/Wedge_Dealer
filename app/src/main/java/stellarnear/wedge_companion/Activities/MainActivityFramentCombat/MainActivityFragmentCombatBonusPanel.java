package stellarnear.wedge_companion.Activities.MainActivityFramentCombat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.Perso.Resource;
import stellarnear.wedge_companion.PostData;
import stellarnear.wedge_companion.PostDataElement;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Rolls.RangedRoll;
import stellarnear.wedge_companion.Rolls.Roll;
import stellarnear.wedge_companion.Rolls.RollList;
import stellarnear.wedge_companion.TextFilling.PreRandValues;
import stellarnear.wedge_companion.Tools;

public class MainActivityFragmentCombatBonusPanel {
    public Perso pj = PersoManager.getCurrentPJ();
    private Context mC;
    private RollList rollList;
    private PreRandValues preRandValues;
    private ImageView buttonAdd;
    private LinearLayout bonusPanelRollList;
    private TextView bonusPanelRangePicker;
    private TextView nUsageLynxRemaning;
    private View pannel;
    private int basicRange;
    private boolean addBonusPanelIsVisible = false;
    private Tools tools = Tools.getTools();

    public MainActivityFragmentCombatBonusPanel(final Context mC, View mainPage) {
        this.mC = mC;
        buttonAdd = mainPage.findViewById(R.id.fab_add_atk);
        pannel = mainPage.findViewById(R.id.add_bonus_linear);
        bonusPanelRollList = mainPage.findViewById(R.id.bonus_panel_roll_list);
        nUsageLynxRemaning = mainPage.findViewById(R.id.lynx_eye_remaining);
        ((TextView) mainPage.findViewById(R.id.lynx_eye_title)).setText("Oeil de lynx (+" + pj.getAllCapacities().getCapacity("capacity_lynx_eye").getValue() + ")");
        nUsageLynxRemaning.setText("Utilisations restantes : " + pj.getCurrentResourceValue("resource_lynx_eye"));
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flipBonusPanel();
            }
        });
        bonusPanelRangePicker = mainPage.findViewById(R.id.bonus_panel_range_factor);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
        basicRange = tools.toInt(settings.getString("attack_range", String.valueOf(mC.getResources().getInteger(R.integer.attack_range_DEF))));
        bonusPanelRangePicker.setText(basicRange + "m");
        bonusPanelRangePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectAdditionRange();
            }
        });

        final int bonusInit = settings.getInt("last_initiative_score", 0);
        ((TextView) mainPage.findViewById(R.id.text_isillirit_init_bonus)).setText("+" + bonusInit + " dégâts à la premiere attaque");

        final CheckBox boostInitCheck = mainPage.findViewById(R.id.checkbox_isillirit_init_bonus);
        boostInitCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(mC)
                        .setTitle("Demande de confirmation")
                        .setMessage("Voulez vous utiliser la capacité d'Isillirit de boost de dégats lié à l'initiative ?")
                        .setIcon(android.R.drawable.ic_menu_help)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                tools.customToast(mC, "Boost de " + bonusInit + " de dégâts lancé !", "center");
                                ((RangedRoll) rollList.get(0)).makeIsilliritInitBoosted();
                                boostInitCheck.setEnabled(false);
                                boostInitCheck.setOnClickListener(null);
                                new PostData(mC, new PostDataElement("Utilisation de la capacité d'Isillirit de boost de dégats lié à l'initiative", bonusInit + " bonus de dégâts sur le premiere attaque"));
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });
    }


    private void flipBonusPanel() {
        if (!addBonusPanelIsVisible) {
            pannel.setVisibility(View.VISIBLE);
            Animation top = AnimationUtils.loadAnimation(mC, R.anim.infromtop);
            pannel.startAnimation(top);
            if (bonusPanelRollList.getChildCount() == 0) {
                addRollsToBonusPanel();
            }
            addBonusPanelIsVisible = true;
        } else {
            Animation bot = AnimationUtils.loadAnimation(mC, R.anim.outtotop);
            pannel.startAnimation(bot);
            pannel.setVisibility(View.GONE);
            addBonusPanelIsVisible = false;
        }
    }

    private void selectAdditionRange() {
        final EditText input = new EditText(mC);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        new AlertDialog.Builder(mC)
                .setView(input)
                .setTitle("Valeur de portée souhaitée (en mètre)")
                .setIcon(android.R.drawable.ic_menu_help)
                .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        int malus = calculateMalusFromMeter(tools.toInt(input.getText().toString()));
                        for (Roll roll : rollList.getList()) {
                            roll.rangeMalus(malus);
                        }
                        new PostData(mC, new PostDataElement("Augmentation de la portée maximale à\n" +
                                tools.toInt(input.getText().toString()), "Malus appliqué aux jets " + malus));
                        bonusPanelRangePicker.setText(tools.toInt(input.getText().toString()) + "m");
                        preRandValues.refresh();
                    }
                })
                .setNegativeButton("non", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                }).show();
    }

    private int calculateMalusFromMeter(Integer meter) {
        int modul = (int) Math.ceil((meter - basicRange) / 100.0);
        return modul > 0 ? modul * 2 : 0;
    }

    private void addRollsToBonusPanel() {
        for (final Roll roll : rollList.getList()) {
            LinearLayout line = new LinearLayout(mC);
            line.setGravity(Gravity.CENTER);
            line.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            final CheckBox check = new CheckBox(mC);
            line.addView(check);
            bonusPanelRollList.addView(line);
            check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Resource resLynxEye = pj.getAllResources().getResource("resource_lynx_eye");
                    if (resLynxEye.getCurrent() > 0) {
                        new AlertDialog.Builder(mC)
                                .setTitle("Demande de confirmation")
                                .setMessage("Voulez vous utiliser " + resLynxEye.getName() + " sur ce jet d'attaque ?")
                                .setIcon(android.R.drawable.ic_menu_help)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        roll.lynxEyeBoost();
                                        check.setEnabled(false);
                                        check.setOnClickListener(null);
                                        preRandValues.refresh();
                                        resLynxEye.spend(1);
                                        nUsageLynxRemaning.setText("Utilisations restantes : " + pj.getCurrentResourceValue("resource_lynx_eye"));
                                        tools.customToast(mC, resLynxEye.getName() + " lancée !\n" +
                                                "Il te reste " + resLynxEye.getCurrent() + " utilisations", "center");
                                        new PostData(mC, new PostDataElement("Utilisation de la capacité\n" +
                                                resLynxEye.getName(), resLynxEye.getCapaDescr()));
                                    }
                                })
                                .setNegativeButton(android.R.string.no, null).show();
                    } else {
                        tools.customToast(mC, "Tu n'as plus d'utilisation d'oeil de Lynx", "center");
                    }
                }
            });
        }
    }

    public void hide() {
        buttonAdd.setVisibility(View.GONE);
        if (addBonusPanelIsVisible) {
            flipBonusPanel();
        }
    }

    public void show() {
        buttonAdd.setVisibility(View.VISIBLE);
    }

    public ImageView getButton() {
        return buttonAdd;
    }

    public void addRollData(PreRandValues preRandValues, RollList rollList) {
        this.preRandValues = preRandValues;
        this.rollList = rollList;
    }
}
