package stellarnear.wedge_dealer.Rolls;

import android.content.Context;
import android.widget.ImageView;

import java.util.Random;

import stellarnear.wedge_dealer.R;
import stellarnear.wedge_dealer.Tools;

public class Dice {
    private int nFace;
    private int randValue;
    private String element;
    private ImageView img = null;
    private Context mC;
    private boolean rolled=false;
    private boolean canCrit=false;
    private Tools tools=new Tools();

    public Dice(Context mC, Integer nFace,String... elementArg) {
        this.nFace=nFace;
        this.element = elementArg.length > 0 ? elementArg[0] : "";
        this.mC=mC;
    }

    public void rand(){
            Random rand = new Random();
            this.randValue = 1 + rand.nextInt(nFace);
            this.rolled=true;
    }

    public boolean isRolled() {
        return rolled;
    }

    public int getnFace() {
        return nFace;
    }

    public ImageView getImg() {
        if(this.img==null) {
            int drawableId;
            if (this.randValue > 0) {
                drawableId = mC.getResources().getIdentifier("d" + nFace + "_" + String.valueOf(this.randValue) + this.element, "drawable", mC.getPackageName());
            } else {
                drawableId = mC.getResources().getIdentifier("d" + nFace + "_main", "drawable", mC.getPackageName());
            }
            this.img = new ImageView(mC);
            this.img.setImageDrawable(tools.resize(mC, drawableId, mC.getResources().getDimensionPixelSize(R.dimen.icon_main_dices_combat_launcher_size)));
        }
        return this.img;
    }

    public int getRandValue() {
        return this.randValue;
    }

    public String getElement() {
        return this.element;
    }

    public void makeCritable(){
        this.canCrit=true;
    }

    public boolean canCrit(){
        return this.canCrit;
    }
}
