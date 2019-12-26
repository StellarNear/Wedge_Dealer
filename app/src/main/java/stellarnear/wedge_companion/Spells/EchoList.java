package stellarnear.wedge_companion.Spells;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import stellarnear.wedge_companion.CustomAlertDialog;
import stellarnear.wedge_companion.PostData;
import stellarnear.wedge_companion.PostDataElement;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.TinyDB;
import stellarnear.wedge_companion.Tools;

public class EchoList {

    private static EchoList instance = null;
    private static SpellList echoList;
    private CustomAlertDialog tooltipAlert;
    private OnRefreshEventListener mListener;
    private Context mC;

    public static EchoList getInstance(Context mC) {  //pour eviter de relire le xml à chaque fois
        if (instance==null){
            instance = new EchoList(mC);
        }
        return instance;
    }
    private EchoList(Context mC){
        this.mC=mC;
        try {
            loadFromSetting();
        } catch (Exception e) {
            echoList=new SpellList();
            e.printStackTrace();
        }
    }

    private void loadFromSetting(){
        TinyDB tinyDB = new TinyDB(mC);
        SpellList listDB = tinyDB.getListEchoSpells("localSaveEchoList");
        if (listDB.size() > 0) {
            echoList=listDB;
        } else {
            echoList=new SpellList();
        }
    }

    public boolean hasEcho() {
        return echoList.size()>0;
    }

    public void resetEcho() {
        instance = null;
        echoList=new SpellList();
        saveListToDB();
    }



    private void removeEcho(Spell spell) {
        echoList.remove(spell);
        saveListToDB();
    }

    private void saveListToDB() {
        TinyDB tinyDB = new TinyDB(mC);
        tinyDB.putListEchoSpells("localSaveEchoList", echoList);
    }

    public void addEcho(Spell spell){
        echoList.add(spell);
        saveListToDB();
    }

    public SpellList getEchoList() {
        return echoList;
    }

    public void popupList(Activity mA, Context context) {
        LinearLayout line = new LinearLayout(context); line.setGravity(Gravity.CENTER); line.setOrientation(LinearLayout.VERTICAL);
        View tooltip = mA.getLayoutInflater().inflate(R.layout.custom_toast_special_spellslists, null);
        tooltipAlert = new CustomAlertDialog(mA, context, tooltip);
        tooltipAlert.setPermanent(true);
        tooltipAlert.setFill("width");
        String title = EchoList.getInstance(mC).getEchoList().size()+" Écho";
        if(echoList.size()>1){ title+="s magiques";}else { title+=" magique";}
        ((TextView)tooltip.findViewById(R.id.title)).setText(title);

        fillList(mA,context,(LinearLayout)tooltip.findViewById(R.id.linearList));

        tooltipAlert.addConfirmButton("Fermer");
        tooltipAlert.showAlert();
    }

    private void fillList(final Activity mA,final Context context, LinearLayout viewById) {
        viewById.removeAllViews();
        for(final Spell spell : echoList.asList()){
            LinearLayout line = new LinearLayout(context);
            line.setGravity(Gravity.CENTER_VERTICAL);
            viewById.addView(line);
            ImageView delete = new ImageView(context);
            delete.setImageDrawable(context.getDrawable(R.drawable.ic_cast_swirl));
            delete.setLayoutParams(new LinearLayout.LayoutParams(150,150));
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(context)
                            .setTitle("Lancement de l'écho magique")
                            .setMessage("Confirmes-tu la dépense de l'écho magique "+spell.getName()+" ?")
                            .setIcon(android.R.drawable.ic_menu_help)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                   removeEcho(spell);
                                    tooltipAlert.dismissAlert();
                                    if(echoList.size()>1){popupList(mA,context);}
                                    if(mListener!=null){mListener.onEvent();}
                                    new PostData(context,new PostDataElement("Lancement d'un écho magique","Sort : "+spell.getName()));
                                    new PostData(context,new PostDataElement(spell));
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                }
            });
            line.addView(delete);

            View profileView = spell.getProfile().getProfile(mA,context);
            if(profileView.getParent()!=null){((ViewGroup)profileView.getParent()).removeView(profileView);}
            line.addView(profileView);
        }
    }

    public interface OnRefreshEventListener {
        void onEvent();
    }

    public void setRefreshEventListener(OnRefreshEventListener eventListener) {
        mListener = eventListener;
    }


}
