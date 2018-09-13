package stellarnear.wedge_dealer.TextFilling;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import stellarnear.stellarnear.R;
import stellarnear.wedge_dealer.Rolls.Roll;
import stellarnear.wedge_dealer.Rolls.RollList;

public class PostRandValues {
    private Context mC;
    private View mainView;
    private RollList rollList;

    public PostRandValues(Context mC, View mainView, RollList rollList) {
        this.mC = mC;
        this.mainView = mainView;
        this.rollList = rollList;
        addRandDices();
        addPostRandValues();
    }

    private void addRandDices() {
        LinearLayout line = new LinearLayout(mC);
        line.setOrientation(LinearLayout.HORIZONTAL);
        line.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        line.setGravity(Gravity.CENTER);
        line.removeAllViews();
        line.setVisibility(View.VISIBLE);
        LinearLayout mainAtkLin = mainView.findViewById(R.id.mainLinearAtk);
        Boolean fail = false;
        for (Roll roll : rollList.getList()) {
            ImageView diceImg = roll.getImgAtk();
            if (fail) {
                roll.invalidated();
            } else {
                if (roll.isFailed()) {
                    fail = true;
                }
            }
            LinearLayout diceBox = new LinearLayout(mC);
            diceBox.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            diceBox.setGravity(Gravity.CENTER);
            if (diceImg.getParent() != null) {
               ((ViewGroup) diceImg.getParent()).removeView(diceImg);
            }
            diceBox.addView(diceImg);
            line.addView(diceBox);
        }
        mainAtkLin.addView(line);
    }

    private void addPostRandValues() {
        LinearLayout line = new LinearLayout(mC);
        line.setOrientation(LinearLayout.HORIZONTAL);
        line.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        line.setGravity(Gravity.CENTER);
        LinearLayout mainAtkLin = mainView.findViewById(R.id.mainLinearAtk);
        int allRollSet = 0;
        for (Roll roll : rollList.getList()) {
            TextView atkTxt = new TextView(mC);
            atkTxt.setGravity(Gravity.CENTER);
            atkTxt.setTextColor(Color.DKGRAY);
            atkTxt.setTextSize(22);
            atkTxt.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            atkTxt.setText("?");
            if (roll.isInvalid()) {
                atkTxt.setText("-");
                allRollSet += 1;
            } else {
                if ((roll.getAtkValue() != 0)) {
                    atkTxt.setText(String.valueOf(roll.getAtkValue()));
                    allRollSet += 1;
                }
            }
            atkTxt.setGravity(Gravity.CENTER);
            atkTxt.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            line.addView(atkTxt);
        }

        mainAtkLin.addView(line);
        if (allRollSet == rollList.getList().size()) {
            //getHitAndCritLines();
        }
    }
}
