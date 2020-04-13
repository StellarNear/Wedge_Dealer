package stellarnear.wedge_companion.Rolls.Dices;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Tools;

public class ImgFactoryForDice20 {
    private Context mC;
    private View img;
    private Tools tools = Tools.getTools();

    private Dice dice;
    private Dice surgeDice;

    public ImgFactoryForDice20(Dice dice,Dice surgeDice, Context mC) {
        this.dice=dice;
        this.surgeDice=surgeDice;
        this.mC = mC;

        if(surgeDice==null){
            makeImg();
        } else {
            imgWithSurge();
        }
    }

    private void makeImg(){
        ImageView imgDice  = null;
            int drawableId;
            if (dice.getRandValue() > 0) {
                drawableId = mC.getResources().getIdentifier("d" + dice.getnFace() + "_" + dice.getRandValue() + (dice.getElement().equalsIgnoreCase("none") ? "" : dice.getElement()), "drawable", mC.getPackageName());
            } else {
                drawableId = mC.getResources().getIdentifier("d" + dice.getnFace() + "_main", "drawable", mC.getPackageName());
            }
            imgDice  = new ImageView(mC);
            imgDice.setImageDrawable(mC.getDrawable(drawableId));
            tools.resize(imgDice, mC.getResources().getDimensionPixelSize(R.dimen.icon_main_dices_wheel_size));
        this.img= imgDice;
    }

    public View getImg() {
       return this.img;
    }

    public void invalidateImg() {
        ImageView imgDice  = new ImageView(mC);
        imgDice.setImageDrawable(mC.getDrawable(R.drawable.d20_fail));
        tools.resize(imgDice, mC.getResources().getDimensionPixelSize(R.dimen.icon_main_dices_wheel_size));
        this.img=imgDice;
    }

    private void imgWithSurge() {
        LayoutInflater inflater = LayoutInflater.from(mC);
        View surgedView = inflater.inflate(R.layout.surged_dice, null);

        int drawableMainId = mC.getResources().getIdentifier("d" + dice.getnFace() + "_" + dice.getRandValue() + (dice.getElement().equalsIgnoreCase("none") ? "" : dice.getElement()), "drawable", mC.getPackageName());
        ImageView newMain = new ImageView(mC);
        newMain.setImageDrawable(mC.getDrawable(drawableMainId));
        ((FrameLayout)surgedView.findViewById(R.id.main_dice)).addView(newMain);


        ImageView surge = new ImageView(mC);
        int drawableSubId = mC.getResources().getIdentifier("d" + surgeDice.getnFace() + "_" + surgeDice.getRandValue() + (surgeDice.getElement().equalsIgnoreCase("none") ? "" : surgeDice.getElement()), "drawable", mC.getPackageName());
        surge.setImageDrawable(mC.getDrawable(drawableSubId));
        ((FrameLayout)surgedView.findViewById(R.id.second_dice)).addView(surge);

        this.img=surgedView;
    }

}
