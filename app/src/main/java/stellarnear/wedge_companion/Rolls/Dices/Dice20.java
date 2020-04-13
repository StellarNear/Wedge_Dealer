package stellarnear.wedge_companion.Rolls.Dices;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.PostData;
import stellarnear.wedge_companion.PostDataElement;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Tools;

public class Dice20 {
    private Activity mA;
    private Context mC;
    private Dice mainDice20;
    private Dice surgedDice=null;
    private Boolean canBeLegendarySurge=false;
    private View img;

    private Perso pj = PersoManager.getCurrentPJ();
    private OnMythicEventListener mListenerMythic;

    private Tools tools = Tools.getTools();

    public Dice20(Activity mA, Context mC) {
        this.mA=mA;
        this.mC=mC;
        this.mainDice20 = new Dice(mA,mC,20);
    }

    public void rand(Boolean manual) {
        mainDice20.rand(manual);
    }

    public void setRand(int randFromWheel) { // le retour depuis wheelpicker
        mainDice20.setRand(randFromWheel);
    }

    public void canBeLegendarySurge() {
        this.canBeLegendarySurge=true;
    }

    public void setRefreshEventListener(Dice.OnRefreshEventListener eventListener) {
        mainDice20.setRefreshEventListener(eventListener);
    }

    public int getnFace() {
        return mainDice20.getnFace();
    }

    public View getImg() {
        if(this.img == null ){
            ImgFactoryForDice20 facto20 = new ImgFactoryForDice20(mainDice20,surgedDice, mC);
            this.img=facto20.getImg();

            setRefreshImg();
            if (pj.getAllResources().getResource("resource_mythic_points") != null && pj.getAllResources().getResource("resource_mythic_points").getMax() > 0) {
                setRefreshMythicSurge(); //on assigne un lsitener pour creer le des mythique si clic sur l'image du dès
            }
        }
        return this.img;
    }

    private void setRefreshImg() {
        mainDice20.setRefreshEventListener(new Dice.OnRefreshEventListener() {
            @Override
            public void onEvent() {
                refreshImg();
            }
        });
    }

    private void refreshImg() {
        View newImg =  new ImgFactoryForDice20(mainDice20,surgedDice,mC).getImg();
        if(this.img != null) {
            ViewGroup parent = ((ViewGroup) this.img.getParent());
            if (parent != null) {
                parent.removeView(this.img);
                parent.addView(newImg);
            }
        }
        if(mListenerMythic!=null){
            mListenerMythic.onEvent();
        }
        this.img = newImg;
        if (pj.getAllResources().getResource("resource_mythic_points") != null && pj.getAllResources().getResource("resource_mythic_points").getMax() > 0) {
            setRefreshMythicSurge(); //on assigne un lsitener pour creer le des mythique si clic sur l'image du dès
        }
    }

          /*

    Partie Mythique !

     */

    private void setRefreshMythicSurge() {
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

        if (points > 0 && surgedDice == null) {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
            if (mode.equalsIgnoreCase("légendaire")) {
                surgedDice = new Dice(mA, mC, tools.toInt(settings.getString("legendary_dice", String.valueOf(mC.getResources().getInteger(R.integer.legendary_dice_def)))));
                pj.getAllResources().getResource("resource_legendary_points").spend(1);
            } else {
                surgedDice = new Dice(mA, mC, tools.toInt(settings.getString("mythic_dice", String.valueOf(mC.getResources().getInteger(R.integer.mythic_dice_def)))));
                pj.getAllResources().getResource("resource_mythic_points").spend(1);
                new PostData(mC, new PostDataElement("Surcharge mythique du d20", "-1pt mythique"));
            }

            if (settings.getBoolean("switch_manual_diceroll", mC.getResources().getBoolean(R.bool.switch_manual_diceroll_def))) {
                surgedDice.rand(true);
                surgedDice.setRefreshEventListener(new Dice.OnRefreshEventListener() {
                    @Override
                    public void onEvent() {
                        newImgWithSurge();
                    }
                });
            } else {
                surgedDice.rand(false);
                newImgWithSurge();
            }
        } else if (surgedDice != null) {
            tools.customToast(mC, "Tu as déjà fais une montée en puissance sur ce dès", "center");
        } else {
            tools.customToast(mC, "Tu n'as plus de point " + mode, "center");
        }
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
        linear.addView(surgedDice.getImg());
        Toast toast = new Toast(mC);
        toast.setView(linear);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }


    private void newImgWithSurge() {
        toastResultDice();
        refreshImg();
    }

    /*

        OTHER

     */

    public int getRandValue() {
        return mainDice20.getRandValue();
    }

    public Dice getMythicDice() {
        return this.surgedDice;
    }

    public void invalidate() {
        ImgFactoryForDice20 facto20 = new ImgFactoryForDice20(mainDice20,surgedDice, mC);
        facto20.invalidateImg();
        this.img = facto20.getImg();
    }

    public void setMythicEventListener(OnMythicEventListener eventListener) {
        mListenerMythic = eventListener;
    }

    public interface OnMythicEventListener {
        void onEvent();
    }

}
