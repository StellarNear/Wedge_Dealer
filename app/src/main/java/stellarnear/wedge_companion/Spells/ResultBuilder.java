package stellarnear.wedge_companion.Spells;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import stellarnear.wedge_companion.DisplayRolls;
import stellarnear.wedge_companion.Elems.ElemsManager;
import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Rolls.DiceList;
import stellarnear.wedge_companion.Rolls.Dices.Dice;
import stellarnear.wedge_companion.Rolls.ProbaFromDiceRand;
import stellarnear.wedge_companion.Tools;

public class ResultBuilder {

    private Perso pj = PersoManager.getCurrentPJ();
    private Spell spell;
    private int spellColorId;
    private Context mC;
    private Activity mA;
    private CalculationSpell calculationSpell ;
    private DisplayedText displayedText=new DisplayedText();
    private Tools tools=new Tools();


    public ResultBuilder(Activity mA, Context mC, Spell spell){
        this.mA=mA;
        this.mC=mC;
        this.spell=spell;
        calculationSpell=new CalculationSpell();
        setSpellColor();
    }

    public void addResults(LinearLayout layout) {
        LayoutInflater inflater = mA.getLayoutInflater();
        LinearLayout resultTemplate = (LinearLayout) inflater.inflate(R.layout.spell_profile_result, null);
        if (spell.getDmg_type().equalsIgnoreCase("")) {
            resultTemplate.removeAllViews();
            TextView txt_view = new TextView(mC);
            txt_view.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            txt_view.setText("Sortilège " + spell.getName() + " lancé !");
            txt_view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
            resultTemplate.addView(txt_view);
        } else if ( spell.getMetaList().metaIdIsActive("meta_max") || !displayedText.damageTxt(spell).contains("d") ) {
            resultTemplate.removeAllViews();
            TextView txt_view = new TextView(mC);
            txt_view.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            txt_view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
            int sumDmg=tools.toInt(displayedText.damageTxt(spell));
            if(spell.isCrit()){
                addExplo(resultTemplate);
                sumDmg = 2*sumDmg;
            }

            resultTemplate.addView(txt_view);

            if(spell.isCrit()) {
                addExplo(resultTemplate);
            }
            if(spell.getDmg_type().equalsIgnoreCase("heal")){
                txt_view.setText(sumDmg + " soins !");
            } else {
                txt_view.setText(sumDmg + " dégâts !");
            }
            txt_view.setTextColor(spellColorId);
            spell.setDmgResult(sumDmg);
            checkHighScore(sumDmg);
        } else {
            final DiceList diceList = new DiceList();
            List<Integer> listDiceAllowed = Arrays.asList(3,4,6,8,10,12);
            if (listDiceAllowed.contains(calculationSpell.diceType(spell))){
                for(int i = 1; i<= calculationSpell.nDice(spell); i++){
                    diceList.add(new Dice(mA,mC, calculationSpell.diceType(spell),spell.getDmg_type()));
                }
                for (Dice dice : diceList.getList()){
                    dice.rand(false);
                }
            }
            if(spell.getDmg_type().equalsIgnoreCase("heal")){((TextView)resultTemplate.findViewById(R.id.damage_title)).setText("Soins :");}
            ((TextView)resultTemplate.findViewById(R.id.highscore)).setText("(record:"+String.valueOf(spell.getHighscore(mC))+")");
            resultTemplate.findViewById(R.id.highscore).setVisibility(View.VISIBLE);
            int sumDmg =diceList.getSum();
            if(calculationSpell.getFlatDmg(spell)>0){sumDmg+=calculationSpell.getFlatDmg(spell);}
            if(spell.isCrit()){
                addExplo((LinearLayout) resultTemplate.findViewById(R.id.damage_top_icon));
                addExplo((LinearLayout) resultTemplate.findViewById(R.id.damage_bot_icon));
                sumDmg = 2*sumDmg;
            }

            ((TextView)resultTemplate.findViewById(R.id.damage)).setText(String.valueOf(sumDmg));
            spell.setDmgResult(sumDmg);
            checkHighScore(sumDmg);

            ((TextView)resultTemplate.findViewById(R.id.damage)).setTextColor(spellColorId);

            ProbaFromDiceRand probaFromDiceRand = new ProbaFromDiceRand(diceList);
            ((TextView)resultTemplate.findViewById(R.id.damage_range)).setText(String.valueOf(probaFromDiceRand.getRange()));
            ((TextView)resultTemplate.findViewById(R.id.damage_range)).setTextColor(spellColorId);
            ((TextView)resultTemplate.findViewById(R.id.proba)).setText(String.valueOf(probaFromDiceRand.getProba()));
            ((TextView)resultTemplate.findViewById(R.id.proba)).setTextColor(spellColorId);
            ((FloatingActionButton)resultTemplate.findViewById(R.id.fab_detail)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DisplayRolls displayRolls=new DisplayRolls(mA,mC,diceList);
                    displayRolls.showPopup();
                }
            });
        }
        layout.addView(resultTemplate);
    }

    private void addExplo(LinearLayout resultTemplate) {
        Drawable explo = mC.getDrawable(R.drawable.ic_grade_black_24dp);
        explo.mutate().setColorFilter(spellColorId,PorterDuff.Mode.SRC_IN);
        ImageView exploImg =new ImageView(mC);
        exploImg.setImageDrawable(explo);
        resultTemplate.addView(exploImg);
    }

    private void checkHighScore(int sumDmg) {
        if(spell.isHighscore(sumDmg,mC)){
            Tools tools = new Tools();
            tools.playVideo(mA,mC,"/raw/explosion");
            tools.customToast(mC, String.valueOf(sumDmg) + " dégats !\nC'est un nouveau record !", "center");
        }
    }

    private void setSpellColor() {
        ElemsManager elemsManager = ElemsManager.getInstance(mC);
        try {
            spellColorId= elemsManager.getColorIdDark(spell.getDmg_type());
        } catch (Exception e) {
            spellColorId=mC.getColor(R.color.aucun_dark);
            e.printStackTrace();
        }
    }
}
