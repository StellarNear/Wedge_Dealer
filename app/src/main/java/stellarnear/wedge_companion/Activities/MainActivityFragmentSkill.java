package stellarnear.wedge_companion.Activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.Perso.Skill;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.TestAlertDialog;
import stellarnear.wedge_companion.Tools;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragmentSkill extends Fragment {
    private Perso pj = PersoManager.getCurrentPJ();
    private LinearLayout linearSkillScroll;
    private View returnFragView;
    private Tools tools=new Tools();
    public MainActivityFragmentSkill() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (container != null) {
            container.removeAllViews();
        }

        returnFragView = inflater.inflate(R.layout.fragment_main_skill, container, false);

        linearSkillScroll = returnFragView.findViewById(R.id.skillscrollLayout);

        for (Skill skill : pj.getAllSkills().getSkillsList()) {
            addAllColumns(skill);
        }

        ImageButton buttonMain = (ImageButton) returnFragView.findViewById(R.id.button_frag_skill_to_main);

        animate(buttonMain);

        buttonMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment;
                if(getActivity() instanceof MainActivity) {
                    fragment = new MainActivityFragment();
                }else{
                    fragment = new PetActivityFragment();
                }
                FragmentManager fragmentManager = getActivity().getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.animator.infadefrag,R.animator.outtoleftfrag);
                fragmentTransaction.replace(R.id.fragment_main_frame_layout, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });


        return returnFragView;
    }


    private void addAllColumns(Skill skill) {
        LinearLayout line = new LinearLayout(getContext());
        line.setOrientation(LinearLayout.HORIZONTAL);
        line.setGravity(Gravity.CENTER);
        line.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,getResources().getDimensionPixelSize(R.dimen.icon_skills_list_height)));
        line.setBackground(getResources().getDrawable(R.drawable.skill_bar_gradient));
        setNameListnerRollSkill(line,skill);

        TextView nameTxt = new TextView(getContext());
        TextView nameTitle = returnFragView.findViewById(R.id.skillNameTitle);
        nameTxt.setLayoutParams(nameTitle.getLayoutParams());
        nameTxt.setText(skill.getName());
        int imgId = getResources().getIdentifier(skill.getId(), "drawable", getContext().getPackageName());
        nameTxt.setCompoundDrawablesWithIntrinsicBounds(tools.resize(getContext(),getContext().getDrawable(imgId),(int) (getResources().getDimensionPixelSize(R.dimen.icon_skills_list_height)*0.8)),null,null,null);
        nameTxt.setPadding(getResources().getDimensionPixelSize(R.dimen.general_margin),0,0,0);
        nameTxt.setGravity(Gravity.CENTER);

        TextView totalTxt = new TextView(getContext());
        TextView totalTitle = returnFragView.findViewById(R.id.skillTotalTitle);
        totalTxt.setLayoutParams(totalTitle.getLayoutParams());
        int total = pj.getAbilityMod(skill.getAbilityDependence())+pj.getSkillRank(skill.getId())+pj.getSkillBonus(skill.getId());
        totalTxt.setText(String.valueOf(total));
        totalTxt.setTypeface(null, Typeface.BOLD);
        totalTxt.setGravity(Gravity.CENTER);

        TextView abiTxt = new TextView(getContext());
        TextView abiTitle = returnFragView.findViewById(R.id.skillAbiTitle);
        abiTxt.setLayoutParams(abiTitle.getLayoutParams());
        String abScore;
        if(pj.getAbilityMod(skill.getAbilityDependence())>=0){
            abScore = "+"+pj.getAbilityMod(skill.getAbilityDependence());
        } else {
            abScore = String.valueOf(pj.getAbilityMod(skill.getAbilityDependence()));
        }
        abiTxt.setText(skill.getAbilityDependence().substring(8,11) + " : " +abScore );  //la clef de l'id etant ability_x
        abiTxt.setGravity(Gravity.CENTER);

        TextView rankTxt = new TextView(getContext());
        TextView rankTitle = returnFragView.findViewById(R.id.skillRankTitle);
        rankTxt.setLayoutParams(rankTitle.getLayoutParams());
        rankTxt.setText(String.valueOf(pj.getSkillRank(skill.getId())));
        rankTxt.setGravity(Gravity.CENTER);

        TextView bonusTxt = new TextView(getContext());
        TextView bonusTitle = returnFragView.findViewById(R.id.skillBonusTitle);
        bonusTxt.setLayoutParams(bonusTitle.getLayoutParams());
        bonusTxt.setText(String.valueOf(pj.getSkillBonus(skill.getId())));
        bonusTxt.setGravity(Gravity.CENTER);

        line.addView(nameTxt);
        line.addView(totalTxt);
        line.addView(abiTxt);
        line.addView(rankTxt);
        line.addView(bonusTxt);

        linearSkillScroll.addView(line);

    }

    private void setNameListnerRollSkill(LinearLayout line,final Skill skill) {
        line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TestAlertDialog(getActivity(),getContext(),skill,pj.getAbilityMod(skill.getAbilityDependence()));
            }
        });
    }

    private void animate(final ImageButton buttonMain) {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Animation anim = new ScaleAnimation(1f,1.25f,1f,1.25f,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                anim.setRepeatCount(1);
                anim.setRepeatMode(Animation.REVERSE);
                anim.setDuration(666);

                buttonMain.startAnimation(anim);
            }
        }, getResources().getInteger(R.integer.translationFragDuration));


    }
}
