package stellarnear.wedge_dealer.TextFilling;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
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

import stellarnear.stellarnear.R;
import stellarnear.wedge_dealer.Rolls.RollList;
import stellarnear.wedge_dealer.Tools;

public class Damages {
    private Context mC;
    private View mainView;
    private RollList selectedRolls;
    private SharedPreferences settings;
    private LinearLayout mainDmgLin;
    private List<String> elements;
    private Map<String,Integer> mapElemColor=new HashMap<>();

    private Tools tools=new Tools();

    public Damages(Context mC, View mainView, RollList selectedRolls) {
        this.mC = mC;
        this.mainView = mainView;
        this.selectedRolls = selectedRolls;
        this.settings = PreferenceManager.getDefaultSharedPreferences(mC);
        this.mainDmgLin = mainView.findViewById(R.id.mainLinearDmg);
        this.elements = Arrays.asList("","fire","shock","frost");
        this.mapElemColor.put("",R.color.phy);
        this.mapElemColor.put("fire",R.color.fire);
        this.mapElemColor.put("shock",R.color.shock);
        this.mapElemColor.put("frost",R.color.frost);

        TextView dmgTitle = new TextView(mC);
        dmgTitle.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,1));
        dmgTitle.setGravity(Gravity.CENTER);
        dmgTitle.setTextColor(Color.DKGRAY);
        dmgTitle.setTextSize(22);
        dmgTitle.setText("Dégâts :");
        mainDmgLin.addView(dmgTitle);

        addDamage();
    }

    private void addDamage() {
        LinearLayout lineLogos = new LinearLayout(mC);
        lineLogos.setOrientation(LinearLayout.HORIZONTAL);
        lineLogos.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        lineLogos.setGravity(Gravity.CENTER);
        LinearLayout lineSums = new LinearLayout(mC);
        lineSums.setOrientation(LinearLayout.HORIZONTAL);
        lineSums.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        lineSums.setGravity(Gravity.CENTER);
        for (String elem : elements){
            addElementDamage(elem,lineLogos,lineSums);
        }
        mainDmgLin.addView(lineLogos);
        mainDmgLin.addView(lineSums);
    }

    private void addElementDamage(String elem,LinearLayout lineLogos,LinearLayout lineSums) {
        int sum = selectedRolls.getDmgSumFromType(elem);
        if (sum>0){
            addElementLogo(elem,lineLogos);
            addElementSum(elem,lineSums);
        }
    }

    private void addElementLogo(String elem,LinearLayout line) {
        LinearLayout logoBox = new LinearLayout(mC);
        logoBox.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        logoBox.setGravity(Gravity.CENTER);
        ImageView logo = new ImageView(mC);
        if(elem.equalsIgnoreCase("")){elem="phy";}
        int drawableId = mC.getResources().getIdentifier(elem+"_logo", "drawable", mC.getPackageName());
        logo.setImageDrawable(tools.resize(mC, drawableId, mC.getResources().getDimensionPixelSize(R.dimen.logo_element)));
        if (logo.getParent() != null) {
            ((ViewGroup) logo.getParent()).removeView(logo);
        }
        logoBox.addView(logo);
        line.addView(logoBox);
    }

    private void addElementSum(String elem,LinearLayout line) {
        LinearLayout sumBox = new LinearLayout(mC);
        sumBox.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        sumBox.setGravity(Gravity.CENTER);
        TextView sumTxt = new TextView(mC);
        sumTxt.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        sumTxt.setGravity(Gravity.CENTER);
        sumTxt.setTypeface(null, Typeface.BOLD);
        sumTxt.setTextColor(mC.getResources().getColor(mapElemColor.get(elem)));
        sumTxt.setTextSize(30);
        sumTxt.setText(String.valueOf(selectedRolls.getDmgSumFromType(elem)));
        sumBox.addView(sumTxt);
        line.addView(sumBox);
    }
}
