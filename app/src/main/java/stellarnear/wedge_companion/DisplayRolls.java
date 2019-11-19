package stellarnear.wedge_companion;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import stellarnear.wedge_companion.Rolls.Dices.Dice;
import stellarnear.wedge_companion.Rolls.DmgRoll;
import stellarnear.wedge_companion.Rolls.Roll;
import stellarnear.wedge_companion.Rolls.RollList;

public class DisplayRolls {
    private RollList selectedRolls;
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

    public void showPopup() {
        metaPopup.showAlert();
    }

    private void fillWithRolls() {
        LinearLayout scrollLinear= this.mainView.findViewById(R.id.scroll_linear);

        for(Roll roll :selectedRolls.getList()){
            for (DmgRoll dmgRoll : roll.getDmgRollList()){
                LinearLayout line = new LinearLayout(mC);
                line.setOrientation(LinearLayout.HORIZONTAL);
                line.setGravity(Gravity.CENTER_VERTICAL);

                TextView flatDamage = new TextView(mC);
                flatDamage.setTextSize(20);
                flatDamage.setTextColor(Color.DKGRAY);
                flatDamage.setText(String.valueOf(dmgRoll.getBonusDmg()));
                line.addView(flatDamage);

                TextView plus = new TextView(mC);
                plus.setTextSize(16);
                plus.setTextColor(Color.DKGRAY);
                plus.setText("+");
                line.addView(plus);

                for (Dice dice : dmgRoll.getDmgDiceList().getList()){
                    ImageView diceImg = dice.getImg();
                    line.addView(diceImg);
                }
                scrollLinear.addView(line);
            }
        }
    }

    public int size() {
        return selectedRolls.getList().size();
    }
}
