package stellarnear.wedge_companion.SettingsFraments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import stellarnear.wedge_companion.Activities.MainActivity;
import stellarnear.wedge_companion.CustomAlertDialog;
import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.Perso.Resource;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Tools;


public class PrefSpellgemScreenFragment {
    private Activity mA;
    private Context mC;
    private CustomAlertDialog spellgemPopup;
    private Perso pj = PersoManager.getCurrentPJ();
    private Tools tools=new Tools();

    public PrefSpellgemScreenFragment(Activity mA, Context mC) {
        this.mA = mA;
        this.mC = mC;
    }

    private void createSpellgemPopup() {
        LayoutInflater inflater = LayoutInflater.from(mC);
        View mainView = inflater.inflate(R.layout.custom_spellgem,null);
        LinearLayout mainLin = mainView.findViewById(R.id.custom_mainlin);

        int nPerLine=0;
        LinearLayout line = new LinearLayout(mC);
        line.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,1));
        line.setGravity(Gravity.CENTER);
        mainLin.addView(line);
        pj.getAllResources().getRankManager().refreshRanks();
        pj.getAllResources().getRankManager().refreshMax();
        for (final Resource res : pj.getAllResources().getRankManager().getSpellTiers()){
            if(res.getMax()>0) {
                if(nPerLine>3){
                    line = new LinearLayout(mC);
                    line.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,1));
                    mainLin.addView(line);
                    line.setGravity(Gravity.CENTER);
                    nPerLine=0;
                }
                TextView text = new TextView(mC);
                text.setText(res.getShortname().replace("Sort","Rang"));
                text.setTextSize(20);
                text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                text.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,1));
                text.setTextColor(Color.DKGRAY);
                text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        new AlertDialog.Builder(mA)
                                .setTitle("Recharger ce tier de sort")
                                .setMessage("Confirmes tu recharger une utilisation de "+res.getName()+" ?")
                                .setIcon(android.R.drawable.ic_menu_help)
                                .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        reload(res);
                                    }
                                })
                                .setNegativeButton("non", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                    }
                                }).show();
                    }
                });
                line.addView(text);
                nPerLine++;
            }
        }

        this.spellgemPopup = new CustomAlertDialog(mA,mC,mainView);
        this.spellgemPopup.setPermanent(true);
        this.spellgemPopup.addConfirmButton("Valider");
        this.spellgemPopup.setAcceptEventListener(new CustomAlertDialog.OnAcceptEventListener() {
            @Override
            public void onEvent() {
                Intent intent = new Intent(mA, MainActivity.class);// Switch to MainActivityFragmentSpell
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                mA.startActivity(intent);
            }
        });
    }

    private void reload(Resource res) {
        res.earn(1);
        Resource convRes = pj.getAllResources().getResource(res.getId().replace("spell_rank_","spell_conv_rank_"));
        if(convRes!=null && convRes.getMax()>0){
            convRes.earn(1);
        }
        tools.customToast(mC,res.getName()+" : "+res.getCurrent(),"center");
    }

    public void showSpellgem() {
        createSpellgemPopup();
        this.spellgemPopup.showAlert();
    }

}
