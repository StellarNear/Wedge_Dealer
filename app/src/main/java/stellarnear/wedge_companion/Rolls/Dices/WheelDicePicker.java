package stellarnear.wedge_companion.Rolls.Dices;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Tools;


public class WheelDicePicker extends AppCompatActivity {
    private Context mC;
    private int valueSelected;
    private Dice dice;
    private Tools tools = Tools.getTools();

    public WheelDicePicker(RelativeLayout relativeCenter, Dice dice, Context mC) {
        this.mC = mC;
        this.dice = dice;
        double angle_part = 360.0 / dice.getnFace();
        double time_delay_anim = 1000 / dice.getnFace();
        int dist = mC.getResources().getDimensionPixelSize(R.dimen.distance_dice_wheel);
        final ImageView mainDice = new ImageView(mC);

        for (int i = 0; i < dice.getnFace(); i++) {
            RelativeLayout frame = new RelativeLayout(mC);
            setPara(frame);

            ImageView imgViewDice = new ImageView(mC);
            int drawableId = mC.getResources().getIdentifier("d" + dice.getnFace() + "_" + (i + 1) + dice.getElement(), "drawable", mC.getPackageName());
            imgViewDice.setImageDrawable(mC.getDrawable(drawableId));
            tools.resize(imgViewDice, mC.getResources().getDimensionPixelSize(R.dimen.icon_dices_wheel_size));

            frame.addView(imgViewDice);


            final int val_dice = i + 1;
            imgViewDice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    valueSelected = val_dice;
                    changeMainDice(val_dice, mainDice);
                }
            });

            relativeCenter.addView(frame);

            double angle = -90d + angle_part * i;
            int distX = (int) (dist * Math.cos(Math.toRadians(angle)));
            int distY = (int) (dist * Math.sin(Math.toRadians(angle)));

            frame.animate().setDuration(1000).setInterpolator(new OvershootInterpolator(3.0f)).translationX(distX).translationY(distY).setStartDelay((int) (i * time_delay_anim)).start();
        }


        RelativeLayout mainFrame = new RelativeLayout(mC);
        setPara(mainFrame);
        int drawableIdMain = mC.getResources().getIdentifier("d" + dice.getnFace() + "_main", "drawable", mC.getPackageName());
        mainDice.setImageDrawable(mC.getDrawable(drawableIdMain));
        tools.resize(mainDice, mC.getResources().getDimensionPixelSize(R.dimen.icon_dices_wheel_size));
        mainFrame.addView(mainDice);

        relativeCenter.addView(mainFrame);
    }

    private void setPara(RelativeLayout frame) {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        frame.setLayoutParams(layoutParams);
    }

    private void changeMainDice(int val_dice, ImageView mainDice) {
        int drawableId = mC.getResources().getIdentifier("d" + dice.getnFace() + "_" + val_dice + dice.getElement(), "drawable", mC.getPackageName());
        mainDice.setImageDrawable(mC.getDrawable(drawableId));
    }

    public int getValueSelected() {
        return this.valueSelected;
    }
}