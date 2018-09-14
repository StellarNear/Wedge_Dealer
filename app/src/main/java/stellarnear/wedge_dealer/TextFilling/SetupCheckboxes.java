package stellarnear.wedge_dealer.TextFilling;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import stellarnear.stellarnear.R;
import stellarnear.wedge_dealer.Rolls.Roll;
import stellarnear.wedge_dealer.Rolls.RollList;

public class SetupCheckboxes {
    private Context mC;
    private View mainView;
    private RollList rollList;
    private SharedPreferences settings;
    private LinearLayout mainAtkLin;

    public SetupCheckboxes(Context mC, View mainView, RollList rollList) {
        this.mC = mC;
        this.mainView = mainView;
        this.rollList = rollList;
        this.settings = PreferenceManager.getDefaultSharedPreferences(mC);
        this.mainAtkLin = mainView.findViewById(R.id.mainLinearAtk);
        addCheckboxes();
    }

    private void addCheckboxes() {

        TextView hitTxt = new TextView(mC);
        hitTxt.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        hitTxt.setGravity(Gravity.CENTER);
        hitTxt.setTextColor(Color.GRAY);
        hitTxt.setTextSize(18);
        hitTxt.setText("Coups qui touchent :");
        mainAtkLin.addView(hitTxt);

        LinearLayout line = new LinearLayout(mC);
        line.setOrientation(LinearLayout.HORIZONTAL);
        line.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        line.setGravity(Gravity.CENTER);

        for (Roll roll : rollList.getList()) {
            LinearLayout frame = new LinearLayout(mC);
            frame.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
            frame.setLayoutParams(params);
            if (!roll.isInvalid() && !roll.isFailed()) {
                frame.addView(roll.getHitCheckbox());
            }
            line.addView(frame);
        }
        mainAtkLin.addView(line);


        if (rollList.haveAnyCrit()) {
            TextView critTxt = new TextView(mC);
            critTxt.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            critTxt.setGravity(Gravity.CENTER);
            critTxt.setTextColor(Color.GRAY);
            critTxt.setTextSize(18);
            critTxt.setText("Coups critiques confirmés :");
            mainAtkLin.addView(critTxt);
            LinearLayout lineCrit = new LinearLayout(mC);
            lineCrit.setOrientation(LinearLayout.HORIZONTAL);
            lineCrit.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            lineCrit.setGravity(Gravity.CENTER);
            for (final Roll roll : rollList.getList()) {
                LinearLayout frame = new LinearLayout(mC);
                frame.setGravity(Gravity.CENTER);
                frame.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                if (!roll.isInvalid() && !roll.isFailed() && roll.isCrit()) {
                    CheckBox check = roll.getCritCheckbox();
                    frame.addView(check);
                    Animation animCheck = AnimationUtils.loadAnimation(mC, R.anim.zoomin);
                    check.startAnimation(animCheck);
                }
                lineCrit.addView(frame);
            }
            mainAtkLin.addView(lineCrit);
        }
    }
}
