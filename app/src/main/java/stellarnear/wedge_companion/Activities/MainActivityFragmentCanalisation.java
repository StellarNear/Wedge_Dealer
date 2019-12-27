package stellarnear.wedge_companion.Activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import stellarnear.wedge_companion.CanalisationAlertDialog;
import stellarnear.wedge_companion.Perso.CanalisationCapacity;
import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.PostData;
import stellarnear.wedge_companion.PostDataElement;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Tools;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragmentCanalisation extends Fragment {
    private Perso pj = PersoManager.getCurrentPJ();
    private View returnFragView;
    private List<LinearLayout> allKiCapa=new ArrayList<>();
    private CanalisationCapacity canalCapaSelected;

    private Button valid;
    private Tools tools=new Tools();
    public MainActivityFragmentCanalisation() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (container != null) {
            container.removeAllViews();
        }

        returnFragView= inflater.inflate(R.layout.fragment_main_canalisation, container, false);
        ImageButton buttonMain = (ImageButton) returnFragView.findViewById(R.id.button_frag_canal_to_main);

        animate(buttonMain);
        buttonMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToMain();
            }
        });

        TextView mainTitleKicount = returnFragView.findViewById(R.id.mainPanelKicount);
        String canalCount = "(";
        int currentKi= pj.getCurrentResourceValue("resource_canalisation");
        if(currentKi>0){
            canalCount+="points restants : "+currentKi;
        } else {
            canalCount+="aucun points restants";
        }
        canalCount+=")";
        mainTitleKicount.setText(canalCount);

        LinearLayout contentLinear = returnFragView.findViewById(R.id.canalFragmentContentLinear);
        addContent(contentLinear);
        return returnFragView;
    }

    private void backToMain() {
        Fragment fragment = new MainActivityFragment();
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.animator.infadefrag,R.animator.outtobotfrag);
        fragmentTransaction.replace(R.id.fragment_main_frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void addContent(LinearLayout contentLinear) {

        for (CanalisationCapacity canalCapa : pj.getAllCanalisationCapacities().getAllCanalCapacitiesList()){
            LinearLayout lineCapa = new LinearLayout(getContext());
            lineCapa.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,1));
            lineCapa.setOrientation(LinearLayout.HORIZONTAL);
            lineCapa.setGravity(Gravity.CENTER);
            lineCapa.setBackground(getResources().getDrawable(R.drawable.canal_bar_gradient));

            TextView nameTxt = new TextView(getContext());
            TextView nameTitle = returnFragView.findViewById(R.id.canalNameTitle);
            nameTxt.setLayoutParams(nameTitle.getLayoutParams());
            nameTxt.setText(canalCapa.getName());

            int imgId = getResources().getIdentifier(canalCapa.getId(), "drawable", getContext().getPackageName());
            try {
                nameTxt.setCompoundDrawablesWithIntrinsicBounds(null,tools.resize(getContext(),getContext().getDrawable(imgId),(int) (getResources().getDimensionPixelSize(R.dimen.icon_canalcapacities_list))),null,null);
            } catch (Resources.NotFoundException e) {
                nameTxt.setCompoundDrawablesWithIntrinsicBounds(null,tools.resize(getContext(),getContext().getDrawable(R.drawable.mire_test),(int) (getResources().getDimensionPixelSize(R.dimen.icon_canalcapacities_list))),null,null);
                e.printStackTrace();
            }
            nameTxt.setPadding(getResources().getDimensionPixelSize(R.dimen.general_margin),0,0,0);
            nameTxt.setGravity(Gravity.CENTER);

            lineCapa.addView(nameTxt);

            TextView summary = new TextView(getContext());
            TextView summaryTitle = returnFragView.findViewById(R.id.canalEffectTitle);
            summary.setLayoutParams(summaryTitle.getLayoutParams());
            summary.setGravity(Gravity.CENTER);
            summary.setTextSize(12);
            summary.setPadding(getResources().getDimensionPixelSize(R.dimen.general_margin),0,0,0);
            String descr = canalCapa.getShortdescr();
            if(canalCapa.getId().equalsIgnoreCase("canalcapacity_heal")){
                int nDice = 1+(pj.getAbilityScore("ability_lvl")-1)/2;
                descr+="\n\nSoigne : "+nDice+"d6";
                if(pj.getAllCapacities().capacityIsActive("capacity_epic_revelation_canal")){
                    descr+="+4";
                    descr=descr.replace("9m","13m");
                }
            } else if(canalCapa.getId().equalsIgnoreCase("canalcapacity_heal_trigger")){
                if(pj.getCurrentResourceValue("resource_heal_trigger")>0){
                    descr+="\n\nCanalisation programmée à dépenser !";
                } else {
                    descr+="\n\nAucune canalisation programmée";
                }
            }

            summary.setText(descr);
            lineCapa.addView(summary);

            TextView cost = new TextView(getContext());
            TextView costTitle = returnFragView.findViewById(R.id.canalCostTitle);
            cost.setLayoutParams(costTitle.getLayoutParams());
            cost.setGravity(Gravity.CENTER);
            cost.setText(String.valueOf(canalCapa.getCost()));
            lineCapa.addView(cost);

            setCapaLineListner(lineCapa,canalCapa);
            allKiCapa.add(lineCapa);

            contentLinear.addView(lineCapa);
        }

        LinearLayout lineStep = new LinearLayout(getContext());
        LinearLayout.LayoutParams para = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1); //le weight est là pour que ca remplisse le restant du layout
        lineStep.setLayoutParams(para);
        lineStep.setGravity(Gravity.CENTER);
        valid = new Button(getContext());
        valid.setText("Confirmation");
        valid.setTextColor(getContext().getColor(R.color.colorBackground));
        valid.setBackground(getContext().getDrawable(R.drawable.button_basic_gradient));
        lineStep.addView(valid);

        contentLinear.addView(lineStep);

    }

    private void setCapaLineListner(final LinearLayout lineCapa, final CanalisationCapacity canalCapa) {
        lineCapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                canalCapaSelected=canalCapa;
                setLinearCapaColor(lineCapa);
            }
        });
    }

    private void setLinearCapaColor(LinearLayout lineCapa) {
        for (LinearLayout lin : allKiCapa){
            if (lin.equals(lineCapa)){
                lin.setBackground(getResources().getDrawable(R.drawable.canal_capacity_selected_bar_gradient));
            } else {
                lin.setBackground(getResources().getDrawable(R.drawable.canal_bar_gradient));
            }
        }

        if (pj.getAllResources().getResource("resource_canalisation").getCurrent()-canalCapaSelected.getCost()>=0){
            valid.setBackground(getContext().getDrawable(R.drawable.button_ok_gradient));
            valid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        String txt = "Lancement de : " + canalCapaSelected.getName();
                        if (canalCapaSelected.getId().equalsIgnoreCase("canalcapacity_heal")) {
                            launchCanalDialog(canalCapaSelected);
                            Snackbar snackbar = Snackbar.make(view, txt, Snackbar.LENGTH_LONG);
                            snackbar.show();
                        } else if(canalCapaSelected.getId().equalsIgnoreCase("canalcapacity_heal_trigger")){
                            if( pj.getCurrentResourceValue("resource_heal_trigger")>0){
                                new PostData(getContext(),new PostDataElement("Déclenchement de "+canalCapaSelected.getName(),"Lancement d'une canalisation automatique"));
                                pj.getAllResources().getResource("resource_heal_trigger").spend(1);
                                launchCanalDialog(canalCapaSelected);
                                Snackbar snackbar = Snackbar.make(view, txt, Snackbar.LENGTH_LONG);
                                snackbar.show();
                            } else if(pj.getCurrentResourceValue("resource_mythic_points")>0){
                                new PostData(getContext(),new PostDataElement("Initialisation de "+canalCapaSelected.getName(),"Programmation d'une canalisation automatique\n(-1 pt mythique)"));
                                pj.getAllResources().getResource("resource_mythic_points").spend(1);
                                pj.getAllResources().getResource("resource_heal_trigger").earn(1);
                                Snackbar snackbar = Snackbar.make(view, txt, Snackbar.LENGTH_LONG);
                                snackbar.show();
                                backToMain();
                            } else {
                                tools.customToast(getContext(),"Tu n'as plus de point mythique","center");
                            }
                        } else {
                            pj.getAllResources().getResource("resource_canalisation").spend(canalCapaSelected.getCost());
                            new PostData(getContext(),new PostDataElement(canalCapaSelected));
                            Snackbar snackbar = Snackbar.make(view, txt, Snackbar.LENGTH_LONG);
                            snackbar.show();
                            backToMain();
                        }

                }
            });
        } else {
            valid.setBackground(getContext().getDrawable(R.drawable.button_cancel_gradient));
            valid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tools.customToast(getContext(),"Tu n'as plus d'utilisation de canalisation d'énergie","center");
                }
            });
        }
    }

    private void launchCanalDialog(CanalisationCapacity canal) {
        CanalisationAlertDialog alertDialog = new CanalisationAlertDialog(getActivity(),getContext(),canal);
        alertDialog.setRefreshEventListener(new CanalisationAlertDialog.OnRefreshEventListener() {
            @Override
            public void onEvent() {
                backToMain();
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
