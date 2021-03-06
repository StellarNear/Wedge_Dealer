package stellarnear.wedge_companion.TextFilling;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Rolls.Roll;
import stellarnear.wedge_companion.Rolls.RollList;

public class PreRandValues {
    private Context mC;
    private View mainView;
    private RollList rollList;

    public PreRandValues(Context mC, View mainView, RollList rollList) {
        this.mC = mC;
        this.mainView = mainView;
        this.rollList = rollList;
        addPreRandValues();
    }

    public void hideViews() {
        mainView.findViewById(R.id.mainLinearNAtk).setVisibility(View.GONE);
        mainView.findViewById(R.id.mainLinearPreRand).setVisibility(View.GONE);
    }

    private void addPreRandValues() {
        TextView nAtt = mainView.findViewById(R.id.mainLinearNAtk);
        nAtt.setText(rollList.getList().size() + " attaques :");
        mainView.findViewById(R.id.mainLinearPreRand).setVisibility(View.VISIBLE);
        mainView.findViewById(R.id.mainLinearNAtk).setVisibility(View.VISIBLE);
        ((LinearLayout) mainView.findViewById(R.id.mainLinearPreRand)).removeAllViews();
        for (Roll roll : rollList.getList()) {
            LinearLayout scoreBox = new LinearLayout(mC);
            scoreBox.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            scoreBox.setGravity(Gravity.CENTER);
            TextView score = new TextView(mC);
            score.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            score.setGravity(Gravity.CENTER);
            score.setTextColor(Color.DKGRAY);
            score.setTextSize(22);
            score.setText("+" + roll.getPreRandValue());
            scoreBox.addView(score);
            ((LinearLayout) mainView.findViewById(R.id.mainLinearPreRand)).addView(scoreBox);
        }
    }

    public void refresh() {
        addPreRandValues();
    }
}
