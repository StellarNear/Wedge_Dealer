package stellarnear.wedge_companion;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import stellarnear.wedge_companion.Perso.Ability;
import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.Perso.Skill;
import stellarnear.wedge_companion.Rolls.Dices.Dice;

public class TestAlertDialog {
    private Perso pj = PersoManager.getCurrentPJ();
    private Activity mA;
    private Context mC;
    private AlertDialog alertDialog;
    private Skill skill;
    private Ability abi;
    private View dialogView;
    private OnRefreshEventListener mListener;
    private int modBonus;
    String mode;

    public TestAlertDialog(Activity mA, Context mC, Skill skill, int modBonus) {
        this.mA=mA;
        this.mC=mC;
        this.skill=skill;
        this.modBonus=modBonus;
        this.mode="skill";
        buildAlertDialog();
        showAlertDialog();
    }

    public TestAlertDialog(Activity mA, Context mC, Ability abi) {
        this.mA=mA;
        this.mC=mC;
        this.abi=abi;
        this.mode="abi";
        if (abi.getId().equalsIgnoreCase("ability_equipment")){
            pj.getInventory().showEquipment(mA,mC);
        } else {
            buildAlertDialog();
            showAlertDialog();
        }
    }

    public interface OnRefreshEventListener {
        void onEvent();
    }

    public void setRefreshEventListener(OnRefreshEventListener eventListener) {
        mListener = eventListener;
    }

    private void buildAlertDialog() {
        LayoutInflater inflater = mA.getLayoutInflater();
        dialogView = inflater.inflate(R.layout.custom_alert_dialog, null);
        ImageView icon = dialogView.findViewById(R.id.customDialogTestIcon);
        int imgId;
        String titleTxt;
        int nameLength;
        String summaryTxt;
        if (mode.equalsIgnoreCase("skill")){
            imgId = mC.getResources().getIdentifier(skill.getId(), "drawable", mC.getPackageName());
            titleTxt = "Test de la compétence :\n"+skill.getName();
            nameLength=skill.getName().length();
            //summary
            String abScore;
            if(modBonus>=0){
                abScore = "+"+modBonus;
            } else {
                abScore = String.valueOf(modBonus);
            }
            int sumScore=modBonus+skill.getRank()+ pj.getSkillBonus(mC,skill.getId());
            summaryTxt="Total : "+String.valueOf(sumScore)+"\nAbilité ("+skill.getAbilityDependence().substring(8,11)+") : "+abScore+",  Maîtrise : "+skill.getRank()+",  Bonus : "+ pj.getSkillBonus(mC,skill.getId());
        } else {
            imgId = mC.getResources().getIdentifier(abi.getId(), "drawable", mC.getPackageName());
            titleTxt = "Test de la caractéristique :\n"+abi.getName();
            nameLength=abi.getName().length();

            //summary
            if (abi.getType().equalsIgnoreCase("base")){
                String abScore;
                if(pj.getAbilityMod(abi.getId())>=0){
                    abScore = "+"+ pj.getAbilityMod(abi.getId());
                } else {
                    abScore = String.valueOf(pj.getAbilityMod(abi.getId()));
                }
                summaryTxt="Bonus : "+abScore;
            } else {
                summaryTxt="Bonus : "+ pj.getAbilityScore(abi.getId());
            }

        }
        icon.setImageDrawable(mC.getDrawable(imgId));

        SpannableString titleSpan = new SpannableString(titleTxt);
        titleSpan.setSpan(new RelativeSizeSpan(2.0f)  ,titleTxt.length()-nameLength,titleTxt.length(),0);
        TextView title = dialogView.findViewById(R.id.customDialogTestTitle);
        title.setSingleLine(false);
        title.setText(titleSpan);

        TextView summary = dialogView.findViewById(R.id.customDialogTestSummary);
        summary.setText(summaryTxt);

        Button diceroll = dialogView.findViewById(R.id.button_customDialog_test_diceroll);
        diceroll.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(((TextView)dialogView.findViewById(R.id.customDialogTestResult)).getText().equals("")){
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

        Button passive = dialogView.findViewById(R.id.button_customDialog_test_passive);
        passive.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dice dice = new Dice(mA,mC,20);
                dice.setRand(10);
                endSkillCalculation(dice);
            }
        });

        Button focus = dialogView.findViewById(R.id.button_customDialog_test_focus);
        focus.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dice dice = new Dice(mA,mC,20);
                dice.setRand(20);
                endSkillCalculation(dice);
            }
        });

        if (mode.equalsIgnoreCase("abi") && !abi.isFocusable()){
            passive.setVisibility(View.GONE);
            focus.setVisibility(View.GONE);
        }

        AlertDialog.Builder dialogBuilder  = new AlertDialog.Builder(mA, R.style.CustomDialog);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if(mListener!=null){mListener.onEvent();}
            }
        });
        alertDialog = dialogBuilder.create();
    }

    private void startRoll() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
        final Dice dice = new Dice(mA,mC,20);
        if (settings.getBoolean("switch_manual_diceroll",mC.getResources().getBoolean(R.bool.switch_manual_diceroll_def))){
            dice.rand(true);
            dice.setRefreshEventListener(new Dice.OnRefreshEventListener() {
                @Override
                public void onEvent() {
                    endSkillCalculation(dice);
                }
            });
        } else {
            dice.rand(false);
            endSkillCalculation(dice);
        }
    }

    public void showAlertDialog(){
        alertDialog.show();
        Display display = mA.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Float factor = mC.getResources().getInteger(R.integer.percent_fullscreen_customdialog)/100f;
        alertDialog.getWindow().setLayout((int) (factor*size.x), (int)(factor*size.y));
        Button onlyButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        LinearLayout.LayoutParams onlyButtonLL = (LinearLayout.LayoutParams) onlyButton.getLayoutParams();
        onlyButtonLL.width=ViewGroup.LayoutParams.WRAP_CONTENT;
        onlyButton.setLayoutParams(onlyButtonLL);
        onlyButton.setTextColor(mC.getColor(R.color.colorBackground));
        onlyButton.setBackground(mC.getDrawable(R.drawable.button_cancel_gradient));
    }

    private void endSkillCalculation(final Dice dice) {
        List<String> listSave = Arrays.asList("ability_ref","ability_vig","ability_vol");
        if(abi!=null && listSave.contains(abi.getId())){
            //dice.setLegendarySurge(true); //la bague permet d'avoir un jet legendaire de montée en pusisance sur les jets de sauv
        }

        FrameLayout resultDice= dialogView.findViewById(R.id.customDialogTestResultDice);
        resultDice.removeAllViews();
        resultDice.addView(dice.getImg());

        Button onlyButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        onlyButton.setText("Ok");
        onlyButton.setBackground(mC.getDrawable(R.drawable.button_ok_gradient));
        displayResult(dice);
        dice.setMythicEventListener(new Dice.OnMythicEventListener() {
            @Override
            public void onEvent() {
                displayResult(dice);
            }
        });
    }

    private void displayResult(Dice dice) {
        String modePostData; int sumResultPostData;
        TextView resultTitle = dialogView.findViewById(R.id.customDialogTitleResult);
        TextView callToAction = dialogView.findViewById(R.id.customDialogTestCallToAction);
        callToAction.setTextColor(mC.getColor(R.color.secondaryTextCustomDialog));
        if (mode.equalsIgnoreCase("skill")){
            resultTitle.setText("Résultat du test de compétence :");
            int sumResult=dice.getRandValue()+skill.getRank()+ pj.getSkillBonus(mC,skill.getId())+ modBonus;
            if(dice.getMythicDice()!=null){sumResult+=dice.getMythicDice().getRandValue();}
            TextView result = dialogView.findViewById(R.id.customDialogTestResult);
            result.setText(String.valueOf(sumResult));
            callToAction.setText("Fin du test de compétence");

            modePostData="Test compétence "+skill.getName();sumResultPostData=sumResult;
        } else {
            resultTitle.setText("Résultat du test de caractéristique :");
            int sumResult;
            if (abi.getType().equalsIgnoreCase("base")){
                sumResult=dice.getRandValue()+ pj.getAbilityMod(abi.getId());
            } else {
                sumResult=dice.getRandValue()+ pj.getAbilityScore(abi.getId());
            }
            if(dice.getMythicDice()!=null){sumResult+=dice.getMythicDice().getRandValue();}
            TextView result = dialogView.findViewById(R.id.customDialogTestResult);
            result.setText(String.valueOf(sumResult));

            callToAction.setText("Fin du test de caractéristique");
            modePostData="Test caractéristique "+abi.getName();sumResultPostData=sumResult;
        }
        new PostData(mC,new PostDataElement(modePostData,dice,sumResultPostData));
    }
}