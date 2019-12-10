package stellarnear.wedge_companion.PetTextFilling;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import stellarnear.wedge_companion.Elems.AttacksElemsManager;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Rolls.ProbaFromDiceRand;
import stellarnear.wedge_companion.Rolls.RollList;

public class PetRangesAndProba {
    private Context mC;
    private AttacksElemsManager elems;
    private View mainPage;
    private ProbaFromDiceRand probaForRolls;
    private RollList selectedRolls;

    public PetRangesAndProba(Context mC, View mainPage, RollList selectedRolls) {
        this.mC = mC;
        this.selectedRolls = selectedRolls;
        this.mainPage = mainPage;
        this.elems= AttacksElemsManager.getInstance(mC);
        this.probaForRolls = new ProbaFromDiceRand(selectedRolls);
        showViews();
        addRanges();
        addProbas();
    }

    public void hideViews() {
        ((LinearLayout) mainPage.findViewById(R.id.mainLinearRangeDmg)).setVisibility(View.GONE);
        ((LinearLayout) mainPage.findViewById(R.id.mainLinearProbaDmg)).setVisibility(View.GONE);
        ((TextView) mainPage.findViewById(R.id.mainLinearProbaTitle)).setVisibility(View.GONE);
    }

    private void showViews() {
        ((LinearLayout) mainPage.findViewById(R.id.mainLinearRangeDmg)).setVisibility(View.VISIBLE);
        ((LinearLayout) mainPage.findViewById(R.id.mainLinearProbaDmg)).setVisibility(View.VISIBLE);
        ((TextView) mainPage.findViewById(R.id.mainLinearProbaTitle)).setVisibility(View.VISIBLE);
    }

    private void addRanges() {
        ((LinearLayout) mainPage.findViewById(R.id.mainLinearRangeDmg)).removeAllViews();
        for (String elem : elems.getListKeys()) {
            if (selectedRolls.getDmgSumFromType(elem) > 0) {
                probaForRolls.getRange(elem);
                LinearLayout sumBox = new LinearLayout(mC);
                sumBox.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                sumBox.setGravity(Gravity.CENTER);
                TextView rangeTxt = new TextView(mC);
                rangeTxt.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                rangeTxt.setGravity(Gravity.CENTER);
                rangeTxt.setTextColor(elems.getColorId(elem));
                rangeTxt.setTextSize(15);
                rangeTxt.setText(probaForRolls.getRange(elem));
                sumBox.addView(rangeTxt);
                ((LinearLayout) mainPage.findViewById(R.id.mainLinearRangeDmg)).addView(sumBox);
            }
        }
    }

    private void addProbas() {
        ((LinearLayout) mainPage.findViewById(R.id.mainLinearProbaDmg)).removeAllViews();
        for (String elem : elems.getListKeys()) {
            if (selectedRolls.getDmgSumFromType(elem) > 0) {
                probaForRolls.getRange(elem);
                LinearLayout sumBox = new LinearLayout(mC);
                sumBox.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                sumBox.setGravity(Gravity.CENTER);
                TextView probaTextview = new TextView(mC);
                probaTextview.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                probaTextview.setGravity(Gravity.CENTER);
                probaTextview.setTextColor(elems.getColorId(elem));
                probaTextview.setTextSize(15);
                String probaTxt=probaForRolls.getProba(elem);
                if (elem.equalsIgnoreCase("") && selectedRolls.haveAnyCritConfirmed()){
                    probaTxt+="\ncrit:\n"+probaForRolls.getProba("",true);
                }
                probaTextview.setText(probaTxt);
                sumBox.addView(probaTextview);
                ((LinearLayout) mainPage.findViewById(R.id.mainLinearProbaDmg)).addView(sumBox);
            }
        }
    }

}

