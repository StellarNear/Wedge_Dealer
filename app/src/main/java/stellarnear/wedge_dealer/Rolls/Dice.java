package stellarnear.wedge_dealer.Rolls;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import stellarnear.wedge_dealer.CustomAlertDialog;
import stellarnear.wedge_dealer.MainActivity;
import stellarnear.wedge_dealer.R;
import stellarnear.wedge_dealer.Tools;

public class Dice {
    private int nFace;
    private int randValue;
    private String element;
    private ImageView img = null;
    private Context mC;
    private Activity mA;
    private boolean rolled=false;
    private boolean canCrit=false;
    private Tools tools=new Tools();

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
        if(this.img==null) {
            int drawableId;
            if (this.randValue > 0) {
                drawableId = mC.getResources().getIdentifier("d" + nFace + "_" + String.valueOf(this.randValue) + this.element, "drawable", mC.getPackageName());
            } else {
                drawableId = mC.getResources().getIdentifier("d" + nFace + "_main", "drawable", mC.getPackageName());
            }
            this.img = new ImageView(mC);
            this.img.setImageDrawable(tools.resize(mC, drawableId, mC.getResources().getDimensionPixelSize(R.dimen.icon_main_dices_combat_launcher_size)));

            if(this.nFace==20){
                setMythicSurge();
            }
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


    /*

    Partie Mythique !

     */

    private void setMythicSurge() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
        int mythicPoints = tools.toInt(settings.getString("mythic_points", mC.getString(R.integer.mythic_points_per_day_def)));

        if(mythicPoints>0){
            this.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(mA)
                            .setIcon(R.drawable.ic_warning_black_24dp)
                            .setTitle("Montée en puissance mythique")
                            .setMessage("Es-tu sûre de vouloir utiliser un point mythique ?")
                            .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                   launchingMythicDice();
                                }
                            })
                            .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();


                }
            });
        }
    }

    private void launchingMythicDice() {
        LinearLayout linear = new LinearLayout(mC);
        linear.setBackground(mC.getDrawable(R.drawable.background_border_infos));
        linear.setOrientation(LinearLayout.VERTICAL);

        TextView text = new TextView(mC);
        text.setText("Résultat du dès mythique :");

        linear.addView(text);

        Dice mythicDice = new Dice(mA,mC,6);
        mythicDice.rand();

        linear.addView(mythicDice.getImg());

        Toast toast = new Toast(mC);
        toast.setView(linear);
        toast.setGravity(Gravity.CENTER, 0, 0);

        toast.show();

        int subSize = mC.getResources().getDimensionPixelSize(R.dimen.icon_main_dices_combat_launcher_size_double_dice_sub);
        LayerDrawable finalDrawable = new LayerDrawable(new Drawable[] {tools.resize(mC,this.img.getDrawable(),subSize), tools.resize(mC,mythicDice.getImg().getDrawable(),subSize)});

        int splitSize = mC.getResources().getDimensionPixelSize(R.dimen.icon_main_dices_combat_launcher_size_double_dice_split);
        finalDrawable.setLayerInsetTop(1,splitSize);
        finalDrawable.setLayerInsetStart(1,splitSize);
        finalDrawable.setLayerGravity(0, Gravity.START | Gravity.TOP);
        finalDrawable.setLayerGravity(1, Gravity.END | Gravity.BOTTOM);

        this.img.setImageDrawable(finalDrawable);
        this.img.setOnClickListener(null);
    }
}
