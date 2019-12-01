package stellarnear.wedge_companion.FormSpell;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import stellarnear.wedge_companion.DisplayRolls;
import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Rolls.DiceList;
import stellarnear.wedge_companion.Rolls.Dices.Dice;
import stellarnear.wedge_companion.Rolls.ProbaFromDiceRand;
import stellarnear.wedge_companion.Spells.DisplayedText;
import stellarnear.wedge_companion.Tools;

public class FormResultBuilder {

    private Perso pj = PersoManager.getCurrentPJ();
    private FormPower spell;
    private int spellColorId;
    private Context mC;
    private Activity mA;
    private FormCalculationSpell calculationSpell =new FormCalculationSpell();
    private DisplayedText displayedText=new DisplayedText();
    private Tools tools=new Tools();


    public FormResultBuilder(Activity mA, Context mC, FormPower spell){
        this.mA=mA;
        this.mC=mC;
        this.spell=spell;
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
        } else {
            final DiceList diceList = new DiceList();
            List<Integer> listDiceAllowed = Arrays.asList(3,4,6,8,10);
            if (listDiceAllowed.contains(calculationSpell.diceType(spell))){
                for(int i = 1; i<= calculationSpell.nDice(spell); i++){
                    diceList.add(new Dice(mA,mC, calculationSpell.diceType(spell),spell.getDmg_type()));
                }
                for (Dice dice : diceList.getList()){
                    dice.rand(false);
                }
            }
            int sumDmg =diceList.getSum();
            if(spell.getFlat_dmg()>0){sumDmg+=spell.getFlat_dmg();}

            ((TextView)resultTemplate.findViewById(R.id.damage)).setText(String.valueOf(sumDmg));
            spell.setDmgResult(sumDmg);

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

    private void setSpellColor() {
        switch (spell.getDmg_type()){
            case "froid":
                spellColorId =mC.getColor(R.color.froid_dark);
                break;

            case "feu":
                spellColorId =mC.getColor(R.color.feu_dark);
                break;

            case "foudre":
                spellColorId =mC.getColor(R.color.foudre_dark);
                break;

            case "acide":
                spellColorId =mC.getColor(R.color.acide_dark);
                break;

            default:
                spellColorId=mC.getColor(R.color.aucun_dark);
                break;
        }
    }
}
