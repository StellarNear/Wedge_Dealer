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


public class CritConfirmAlertDialog {
    private Activity mA;
    private Context mC;
    private AlertDialog alertDialog;

    private View dialogView;

    private int sumScore;

    private Dice dice;

    private Perso pj = PersoManager.getCurrentPJ();

    private OnSuccessEventListener mListenerSuccess;
    private OnFailEventListener mListenerFail;

    private Tools tools = Tools.getTools();

    public CritConfirmAlertDialog(Activity mA,Context mC, int atkVal) {
        this.mA=mA;
        this.mC=mC;
        this.sumScore = atkVal;
        buildAlertDialog();
    }

    private void buildAlertDialog() {
        LayoutInflater inflater = mA.getLayoutInflater();
        dialogView = inflater.inflate(R.layout.custom_alert_dialog, null);
        ImageView icon = dialogView.findViewById(R.id.customDialogTestIcon);
        icon.setImageDrawable(mC.getDrawable(R.drawable.critical_hit));

        String titleTxt = "Test de confirmation du critique";

        final TextView title = dialogView.findViewById(R.id.customDialogTestTitle);
        title.setSingleLine(false);
        title.setTextSize(22);
        title.setText(titleTxt);

        String summaryDetail="Bonus d'attaque de base (+"+sumScore+")";

        if(pj.getAllFeats().featIsActive("feat_crit")){
            sumScore+=4;
            summaryDetail+="\nBonus don pour les critiques (+4)";
        }


        summaryDetail+="\nTotal : +"+String.valueOf(sumScore);
        TextView summary = dialogView.findViewById(R.id.customDialogTestSummary);
        summary.setText(summaryDetail);


        Button focus = dialogView.findViewById(R.id.button_customDialog_test_focus);
        focus.setVisibility(View.GONE);
        Button passive = dialogView.findViewById(R.id.button_customDialog_test_passive);
        passive.setVisibility(View.GONE);

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
                if(mListenerSuccess !=null){
                    mListenerSuccess.onSuccessEvent();}
                tools.customToast(mC,"Bravo ! il va prendre cher");
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


        resultTitle.setText("Résultat du test confirmation :");
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

        callToAction.setText("Fin du test de confirmation.");

        Button failButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        failButton.setText("Raté");
        failButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tools.customToast(mC,"Bon c'est pas un crit pas grave...\nla prochaine fois ca confirme !");
                mListenerFail.onFailEvent();
                alertDialog.dismiss();
            }
        });

        Button success = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        success.setVisibility(View.VISIBLE);

        new PostData(mC,new PostDataElement("Test confirmation de critique",dice,sumResult));
    }

    public interface OnSuccessEventListener {
        void onSuccessEvent();
    }

    public void setSuccessEventListener(OnSuccessEventListener eventListener) {
        mListenerSuccess = eventListener;
    }

    public interface OnFailEventListener {
        void onFailEvent();
    }

    public void setFailEventListener(OnFailEventListener eventListener) {
        mListenerFail = eventListener;
    }
}

