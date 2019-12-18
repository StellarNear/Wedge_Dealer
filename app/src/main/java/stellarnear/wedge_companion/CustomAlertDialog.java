package stellarnear.wedge_companion;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class CustomAlertDialog {
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog alert;
    private Activity mA;
    private Context mC;
    private boolean permanent=false;
    private boolean positiveButton=false;
    private boolean cancelButton=false;
    private String mode;
    private OnAcceptEventListener mListener;

    public CustomAlertDialog(Activity mA, Context mC, View view) {
        // Set the toast and duration
        this.mA=mA;
        this.mC=mC;
        if(mA!=null){ dialogBuilder  = new AlertDialog.Builder(mA, R.style.CustomDialog); }
        else { dialogBuilder  = new AlertDialog.Builder(mC, R.style.CustomDialog);  }
        dialogBuilder.setView(view);
    }

    public void showAlert() {
        if(alert==null){alert = dialogBuilder.create();}
        alert.show();
        if(positiveButton){applyStyleToOkButton();}
        if(cancelButton){applyStyleToCancelButton();}
        setTimer();
        alert.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        alert.getWindow().setGravity(Gravity.CENTER);
    }


    private void applyStyleToOkButton() {
        Button button = alert.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout.LayoutParams positiveButtonLL = (LinearLayout.LayoutParams) button.getLayoutParams();
        positiveButtonLL.width= ViewGroup.LayoutParams.WRAP_CONTENT;
        positiveButtonLL.setMargins(mC.getResources().getDimensionPixelSize(R.dimen.general_margin),0,0,0);
        button.setLayoutParams(positiveButtonLL);
        button.setTextColor(mC.getColor(R.color.colorBackground));
        button.setBackground(mC.getDrawable(R.drawable.button_ok_gradient));
    }

    private void applyStyleToCancelButton() {
        Button button = alert.getButton(AlertDialog.BUTTON_NEGATIVE);
        LinearLayout.LayoutParams negativeButtonLL = (LinearLayout.LayoutParams) button.getLayoutParams();
        negativeButtonLL.width= ViewGroup.LayoutParams.WRAP_CONTENT;
        button.setLayoutParams(negativeButtonLL);
        button.setTextColor(mC.getColor(R.color.colorBackground));
        button.setBackground(mC.getDrawable(R.drawable.button_cancel_gradient));
    }


    private void setTimer() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
        if (settings.getBoolean("switch_autoclose_dialog",mC.getResources().getBoolean(R.bool.switch_autoclose_dialog_DEF)) && !permanent){
            int duration = new Tools().toInt(settings.getString("custom_alert_long_duration", String.valueOf(mC.getResources().getInteger(R.integer.custom_alert_long_duration_DEF))));
            Handler h = new Handler();
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dismissAlert();
                }
            }, duration);
        }
    }

    public void dismissAlert() {
        alert.dismiss();
    }

    public void clickToHide(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissAlert();
            }
        });
    }

    public void setPermanent(boolean b) {
        this.permanent=b;
    }

    public void addConfirmButton(String txt) {
        dialogBuilder.setPositiveButton(txt, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mListener.onEvent();
            }
        });
        positiveButton=true;
    }

    public void addCancelButton(String txt) {
        dialogBuilder.setNegativeButton(txt, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dismissAlert();
            }
        });
        cancelButton=true;
    }

    public interface OnAcceptEventListener {
        void onEvent();
    }

    public void setAcceptEventListener(OnAcceptEventListener eventListener) {
        mListener = eventListener;
    }
}
