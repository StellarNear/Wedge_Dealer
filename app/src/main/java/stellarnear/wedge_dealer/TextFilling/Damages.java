package stellarnear.wedge_dealer.TextFilling;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stellarnear.wedge_dealer.R;
import stellarnear.wedge_dealer.Rolls.Roll;
import stellarnear.wedge_dealer.Rolls.RollList;
import stellarnear.wedge_dealer.Tools;

public class Damages {
    private Activity mA;
    private Context mC;
    private View mainView;
    private RollList selectedRolls;
    private List<String> elements;
    private Map<String, Integer> mapElemColor = new HashMap<>();

    private Tools tools = new Tools();

    public Damages(Activity mA,Context mC, View mainView, RollList selectedRolls) {
        this.mA = mA;
        this.mC = mC;
        this.mainView = mainView;
        this.selectedRolls = selectedRolls;
        this.elements = Arrays.asList("", "fire", "shock", "frost");
        this.mapElemColor.put("", R.color.phy);
        this.mapElemColor.put("fire", R.color.fire);
        this.mapElemColor.put("shock", R.color.shock);
        this.mapElemColor.put("frost", R.color.frost);


        clearAllViews();
        addDamage();
        for (Roll roll : selectedRolls.getList()) {
            roll.isDelt();
        }
    }

    public void hideViews() {
        ((View) mainView.findViewById(R.id.bar_sep)).setVisibility(View.GONE);
        ((TextView) mainView.findViewById(R.id.mainLinearDmgTitle)).setVisibility(View.GONE);
        ((LinearLayout) mainView.findViewById(R.id.mainLinearLogoDmg)).setVisibility(View.GONE);
        ((LinearLayout) mainView.findViewById(R.id.mainLinearSumDmg)).setVisibility(View.GONE);
    }

    private void showViews() {
        ((View) mainView.findViewById(R.id.bar_sep)).setVisibility(View.VISIBLE);
        ((TextView) mainView.findViewById(R.id.mainLinearDmgTitle)).setVisibility(View.VISIBLE);
        ((LinearLayout) mainView.findViewById(R.id.mainLinearLogoDmg)).setVisibility(View.VISIBLE);
        ((LinearLayout) mainView.findViewById(R.id.mainLinearSumDmg)).setVisibility(View.VISIBLE);
    }

    private void clearAllViews() {
        ((LinearLayout) mainView.findViewById(R.id.mainLinearLogoDmg)).removeAllViews();
        ((LinearLayout) mainView.findViewById(R.id.mainLinearSumDmg)).removeAllViews();
    }

    private void addDamage() {
        int totalSum = 0;
        for (String elem : elements) {
            for (Roll roll : selectedRolls.getList()) {
                roll.setDmgRand();
            }
            int sumElem = selectedRolls.getDmgSumFromType(elem);
            if (sumElem > 0) {
                totalSum+=sumElem;
                addElementDamage(elem);
            }
        }

        if (totalSum>0){
            showViews();
            ((TextView) mainView.findViewById(R.id.mainLinearDmgTitle)).setText("Dégâts : "+String.valueOf(totalSum));
            checkHighscore(totalSum);
        } else {
            ((View) mainView.findViewById(R.id.bar_sep)).setVisibility(View.VISIBLE);
            ((TextView) mainView.findViewById(R.id.mainLinearDmgTitle)).setVisibility(View.VISIBLE);
            ((TextView) mainView.findViewById(R.id.mainLinearDmgTitle)).setText("Aucun dégât");
        }
    }

    private void addElementDamage(String elem) {
        addElementLogo(elem);
        addElementSum(elem);
    }

    private void addElementLogo(String elem) {
        LinearLayout logoBox = new LinearLayout(mC);
        logoBox.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        logoBox.setGravity(Gravity.CENTER);
        ImageView logo = new ImageView(mC);
        if (elem.equalsIgnoreCase("")) {
            elem = "phy";
        }
        int drawableId = mC.getResources().getIdentifier(elem + "_logo", "drawable", mC.getPackageName());
        logo.setImageDrawable(tools.resize(mC, drawableId, mC.getResources().getDimensionPixelSize(R.dimen.logo_element)));
        if (logo.getParent() != null) {
            ((ViewGroup) logo.getParent()).removeView(logo);
        }
        logoBox.addView(logo);
        ((LinearLayout) mainView.findViewById(R.id.mainLinearLogoDmg)).addView(logoBox);
    }

    private void addElementSum(String elem) {
        LinearLayout sumBox = new LinearLayout(mC);
        sumBox.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        sumBox.setGravity(Gravity.CENTER);
        TextView sumTxt = new TextView(mC);
        sumTxt.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        sumTxt.setGravity(Gravity.CENTER);
        sumTxt.setTypeface(null, Typeface.BOLD);
        sumTxt.setTextColor(mC.getResources().getColor(mapElemColor.get(elem)));
        sumTxt.setTextSize(35);
        sumTxt.setText(String.valueOf(selectedRolls.getDmgSumFromType(elem)));
        sumBox.addView(sumTxt);
        ((LinearLayout) mainView.findViewById(R.id.mainLinearSumDmg)).addView(sumBox);
    }

    private void checkHighscore(int sumDmg) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
        int currentHigh = new Tools().toInt(settings.getString("highscore", "0"));
        if (currentHigh < sumDmg) {
            settings.edit().putString("highscore",String.valueOf(sumDmg)).apply();
            Tools tools = new Tools();
            tools.playVideo(mA,mC,"/raw/explosion");
            tools.customToast(mC, String.valueOf(sumDmg) + " dégats !\nC'est un nouveau record !", "center");
        }
    }
}
