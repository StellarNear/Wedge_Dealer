package stellarnear.wedge_companion.SettingsFraments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;

import stellarnear.wedge_companion.Activities.MainActivity;
import stellarnear.wedge_companion.CustomAlertDialog;
import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.Perso.Resource;
import stellarnear.wedge_companion.PostData;
import stellarnear.wedge_companion.PostDataElement;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Tools;


public class PrefSpellgemScreenFragment {
    private Activity mA;
    private Context mC;
    private View mainView;
    private CustomAlertDialog spellgemPopup;
    private Perso pj = PersoManager.getCurrentPJ();
    private Tools tools = Tools.getTools();
    private Set<TierCard> allTiers;
    private Integer totalRankToSpend=0;
    private Integer currentTotal=0;

    public PrefSpellgemScreenFragment(Activity mA, Context mC) {
        this.mA = mA;
        this.mC = mC;
    }

    private void createSpellgemPopup() {
        LayoutInflater inflater = LayoutInflater.from(mC);
        mainView = inflater.inflate(R.layout.custom_spellgem, null);

        buildAllTierCards();
        this.spellgemPopup = new CustomAlertDialog(mA, mC, mainView);
        this.spellgemPopup.setPermanent(true);

        this.spellgemPopup.addCancelButton("Annuler");
        this.spellgemPopup.addConfirmButton("Valider");
        this.spellgemPopup.setAcceptEventListener(new CustomAlertDialog.OnAcceptEventListener() {
            @Override
            public void onEvent() {
                if (allTiers != null) {
                    startRefundResources();
                }
                Intent intent = new Intent(mA, MainActivity.class);// Switch to MainActivityFragmentSpell
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                mA.startActivity(intent);
            }
        });
        ((EditText) mainView.findViewById(R.id.spellgem_input_number)).addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                totalRankToSpend=tools.toInt(s.toString());
                if(allTiers!=null) {
                    for (TierCard tierCard : allTiers) {
                        tierCard.resetCurrent();
                    }
                    refreshCurrentValues();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {         }
        });
    }


    private void buildAllTierCards() {
        LinearLayout mainLin = mainView.findViewById(R.id.custom_mainlin);
        mainLin.removeAllViews();
        allTiers=new HashSet<>();
        LinearLayout line =createNewLine();
        mainLin.addView(line);
        pj.getAllResources().getRankManager().refreshRanks();
        pj.getAllResources().getRankManager().refreshMax();

        int nPerLine = 0;
        for (final Resource res : pj.getAllResources().getRankManager().getSpellTiers()) {
            if (res.getMax() > 0) {
                if (nPerLine > 3) {
                    line = createNewLine();
                    mainLin.addView(line);
                    nPerLine = 0;
                }
                TierCard tierCard = new TierCard(res);
                allTiers.add(tierCard);
                line.addView(tierCard.getView());
                nPerLine++;
            }
        }
    }

    private LinearLayout createNewLine() {
        LinearLayout line = new LinearLayout(mC);
        line.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        line.setGravity(Gravity.CENTER);
        return line;
    }

    private void startRefundResources() {
        String postDataSummary = "Recharge de "+totalRankToSpend+" rangs de sorts :";
        for(TierCard tierCard : allTiers){
            if(tierCard.currentValue>0){
                reload(tierCard);
                postDataSummary += "\n"+ tierCard.currentValue+" sorts de rang "+tierCard.rankTier;
            }
        }
        tools.customToast(mC, postDataSummary, "center");
        new PostData(mC,new PostDataElement("Consomation de gemmes magiques",postDataSummary));
    }

    private void reload(TierCard tierCard) {
        tierCard.getRes().earn(tierCard.currentValue);
        Resource convRes = pj.getAllResources().getResource(tierCard.getRes().getId().replace("spell_rank_", "spell_conv_rank_"));
        if (convRes != null && convRes.getMax() > 0) {
            convRes.earn(tierCard.currentValue);
        }
    }

    public void showSpellgem() {
        createSpellgemPopup();
        this.spellgemPopup.showAlert();
        this.spellgemPopup.getConfirmButton().setVisibility(View.GONE); //on l'afiche que si on a pick tout les rank
    }

    private void refreshCurrentValues() {
        currentTotal = 0 ;
        for(TierCard tierCard : allTiers){
            currentTotal+=tierCard.getCurrentSpendRanks();
        }
        ((TextView) mainView.findViewById(R.id.spellgem_spend)).setText("Consommés : "+currentTotal);
        ((TextView) mainView.findViewById(R.id.spellgem_remaining)).setText("Restants : "+String.valueOf(totalRankToSpend-currentTotal));
        if(currentTotal==totalRankToSpend){
            this.spellgemPopup.getConfirmButton().setVisibility(View.VISIBLE);
        } else {
            this.spellgemPopup.getConfirmButton().setVisibility(View.GONE);
        }
    }

    private class TierCard {
        private TextView text;
        private Resource res;
        private Integer rankTier=0;
        private Integer currentValue=0;
        private TierCard(final Resource resource){
            res=resource;
            rankTier=tools.toInt(res.getId().replace("spell_rank_", ""));
            text = new TextView(mC);
            text.setTextSize(20);
            text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            text.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            text.setTextColor(Color.DKGRAY);

            refreshCard();
            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if((res.getCurrent()+currentValue+1)<= res.getMax()) {
                        if ((currentTotal + rankTier) <= totalRankToSpend) {
                            currentValue++;
                            refreshCard();
                            refreshCurrentValues();
                        } else {
                            tools.customToast(mC, "Pas assez de rang à dépenser...", "center");
                        }
                    } else  {
                        tools.customToast(mC, "Cela dépasserai le nombre maximal pour ce rang", "center");
                    }
                }
            });
            text.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    currentValue--;
                    refreshCard();
                    refreshCurrentValues();
                    return true;
                }
            });
        }

        private void refreshCard() {
            if(currentValue>0){
                text.setText("Rang "+rankTier + "\n"+"("+currentValue+") "+"["+res.getCurrent()+"/"+res.getMax()+"]");
            }else {
                text.setText("Rang "+rankTier+ "\n"+"["+res.getCurrent()+"/"+res.getMax()+"]");
            }
        }

        public TextView getView() {
            return text;
        }

        public void resetCurrent() {
            this.currentValue=0;
            refreshCard();
        }

        public Integer getCurrentSpendRanks() {
            return currentValue*rankTier;
        }

        public Resource getRes() {
            return res;
        }
    }


}
