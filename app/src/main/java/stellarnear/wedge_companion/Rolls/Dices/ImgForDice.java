package stellarnear.wedge_companion.Rolls.Dices;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.PostData;
import stellarnear.wedge_companion.PostDataElement;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Tools;

public class ImgForDice {
    private Activity mA;
    private Context mC;
    private Dice dice;       //todo reflechir au fait que Dice contient un autre IMGDice qui contient un dice etc
    private Dice surgeDice;  //todo reflechir au fait que Dice contient un autre IMGDice qui contient un dice etc
    private View img;
    private Tools tools = Tools.getTools();
    private Perso pj = PersoManager.getCurrentPJ();

    private boolean wasRand = false;
    private boolean canBeLegendarySurge = false;

    public ImgForDice(Dice dice, Activity mA, Context mC) {
        this.mA = mA;
        this.mC = mC;
        this.dice = dice;
    }

    public View getImg() {
        if (!wasRand) {
            int drawableId;
            if (dice.getRandValue() > 0) {
                drawableId = mC.getResources().getIdentifier("d" + dice.getnFace() + "_" + dice.getRandValue() + (dice.getElement().equalsIgnoreCase("none") ? "" : dice.getElement()), "drawable", mC.getPackageName());
                wasRand = true;
            } else {
                drawableId = mC.getResources().getIdentifier("d" + dice.getnFace() + "_main", "drawable", mC.getPackageName());
            }
            ImageView imgDice  = new ImageView(mC);
            imgDice.setImageDrawable(mC.getDrawable(drawableId));
            tools.resize(imgDice, mC.getResources().getDimensionPixelSize(R.dimen.icon_main_dices_wheel_size));

            if (this.img !=null && this.img.getParent() != null) { //on le remplace là où il était
                ViewGroup parent = ((ViewGroup) this.img.getParent());
                parent.removeView(this.img);
                parent.addView(imgDice);
            }

            this.img=imgDice;

            if (dice.getnFace() == 20) {
                if (pj.getAllResources().getResource("resource_mythic_points") != null && pj.getAllResources().getResource("resource_mythic_points").getMax() > 0) {
                    setMythicSurge(); //on assigne un lsitener pour creer le des mythique si clic sur l'image du dès
                }
            }
        }
        return this.img;
    }

    public void invalidateImg() {
        ImageView imgDice  = new ImageView(mC);
        imgDice.setImageDrawable(mC.getDrawable(R.drawable.d20_fail));
        tools.resize(imgDice, mC.getResources().getDimensionPixelSize(R.dimen.icon_main_dices_wheel_size));
        if (this.img !=null && this.img.getParent() != null) { //on le remplace là où il était
            ViewGroup parent = ((ViewGroup) this.img.getParent());
            parent.removeView(this.img);
            parent.addView(imgDice);
        }
        wasRand=true;
        this.img=imgDice;
    }

      /*

    Partie Mythique !

     */

    private void setMythicSurge() {
        this.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mA)
                        .setIcon(R.drawable.ic_warning_black_24dp)
                        .setTitle("Montée en puissance")
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
                        });
                String message = "Ressources :\n\n" +
                        "Point(s) mythique restant(s) : " + pj.getCurrentResourceValue("resource_mythic_points");
                if (canBeLegendarySurge && pj.getAllResources().getResource("resource_legendary_points") != null && pj.getAllResources().getResource("resource_legendary_points").getMax() > 0) {
                    message += "\nPoint(s) légendaire restant(s) : " + pj.getCurrentResourceValue("resource_legendary_points");
                    alertDialogBuilder.setNegativeButton("Legendaire", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            launchingMythicDice("légendaire");
                        }
                    });
                }
                alertDialogBuilder.setMessage(message);
                alertDialogBuilder.show();
            }
        });
    }

    private void launchingMythicDice(String mode) {
        int points = 0;
        if (mode.equalsIgnoreCase("légendaire")) {
            points = pj.getCurrentResourceValue("resource_legendary_points");
        } else {
            points = pj.getCurrentResourceValue("resource_mythic_points");
        }

        if (points > 0 && dice.getMythicDice() == null) {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
            if (mode.equalsIgnoreCase("légendaire")) {
                surgeDice = new Dice(mA, mC, tools.toInt(settings.getString("legendary_dice", String.valueOf(mC.getResources().getInteger(R.integer.legendary_dice_def)))));
                pj.getAllResources().getResource("resource_legendary_points").spend(1);
            } else {
                surgeDice = new Dice(mA, mC, tools.toInt(settings.getString("mythic_dice", String.valueOf(mC.getResources().getInteger(R.integer.mythic_dice_def)))));
                pj.getAllResources().getResource("resource_mythic_points").spend(1);
                new PostData(mC, new PostDataElement("Surcharge mythique du d20", "-1pt mythique"));
            }

            if (settings.getBoolean("switch_manual_diceroll", mC.getResources().getBoolean(R.bool.switch_manual_diceroll_def))) {
                surgeDice.rand(true);
                surgeDice.setRefreshEventListener(new Dice.OnRefreshEventListener() {
                    @Override
                    public void onEvent() {
                        dice.setMythicDice(surgeDice);
                        toastResultDice();
                        newImgWithSurge();
                    }
                });
            } else {
                surgeDice.rand(false);
                dice.setMythicDice(surgeDice);
                toastResultDice();
                newImgWithSurge();
            }
        } else if (dice.getMythicDice() != null) {
            tools.customToast(mC, "Tu as déjà fais une montée en puissance sur ce dès", "center");
        } else {
            tools.customToast(mC, "Tu n'as plus de point " + mode, "center");
        }
    }

    private void newImgWithSurge() {
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

        if (this.img.getParent() != null) { //on le remplace là où il était
            ViewGroup parent = ((ViewGroup) this.img.getParent());
            parent.removeView(this.img);
            parent.addView(surgedView);
        }
        this.img=surgedView;
    }

    private void toastResultDice() {
        LinearLayout linear = new LinearLayout(mC);
        int marge = 2 * mC.getResources().getDimensionPixelSize(R.dimen.general_margin);
        linear.setPadding(marge, marge, marge, marge);
        linear.setBackground(mC.getDrawable(R.drawable.background_border_infos));
        linear.setOrientation(LinearLayout.VERTICAL);

        TextView text = new TextView(mC);
        text.setText("Résultat du dès :");
        linear.addView(text);
        linear.addView(surgeDice.getImg());
        Toast toast = new Toast(mC);
        toast.setView(linear);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public void canBeLegendarySurge() {
        this.canBeLegendarySurge = true;
    }
}
