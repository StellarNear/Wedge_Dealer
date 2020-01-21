package stellarnear.wedge_companion.Activities.MainActivityFramentCombat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import stellarnear.wedge_companion.GetData;
import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.PostData;
import stellarnear.wedge_companion.PostDataElement;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.RemoveDataElementSpellArrow;
import stellarnear.wedge_companion.Rolls.RollList;
import stellarnear.wedge_companion.Tools;

public class MainActivityFragmentSpellArrowBonusPanel {
    private Activity mA;
    private Context mC;
    private RollList rollList;

    private ImageView buttonAdd;

    private TextView nArrow;
    private View pannel;

    public Perso pj= PersoManager.getCurrentPJ();
    private boolean addBonusPanelIsVisible=false;
    private Tools tools=new Tools();

    private List<GetData.PairSpellUuid> arrowsSpellsUuids;

    public MainActivityFragmentSpellArrowBonusPanel(final Activity mA,final Context mC, View mainPage){
        this.mA=mA;
        this.mC=mC;
        buttonAdd =(ImageView) mainPage.findViewById(R.id.fab_spell_arrow);
        pannel = mainPage.findViewById(R.id.spell_arrow_linear);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flipBonusPanel();
                if(addBonusPanelIsVisible){
                    final GetData getData=new GetData(mC);
                    getData.setOnDataRecievedEventListener(new GetData.OnDataRecievedEventListener() {
                        @Override
                        public void onEvent() {
                            arrowsSpellsUuids=getData.getListPairSpellUuidList();
                            if(arrowsSpellsUuids!=null && arrowsSpellsUuids.size()>0) {
                                addSpellArrowList();
                            }
                        }
                    });
                }
            }
        });

    }

    private void addSpellArrowList() {
        LinearLayout viewById=pannel.findViewById(R.id.spell_arrow_scroll_lin);
        viewById.removeAllViews();
        for(final GetData.PairSpellUuid pairSpellUuid : arrowsSpellsUuids){
            LinearLayout line = new LinearLayout(mC);
            line.setGravity(Gravity.CENTER_VERTICAL);
            viewById.addView(line);
            ImageView delete = new ImageView(mC);
            delete.setImageDrawable(mC.getDrawable(R.drawable.ic_winged_arrow));
            int pxMaging = mC.getResources().getDimensionPixelSize(R.dimen.general_margin);
            delete.setPadding(pxMaging,0,0,0);
            delete.setLayoutParams(new LinearLayout.LayoutParams(150,150));
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(mC)
                            .setTitle("Lancement d'un sort naturalisé")
                            .setMessage("Confirmes-tu le lancement du sort naturalisé "+pairSpellUuid.getSpell().getName()+" ?")
                            .setIcon(android.R.drawable.ic_menu_help)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    removeFromList(pairSpellUuid);
                                    addSpellArrowList();
                                    if(arrowsSpellsUuids.size()<1){flipBonusPanel();}
                                    new PostData(mC,new PostDataElement("Lancement d'un sort naturalisé","Sort : "+pairSpellUuid.getSpell().getName()));
                                    new PostData(mC,new PostDataElement(pairSpellUuid));
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                }
            });
            line.addView(delete);

            View profileView = pairSpellUuid.getSpell().getProfile().getProfile(mA,mC);
            if(profileView.getParent()!=null){((ViewGroup)profileView.getParent()).removeView(profileView);}
            line.addView(profileView);
        }

        ((TextView)pannel.findViewById(R.id.spell_arrow_remaining)).setText("Nombre de flèches naturalisées : "+arrowsSpellsUuids.size());
    }

    private void removeFromList(GetData.PairSpellUuid pairSpellUuid) {
        arrowsSpellsUuids.remove(pairSpellUuid);
        new PostData(mC,new RemoveDataElementSpellArrow(pairSpellUuid));
    }


    private void flipBonusPanel() {
        if (!addBonusPanelIsVisible){
            pannel.setVisibility(View.VISIBLE);
            Animation top = AnimationUtils.loadAnimation(mC,R.anim.infromtop);
            pannel.startAnimation(top);
            addBonusPanelIsVisible=true;
        } else {
            Animation bot = AnimationUtils.loadAnimation(mC,R.anim.outtotop);
            pannel.startAnimation(bot);
            pannel.setVisibility(View.GONE);
            addBonusPanelIsVisible=false;
        }
    }

    public void hide() {
        buttonAdd.setVisibility(View.GONE);
        if(addBonusPanelIsVisible){flipBonusPanel();}
    }

    public void show() {
        buttonAdd.setVisibility(View.VISIBLE);
    }

    public ImageView getButton() {
        return buttonAdd;
    }

}
