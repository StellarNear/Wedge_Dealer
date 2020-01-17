package stellarnear.wedge_companion;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import stellarnear.wedge_companion.Spells.Spell;


public class GetData {
    private ProgressDialog dialog;
    private Context mC;
    private Tools tools=new Tools();
    private List<PairSpellUuid> listPairSpellUuidList =new ArrayList<>();
    private OnDataRecievedEventListener mListener;
    public GetData(Context mC){
        this.mC=mC;
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
        if (settings.getBoolean("switch_shadow_link",mC.getResources().getBoolean(R.bool.switch_shadow_link_def))
            && !settings.getBoolean("switch_demo_mode",mC.getResources().getBoolean(R.bool.switch_demo_mode_def))) {
            new JsonTask().execute("https://script.google.com/macros/s/AKfycbxOLElujQcy1-ZUer1KgEvK16gkTLUqYftApjNCM_IRTL3HSuDk/exec?id=1AmQOsFXgWBb9ipxhnKEj_JfKZUMt1bUJiBeNVNhH6oc&sheet=spell_arrow");
        }
    }

    public List<PairSpellUuid> getListPairSpellUuidList() {
        return listPairSpellUuidList;
    }

    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(mC);
            dialog.setMessage("Récupération des sorts naturalisés...");
            dialog.setCancelable(false);
            dialog.show();
        }

        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(30000);
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (dialog.isShowing()){
                dialog.dismiss();
            }

            try {
                JSONObject allObjs = new JSONObject(result);
                JSONArray spellsJsonArray = (JSONArray)allObjs.get("spell_arrow");

                Gson gson = new Gson();
                listPairSpellUuidList=new ArrayList<>();
                for(int i = 0 ; i<spellsJsonArray.length();i++){
                    GetDataElement dataElement = gson.fromJson(spellsJsonArray.get(i).toString(),GetDataElement.class);
                    listPairSpellUuidList.add(new PairSpellUuid(gson.fromJson(dataElement.getSpelljson(),Spell.class),dataElement.getUuid()));
                }
                if(mListener!=null){
                    mListener.onEvent();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public interface OnDataRecievedEventListener {
        void onEvent();
    }

    public void setOnDataRecievedEventListener(OnDataRecievedEventListener eventListener) {
        mListener = eventListener;
    }


    public class  PairSpellUuid{
        private Spell spell;
        private String uuid;

        private PairSpellUuid(Spell spell,String uuid){
            this.spell=spell;
            this.uuid=uuid;
        }

        public Spell getSpell() {
            return spell;
        }

        public String getUuid() {
            return uuid;
        }
    }
}