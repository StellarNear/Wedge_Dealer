package stellarnear.wedge_companion.Rolls.Dices;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AlertDialog;

import stellarnear.wedge_companion.R;

/**
 * Created by Utilisateur on 12/02/2018.
 */

public class DiceDealerDialog {
    private AlertDialog alertDialogWheelPicker;

    private Context mC;

    public DiceDealerDialog(Context mC, final Dice dice) {
        this.mC = mC;
        LayoutInflater inflater = LayoutInflater.from(mC);
        View dialogViewWheelPicker = inflater.inflate(R.layout.custom_dialog_wheel_picker, null);
        RelativeLayout relativeCenter = dialogViewWheelPicker.findViewById(R.id.relative_custom_dialog_center);
        final WheelDicePicker wheelPicker = new WheelDicePicker(relativeCenter, dice, mC);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mC, R.style.CustomDialog);
        dialogBuilder.setView(dialogViewWheelPicker);
        dialogBuilder.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dice.setRand(wheelPicker.getValueSelected());
            }
        });
        dialogBuilder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        alertDialogWheelPicker = dialogBuilder.create();

        showDialog();
    }

    private void showDialog() {
        alertDialogWheelPicker.show();
        WindowManager windowManager = (WindowManager) mC.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Float factor = (mC.getResources().getInteger(R.integer.percent_fullscreen_combat_launcher_dialog) - 5) / 100f;
        alertDialogWheelPicker.getWindow().setLayout((int) (factor * size.x), (int) (factor * size.y));

        Button positiveButton = alertDialogWheelPicker.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout.LayoutParams positiveButtonLL = (LinearLayout.LayoutParams) positiveButton.getLayoutParams();
        positiveButtonLL.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        positiveButtonLL.setMargins(mC.getResources().getDimensionPixelSize(R.dimen.general_margin), 0, 0, 0);
        positiveButton.setLayoutParams(positiveButtonLL);
        positiveButton.setTextColor(mC.getColor(R.color.colorBackground));
        positiveButton.setBackground(mC.getDrawable(R.drawable.button_ok_gradient));

        Button negativeButton = alertDialogWheelPicker.getButton(AlertDialog.BUTTON_NEGATIVE);
        LinearLayout.LayoutParams negativeButtonLL = (LinearLayout.LayoutParams) negativeButton.getLayoutParams();
        negativeButtonLL.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        negativeButton.setLayoutParams(negativeButtonLL);
        negativeButton.setTextColor(mC.getColor(R.color.colorBackground));
        negativeButton.setBackground(mC.getDrawable(R.drawable.button_cancel_gradient));
    }
}

