package stellarnear.wedge_companion.TextFilling;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.PostData;
import stellarnear.wedge_companion.PostDataElement;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Rolls.AtkRoll;
import stellarnear.wedge_companion.Rolls.Dices.Dice;
import stellarnear.wedge_companion.Rolls.RangeAtkRoll;
import stellarnear.wedge_companion.Rolls.RangedRoll;
import stellarnear.wedge_companion.Rolls.Roll;
import stellarnear.wedge_companion.Rolls.RollList;

public class PostRandValues {
    private Context mC;
    private View mainView;
    private RollList rollList;
    private SharedPreferences settings;
    private Perso pj= PersoManager.getCurrentPJ();

    public PostRandValues(Context mC, View mainView, RollList rollList) {
        this.mC = mC;
        this.mainView = mainView;
        this.rollList = rollList;
        this.settings = PreferenceManager.getDefaultSharedPreferences(mC);
        showViews();
        clearAllView();
        addRandDices();
        addPostRandValues();
    }

    private void clearAllView() {
        ((LinearLayout)mainView.findViewById(R.id.mainLinearAtkDices)).removeAllViews();
        ((LinearLayout)mainView.findViewById(R.id.mainLinearMultishot)).removeAllViews();
        ((LinearLayout)mainView.findViewById(R.id.mainLinearPostRand)).removeAllViews();
    }

    private void showViews() {
        ((LinearLayout)mainView.findViewById(R.id.mainLinearAtkDices)).setVisibility(View.VISIBLE);
        ((LinearLayout)mainView.findViewById(R.id.mainLinearMultishot)).setVisibility(View.VISIBLE);
        ((LinearLayout)mainView.findViewById(R.id.mainLinearPostRand)).setVisibility(View.VISIBLE);
    }

    public void hideViews() {
        ((LinearLayout)mainView.findViewById(R.id.mainLinearAtkDices)).setVisibility(View.GONE);
        ((LinearLayout)mainView.findViewById(R.id.mainLinearMultishot)).setVisibility(View.GONE);
        ((LinearLayout)mainView.findViewById(R.id.mainLinearPostRand)).setVisibility(View.GONE);
    }

    private void addRandDices() {
        Boolean fail = false;
        for (Roll roll : rollList.getList()) {
            roll.getAtkRoll().setAtkRand();
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
            ((LinearLayout)mainView.findViewById(R.id.mainLinearAtkDices)).addView(diceBox);

            roll.getAtkRoll().getAtkDice().setMythicEventListener(new Dice.OnMythicEventListener() {
                @Override
                public void onEvent() {
                    refreshPostRandValues();
                }
            });
        }
        checkForMultipleArrows();
    }

    public void refreshPostRandValues() {
        ((LinearLayout)mainView.findViewById(R.id.mainLinearPostRand)).removeAllViews();
        addPostRandValues();
    }

    private void checkForMultipleArrows() {
        if (pj.featIsActive("feat_manyshot_suprem")) {
            String multi_val_str = settings.getString("feat_manyshot_suprem_val", String.valueOf(mC.getResources().getInteger(R.integer.feat_manyshot_suprem_val_def)));

            for (Roll roll : rollList.getList()) {
                LinearLayout txtBox = new LinearLayout(mC);
                txtBox.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                txtBox.setGravity(Gravity.CENTER);
                TextView multiple = new TextView(mC);
                multiple.setGravity(Gravity.CENTER);
                multiple.setTextColor(Color.DKGRAY);
                multiple.setTextSize(15);
                multiple.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                multiple.setText("x"+multi_val_str);
                txtBox.addView(multiple);
                ((LinearLayout)mainView.findViewById(R.id.mainLinearMultishot)).addView(txtBox);
            }
        }
    }

    private void addPostRandValues() {
        for (Roll roll : rollList.getList()) {
            LinearLayout txtBox = new LinearLayout(mC);
            txtBox.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            txtBox.setGravity(Gravity.CENTER);
            TextView atkTxt = new TextView(mC);
            atkTxt.setGravity(Gravity.CENTER);
            atkTxt.setTextColor(Color.DKGRAY);
            atkTxt.setTextSize(22);
            atkTxt.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            atkTxt.setText("?");
            if (roll.isInvalid()) {
                atkTxt.setText("-");
            } else {
                if ((roll.getAtkValue() != 0)) {
                    atkTxt.setText(String.valueOf(roll.getAtkValue()));
                }
            }
            atkTxt.setGravity(Gravity.CENTER);
            atkTxt.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            txtBox.addView(atkTxt);
            ((LinearLayout)mainView.findViewById(R.id.mainLinearPostRand)).addView(txtBox);
        }
        new PostData(mC,new PostDataElement(rollList,"atk"));
    }
}
