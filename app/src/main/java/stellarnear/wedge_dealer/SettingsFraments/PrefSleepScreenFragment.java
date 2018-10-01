package stellarnear.wedge_dealer.SettingsFraments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;

import java.util.Arrays;
import java.util.List;

import stellarnear.wedge_dealer.MainActivity;
import stellarnear.wedge_dealer.Perso.Perso;
import stellarnear.wedge_dealer.R;
import stellarnear.wedge_dealer.Tools;

public class PrefSleepScreenFragment {
    private Perso wedge= MainActivity.wedge;
    private Activity mA;
    private Context mC;

    public PrefSleepScreenFragment(Activity mA, Context mC) {
        this.mA=mA;
        this.mC=mC;
    }

    public void addSleepScreen() {
        View window = mA.findViewById(android.R.id.content);
        window.setBackgroundResource(R.drawable.sleep_background);
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
                        Intent intent = new Intent(mA, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        mA.startActivity(intent);
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
                wedge.getAllResources().sleepReset();
                resetTemp();
                tools.customToast(mC, "Une nouvelle journée pleine de coups critiques t'attends.", "center");
                Intent intent = new Intent(mA, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                mA.startActivity(intent);
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
