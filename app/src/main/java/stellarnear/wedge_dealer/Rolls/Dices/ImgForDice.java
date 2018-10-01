package stellarnear.wedge_dealer.Rolls.Dices;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import stellarnear.wedge_dealer.R;
import stellarnear.wedge_dealer.Tools;

public class ImgForDice {
    private Activity mA;
    private Context mC;
    private Dice dice;
    private ImageView img;
    private Tools tools=new Tools();
    public ImgForDice(Dice dice, Activity mA, Context mC){
        this.mA=mA;
        this.mC=mC;
        this.dice=dice;
    }

    public ImageView getImg() {
        if(this.img==null) {
            int drawableId;
            if (dice.getRandValue() > 0) {
                drawableId = mC.getResources().getIdentifier("d" + dice.getnFace() + "_" + String.valueOf(dice.getRandValue()) + dice.getElement(), "drawable", mC.getPackageName());
            } else {
                drawableId = mC.getResources().getIdentifier("d" + dice.getnFace() + "_main", "drawable", mC.getPackageName());
            }
            this.img = new ImageView(mC);
            this.img.setImageDrawable(tools.resize(mC, drawableId, mC.getResources().getDimensionPixelSize(R.dimen.icon_main_dices_combat_launcher_size)));

            if(dice.getnFace()==20){
                setMythicSurge();
            }
        }
        return this.img;
    }

      /*

    Partie Mythique !

     */

    private void setMythicSurge() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
        int mythicPoints = tools.toInt(settings.getString("mythic_points",String.valueOf(mC.getResources().getInteger(R.integer.mythic_points_per_day_def))));

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
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
        int mythicPoints = tools.toInt(settings.getString("mythic_points",String.valueOf(mC.getResources().getInteger(R.integer.mythic_points_per_day_def))));
        if(mythicPoints>0) {
            LinearLayout linear = new LinearLayout(mC);
            int marge = 2 * mC.getResources().getDimensionPixelSize(R.dimen.general_margin);
            linear.setPadding(marge, marge, marge, marge);
            linear.setBackground(mC.getDrawable(R.drawable.background_border_infos));
            linear.setOrientation(LinearLayout.VERTICAL);

            TextView text = new TextView(mC);
            text.setText("Résultat du dès mythique :");
            linear.addView(text);

            Dice mythicDice = new Dice(mA, mC, 6);
            mythicDice.rand();
            dice.setMythicDice(mythicDice);
            settings.edit().putString("mythic_points",String.valueOf(mythicPoints-1)).apply();

            linear.addView(mythicDice.getImg());
            Toast toast = new Toast(mC);
            toast.setView(linear);
            toast.setGravity(Gravity.CENTER, 0, 0);

            toast.show();

            int subSize = mC.getResources().getDimensionPixelSize(R.dimen.icon_main_dices_combat_launcher_size_double_dice_sub);
            LayerDrawable finalDrawable = new LayerDrawable(new Drawable[]{tools.resize(mC, this.img.getDrawable(), subSize), tools.resize(mC, mythicDice.getImg().getDrawable(), subSize)});

            int splitSize = mC.getResources().getDimensionPixelSize(R.dimen.icon_main_dices_combat_launcher_size_double_dice_split);
            finalDrawable.setLayerInsetTop(1, splitSize);
            finalDrawable.setLayerInsetStart(1, splitSize);
            finalDrawable.setLayerGravity(0, Gravity.START | Gravity.TOP);
            finalDrawable.setLayerGravity(1, Gravity.END | Gravity.BOTTOM);

            this.img.setImageDrawable(finalDrawable);

        } else {
            tools.customToast(mC,"Tu n'as plus de point mythique","center");
        }
        this.img.setOnClickListener(null);
    }
}
