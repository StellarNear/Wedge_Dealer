package stellarnear.wedge_companion.TextFilling;

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

import stellarnear.wedge_companion.Elems.ElemsManager;
import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Rolls.Roll;
import stellarnear.wedge_companion.Rolls.RollList;
import stellarnear.wedge_companion.Tools;

public class Damages {
    private Activity mA;
    private Context mC;
    private View mainView;
    private RollList selectedRolls;
    private ElemsManager elems;
    private Perso pj = PersoManager.getCurrentPJ();
    private int totalSum;

    private Tools tools = new Tools();

    public Damages(Activity mA,Context mC, View mainView, RollList selectedRolls) {
        this.mA = mA;
        this.mC = mC;
        this.mainView = mainView;
        this.selectedRolls = selectedRolls;
        this.elems= ElemsManager.getInstance(mC);

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
        totalSum = 0;
        for (String elem : elems.getListKeysWedgeDamage()) {
            for (Roll roll : selectedRolls.getList()) {
                roll.setDmgRand();
            }
            int sumElem = selectedRolls.getDmgSumFromType(elem);
            if (sumElem > 0) {
                totalSum += sumElem;
                addElementDamage(elem);
            }
        }

        if (totalSum>0){
            //if(pj.getAllForms()==null || !pj.getAllForms().hasActiveForm() ){pj.getStats().storeStatsFromRolls(selectedRolls);}       //storing results
            showViews();
            ((TextView) mainView.findViewById(R.id.mainLinearDmgTitle)).setText("Dégâts : "+String.valueOf(totalSum));
            checkHighscore();
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
        logo.setImageDrawable(tools.resize(mC, elems.getDrawableId(elem), mC.getResources().getDimensionPixelSize(R.dimen.logo_element)));
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
        sumTxt.setTextColor(elems.getColorIdDark(elem));
        sumTxt.setTextSize(35);
        sumTxt.setText(String.valueOf(selectedRolls.getDmgSumFromType(elem)));
        sumBox.addView(sumTxt);
        ((LinearLayout) mainView.findViewById(R.id.mainLinearSumDmg)).addView(sumBox);
    }

    private void checkHighscore() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
        int currentHigh = new Tools().toInt(settings.getString("highscore"+PersoManager.getPJSuffix(), "0"));
        if (currentHigh < totalSum) {
            settings.edit().putString("highscore"+PersoManager.getPJSuffix(),String.valueOf(totalSum)).apply();
            Tools tools = new Tools();
            tools.playVideo(mA,mC,"/raw/explosion");
            tools.customToast(mC, String.valueOf(totalSum) + " dégats !\nC'est un nouveau record !", "center");
        }
    }

    public int getDamageTot() {
        return totalSum;
    }

    public void refreshDisplay() {
        clearAllViews();
        addDamage();
    }
}
