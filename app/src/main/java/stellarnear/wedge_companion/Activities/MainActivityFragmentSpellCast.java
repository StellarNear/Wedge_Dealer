package stellarnear.wedge_companion.Activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import stellarnear.wedge_companion.CalculationSpell;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Spells.SelectedSpells;
import stellarnear.wedge_companion.Spells.Spell;
import stellarnear.wedge_companion.Spells.SpellList;
import stellarnear.wedge_companion.Spells.SpellProfile;
import stellarnear.wedge_companion.Tools;


public class MainActivityFragmentSpellCast extends Fragment {

    private SpellList selectedSpells;
    private Tools tools=new Tools();
    private CalculationSpell calculationSpell =new CalculationSpell();
    private TextView round;
    private LinearLayout mainLin;
    private View returnFragView;

    public MainActivityFragmentSpellCast() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (container != null) {
            container.removeAllViews();
        }
        super.onCreate(savedInstanceState);

        returnFragView= inflater.inflate(R.layout.spell_cast, container, false);

        mainLin = (LinearLayout) returnFragView.findViewById(R.id.linear2);
        round = (TextView) returnFragView.findViewById(R.id.n_round);

        ((ImageButton) returnFragView.findViewById(R.id.back_spell_from_spell_cast)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToSpell();
            }
        });

        addSpellsForTarget();

        refreshRound();
        return returnFragView;
    }

    private void addSpellsForTarget() {
        selectedSpells= SelectedSpells.getInstance().getSelection();
        for (final Spell spell : selectedSpells.asList()) {
            SpellProfile spellProfile = spell.getProfile() ;
            View spellProfileView=spellProfile.getProfile(getActivity(),getContext());
            if(spellProfileView.getParent()!=null){((ViewGroup)spellProfileView.getParent()).removeView(spellProfileView);}
            mainLin.addView(spellProfileView);
            spellProfile.setRefreshEventListener(new SpellProfile.OnRefreshEventListener() {
                @Override
                public void onEvent() {
                    refreshRound();
                    if(selectedSpells.haveBindedSpells()){
                        refreshAllProfiles();
                    }
                    testAllEndRound();
                }
            });
        }
    }



    private void refreshAllProfiles() {
        for(Spell spell : selectedSpells.asList()){
            spell.refreshProfile();
        }
    }

    private void refreshRound() {
        int nRound = calculationSpell.calcRounds(getContext(), selectedSpells);
        round.setText("["+nRound+" round"+(nRound>1 ? "s":"")+"]");
    }

    private void testAllEndRound() {
        boolean end = true;
        for (Spell spell : selectedSpells.asList()){
            if(!spell.getProfile().isDone()){
                end=false;
            }
        }
        if(end){
            tools.customToast(getContext(),"Plus de sort à lancer");
        }
    }

    public void backToSpell() {
        Fragment fragment = new MainActivityFragmentSpell();
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.animator.infadefrag,R.animator.outtorightfrag);
        fragmentTransaction.replace(R.id.fragment_main_frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}



