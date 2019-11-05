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
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.List;

import stellarnear.wedge_dealer.MainActivity;
import stellarnear.wedge_dealer.Perso.Perso;
import stellarnear.wedge_dealer.PostData;
import stellarnear.wedge_dealer.PostDataElement;
import stellarnear.wedge_dealer.R;
import stellarnear.wedge_dealer.Tools;

public class PrefSleepScreenFragment extends Preference {
    private Perso wedge= MainActivity.wedge;
    private Context mC;
    private View mainView;

    public PrefSleepScreenFragment(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public PrefSleepScreenFragment(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }
    public PrefSleepScreenFragment(Context context) {
        super(context);
    }

    @Override
    protected View onCreateView(ViewGroup parent)
    {
        super.onCreateView(parent);
        this.mC=getContext();

        mainView = new View(getContext());
        final ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(parent.getWidth(), parent.getHeight());  //pour full screen
        mainView.setLayoutParams(params);
        addSleepScreen();
        return mainView;
    }


    public void addSleepScreen() {
        mainView.setBackgroundResource(R.drawable.sleep_background);
        new AlertDialog.Builder(mC)
                .setIcon(R.drawable.ic_warning_black_24dp)
                .setTitle("Repos")
                .setMessage("Es-tu sûre de vouloir te reposer maintenant ?")
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sleep();
                    }
                })
                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }


    private void sleep() {
        final Tools tools = new Tools();
        tools.customToast(mC, "Fais de beaux rêves !", "center");
        int time = 2000; // in milliseconds
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                wedge.getAllResources().refreshMaxs();
                wedge.getAllResources().sleepReset();
                resetTemp();
                tools.customToast(mC, "Une nouvelle journée pleine de coups critiques t'attends.", "center");
                new PostData(mC,new PostDataElement("Nuit de repos","Recharge des ressources journalières"));
                Intent intent = new Intent(mC, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                mC.startActivity(intent);
            }
        }, time);
    }

    private void resetTemp() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mC);
        List<String> allTempList = Arrays.asList("dmg_buff", "att_buff");
        for (String temp : allTempList) {
            prefs.edit().putString(temp, "0").apply();
        }
    }

}
