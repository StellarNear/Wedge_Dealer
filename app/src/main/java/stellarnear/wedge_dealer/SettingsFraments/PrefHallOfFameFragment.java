package stellarnear.wedge_dealer.SettingsFraments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import stellarnear.wedge_dealer.MainActivity;
import stellarnear.wedge_dealer.Perso.Perso;
import stellarnear.wedge_dealer.R;
import stellarnear.wedge_dealer.Stats.Stat;
import stellarnear.wedge_dealer.Tools;

public class PrefHallOfFameFragment extends Preference {
    private Perso wedge= MainActivity.wedge;
    private Context mC;
    private LinearLayout mainView;
    private LinearLayout fameList;
    private Tools tools=new Tools();

    public PrefHallOfFameFragment(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public PrefHallOfFameFragment(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }
    public PrefHallOfFameFragment(Context context) {
        super(context);
    }

    @Override
    protected View onCreateView(ViewGroup parent)
    {
        super.onCreateView(parent);
        this.mC=getContext();

        mainView = new LinearLayout(getContext());
        mainView.setOrientation(LinearLayout.VERTICAL);
        final ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(parent.getWidth(), parent.getHeight());  //pour full screen
        mainView.setLayoutParams(params);

        addHallOfFame();
        fameList = new LinearLayout(getContext());
        fameList.setOrientation(LinearLayout.VERTICAL);
        fameList.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        mainView.addView(fameList);
        refreshHall();
        return mainView;
    }


    private void addHallOfFame() {
        LinearLayout saveLine = new LinearLayout(mC);
        saveLine.setOrientation(LinearLayout.HORIZONTAL);
        saveLine.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        saveLine.setGravity(Gravity.CENTER);
        ImageView buttonSave = new ImageView(mC);
        buttonSave.setImageDrawable(mC.getDrawable(R.drawable.ic_save_black_24dp));
        saveLine.addView(buttonSave);
        TextView textSave = new TextView(mC);
        textSave.setText("Enregistrer la dernière entrée");
        saveLine.addView(textSave);

        saveLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveLast();
                refreshHall();
            }
        });

        mainView.addView(saveLine);
    }

    private void refreshHall() {
        fameList.removeAllViews();
        for(Stat stat : wedge.getStats().getHallOfFameList().asList()){
            LinearLayout statLine = new LinearLayout(mC);
            statLine.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams para =new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            para.setMargins(10,10,10,10);
            statLine.setLayoutParams(para);
            statLine.setGravity(Gravity.CENTER);
            statLine.setBackground(mC.getDrawable(R.drawable.background_border_infos));

            TextView textSave = new TextView(mC);
            textSave.setText(String.valueOf(stat.getSumDmg()));
            textSave.setPadding(0,10,0,10);
            statLine.addView(textSave);
            fameList.addView(statLine);
        }
    }


    private void saveLast() {
        Stat lastStat = wedge.getStats().getStatsList().getLastStat();
        if(wedge.getStats().getHallOfFameList().contains(lastStat)){
            tools.customToast(mC,"Entrée déjà présente","center");
        } else {
            wedge.getStats().addToHallOfFame(lastStat);
        }
    }



}
