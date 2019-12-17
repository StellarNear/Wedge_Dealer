package stellarnear.wedge_companion;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import stellarnear.wedge_companion.Rolls.DiceList;
import stellarnear.wedge_companion.Rolls.Dices.Dice;
import stellarnear.wedge_companion.Rolls.DmgRoll;
import stellarnear.wedge_companion.Rolls.Roll;
import stellarnear.wedge_companion.Rolls.RollList;

public class DisplayRolls {
    private RollList selectedRolls=null;
    private DiceList selectedDiceList=null;
    private Context mC;
    private View mainView;
    private CustomAlertDialog metaPopup;

    public DisplayRolls(Activity mA,Context mC, RollList selectedRolls) {
        this.mC=mC;
        this.selectedRolls=selectedRolls;
        LayoutInflater inflate = mA.getLayoutInflater();
        this.mainView = inflate.inflate(R.layout.damagedetail, null);

        fillWithRolls();

        metaPopup =new CustomAlertDialog(mA,mC,mainView);
        metaPopup.setPermanent(true);
        metaPopup.clickToHide(mainView.findViewById(R.id.fab_damage_detail_ondetlay));
    }

    public DisplayRolls(Activity mA, Context mC, DiceList selectedDiceList) {
        this.mC=mC;
        this.selectedDiceList = selectedDiceList;
        LayoutInflater inflate = mA.getLayoutInflater();
        this.mainView = inflate.inflate(R.layout.damagedetail, null);

        fillWithDices();

        metaPopup =new CustomAlertDialog(mA,mC,mainView);
        metaPopup.setPermanent(true);
        metaPopup.clickToHide(mainView.findViewById(R.id.fab_damage_detail_ondetlay));

    }

    public void showPopup() {
        metaPopup.showAlert();
    }

    private void fillWithRolls() {
        LinearLayout scrollLinear= this.mainView.findViewById(R.id.scroll_linear);

        for(Roll roll :selectedRolls.getList()){
            for (DmgRoll dmgRoll : roll.getDmgRollList()){
                LinearLayout line = new LinearLayout(mC);
                if(roll.isCritConfirmed()){line.setBackground(mC.getDrawable(R.drawable.dice_detail_crit_background));line.getBackground().setAlpha(70);}
                line.setOrientation(LinearLayout.HORIZONTAL);
                line.setGravity(Gravity.CENTER_VERTICAL);

                for (Dice dice : dmgRoll.getDmgDiceList().getList()){
                    ImageView diceImg = dice.getImg();
                    line.addView(diceImg);
                }

                TextView plus = new TextView(mC);
                plus.setTextSize(16);
                plus.setTextColor(Color.DKGRAY);
                plus.setText("+");
                line.addView(plus);

                TextView flatDamage = new TextView(mC);
                flatDamage.setTextSize(20);
                flatDamage.setTextColor(Color.DKGRAY);
                flatDamage.setText(String.valueOf(dmgRoll.getBonusDmg()));
                line.addView(flatDamage);

                scrollLinear.addView(line);
            }
        }
    }

    private void fillWithDices() {
        LinearLayout scrollLinear= this.mainView.findViewById(R.id.scroll_linear);
        int count=0;
        LinearLayout line = new LinearLayout(mC);
        scrollLinear.addView(line);
        line.setOrientation(LinearLayout.HORIZONTAL);
        for (Dice dice : selectedDiceList.getList()){
            if(count>=5){
                line = new LinearLayout(mC);
                line.setOrientation(LinearLayout.HORIZONTAL);
                ViewGroup parentImg = (ViewGroup) line.getParent();
                if (parentImg != null) {
                    parentImg.removeView(line);
                }
                scrollLinear.addView(line);
                count=0;
            }
            ViewGroup parentImg = (ViewGroup) dice.getImg().getParent();
            if (parentImg != null) {
                parentImg.removeView(dice.getImg());
            }
            line.addView(dice.getImg());
            count++;
        }
    }

    public int size() {
        int val=0;
        if(selectedDiceList!=null){
            val=selectedDiceList.getList().size();
        } else if(selectedRolls!=null)  {
            val=selectedRolls.getList().size();
        }
        return val;
    }
}
