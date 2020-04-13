package stellarnear.wedge_companion.Rolls.Dices;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.Random;

public class Dice {
    private int nFace;
    private int randValue;
    private String element;
    private Context mC;
    private Activity mA;
    private View img;
    private boolean canCrit = false;

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
            refreshImage();
        }
    }

    private void refreshImage() {
        View newImg =  new ImgFactoryForDice(this,mC).getImg();
        if(this.img != null) {
            ViewGroup parent = ((ViewGroup) this.img.getParent());
            if (parent != null) {
                parent.removeView(this.img);
                parent.addView(newImg);
            }
        }
        this.img = newImg;
        if (mListenerRefresh != null) {
            mListenerRefresh.onEvent();
        }
    }

    public void setRand(int randFromWheel) { // le retour depuis wheelpicker
        this.randValue = randFromWheel;
        refreshImage();
    }

    public void setRefreshEventListener(OnRefreshEventListener eventListener) {
        mListenerRefresh = eventListener;
    }

    public int getnFace() {
        return nFace;
    }

    public View getImg() {
        if (this.img == null) {
            this.img = new ImgFactoryForDice(this,mC).getImg();
        }
        return this.img;
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


    public interface OnRefreshEventListener {
        void onEvent();
    }
}
