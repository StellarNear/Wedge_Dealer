package stellarnear.wedge_companion.PetTextFilling;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import stellarnear.wedge_companion.PostData;
import stellarnear.wedge_companion.PostDataElement;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Rolls.Dices.Dice20;
import stellarnear.wedge_companion.Rolls.Roll;
import stellarnear.wedge_companion.Rolls.RollList;
import stellarnear.wedge_companion.Tools;

public class PetPostRandValues {
    private Context mC;
    private View mainView;
    private RollList rollList;
    private SharedPreferences settings;
    private Tools tools = Tools.getTools();

    public PetPostRandValues(Context mC, View mainView, RollList rollList) {
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
        ((LinearLayout) mainView.findViewById(R.id.mainLinearAtkDices)).removeAllViews();
        ((LinearLayout) mainView.findViewById(R.id.mainLinearPostRand)).removeAllViews();
    }

    private void showViews() {
        mainView.findViewById(R.id.mainLinearAtkDices).setVisibility(View.VISIBLE);
        mainView.findViewById(R.id.mainLinearPostRand).setVisibility(View.VISIBLE);
    }

    public void hideViews() {
        mainView.findViewById(R.id.mainLinearAtkDices).setVisibility(View.GONE);
        mainView.findViewById(R.id.mainLinearPostRand).setVisibility(View.GONE);
    }

    private void addRandDices() {
        Boolean fail = false;
        for (Roll roll : rollList.getList()) {
            roll.getAtkRoll().setAtkRand();
            if (fail) {
                roll.invalidated();
            } else {
                if (roll.isFailed()) {
                    fail = true;
                }
            }
            View diceImg = roll.getImgAtk();
            LinearLayout diceBox = new LinearLayout(mC);
            diceBox.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 0, 1));
            diceBox.setGravity(Gravity.CENTER);
            if (diceImg.getParent() != null) {
                ((ViewGroup) diceImg.getParent()).removeView(diceImg);
            }
            diceImg.setLayoutParams(new LinearLayout.LayoutParams(mC.getResources().getDimensionPixelSize(R.dimen.icon_main_dices_pet_size),mC.getResources().getDimensionPixelSize(R.dimen.icon_main_dices_pet_size)));
            diceBox.addView(diceImg);
            ((LinearLayout) mainView.findViewById(R.id.mainLinearAtkDices)).addView(diceBox);

            roll.getAtkRoll().getAtkDice().setMythicEventListener(new Dice20.OnMythicEventListener() {
                @Override
                public void onEvent() {
                    refreshPostRandValues();
                }
            });
        }
    }

    public void refreshPostRandValues() {
        ((LinearLayout) mainView.findViewById(R.id.mainLinearPostRand)).removeAllViews();
        addPostRandValues();
    }


    private void addPostRandValues() {
        for (Roll roll : rollList.getList()) {
            LinearLayout txtBox = new LinearLayout(mC);
            txtBox.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 0, 1));
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
            ((LinearLayout) mainView.findViewById(R.id.mainLinearPostRand)).addView(txtBox);
        }
        new PostData(mC, new PostDataElement(rollList, "atk"));
    }
}
