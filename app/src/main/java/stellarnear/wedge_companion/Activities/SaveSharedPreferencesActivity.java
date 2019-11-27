package stellarnear.wedge_companion.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.provider.DocumentFile;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Map;
import java.util.stream.Collectors;

import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Tools;

/**
 * A placeholder fragment containing a simple view.
 */
public class SaveSharedPreferencesActivity extends Activity {

    private Tools tools=new Tools();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String action=getIntent().getExtras().getString("ACTION_TYPE");
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        tools.customToast(getApplicationContext(),"Selection du dossier cible");
        if(action.equalsIgnoreCase("save")){
            startActivityForResult(intent, 42);
        } else if(action.equalsIgnoreCase("load")) {
            startActivityForResult(intent, 666);
        } else {
            finish();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        Log.i("SAVE_INF", "Request:"+requestCode+" "+"Result:"+resultCode);
        if (requestCode == 42 && resultCode==Activity.RESULT_OK) {
            Uri treeUri = resultData.getData();

            DocumentFile pickedDir = DocumentFile.fromTreeUri(this, treeUri);

            DocumentFile previousSave=pickedDir.findFile(getString(R.string.app_name)+".sav");
            if(previousSave==null){
                writeFile(pickedDir);
            } else {
                tools.customToast(getApplicationContext(),"Sauvegarde déjà présente !");
            }
            finish();
        } else if(requestCode == 666 && resultCode==Activity.RESULT_OK){
            Uri treeUri = resultData.getData();

            DocumentFile pickedDir = DocumentFile.fromTreeUri(this, treeUri);

            DocumentFile previousSave=pickedDir.findFile(getString(R.string.app_name)+".sav");
            if(previousSave==null){
                tools.customToast(getApplicationContext(),"Aucune Sauvegarde présente ...");
            } else {
                loadFile(previousSave);
            }
            finish();
        } else {
            finish();
        }
    }

    private void writeFile(DocumentFile pickedDir) {
        try {
            DocumentFile newFile = pickedDir.createFile("application/json", getString(R.string.app_name)+".sav");
            OutputStream out = this.getContentResolver().openOutputStream(newFile.getUri());

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            Map<String,?> prefsMap = prefs.getAll();
            Gson gson = new Gson();
            String jsonString = gson.toJson(prefsMap);

            PrintStream printStream = new PrintStream(out);
            printStream.print(jsonString);
            printStream.close();

            tools.customToast(getApplicationContext(),"Sauvegarde crée");
        } catch (Exception e) {
            Log.e("SAVE_ERR",e.getMessage());
            tools.customToast(getApplicationContext(),"Erreur:"+e.getStackTrace()[0]);
            DocumentFile previousSave=pickedDir.findFile(getString(R.string.app_name)+".sav");
            if(previousSave!=null){
                previousSave.delete();
            }
        }
    }

    private void loadFile(DocumentFile previousSave){
        try {
            InputStream in = this.getContentResolver().openInputStream(previousSave.getUri());

            String jsonString = new BufferedReader(new InputStreamReader(in))
                    .lines().collect(Collectors.joining("\n"));

            Gson gson = new Gson();

            Map<String,?> savedMap = gson.fromJson(jsonString, Map.class);


            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

            for(Map.Entry<String,?> entry : savedMap.entrySet()){
                if (entry.getValue() instanceof String ){
                    prefs.edit().putString(entry.getKey(),(String)entry.getValue()).apply();
                } else  if (entry.getValue() instanceof Boolean ){
                    prefs.edit().putBoolean(entry.getKey(),(Boolean) entry.getValue()).apply();
                } else if (entry.getValue() instanceof Integer || entry.getValue() instanceof Double ){ //on a pas de double dans sharedPref mais ils sont store comme ca
                    prefs.edit().putInt(entry.getKey(), (int)Math.round((Double)entry.getValue())).apply();
                }
            }

            PersoManager.loadFromSaveAllPJs();
            tools.customToast(getApplicationContext(),"Sauvegarde chargée");
        } catch (Exception e) {
            Log.e("LOAD_ERR",e.getMessage());
            tools.customToast(getApplicationContext(),"Erreur:"+e.getStackTrace()[0]);
        }
    }

    @Override
    public void onBackPressed() {
        //ne rien faire il faut finir cette activité !
    }


}
