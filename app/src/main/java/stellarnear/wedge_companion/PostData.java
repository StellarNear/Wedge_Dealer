package stellarnear.wedge_companion;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

public class PostData {

    public PostData(Context mC, Object dataElement) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
        if (settings.getBoolean("switch_shadow_link", mC.getResources().getBoolean(R.bool.switch_shadow_link_def)
                && !settings.getBoolean("switch_demo_mode", mC.getResources().getBoolean(R.bool.switch_demo_mode_def)))) {
            SendRequestData send = new SendRequestData();
            send.addParams(dataElement);
            send.execute();
            if (dataElement instanceof PostDataElement && ((PostDataElement) dataElement).getArrowSpell() != null) {
                new PostData(mC, new PostDataElementSpellArrow(((PostDataElement) dataElement).getArrowSpell()));
            }
        }

    }

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;
        Iterator<String> itr = params.keys();
        while (itr.hasNext()) {

            String key = itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }

    public class SendRequestData extends AsyncTask<String, Void, String> {
        private Object element;

        protected void onPreExecute() {
        }

        protected String doInBackground(String... arg0) {
            try {
                URL url = new URL("https://script.google.com/macros/s/AKfycbwi81ryWCJuLyQiybPqeTmmyQpb-tNYbRft2eXCH_Yn2QpzjAZI/exec");
                JSONObject postDataParams = new JSONObject();
                String id = "1AmQOsFXgWBb9ipxhnKEj_JfKZUMt1bUJiBeNVNhH6oc";
                postDataParams.put("id", id);
                if (element instanceof PostDataElement) {
                    postDataParams.put("sheet", ((PostDataElement) element).getTargetSheet());
                    postDataParams.put("date", ((PostDataElement) element).getDate());
                    postDataParams.put("type_event", ((PostDataElement) element).getTypeEvent());
                    postDataParams.put("detail", ((PostDataElement) element).getDetail());
                    postDataParams.put("result", ((PostDataElement) element).getResult());
                } else if (element instanceof PostDataElementSpellArrow) {
                    postDataParams.put("sheet", ((PostDataElementSpellArrow) element).getTargetSheet());
                    postDataParams.put("date", ((PostDataElementSpellArrow) element).getDate());
                    postDataParams.put("type_event", ((PostDataElementSpellArrow) element).getTypeEvent());
                    postDataParams.put("caster", ((PostDataElementSpellArrow) element).getCaster());
                    postDataParams.put("uuid", ((PostDataElementSpellArrow) element).getUuid());
                    postDataParams.put("result", ((PostDataElementSpellArrow) element).getResult());
                } else if (element instanceof RemoveDataElementSpellArrow) {
                    postDataParams.put("sheet", ((RemoveDataElementSpellArrow) element).getTargetSheet());
                    postDataParams.put("type_event", ((RemoveDataElementSpellArrow) element).getTypeEvent());
                    postDataParams.put("uuid", ((RemoveDataElementSpellArrow) element).getUuid());
                } else if (element instanceof RemoveDataElementAllSpellArrow) {
                    postDataParams.put("sheet", ((RemoveDataElementAllSpellArrow) element).getTargetSheet());
                    postDataParams.put("caster", ((RemoveDataElementAllSpellArrow) element).getCaster());
                    postDataParams.put("type_event", ((RemoveDataElementAllSpellArrow) element).getTypeEvent());
                } else {
                    throw new Exception("no compatible element to post");
                }

                Log.i("params", postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, StandardCharsets.UTF_8));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode = conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuffer sb = new StringBuffer();
                    String line = "";

                    while ((line = in.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    in.close();
                    return sb.toString();
                } else {
                    return "false : " + responseCode;
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("PostData error", e.getMessage());
                return "Exception: " + e.getMessage();
            }
        }

        private AsyncTask addParams(Object object) {
            this.element = object;
            return this;
        }
    }

}