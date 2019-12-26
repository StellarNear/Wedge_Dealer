package stellarnear.wedge_companion.Spells;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import stellarnear.wedge_companion.CustomAlertDialog;
import stellarnear.wedge_companion.PostData;
import stellarnear.wedge_companion.PostDataElement;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.TinyDB;

public class GuardianList {

    private static GuardianList instance = null;
    private static List<PairSpellCondition> guardianList;
    private CustomAlertDialog tooltipAlert;
    private OnRefreshEventListener mListener;
    private Activity mA;
    private Context mC;

    public static GuardianList getInstance(Context mC) {  //pour eviter de relire le xml à chaque fois
        if (instance==null){
            instance = new GuardianList(mC);
        }
        return instance;
    }

    private GuardianList(Context mC){
        this.mC=mC;
        try {
            loadFromSetting();
        } catch (Exception e) {
            guardianList =new ArrayList<>();
            e.printStackTrace();
        }
    }

    private void loadFromSetting(){
        TinyDB tinyDB = new TinyDB(mC);
        List<PairSpellCondition> listDB = tinyDB.getListGuardianSpellsCondition("localSaveGuardianList");
        if (listDB.size() > 0) {
            guardianList=listDB;
        } else {
            guardianList=new ArrayList<>();
        }
    }

    public boolean hasGuardian() {
        return guardianList.size()>0;
    }

    public void resetGuardian() {
        instance = null;
        guardianList=new ArrayList<>();
        saveToDB();
    }

    private void saveToDB() {
        TinyDB tinyDB = new TinyDB(mC);
        tinyDB.putListGuardianSpellsCondition("localSaveGuardianList", guardianList);
    }


    private void removeGuardian(PairSpellCondition spellCond) {
        guardianList.remove(spellCond);
        saveToDB();
    }

    public void addGuardian(final Spell spell){
        AlertDialog.Builder alert = new AlertDialog.Builder(mC);
        final EditText edittext = new EditText(mC);
        alert.setMessage("Condition de déclenchement");
        alert.setTitle("Déclenchement");
        alert.setView(edittext);
        alert.setIcon(R.drawable.ic_notifications_black_24dp);
        alert.setPositiveButton("Ajouter", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                guardianList.add(new PairSpellCondition(spell, edittext.getText().toString()));
                saveToDB();
            }
        });
        alert.show();
    }

    public List<PairSpellCondition> getGuardianList() {
        return guardianList;
    }


    public void popupList(Activity mA, Context context) {
        LinearLayout line = new LinearLayout(context); line.setGravity(Gravity.CENTER); line.setOrientation(LinearLayout.VERTICAL);
        View tooltip = mA.getLayoutInflater().inflate(R.layout.custom_toast_special_spellslists, null);
        tooltipAlert = new CustomAlertDialog(mA, context, tooltip);
        tooltipAlert.setPermanent(true);
        tooltipAlert.setFill("width");
        String title = GuardianList.getInstance(context).getGuardianList().size()+" Sort";
        if(guardianList.size()>1){ title+="s gardiens";}else { title+=" gardien";}
        ((TextView)tooltip.findViewById(R.id.title)).setText(title);
        ((ImageView)tooltip.findViewById(R.id.iconTitle)).setImageDrawable(context.getDrawable(R.drawable.ic_trigger_spell));

        fillList((LinearLayout)tooltip.findViewById(R.id.linearList),context);

        tooltipAlert.addConfirmButton("Fermer");
        tooltipAlert.showAlert();
    }

    private void fillList(LinearLayout viewById,final Context context) {
        viewById.removeAllViews();
        for(final PairSpellCondition spellCondition : guardianList){
            TextView condTxt=new TextView(context); condTxt.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            condTxt.setGravity(Gravity.CENTER);
            condTxt.setText(spellCondition.getCond());
            viewById.addView(condTxt);

            LinearLayout line = new LinearLayout(context);
            line.setGravity(Gravity.CENTER_VERTICAL);

            viewById.addView(line);
            ImageView delete = new ImageView(context);
            delete.setImageDrawable(context.getDrawable(R.drawable.ic_trigger_spell));
            delete.setLayoutParams(new LinearLayout.LayoutParams(150,150));
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(context)
                            .setTitle("Déclenchement du sort Gardien")
                            .setMessage("Confirmes-tu le déclenchement du sort gardien "+spellCondition.getSpell().getName()+" ?\n\n"+"Condition : "+spellCondition.getCond())
                            .setIcon(android.R.drawable.ic_menu_help)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    removeGuardian(spellCondition);
                                    tooltipAlert.dismissAlert();
                                    if(guardianList.size()>1){popupList(mA,context);}
                                    if(mListener!=null){mListener.onEvent();}
                                    new PostData(context,new PostDataElement("Déclenchement d'un sort gardien","Sort : "+spellCondition.getSpell().getName()));
                                    new PostData(context,new PostDataElement(spellCondition.getSpell()));
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                }
            });
            line.addView(delete);

            View profileView = spellCondition.getSpell().getProfile().getProfile(mA,context);
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


    public class  PairSpellCondition{
        private Spell spell;
        private String cond;

        private PairSpellCondition(Spell spell,String cond){
            this.spell=spell;
            this.cond=cond;
        }

        public Spell getSpell() {
            return spell;
        }

        public String getCond() {
            return cond;
        }
    }
}
