package stellarnear.wedge_companion;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

public class PreparationSpellsAlertDialog {
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog alert;
    private Context mC;
    private boolean positiveButton=false;
    private OnAcceptEventListener mListener;

    public PreparationSpellsAlertDialog(Context mC) {
        // Set the toast and duration
        LayoutInflater inflater = LayoutInflater.from(mC);
        View mainView = inflater.inflate(R.layout.custom_spellgem,null);
        this.mC=mC;
        dialogBuilder  = new AlertDialog.Builder(mC, R.style.CustomDialog);
        dialogBuilder.setView(mainView);
        dialogBuilder.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if(mListener!=null) {
                    mListener.onEvent();
                }
            }
        });
        positiveButton=true;
        alert = dialogBuilder.create();
    }

    public void showAlert() {
        alert.show();
        if(positiveButton){applyStyleToOkButton();}
        int height=LinearLayout.LayoutParams.MATCH_PARENT;
        int width=LinearLayout.LayoutParams.MATCH_PARENT;
        alert.getWindow().setLayout(width,height);
        alert.getWindow().setGravity(Gravity.CENTER);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
        if (settings.getBoolean("switch_fullscreen_mode", mC.getResources().getBoolean(R.bool.switch_fullscreen_mode_DEF))) {
            alert.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        alert.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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

    public interface OnAcceptEventListener {
        void onEvent();
    }

    public void setAcceptEventListener(OnAcceptEventListener eventListener) {
        mListener = eventListener;
    }
}
