package stellarnear.wedge_dealer.TextFilling;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import stellarnear.stellarnear.R;
import stellarnear.wedge_dealer.Rolls.Roll;
import stellarnear.wedge_dealer.Rolls.RollList;

public class PreRandValues {
    private Context mC;
    private View mainView;
    private RollList rollList;
    public PreRandValues(Context mC,View mainView, RollList rollList){
        this.mC=mC;
        this.mainView=mainView;
        this.rollList=rollList;
        addPreRandValues();
    }

    private void addPreRandValues() {
        TextView nAtt = new TextView(mC);
        nAtt.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,1));
        nAtt.setGravity(Gravity.CENTER);
        nAtt.setTextColor(Color.DKGRAY);
        nAtt.setTextSize(22);
        nAtt.setText(String.valueOf(rollList.getList().size()+" attaques"));
        LinearLayout mainAtkLin = mainView.findViewById(R.id.mainLinearAtk);
        mainAtkLin.removeAllViews();
        mainAtkLin.addView(nAtt);

        LinearLayout preRandValues=new LinearLayout(mC);
        preRandValues.setOrientation(LinearLayout.HORIZONTAL);
        preRandValues.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        preRandValues.setGravity(Gravity.CENTER);

        for(Roll roll : rollList.getList()){
            TextView score = new TextView(mC);
            score.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,1));
            score.setGravity(Gravity.CENTER);
            score.setTextColor(Color.DKGRAY);
            score.setTextSize(22);
            score.setText("+"+String.valueOf(roll.getPreRandValue()));
            preRandValues.addView(score);
        }

        mainAtkLin.addView(preRandValues);
    }

}
