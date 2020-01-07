package stellarnear.wedge_companion;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.support.v7.app.AlertDialog;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import stellarnear.wedge_companion.Perso.CanalisationCapacity;
import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.Rolls.Dices.Dice;

public class CanalisationAlertDialog {
    private Perso pj = PersoManager.getCurrentPJ();
    private Activity mA;
    private Context mC;
    private AlertDialog alertDialog;
    private CanalisationCapacity canal;
    private View dialogView;
    private OnRefreshEventListener mListener;
    private Tools tools=new Tools();

    public CanalisationAlertDialog(Activity mA, Context mC, CanalisationCapacity canal) {
        this.mA=mA;
        this.mC=mC;
        this.canal = canal;
        buildAlertDialog();
        showAlertDialog();
    }

    public interface OnRefreshEventListener {
        void onEvent();
    }

    public void setRefreshEventListener(OnRefreshEventListener eventListener) {
        mListener = eventListener;
    }

    private void buildAlertDialog() {
        LayoutInflater inflater = mA.getLayoutInflater();
        dialogView = inflater.inflate(R.layout.custom_canalisation_alert_dialog, null);
        ((TextView)dialogView.findViewById(R.id.customDialogCanalSummary)).setText("Détails "+ canal.getName());
        dialogView.findViewById(R.id.customDialogCanalSummary).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupLongTooltip();
            }
        });

        Button diceroll = dialogView.findViewById(R.id.button_customDialog_launch);
        diceroll.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRoll();
            }
        });


        AlertDialog.Builder dialogBuilder  = new AlertDialog.Builder(mA, R.style.CustomDialog);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if(mListener!=null){mListener.onEvent();}
            }
        });
        alertDialog = dialogBuilder.create();
    }

    private void popupLongTooltip(){
        View tooltip = mA.getLayoutInflater().inflate(R.layout.custom_toast_longtooltip, null);
        final CustomAlertDialog tooltipAlert = new CustomAlertDialog(mA, mC, tooltip);
        tooltipAlert.setPermanent(true);
        ((TextView)tooltip.findViewById(R.id.toast_textName)).setText(canal.getName());
        ((TextView)tooltip.findViewById(R.id.toast_textDescr)).setText(canal.getDescr());
        tooltipAlert.clickToHide(tooltip.findViewById(R.id.toast_LinearLayout));
        tooltipAlert.showAlert();
    }

    private void startRoll() {
        dialogView.findViewById(R.id.button_customDialog_launch).setVisibility(View.GONE);
        spendAndEvent();
        int sum=0;
        FrameLayout fram = dialogView.findViewById(R.id.customDialogCanalResultDice);
        fram.removeAllViews();
        LinearLayout allLines =new LinearLayout(mC);allLines.setOrientation(LinearLayout.VERTICAL);
        fram.addView(allLines);
        LinearLayout line = new LinearLayout(mC); line.setGravity(Gravity.CENTER);
        allLines.addView(line);

        int nDice= 1+(pj.getAbilityScore("ability_lvl")-1)/2;
        int nthDice=0;
        for (int i=1;i<=nDice;i++){
            nthDice++;
            if(nthDice>5){
                line = new LinearLayout(mC); line.setGravity(Gravity.CENTER);
                allLines.addView(line);
                nthDice=1;
            }
            Dice dice=new Dice(mA,mC,6);
            dice.rand(false);
            line.addView(dice.getImg());
            sum+=dice.getRandValue();
        }
        if(pj.getAllCapacities().capacityIsActive("capacity_epic_revelation_canal")){
            int flatDmg=4;
            TextView flat=new TextView(mC); flat.setTextSize(20); flat.setTextColor(mC.getColor(R.color.colorPrimaryDark));
            flat.setText("+"+flatDmg);
            line.addView(flat);
            sum+=flatDmg;
        }
        endSkillCalculation(sum);

    }

    private void spendAndEvent() {
        new PostData(mC,new PostDataElement(canal));
        pj.getAllResources().getResource("resource_canalisation").spend(1);
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

    private void endSkillCalculation(int sumResult) {
        Button onlyButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        onlyButton.setText("Ok");
        onlyButton.setBackground(mC.getDrawable(R.drawable.button_ok_gradient));
        TextView resultTitle = dialogView.findViewById(R.id.customDialogTitleResult);
        TextView callToAction = dialogView.findViewById(R.id.customDialogCanalCallToAction);
        callToAction.setTextColor(mC.getColor(R.color.secondaryTextCustomDialog));

        resultTitle.setText("Soins :");

        TextView result = dialogView.findViewById(R.id.customDialogCanalResult);
        result.setText(String.valueOf(sumResult));
        callToAction.setText("Fin de la canalisation");
        new PostData(mC,new PostDataElement(canal,sumResult));


    }
}