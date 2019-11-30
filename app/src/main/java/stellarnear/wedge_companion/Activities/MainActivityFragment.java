package stellarnear.wedge_companion.Activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.util.List;

import stellarnear.wedge_companion.Activities.MainActivityFramentCombat.MainActivityFragmentCombat;
import stellarnear.wedge_companion.Quadrants.QuadrantManager;
import stellarnear.wedge_companion.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    View returnFragView;
    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater,final ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (container != null) {
            container.removeAllViews();
        }

        returnFragView= inflater.inflate(R.layout.fragment_main, container, false);

        ImageButton fabSkill = (ImageButton) returnFragView.findViewById(R.id.button_frag_to_skill);
        setButtonActivity(fabSkill, new MainActivityFragmentSkill(), R.animator.infromleftfrag,R.animator.outfadefrag,"frag_skill");
        Animation left = AnimationUtils.loadAnimation(getContext(),R.anim.infromleft);
        fabSkill.startAnimation(left);

        ImageButton fabCombat = (ImageButton) returnFragView.findViewById(R.id.button_frag_to_combat);
        setButtonActivity(fabCombat,new MainActivityFragmentCombat(),R.animator.infrombotfrag,R.animator.outfadefrag,"frag_combat");
        Animation top = AnimationUtils.loadAnimation(getContext(),R.anim.infromtop);
        fabCombat.startAnimation(top);

        ImageButton fabForm = (ImageButton) returnFragView.findViewById(R.id.button_frag_to_form);
        setButtonActivity(fabForm,new MainActivityFragmentForm(),R.animator.infrombotfrag,R.animator.outfadefrag,"frag_combat");
        fabForm.startAnimation(top);

        ImageButton fabCast = (ImageButton) returnFragView.findViewById(R.id.button_frag_to_spell);
        setButtonActivity(fabCast,new MainActivityFragmentSpell(),R.animator.infromrightfrag,R.animator.outfadefrag,"frag_spell");
        Animation right = AnimationUtils.loadAnimation(getContext(),R.anim.infromright);
        fabCast.startAnimation(right);

        fadeInTooltips(getContext());

        final Context mC=getContext();
        final Activity mA=getActivity();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation fadeIn = AnimationUtils.loadAnimation(mC,R.anim.infade);
                View mainAbiText = returnFragView.findViewById(R.id.quadrantGeneralTitle);
                mainAbiText.setVisibility(View.VISIBLE);
                mainAbiText.startAnimation(fadeIn);
                popInQuadrant(mC,mA);
            }
        }, top.getDuration());

        return returnFragView;
    }


    private void fadeInTooltips(Context mC) {
        Animation fadeIn = AnimationUtils.loadAnimation(mC,R.anim.infade);
        View tooltip1 = returnFragView.findViewById(R.id.textCombatActionTooltip);
        View tooltip2 = returnFragView.findViewById(R.id.textSkillActionTooltip);
        View tooltip3 = returnFragView.findViewById(R.id.textSpellActionTooltip);
        View tooltip4 = returnFragView.findViewById(R.id.textFormActionTooltip);
        tooltip1.setVisibility(View.VISIBLE);
        tooltip2.setVisibility(View.VISIBLE);
        tooltip3.setVisibility(View.VISIBLE);
        tooltip4.setVisibility(View.VISIBLE);
        tooltip1.startAnimation(fadeIn);
        tooltip2.startAnimation(fadeIn);
        tooltip3.startAnimation(fadeIn);
        tooltip4.startAnimation(fadeIn);
    }

    private void popInQuadrant(Context mC,Activity mA) {
        QuadrantManager quadrantManager = new QuadrantManager(returnFragView,mC,mA);
        Animation pop = AnimationUtils.loadAnimation(mC,R.anim.popinquadrant);
        int delay=(int) (0.5*pop.getDuration());
        Animation pop2 = AnimationUtils.loadAnimation(mC,R.anim.popinquadrant);
        Animation pop3 = AnimationUtils.loadAnimation(mC,R.anim.popinquadrant);
        Animation pop4 = AnimationUtils.loadAnimation(mC,R.anim.popinquadrant);
        pop2.setStartOffset(delay);
        pop3.setStartOffset(delay*2);
        pop4.setStartOffset(delay*3);
        List<LinearLayout> quadrantList = quadrantManager.quadrantAsList();
        quadrantManager.showQuadrant();
        quadrantList.get(0).startAnimation(pop);
        quadrantList.get(1).startAnimation(pop2);
        quadrantList.get(2).startAnimation(pop3);
        quadrantList.get(3).startAnimation(pop4);
    }

    private void setButtonActivity(ImageButton button, final Fragment ActivityFragment,final int animIn,final int animOut,final String tag) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lockOrient();
                FragmentManager fragmentManager = getActivity().getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(animIn,animOut);
                fragmentTransaction.replace(R.id.fragment_main_frame_layout, ActivityFragment,tag);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }


    private void lockOrient() {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

}
