package stellarnear.wedge_companion.Activities.MainActivityFramentCombat;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import stellarnear.wedge_companion.Elems.ElemsManager;
import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Rolls.Roll;
import stellarnear.wedge_companion.Rolls.RollList;
import stellarnear.wedge_companion.Tools;

public class MainActivityFragmentCombatEnnemyBitePanel {
    private Context mC;
    private RollList selectedRolls;
    private ImageView buttonBite;
    private LinearLayout bonusPanelRollList;
    private LinearLayout resultLinear;
    private TextView ptLeg;
    private View pannel;
    public Perso pj= PersoManager.getCurrentPJ();
    private boolean addBonusPanelIsVisible=false;
    private Tools tools=new Tools();
    private ElemsManager elems;

    private int nCostLeg=0;

    private Map<Roll,CheckBox> mapRollCheckbox=new HashMap<>();

    public MainActivityFragmentCombatEnnemyBitePanel(final Context mC, View mainPage){
        this.mC=mC;
        buttonBite =(ImageView) mainPage.findViewById(R.id.fab_leg_ennemy_bite);
        pannel = mainPage.findViewById(R.id.leg_ennemy_bite_linear);
        bonusPanelRollList = mainPage.findViewById(R.id.leg_ennemy_bite_roll_list);
        buttonBite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flipBonusPanel();
            }
        });
        resultLinear= mainPage.findViewById(R.id.leg_ennemy_bite_sum_result_linear);
        elems= ElemsManager.getInstance(mC);
        ptLeg=mainPage.findViewById(R.id.leg_ennemy_bite_pts);
        refreshSpendLeg();

        //todo confirmation avec PostData et setup de l'affichage des new degats boosted
    }

    private void refreshSpendLeg() {
        int currentLegPts=pj.getCurrentResourceValue("resource_legendary_points");
        String text ="Points légendaires : " +currentLegPts ;
        if(nCostLeg>0){
            text+=" > "+(currentLegPts-nCostLeg);
        }
        ptLeg.setText(text);
    }


    private void flipBonusPanel() {
        if (!addBonusPanelIsVisible){
            pannel.setVisibility(View.VISIBLE);
            Animation top = AnimationUtils.loadAnimation(mC,R.anim.infromtop);
            pannel.startAnimation(top);
            addBonusPanelIsVisible=true;
        } else {
            Animation bot = AnimationUtils.loadAnimation(mC,R.anim.outtotop);
            pannel.startAnimation(bot);
            pannel.setVisibility(View.GONE);
            addBonusPanelIsVisible=false;
        }
    }

    private void addRollsToBonusPanel() {
        mapRollCheckbox=new HashMap<>();
        bonusPanelRollList.removeAllViews();
        for (final Roll roll:selectedRolls.getList()){
            LinearLayout line =new LinearLayout(mC);
            line.setOrientation(LinearLayout.VERTICAL);
            line.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT,1));
            line.setGravity(Gravity.CENTER);
            int dmgTot=0;
            for(String elem : elems.getListKeysWedgeDamage()){
                int dmgElem = roll.getDmgSum(elem);
                if(dmgElem>0) {
                    TextView elemTxt = new TextView(mC);
                    elemTxt.setText(String.valueOf(dmgElem));
                    elemTxt.setTypeface(null, Typeface.BOLD);
                    elemTxt.setGravity(Gravity.CENTER);
                    elemTxt.setTextColor(elems.getColorIdDark(elem));
                    line.addView(elemTxt);
                    dmgTot+=dmgElem;
                }
            }

            if(dmgTot>0) {
                ImageView img = new ImageView(mC);
                if(roll.isCritConfirmed()){
                    img.setImageDrawable(tools.resize(mC,mC.getDrawable(R.drawable.critical_hit),100));
                } else {
                    img.setImageDrawable(tools.resize(mC,mC.getDrawable(R.drawable.simple_atk),100));
                }
                line.addView(img,0);

                final CheckBox check = new CheckBox(mC);
                LinearLayout.LayoutParams para = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                para.gravity=Gravity.CENTER;
                check.setLayoutParams(para);
                check.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //todo on chance check ? calcul des pts leg remaning etc
                        // faire un setBiteBoosted dans le roll ? et avec l'event de validation recaste un new Damage avec le selected roll (sans passer par le check)
                    }
                });
                mapRollCheckbox.put(roll,check);
                line.addView(check);
                bonusPanelRollList.addView(line);
            }
        }
        setListnersCheckbox();
    }

    private void setListnersCheckbox() {
        for( Map.Entry<Roll,CheckBox> entry : mapRollCheckbox.entrySet()){
            final CheckBox checkBox = entry.getValue();
            final Roll roll = entry.getKey();
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(checkBox.isChecked()){
                        int futureSpend;
                        if(roll.isCritConfirmed()){
                            futureSpend=2;
                        } else {
                            futureSpend=1;
                        }
                        if(pj.getCurrentResourceValue("resource_legendary_points")-nCostLeg-futureSpend>=0) {
                            nCostLeg+=futureSpend;
                            refreshResultDisplay();
                        } else {
                            tools.customToast(mC,"Tu n'aurais pas assez de points légendaires...");
                            checkBox.setChecked(false);
                        }
                    } else {
                        if(roll.isCritConfirmed()){
                            nCostLeg-=2;
                        } else {
                            nCostLeg--;
                        }
                        refreshResultDisplay();
                    }

                }
            });
        }
    }

    private void refreshResultDisplay() {
        resultLinear.removeAllViews();
        LinearLayout elemTitle = new LinearLayout(mC);
        elemTitle.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        LinearLayout lineprevious = new LinearLayout(mC);
        lineprevious.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        LinearLayout linenew = new LinearLayout(mC);
        linenew.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        LinearLayout resultElemsPercents = new LinearLayout(mC);
        resultElemsPercents.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        LinearLayout resultTotalPercent = new LinearLayout(mC);
        resultTotalPercent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        int sumTotalPrevious=0; int sumTotalNew=0;
        for(String elem : elems.getListKeysWedgeDamage()){
            elemTitle.addView(textElement(elems.getName(elem),elem));

            int sumElemPrevious=selectedRolls.getDmgSumFromType(elem); sumTotalPrevious+=sumElemPrevious;
            lineprevious.addView(textElement(String.valueOf(sumElemPrevious),elem));

            int sumElem = 0;
            for(Roll roll : selectedRolls.getList()){
                if(mapRollCheckbox.get(roll).isChecked()){
                    sumElem+=2*roll.getDmgSum(elem);
                } else {
                    sumElem+=roll.getDmgSum(elem);
                }
            }
            sumTotalNew+=sumElem;
            linenew.addView(textElement(String.valueOf(sumElem),elem));

            Double percentDouble = 100*((1.0*sumElem-1.0*sumElemPrevious)/(1.0*sumElemPrevious));
            resultElemsPercents.addView(textElement("+"+String.valueOf(percentDouble.intValue())+"%",elem));
        }
        resultLinear.addView(elemTitle);
        resultLinear.addView(lineprevious);
        resultLinear.addView(linenew);
        resultLinear.addView(resultElemsPercents);

        Double totalPercent =  100*((1.0*sumTotalNew-1.0*sumTotalPrevious)/(1.0*sumTotalPrevious));
        TextView resultTot = textElement("Total : "+sumTotalPrevious+" > "+sumTotalNew +" (+"+totalPercent.intValue()+"%)","");
        resultTot.setTypeface(null,Typeface.BOLD);resultTot.setTextSize(20);
        resultTotalPercent.addView(resultTot);
        resultLinear.addView(resultTotalPercent);

        refreshSpendLeg();
    }

    private TextView textElement(String txt, String elem) {
        TextView element=new TextView(mC);
        element.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT,1));
        element.setText(txt);
        element.setTextColor(elems.getColorIdDark(elem));
        element.setGravity(Gravity.CENTER);
        return element;
    }

    public void hide() {
        buttonBite.setVisibility(View.GONE);
        if(addBonusPanelIsVisible){flipBonusPanel();}
    }

    public void show() {
        buttonBite.setVisibility(View.VISIBLE);
    }

    public ImageView getButton() {
        return buttonBite;
    }

    public void feedSelectedRolls(RollList selectedRolls) {
        this.selectedRolls=selectedRolls;
        addRollsToBonusPanel();
    }
}
