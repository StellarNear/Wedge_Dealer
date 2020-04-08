package stellarnear.wedge_companion.Spells;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    private Tools tools = Tools.getTools();
    private TinyDB tinyDB;

    private EchoList(Context mC) {
        this.tinyDB = new TinyDB(mC);
        try {
            loadFromSetting();
        } catch (Exception e) {
            echoList = new SpellList();
            e.printStackTrace();
        }
    }

    public static EchoList getInstance(Context mC) {  //pour eviter de relire le xml à chaque fois
        if (instance == null) {
            instance = new EchoList(mC);
        }
        return instance;
    }

    private void loadFromSetting() {
        SpellList listDB = tinyDB.getSpellList("localSaveEchoList");
        if (listDB.size() > 0) {
            echoList = listDB;
        } else {
            echoList = new SpellList();
        }
    }

    public boolean hasEcho() {
        return echoList.size() > 0;
    }

    public void resetEcho() {
        instance = null;
        echoList = new SpellList();
        saveListToDB();
    }

    private void removeEcho(Spell spell) {
        echoList.remove(spell);
        saveListToDB();
    }

    private void saveListToDB() {
        tinyDB.putSpellList("localSaveEchoList", echoList);
    }

    public void addEcho(Spell spell) {
        echoList.add(spell);
        saveListToDB();
    }

    public SpellList getEchoList() {
        return echoList;
    }

    public void popupList(Activity mA, Context mC) {
        LinearLayout line = new LinearLayout(mC);
        line.setGravity(Gravity.CENTER);
        line.setOrientation(LinearLayout.VERTICAL);
        View tooltip = mA.getLayoutInflater().inflate(R.layout.custom_toast_special_spellslists, null);
        tooltipAlert = new CustomAlertDialog(mA, mC, tooltip);
        tooltipAlert.setPermanent(true);
        tooltipAlert.setFill("width");
        String title = EchoList.getInstance(mC).getEchoList().size() + " Écho";
        if (echoList.size() > 1) {
            title += "s magiques";
        } else {
            title += " magique";
        }
        ((TextView) tooltip.findViewById(R.id.title)).setText(title);
        ((ImageView) tooltip.findViewById(R.id.iconTitle)).setImageDrawable(mC.getDrawable(R.drawable.ic_cast_swirl));

        fillList(mA, mC, (LinearLayout) tooltip.findViewById(R.id.linearList));

        tooltipAlert.addConfirmButton("Fermer");
        tooltipAlert.showAlert();
    }

    private void fillList(final Activity mA, final Context mC, LinearLayout viewById) {
        viewById.removeAllViews();
        for (final Spell spell : echoList.asList()) {
            LinearLayout line = new LinearLayout(mC);
            line.setGravity(Gravity.CENTER_VERTICAL);
            viewById.addView(line);
            ImageView delete = new ImageView(mC);
            delete.setImageDrawable(mC.getDrawable(R.drawable.ic_cast_swirl));
            delete.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(mC)
                            .setTitle("Lancement de l'écho magique")
                            .setMessage("Confirmes-tu la dépense de l'écho magique " + spell.getName() + " ?")
                            .setIcon(android.R.drawable.ic_menu_help)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    removeEcho(spell);
                                    tooltipAlert.dismissAlert();
                                    if (echoList.size() > 1) {
                                        popupList(mA, mC);
                                    }
                                    if (mListener != null) {
                                        mListener.onEvent();
                                    }
                                    new PostData(mC, new PostDataElement("Lancement d'un écho magique", "Sort : " + spell.getName()));
                                    new PostData(mC, new PostDataElement(spell));
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                }
            });
            line.addView(delete);

            View profileView = spell.getProfile().getProfile(mA, mC);
            if (profileView.getParent() != null) {
                ((ViewGroup) profileView.getParent()).removeView(profileView);
            }
            line.addView(profileView);
        }
    }

    public void setRefreshEventListener(OnRefreshEventListener eventListener) {
        mListener = eventListener;
    }

    public interface OnRefreshEventListener {
        void onEvent();
    }


}
