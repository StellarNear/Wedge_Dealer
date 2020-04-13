package stellarnear.wedge_companion.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import stellarnear.wedge_companion.BuildConfig;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Tools;
import stellarnear.wedge_companion.VersionCheck.GetVersionData;
import stellarnear.wedge_companion.VersionCheck.PostConnectionVersion;
import stellarnear.wedge_companion.VersionCheck.VersionComparator;
import stellarnear.wedge_companion.VersionCheck.VersionData;


/**
 * Created by jchatron on 26/12/2017. updated with check download on 09/04/2020
 */

public class SplashActivity extends AppCompatActivity {
    private Tools tools = Tools.getTools();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (settings.getBoolean("switch_fullscreen_mode", getApplicationContext().getResources().getBoolean(R.bool.switch_fullscreen_mode_DEF))) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        super.onCreate(savedInstanceState);


        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        Boolean internetOk =  activeNetworkInfo != null && activeNetworkInfo.isConnected();
        if(internetOk && settings.getBoolean("switch_shadow_link", getApplicationContext().getResources().getBoolean(R.bool.switch_shadow_link_def))){
            final GetVersionData getDataVersion;
            try {
                getDataVersion = new GetVersionData(SplashActivity.this);
                getDataVersion.setOnDataRecievedEventListener(new GetVersionData.OnDataRecievedEventListener() {
                    @Override
                    public void onEvent() {
                        List<VersionData> listVersionData  = getDataVersion.getVersionDataList();
                        if (listVersionData != null && listVersionData.size() > 0) {
                            VersionData newVersion = checkForNewVersion(listVersionData);
                            if(newVersion!=null){
                                askForUpgrade(newVersion);
                            } else {
                                startMainActivity();
                            }
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                startMainActivity();
            }
        } else {
            startMainActivity();
        }

    }

    private VersionData checkForNewVersion(List<VersionData> listVersionData) {
        VersionData newestVerion=null;
        for(VersionData versionData : listVersionData) {
            if(VersionComparator.isNewer(versionData,newestVerion)){
                newestVerion=versionData;
            }
        }
        return newestVerion;
    }

    private void askForUpgrade(final VersionData newVersion) {
        LayoutInflater inflater =getLayoutInflater();
        View popupView = inflater.inflate(R.layout.ask_for_upgrade, null);
        ((TextView)popupView.findViewById(R.id.ask_upgrade_old)).setText(BuildConfig.VERSION_NAME);
        ((TextView)popupView.findViewById(R.id.ask_upgrade_new)).setText(newVersion.getVersion_name());
        ((TextView)popupView.findViewById(R.id.ask_upgrade_new_sub)).setText("Faite le : "+newVersion.getRelease_date());

        popupView.findViewById(R.id.ask_upgrade_patch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tools.customToast(getApplicationContext(),newVersion.getPatch_note(),"center");
            }
        });

        new AlertDialog.Builder(SplashActivity.this)
                .setTitle("Update de l'application")
                .setView(popupView)
                .setIcon(android.R.drawable.ic_menu_help)
                .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        try {
                            startDownload(newVersion);
                        } catch (Exception e) {
                            e.printStackTrace();
                            startMainActivity();
                        }
                    }
                })
                .setNegativeButton("non", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        startMainActivity();
                    }
                }).show();
    }

    private void startDownload(final VersionData newVersion) {
        String url = newVersion.getDl_link();
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        // Verify that the intent will resolve to an activity
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Here we use an intent without a Chooser
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    private void startMainActivity(){
        new PostConnectionVersion(getApplicationContext());
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}