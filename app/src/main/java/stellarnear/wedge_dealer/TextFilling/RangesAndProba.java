package stellarnear.wedge_dealer.TextFilling;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stellarnear.wedge_dealer.R;
import stellarnear.wedge_dealer.Rolls.ProbaFromDiceRand;
import stellarnear.wedge_dealer.Rolls.RollList;

public class RangesAndProba {
    private Context mC;
    private List<String> elements;
    private View mainPage;
    private ProbaFromDiceRand probaForRolls;
    private RollList selectedRolls;
    private Map<String, Integer> mapElemColor = new HashMap<>();

    public RangesAndProba(Context mC, View mainPage, RollList selectedRolls) {
        this.mC = mC;
        this.selectedRolls = selectedRolls;
        this.mainPage = mainPage;
        this.elements = Arrays.asList("", "fire", "shock", "frost");
        this.probaForRolls = new ProbaFromDiceRand(selectedRolls);
        this.mapElemColor.put("", R.color.phy);
        this.mapElemColor.put("fire", R.color.fire);
        this.mapElemColor.put("shock", R.color.shock);
        this.mapElemColor.put("frost", R.color.frost);
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
        for (String elem : elements) {
            if (selectedRolls.getDmgSumFromType(elem) > 0) {
                probaForRolls.getRange(elem);
                LinearLayout sumBox = new LinearLayout(mC);
                sumBox.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                sumBox.setGravity(Gravity.CENTER);
                TextView rangeTxt = new TextView(mC);
                rangeTxt.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                rangeTxt.setGravity(Gravity.CENTER);
                rangeTxt.setTextColor(mC.getResources().getColor(mapElemColor.get(elem)));
                rangeTxt.setTextSize(20);
                rangeTxt.setText(probaForRolls.getRange(elem));
                sumBox.addView(rangeTxt);
                ((LinearLayout) mainPage.findViewById(R.id.mainLinearRangeDmg)).addView(sumBox);
            }
        }
    }


    private void addProbas() {
        ((LinearLayout) mainPage.findViewById(R.id.mainLinearProbaDmg)).removeAllViews();
        for (String elem : elements) {
            if (selectedRolls.getDmgSumFromType(elem) > 0) {
                probaForRolls.getRange(elem);
                LinearLayout sumBox = new LinearLayout(mC);
                sumBox.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                sumBox.setGravity(Gravity.CENTER);
                TextView probaTextview = new TextView(mC);
                probaTextview.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                probaTextview.setGravity(Gravity.CENTER);
                probaTextview.setTextColor(mC.getResources().getColor(mapElemColor.get(elem)));
                probaTextview.setTextSize(20);
                String probaTxt=probaForRolls.getProba(elem);
                if (elem.equalsIgnoreCase("") && selectedRolls.haveAnyCrit()){
                    probaTxt+="\ncrit:\n"+probaForRolls.getProba("",true);
                }
                probaTextview.setText(probaTxt);
                sumBox.addView(probaTextview);
                ((LinearLayout) mainPage.findViewById(R.id.mainLinearProbaDmg)).addView(sumBox);
            }
        }
    }

}

