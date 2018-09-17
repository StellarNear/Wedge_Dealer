package stellarnear.wedge_dealer;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.ScrollingView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import java.util.HashMap;
import java.util.List;

import stellarnear.stellarnear.R;
import stellarnear.wedge_dealer.Rolls.Dice;
import stellarnear.wedge_dealer.Rolls.DmgRoll;
import stellarnear.wedge_dealer.Rolls.Roll;
import stellarnear.wedge_dealer.Rolls.RollList;

class DisplayRolls {
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
                for (Dice dice : dmgRoll.getDmgDiceList().getList()){
                    line.addView(dice.getImg());
                }
                scrollLinear.addView(line);
            }
        }
    }

    public int size() {
        return selectedRolls.getList().size();
    }
}
