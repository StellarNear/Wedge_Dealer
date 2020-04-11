package stellarnear.wedge_companion.Rolls.Dices;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import java.util.Random;

public class Dice {
    private int nFace;
    private int randValue;
    private String element;
    private ImgForDice imgForDice;
    private Context mC;
    private Activity mA;
    private boolean rolled = false;
    private boolean delt = false;
    private boolean canCrit = false;
    private boolean canBeLegendarySurge = false;
    private boolean manual = false;

    private Dice mythicDice; //si c'est un d20 il a un dés mythic attaché

    private OnMythicEventListener mListenerMythic;
    private OnRefreshEventListener mListenerRefresh;

    public Dice(Activity mA, Context mC, Integer nFace, String... elementArg) {
        this.nFace = nFace;
        this.element = elementArg.length > 0 ? elementArg[0] : "";
        this.mC = mC;
        this.mA = mA;
    }

    public void rand(Boolean manual) {
        if (manual) {
            new DiceDealerDialog(mA, Dice.this);
        } else {
            Random rand = new Random();
            this.randValue = 1 + rand.nextInt(nFace);
            this.rolled = true;
        }
    }

    public void setRand(int randFromWheel) { // le retour depuis wheelpicker
        this.randValue = randFromWheel;
        this.rolled = true;
        if(this.imgForDice!=null){
            this.imgForDice.getImg();
        }//will refresh img
        if (mListenerRefresh != null) {
            mListenerRefresh.onEvent();
        }
    }

    public void canBeLegendarySurge() {
        this.canBeLegendarySurge = true;
    }

    public void setRefreshEventListener(OnRefreshEventListener eventListener) {
        mListenerRefresh = eventListener;
    }

    public boolean isRolled() {
        return rolled;
    }

    public int getnFace() {
        return nFace;
    }

    public View getImg() {
        if (imgForDice == null) {
            imgForDice = new ImgForDice(this, mA, mC);
        }
        if (canBeLegendarySurge) {
            imgForDice.canBeLegendarySurge();
        }
        return imgForDice.getImg();
    }

    public int getRandValue() {
        return this.randValue;
    }

    public String getElement() {
        return this.element;
    }

    public void makeCritable() {
        this.canCrit = true;
    }

    public boolean canCrit() {
        return this.canCrit;
    }

    public void delt() {
        this.delt = true;
        imgForDice.getImg().setOnClickListener(null);
    }

    public boolean isDelt() {
        return this.delt;
    }

    public void setMythicEventListener(OnMythicEventListener eventListener) {
        mListenerMythic = eventListener;
    }

    public Dice getMythicDice() {
        return this.mythicDice;
    }

    public void setMythicDice(Dice mythicDice) {
        this.mythicDice = mythicDice;
        if (mListenerMythic != null) {
            mListenerMythic.onEvent();
        }
    }

    public void invalidate() {
        if (imgForDice == null) {
            imgForDice = new ImgForDice(this, mA, mC);
        }
        this.imgForDice.invalidateImg();
    }

    public interface OnRefreshEventListener {
        void onEvent();
    }

    public interface OnMythicEventListener {
        void onEvent();
    }
}
