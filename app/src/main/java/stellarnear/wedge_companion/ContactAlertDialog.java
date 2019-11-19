package stellarnear.wedge_companion;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.Rolls.Dices.Dice;
import stellarnear.wedge_companion.Rolls.Dices.WheelDicePicker;
import stellarnear.wedge_companion.Spells.Spell;

public class ContactAlertDialog {
    private Activity mA;
    private Context mC;
    private AlertDialog alertDialog;
    private AlertDialog alertDialogWheelPicker;
    private WheelDicePicker wheelPicker;
    private View dialogView;
    private View dialogViewWheelPicker;
    private String mode;
    private Calculation calculation=new Calculation();
    private int sumScore;
    private Spell spell;
    private Dice dice;

    private Perso pj = PersoManager.getCurrentPJ();

    private OnRefreshEventListener mListener;

    private Tools tools = new Tools();

    public ContactAlertDialog(Activity mA, Context mC, Spell spell) {
        this.mA=mA;
        this.mC=mC;
        this.spell=spell;
        this.mode = calculation.getContact(spell);
        this.sumScore = 0;
        buildAlertDialog();
    }

    private void buildAlertDialog() {
        LayoutInflater inflater = mA.getLayoutInflater();
        dialogView = inflater.inflate(R.layout.custom_contact_test_alert_dialog, null);
        ImageView icon = dialogView.findViewById(R.id.customDialogTestIcon);
        icon.setImageDrawable(mC.getDrawable(R.drawable.ic_filter_center_focus_black_24dp));

        String titleTxt = "Test de contact "+(mode.equalsIgnoreCase("melee") ? "au corps à corps" : "à distance") +" :\n";
        final TextView title = dialogView.findViewById(R.id.customDialogTestTitle);
        title.setSingleLine(false);
        title.setText(titleTxt);

        sumScore= pj.getBaseAtk();
        String summaryDetail="BBA (+"+String.valueOf(sumScore)+")";

        if(pj.getBonusAtk()>0){
            sumScore+= pj.getBonusAtk();
            summaryDetail+=" (bonus +"+String.valueOf(pj.getBonusAtk())+")";
        }
        if(mode.equalsIgnoreCase("melee")){
            sumScore+= pj.getAbilityMod("ability_force");
            summaryDetail+="\nBonus force ("+(pj.getAbilityMod("ability_force")>0?"+":"")+ pj.getAbilityMod("ability_force")+")";
        } else {
            sumScore+= pj.getAbilityMod("ability_dexterite");
            summaryDetail+="\nBonus dexterité ("+(pj.getAbilityMod("ability_dexterite")>0?"+":"")+ pj.getAbilityMod("ability_dexterite")+")";
        }
        String summaryTxt="Test contact : +"+String.valueOf(sumScore);
        TextView summary = dialogView.findViewById(R.id.customDialogTestSummary);
        summary.setText(summaryTxt);

        TextView detail = dialogView.findViewById(R.id.customDialogTestDetail);
        detail.setText(summaryDetail);

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

        AlertDialog.Builder dialogBuilder  = new AlertDialog.Builder(mA, R.style.CustomDialog);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked cancel button
            }
        });

        dialogBuilder.setPositiveButton("Succès", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String message ="Bravo le sort touche !";
                if(dice.getRandValue()==20){
                    spell.makeCrit();
                    message="Critique !\nIl va prendre cher !";
                }
                if(mListener!=null){mListener.onEvent();}
                tools.customToast(mC,message,"center");
            }
        });
        alertDialog = dialogBuilder.create();
    }

    private void startRoll() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
        dice = new Dice(mA,mC,20);
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

        Button failButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        LinearLayout.LayoutParams onlyButtonLL = (LinearLayout.LayoutParams) failButton.getLayoutParams();
        onlyButtonLL.width=ViewGroup.LayoutParams.WRAP_CONTENT;
        onlyButtonLL.setMargins(10,0,10,0);
        failButton.setLayoutParams(onlyButtonLL);
        failButton.setTextColor(mC.getColor(R.color.colorBackground));
        failButton.setBackground(mC.getDrawable(R.drawable.button_cancel_gradient));

        Button success = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        success.setLayoutParams(onlyButtonLL);
        success.setTextColor(mC.getColor(R.color.colorBackground));
        success.setBackground(mC.getDrawable(R.drawable.button_ok_gradient));
        success.setVisibility(View.GONE);

        Display display = mA.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Float factor = mC.getResources().getInteger(R.integer.percent_fullscreen_customdialog)/100f;
        alertDialog.getWindow().setLayout((int) (factor*size.x), (int)(factor*size.y));
    }

    private void endSkillCalculation(final Dice dice) {
        FrameLayout resultDice= dialogView.findViewById(R.id.customDialogTestResultDice);
        resultDice.removeAllViews();
        resultDice.addView(dice.getImg());

        TextView resultTitle = dialogView.findViewById(R.id.customDialogTitleResult);
        TextView callToAction = dialogView.findViewById(R.id.customDialogTestCallToAction);
        callToAction.setTextColor(mC.getColor(R.color.secondaryTextCustomDialog));


        resultTitle.setText("Résultat du test de contact :");
        int sumResult=dice.getRandValue()+ sumScore;
        if(dice.getMythicDice()!=null){sumResult+=dice.getMythicDice().getRandValue();}

        final TextView result = dialogView.findViewById(R.id.customDialogTestResult);
        result.setText(String.valueOf(sumResult));

        dice.setMythicEventListener(new Dice.OnMythicEventListener() {
            @Override
            public void onEvent() {
                int sumResult=dice.getRandValue()+ sumScore;
                if(dice.getMythicDice()!=null){sumResult+=dice.getMythicDice().getRandValue();}
                result.setText(String.valueOf(sumResult));
            }
        });

        callToAction.setText("Fin du test de contact.");

        Button failButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        failButton.setText("Raté");
        failButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spell.setContactFailed();
                if(mListener!=null){mListener.onEvent();}
                tools.customToast(mC,"Mince ... prochaine fois ca touche !","center");
                alertDialog.dismiss();
            }
        });

        Button success = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        if(dice.getRandValue()==1){
            success.setVisibility(View.GONE);
        } else {
            success.setVisibility(View.VISIBLE);
        }
        new PostData(mC,new PostDataElement("Test contact "+spell.getName(),dice,sumResult));
    }

    public interface OnRefreshEventListener {
        void onEvent();
    }

    public void setRefreshEventListener(OnRefreshEventListener eventListener) {
        mListener = eventListener;
    }
}

