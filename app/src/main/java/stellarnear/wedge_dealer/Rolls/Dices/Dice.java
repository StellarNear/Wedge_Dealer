package stellarnear.wedge_dealer.Rolls.Dices;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;

import java.util.Random;

public class Dice {
    private int nFace;
    private int randValue;
    private String element;
    private ImgForDice imgForDice;
    private Context mC;
    private Activity mA;
    private boolean rolled=false;
    private boolean delt=false;
    private boolean canCrit=false;

    private Dice mythicDice; //si c'est un d20 il a un dés mythic attaché

    private OnMythicEventListener mListener;

    public Dice(Activity mA, Context mC, Integer nFace, String... elementArg) {
        this.nFace=nFace;
        this.element = elementArg.length > 0 ? elementArg[0] : "";
        this.mC=mC;
        this.mA=mA;
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
        if(imgForDice==null){imgForDice = new ImgForDice(this,mA,mC);}
        return imgForDice.getImg();
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

    public void delt(){
        this.delt=true;
        imgForDice.getImg().setOnClickListener(null);
    }

    public boolean isDelt(){
        return this.delt;
    }

    public interface OnMythicEventListener {
        void onEvent();
    }

    public void setMythicEventListener(OnMythicEventListener eventListener) {
        mListener = eventListener;
    }

    public void setMythicDice(Dice mythicDice){
        this.mythicDice=mythicDice;
        if(mListener!=null){mListener.onEvent();}
    }

    public Dice getMythicDice(){
        return this.mythicDice;
    }
}
