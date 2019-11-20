package stellarnear.wedge_companion.SettingsFraments;

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

import stellarnear.wedge_companion.Activities.MainActivity;
import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.PostData;
import stellarnear.wedge_companion.PostDataElement;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Tools;

public class PrefSleepScreenFragment extends Preference {
    private Perso pj = PersoManager.getCurrentPJ();
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
                .setNegativeButton("Sans sorts", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        halfSleep();
                    }
                })
                .setNeutralButton("Non", new DialogInterface.OnClickListener() {
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
                pj.sleep();
                tools.customToast(mC, "Une nouvelle journée pleine de sortilèges t'attends.", "center");
                new PostData(mC,new PostDataElement("Nuit de repos","Recharge des ressources journalières et sorts"));
                Intent intent = new Intent(mC, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                mC.startActivity(intent);
            }
        }, time);
    }

    private void halfSleep() {
        final Tools tools = new Tools();
        tools.customToast(mC, "Fais de beaux rêves !", "center");
        int time = 2000; // in milliseconds
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                pj.halfSleep();
                tools.customToast(mC, "Une journée sans sortilèges t'attends...", "center");
                new PostData(mC,new PostDataElement("Nuit de repos (sans sorts)","Recharge des ressources journalières"));
                Intent intent = new Intent(mC,  MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                mC.startActivity(intent);
            }
        }, time);
    }

}
