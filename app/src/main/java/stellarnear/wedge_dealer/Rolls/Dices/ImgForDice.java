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

import stellarnear.wedge_dealer.MainActivity;
import stellarnear.wedge_dealer.Perso.Perso;
import stellarnear.wedge_dealer.R;
import stellarnear.wedge_dealer.Tools;

public class ImgForDice {
    private Activity mA;
    private Context mC;
    private Dice dice;
    private ImageView img;
    private Tools tools = new Tools();
    private Perso wedge = MainActivity.wedge;

    public ImgForDice(Dice dice, Activity mA, Context mC) {
        this.mA = mA;
        this.mC = mC;
        this.dice = dice;
    }

    public ImageView getImg() {
        if (this.img == null) {
            int drawableId;
            if (dice.getRandValue() > 0) {
                drawableId = mC.getResources().getIdentifier("d" + dice.getnFace() + "_" + String.valueOf(dice.getRandValue()) + dice.getElement(), "drawable", mC.getPackageName());
            } else {
                drawableId = mC.getResources().getIdentifier("d" + dice.getnFace() + "_main", "drawable", mC.getPackageName());
            }
            this.img = new ImageView(mC);
            this.img.setImageDrawable(tools.resize(mC, drawableId, mC.getResources().getDimensionPixelSize(R.dimen.icon_main_dices_combat_launcher_size)));

            if (dice.getnFace() == 20) {
                setMythicSurge(); //on assigne un lsitener pour creer le des mythique si clic sur l'image du dès
            }
        }
        return this.img;
    }

      /*

    Partie Mythique !

     */

    private void setMythicSurge() {
        this.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(mA)
                        .setIcon(R.drawable.ic_warning_black_24dp)
                        .setTitle("Montée en puissance")
                        .setMessage("Ressources :\n\n" +
                                "Point(s) mythique restant(s) : "+wedge.getResourceValue("mythic_points")+"\n" +
                                "Point(s) légendaire restant(s) : "+wedge.getResourceValue("legendary_points"))
                        .setNeutralButton("Aucune", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setPositiveButton("Mythique", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                launchingMythicDice("mythique");
                            }
                        })
                        .setNegativeButton("Legendaire", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                launchingMythicDice("légendaire");
                            }
                        }).show();
            }
        });
    }

    private void launchingMythicDice(String mode) {
        int points=0;
        if(mode.equalsIgnoreCase("légendaire")){
            points = MainActivity.wedge.getResourceValue("legendary_points");
        } else {
            points = MainActivity.wedge.getResourceValue("mythic_points");
        }

        if (points > 0 && dice.getMythicDice()==null) {
            LinearLayout linear = new LinearLayout(mC);
            int marge = 2 * mC.getResources().getDimensionPixelSize(R.dimen.general_margin);
            linear.setPadding(marge, marge, marge, marge);
            linear.setBackground(mC.getDrawable(R.drawable.background_border_infos));
            linear.setOrientation(LinearLayout.VERTICAL);

            TextView text = new TextView(mC);
            text.setText("Résultat du dès :");
            linear.addView(text);
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
            Dice surgeDice = null;
            if(mode.equalsIgnoreCase("légendaire")){
                surgeDice=new Dice(mA, mC, tools.toInt(settings.getString("legendary_dice",String.valueOf(mC.getResources().getInteger(R.integer.legendary_dice_def)))));
                MainActivity.wedge.getAllResources().getResource("legendary_points").spend(1);
            } else {
                surgeDice=new Dice(mA, mC, tools.toInt(settings.getString("mythic_dice",String.valueOf(mC.getResources().getInteger(R.integer.mythic_dice_def)))));
                MainActivity.wedge.getAllResources().getResource("mythic_points").spend(1);
            }

            surgeDice.rand();
            dice.setMythicDice(surgeDice);
            linear.addView(surgeDice.getImg());

            Toast toast = new Toast(mC);
            toast.setView(linear);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();

            int subSize = mC.getResources().getDimensionPixelSize(R.dimen.icon_main_dices_combat_launcher_size_double_dice_sub);
            LayerDrawable finalDrawable = new LayerDrawable(new Drawable[]{tools.resize(mC, this.img.getDrawable(), subSize), tools.resize(mC, surgeDice.getImg().getDrawable(), subSize)});

            int splitSize = mC.getResources().getDimensionPixelSize(R.dimen.icon_main_dices_combat_launcher_size_double_dice_split);
            finalDrawable.setLayerInsetTop(1, splitSize);
            finalDrawable.setLayerInsetStart(1, splitSize);
            finalDrawable.setLayerGravity(0, Gravity.START | Gravity.TOP);
            finalDrawable.setLayerGravity(1, Gravity.END | Gravity.BOTTOM);

            this.img.setImageDrawable(finalDrawable);

        } else if (dice.getMythicDice()!=null) {
            tools.customToast(mC, "Tu as déjà fais une montée en puissance sur ce dès", "center");
        } else {
            tools.customToast(mC, "Tu n'as plus de point "+mode, "center");
        }
    }
}
