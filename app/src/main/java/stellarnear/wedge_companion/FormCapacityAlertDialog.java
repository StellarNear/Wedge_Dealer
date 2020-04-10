package stellarnear.wedge_companion;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import stellarnear.wedge_companion.FormSpell.FormPower;
import stellarnear.wedge_companion.Perso.FormCapacity;
import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.Rolls.Dices.Dice;

public class FormCapacityAlertDialog {
    private Perso pj = PersoManager.getCurrentPJ();
    private Activity mA;
    private Context mC;
    private AlertDialog alertDialog;
    private FormCapacity capa;
    private View dialogView;
    private OnRefreshEventListener mListener;
    private Tools tools = Tools.getTools();
    private boolean firstLaunch = true;

    public FormCapacityAlertDialog(Activity mA, Context mC, FormCapacity capa) {
        this.mA = mA;
        this.mC = mC;
        this.capa = capa;
        buildAlertDialog();
        showAlertDialog();
    }

    public void setRefreshEventListener(OnRefreshEventListener eventListener) {
        mListener = eventListener;
    }

    private void buildAlertDialog() {
        LayoutInflater inflater = mA.getLayoutInflater();
        dialogView = inflater.inflate(R.layout.custom_form_capacity_alert_dialog, null);
        ImageView icon = dialogView.findViewById(R.id.customDialogCapaIcon);
        ((TextView) dialogView.findViewById(R.id.customDialogCapaTitle)).setText(capa.getName());
        ((TextView) dialogView.findViewById(R.id.customDialogCapaSummary)).setText("Détails " + capa.getName());
        dialogView.findViewById(R.id.customDialogCapaSummary).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupLongTooltip();
            }
        });
        fillStats((TextView) dialogView.findViewById(R.id.customDialogCapaStats));
        addPowers((TextView) dialogView.findViewById(R.id.customDialogCapaPower), capa.getPowerList());

        int imgId = mC.getResources().getIdentifier(capa.getId(), "drawable", mC.getPackageName());
        try {
            ((ImageView) dialogView.findViewById(R.id.customDialogCapaIcon)).setImageDrawable(mC.getDrawable(imgId));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Button diceroll = dialogView.findViewById(R.id.button_customDialog_launch);
        diceroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firstLaunch) {
                    firstLaunch = false;
                    startRoll();
                } else {
                    new AlertDialog.Builder(mA)
                            .setIcon(R.drawable.ic_warning_black_24dp)
                            .setTitle("Demande de confirmation")
                            .setMessage("Es-tu sûre de vouloir te relancer ce jet ?")
                            .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startRoll();
                                }
                            })
                            .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .show();
                }
            }
        });


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mA, R.style.CustomDialog);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (mListener != null) {
                    mListener.onEvent();
                }
            }
        });
        alertDialog = dialogBuilder.create();
    }

    private void popupLongTooltip() {
        View tooltip = mA.getLayoutInflater().inflate(R.layout.custom_toast_info_form_capacity, null);
        final CustomAlertDialog tooltipAlert = new CustomAlertDialog(mA, mC, tooltip);
        tooltipAlert.setPermanent(true);
        ((TextView) tooltip.findViewById(R.id.toast_textName)).setText(capa.getName());
        ((TextView) tooltip.findViewById(R.id.toast_textDescr)).setText(capa.getDescr());
        tooltip.findViewById(R.id.button_use_capacity).setVisibility(View.GONE);
        tooltipAlert.clickToHide(tooltip.findViewById(R.id.toast_LinearLayout));
        tooltipAlert.showAlert();
    }

    private void addPowers(TextView textView, List<FormPower> listPowers) {
        if (capa.hasPower()) {
            String textTxt = "";
            if (listPowers.size() == 1) {
                textTxt += "Pouvoir : " + listPowers.get(0).getName();
            } else {
                String powerList = "";
                for (FormPower power : listPowers) {
                    if (!powerList.equalsIgnoreCase("")) {
                        powerList += ", ";
                    }
                    powerList += power.getName();
                }
                textTxt += "Pouvoirs : " + powerList;
            }
            textView.setText(textTxt);
        } else {
            textView.setVisibility(View.GONE);
        }
    }

    private void fillStats(TextView text) {
        String textTxt = "";
        if (!capa.getDamage().equalsIgnoreCase("")) {
            textTxt += "Dégâts : " + capa.getDamage();
        }
        if (!capa.getCooldown().equalsIgnoreCase("")) {
            if (!textTxt.equalsIgnoreCase("")) {
                textTxt += ", ";
            }
            textTxt += "Cooldown : " + capa.getCooldown();
        }
        if (capa.getDailyUse() > 0) {
            if (!textTxt.equalsIgnoreCase("")) {
                textTxt += ", ";
            }
            textTxt += "Utilisation : " + capa.getDailyUse() + "/j";
        }
        if (!capa.getSaveType().equalsIgnoreCase("") && !capa.getSaveType().equalsIgnoreCase("dmd")) {
            if (!textTxt.equalsIgnoreCase("")) {
                textTxt += ", ";
            }
            textTxt += "Save : " + capa.getSaveType();
            textTxt += ", ";
            int valDD = 10 + pj.getAbilityScore("ability_lvl") / 2 + pj.getAbilityMod("ability_constitution");
            textTxt += "DD : " + valDD;
        }
        text.setText(textTxt);
    }

    private void startRoll() {
        if (capa.hasPower()) {
            LinearLayout fram = dialogView.findViewById(R.id.customDialogCapaBotLinear);
            fram.removeAllViews();
            if (capa.getPowerId().equalsIgnoreCase("powerform_prisma_ray_random")) {
                int random = new Random().nextInt(8);
                List<FormPower> selectedPower = new ArrayList<>();
                if (random == 7) {
                    tools.customToast(mC, "Double rayons !", "center");
                    int random1 = new Random().nextInt(7);
                    selectedPower.add(capa.getPowerList().get(random1));
                    int random2 = new Random().nextInt(7);
                    selectedPower.add(capa.getPowerList().get(random2));
                } else {
                    selectedPower.add(capa.getPowerList().get(random));
                }
                addPowers((TextView) dialogView.findViewById(R.id.customDialogCapaPower), selectedPower);
                LinearLayout line = new LinearLayout(mC);
                line.setGravity(Gravity.CENTER);
                line.setOrientation(LinearLayout.VERTICAL);
                fram.addView(line);
                for (FormPower power : selectedPower) {
                    line.addView(power.getProfile().getProfile(mA, mC));
                }
            } else {
                LinearLayout line = new LinearLayout(mC);
                line.setGravity(Gravity.CENTER);
                line.setOrientation(LinearLayout.VERTICAL);
                fram.addView(line);
                for (FormPower power : capa.getPowerList()) {
                    View profileView = power.getProfile().getProfile(mA, mC);
                    if (profileView.getParent() != null) {
                        ((ViewGroup) profileView.getParent()).removeView(profileView);
                    }
                    line.addView(profileView);
                }
            }
        } else {
            int sum = 0;
            FrameLayout fram = dialogView.findViewById(R.id.customDialogCapaResultDice);
            fram.removeAllViews();
            LinearLayout line = new LinearLayout(mC);
            line.setGravity(Gravity.CENTER);
            fram.addView(line);
            if (capa.getDamage().contains("d")) {
                int nDice = tools.toInt(capa.getDamage().split("d")[0]);
                int indexFlat = capa.getDamage().indexOf("+") == -1 ? capa.getDamage().length() : capa.getDamage().indexOf("+");  // si y a pas de flat on substring pas
                int diceType = tools.toInt(capa.getDamage().substring(capa.getDamage().indexOf("d") + 1, indexFlat));
                for (int i = 1; i <= nDice; i++) {
                    Dice dice = new Dice(mA, mC, diceType);
                    dice.rand(false);
                    line.addView(dice.getImg());
                    sum += dice.getRandValue();
                }
            }
            if (capa.getDamage().contains("+")) {
                String flatDmgTxt = capa.getDamage().split("\\+")[1];
                int flatDmg = 0;
                if (flatDmgTxt.equalsIgnoreCase("2for")) {
                    flatDmg = pj.getAbilityMod("ability_force") * 2;
                } else if (flatDmgTxt.equalsIgnoreCase("for")) {
                    flatDmg = pj.getAbilityMod("ability_force");
                } else {
                    flatDmg = tools.toInt(flatDmgTxt);
                }
                TextView flat = new TextView(mC);
                flat.setTextSize(20);
                flat.setTextColor(mC.getColor(R.color.colorPrimaryDark));
                flat.setText("+" + flatDmg);
                line.addView(flat);
                sum += flatDmg;
            }
            endSkillCalculation(sum);
        }
    }

    public void showAlertDialog() {
        alertDialog.show();
        Display display = mA.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Float factor = mC.getResources().getInteger(R.integer.percent_fullscreen_customdialog) / 100f;
        alertDialog.getWindow().setLayout((int) (factor * size.x), (int) (factor * size.y));
        Button onlyButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        LinearLayout.LayoutParams onlyButtonLL = (LinearLayout.LayoutParams) onlyButton.getLayoutParams();
        onlyButtonLL.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        onlyButton.setLayoutParams(onlyButtonLL);
        onlyButton.setTextColor(mC.getColor(R.color.colorBackground));
        onlyButton.setBackground(mC.getDrawable(R.drawable.button_cancel_gradient));
    }

    private void endSkillCalculation(int sumResult) {
        Button onlyButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        onlyButton.setText("Ok");
        onlyButton.setBackground(mC.getDrawable(R.drawable.button_ok_gradient));
        TextView resultTitle = dialogView.findViewById(R.id.customDialogTitleResult);
        TextView callToAction = dialogView.findViewById(R.id.customDialogCapaCallToAction);
        callToAction.setTextColor(mC.getColor(R.color.secondaryTextCustomDialog));

        resultTitle.setText("Dégâts :");

        TextView result = dialogView.findViewById(R.id.customDialogCapaResult);
        result.setText(String.valueOf(sumResult));
        callToAction.setText("Fin du lancement de capacité");
        new PostData(mC, new PostDataElement(capa, sumResult));


    }

    public interface OnRefreshEventListener {
        void onEvent();
    }
}