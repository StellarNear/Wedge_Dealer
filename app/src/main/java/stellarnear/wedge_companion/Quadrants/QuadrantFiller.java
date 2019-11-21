package stellarnear.wedge_companion.Quadrants;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stellarnear.wedge_companion.Activities.MainActivity;
import stellarnear.wedge_companion.Activities.PetActivity;
import stellarnear.wedge_companion.HealthDialog;
import stellarnear.wedge_companion.Perso.Ability;
import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.Perso.Resource;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.TestAlertDialog;
import stellarnear.wedge_companion.Tools;


public class QuadrantFiller {
    private Context mC;
    private Activity mA;
    private View mainView;
    private LinearLayout quadrant1;
    private LinearLayout quadrant2;
    private LinearLayout quadrant3;
    private LinearLayout quadrant4;
    private Perso pj = PersoManager.getCurrentPJ();
    private Map<LinearLayout,String> mapLayoutTextTitle =new HashMap<>();
    private Map<LinearLayout,String> mapQuadrantType=new HashMap<>();
    private ViewSwitcher viewSwitcher;
    private ImageButton imgExit;
    private LinearLayout quadrantFullSub1;
    private LinearLayout quadrantFullSub2;
    private boolean fullscreen=false;
    private Tools tools=new Tools();
    private View fullView;
    public QuadrantFiller(View mainView, Context mC, Activity mA) {
        this.mC=mC;
        this.mA=mA;
        this.mainView=mainView;
        viewSwitcher=mainView.findViewById(R.id.viewSwitcherQuadrant);
        LayoutInflater inflater = (LayoutInflater) mC.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(PersoManager.isMainPJ()){
            fullView =(View) inflater.inflate(R.layout.quadrant_full, null);
        } else {
            fullView =(View) inflater.inflate(R.layout.quadrant_full_pet, null);
        }
        viewSwitcher.addView(fullView);
        quadrantFullSub1 = fullView.findViewById(R.id.quadrant_full_sub1);
        quadrantFullSub2 = fullView.findViewById(R.id.quadrant_full_sub2);
        imgExit = fullView.findViewById(R.id.button_quadrant_full_exit);
        imgExit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                switchViewPrevious();
            }
        });
        quadrant1 = mainView.findViewById(R.id.main_frag_stats_quadrant1);
        quadrant2 = mainView.findViewById(R.id.main_frag_stats_quadrant2);
        quadrant3 = mainView.findViewById(R.id.main_frag_stats_quadrant3);
        quadrant4 = mainView.findViewById(R.id.main_frag_stats_quadrant4);
        mapLayoutTextTitle.put(quadrant1,mC.getResources().getString(R.string.quadrantQ1Title));
        mapQuadrantType.put(quadrant1,"base");
        mapLayoutTextTitle.put(quadrant2,mC.getResources().getString(R.string.quadrantQ2Title));
        mapQuadrantType.put(quadrant2,"res");
        mapLayoutTextTitle.put(quadrant3,mC.getResources().getString(R.string.quadrantQ3Title));
        mapQuadrantType.put(quadrant3,"def");
        mapLayoutTextTitle.put(quadrant4,mC.getResources().getString(R.string.quadrantQ4Title));
        mapQuadrantType.put(quadrant4,"advanced");
        buildAllMini();
    }

    private void buildAllMini() {
        String[] types = {"base","res","def","advanced"};
        for (int i=0;i<types.length ;i++){
            String nameSub1="main_frag_stats_quadrant"+String.valueOf(i+1)+"_1";
            int layIdSub1 = mC.getResources().getIdentifier(nameSub1, "id", mC.getPackageName());
            LinearLayout sub1 =  mainView.findViewById(layIdSub1);
            String nameSub2="main_frag_stats_quadrant"+String.valueOf(i+1)+"_2";
            int layIdSub2 = mC.getResources().getIdentifier(nameSub2, "id", mC.getPackageName());
            LinearLayout sub2 =  mainView.findViewById(layIdSub2);
            if (i==1){
                List<Resource> resList= pj.getAllResources().getResourcesListDisplay();
                injectStatsRes(resList, sub1, sub2,"mini");
            } else {
                List<Ability> abiList= pj.getAllAbilities().getAbilitiesList(types[i]);
                injectStatsAbi(abiList, sub1, sub2,"mini");
            }
        }
    }

    public void fullscreenQuadrant(LinearLayout layout){
        if (mapQuadrantType.get(layout).equalsIgnoreCase("res")){
            List<Resource> abiRes= pj.getAllResources().getResourcesListDisplay();
            injectStatsRes(abiRes, quadrantFullSub1, quadrantFullSub2,"full");
        } else {
            List<Ability> abiList= pj.getAllAbilities().getAbilitiesList(mapQuadrantType.get(layout));
            injectStatsAbi(abiList, quadrantFullSub1, quadrantFullSub2,"full");
        }
        switchTextTitle(mapLayoutTextTitle.get(layout));
        switchViewNext();
    }

    private void injectStatsAbi(List<Ability> abiList, LinearLayout quadrantSub1, LinearLayout quadrantSub2,String mode) {
        quadrantSub1.removeAllViews();
        quadrantSub2.removeAllViews();
        for (Ability abi : abiList){
            addTextAbi(abi,quadrantSub1,quadrantSub2,mode);
        }
    }

    private void injectStatsRes(List<Resource> resList, LinearLayout quadrantSub1, LinearLayout quadrantSub2, String mode) {
        quadrantSub1.removeAllViews();
        quadrantSub2.removeAllViews();
        for (Resource res : resList){
            addTextRes(res, quadrantSub1, quadrantSub2, mode);
        }
    }

    private void addTextAbi(Ability abi, LinearLayout quadrantSub1, LinearLayout quadrantSub2, String mode) {
        float textSize;  int iconSize;
        if (mode.equalsIgnoreCase("mini")){  textSize=mC.getResources().getDimension(R.dimen.textSizeQuadrantMini); iconSize=mC.getResources().getDimensionPixelSize(R.dimen.iconSizeQuadrantMini);  } else { textSize=mC.getResources().getDimension(R.dimen.textSizeQuadrantFull);  iconSize=mC.getResources().getDimensionPixelSize(R.dimen.iconSizeQuadrantFull);   }
        TextView text = new TextView(mC);
        text.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
        if (mode.equalsIgnoreCase("mini")){
            text.setText(abi.getShortname()+" : ");
        } else {
            text.setText(abi.getName()+" : ");
        }
        text.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,1));
        text.setGravity(Gravity.CENTER_VERTICAL);
        text.setCompoundDrawablesWithIntrinsicBounds( tools.resize(mC,abi.getImg(),iconSize),null,null,null);
        if (mode.equalsIgnoreCase("full")){text.setCompoundDrawablePadding(mC.getResources().getDimensionPixelSize(R.dimen.full_quadrant_icons_margin));
        }else{text.setCompoundDrawablePadding(mC.getResources().getDimensionPixelSize(R.dimen.mini_quadrant_icons_margin));}
        quadrantSub1.addView(text);
        TextView text2 = new TextView(mC);
        text2.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
        String txt2;
        if (abi.getType().equalsIgnoreCase("base")){
            String abScore = "";
            if(pj.getAbilityMod(abi.getId())>=0){
                abScore = "+"+ pj.getAbilityMod(abi.getId());
            } else {
                abScore = String.valueOf(pj.getAbilityMod(abi.getId()));
            }
            txt2=String.valueOf(pj.getAbilityScore(abi.getId()))+ " ("+abScore+")";
        } else {
            txt2=String.valueOf(pj.getAbilityScore(abi.getId()));
        }
        text2.setText(txt2);
        text2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,1));
        text2.setGravity(Gravity.CENTER_VERTICAL);
        quadrantSub2.addView(text2);

        if(mode.equalsIgnoreCase("full") && abi.isTestable()){
            setListner(text,abi);
            setListner(text2,abi);
        }
    }

    private void addTextRes(Resource res, LinearLayout quadrantSub1, LinearLayout quadrantSub2, String mode) {
        float textSize; int iconSize;
        if (mode.equalsIgnoreCase("mini")){ textSize=mC.getResources().getDimension(R.dimen.textSizeQuadrantMini);   iconSize=mC.getResources().getDimensionPixelSize(R.dimen.iconSizeQuadrantMini);  } else {    textSize=mC.getResources().getDimension(R.dimen.textSizeQuadrantFull);    iconSize=mC.getResources().getDimensionPixelSize(R.dimen.iconSizeQuadrantFull); }
        TextView column1 = new TextView(mC);
        column1.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
        if (mode.equalsIgnoreCase("mini")){
            column1.setText(res.getShortname()+" : ");
        } else {
            String column1txt=res.getName()+" : ";
            if(mode.equalsIgnoreCase("full") && res.getId().equalsIgnoreCase("resource_hp") && res.getShield() > 0 ){
                column1txt= res.getName()+" (temp) : ";
            }
            column1.setText(column1txt);
        }
        column1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,1));
        column1.setGravity(Gravity.CENTER_VERTICAL);
        column1.setCompoundDrawablesWithIntrinsicBounds( tools.resize(mC,res.getImg(),iconSize),null,null,null);
        if (mode.equalsIgnoreCase("full")){column1.setCompoundDrawablePadding(mC.getResources().getDimensionPixelSize(R.dimen.full_quadrant_icons_margin));
        }else{column1.setCompoundDrawablePadding(mC.getResources().getDimensionPixelSize(R.dimen.mini_quadrant_icons_margin));}
        quadrantSub1.addView(column1);
        TextView column2 = new TextView(mC);
        column2.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
        String column2txt;

        if(res.getId().equalsIgnoreCase("resource_display_rank")){
            column2txt= pj.getAllResources().getRankManager().getPercentAvail();

        } else {
            column2txt = String.valueOf(pj.getResourceValue(res.getId()));
            if (mode.equalsIgnoreCase("mini") && res.getId().equalsIgnoreCase("resource_hp")) {
                column2txt = String.valueOf(pj.getAllResources().getResource(res.getId()).getCurrent() + pj.getAllResources().getResource(res.getId()).getShield());
            }
            if (mode.equalsIgnoreCase("full") && res.getId().equalsIgnoreCase("resource_hp")) {
                column2txt = String.valueOf(pj.getAllResources().getResource(res.getId()).getCurrent());
                if (pj.getAllResources().getResource(res.getId()).getShield() > 0) {
                    column2txt += " (" + String.valueOf(pj.getAllResources().getResource(res.getId()).getShield()) + ")";
                }
            }
        }
        column2.setText(column2txt);
        column2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,1));
        column2.setGravity(Gravity.CENTER_VERTICAL);
        quadrantSub2.addView(column2);

        if(mode.equalsIgnoreCase("full") && res.isTestable()){ //correspond à hp
            setListner(column1,res);
            setListner(column2,res);
        }
    }

    private void setListner(TextView text,final Ability abi) {
        text.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TestAlertDialog testAlertDialog = new TestAlertDialog(mA, mC, abi);
                testAlertDialog.setRefreshEventListener(new TestAlertDialog.OnRefreshEventListener() {
                    @Override
                    public void onEvent() {
                        buildAllMini();
                    }
                });
            }
        });
    }

    private void setListner(TextView text,final Resource res) { //constructeur ressoruce pour Healthbar
        text.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HealthDialog healthDialog = new HealthDialog(mA,mC);
                healthDialog.setRefreshEventListener(new HealthDialog.OnRefreshEventListener() {
                    public void onEvent() {
                        List<Resource> abiRes= pj.getAllResources().getResourcesListDisplay();
                        injectStatsRes(abiRes, quadrantFullSub1, quadrantFullSub2,"full"); //refresh le full
                        buildAllMini(); //refresh les mini
                    }
                });
                healthDialog.showAlertDialog();
            }
        });
    }

    private void switchViewNext() {
        imgExit.setVisibility(View.VISIBLE);
        quadrantFullSub1.setVisibility(View.VISIBLE);
        quadrantFullSub2.setVisibility(View.VISIBLE);
        viewSwitcher.setInAnimation(mC,R.anim.infromright);
        viewSwitcher.setOutAnimation(mC,R.anim.outtoleft);
        viewSwitcher.showNext();
        fullscreen=true;
        lockOrient();
    }
    private void switchViewPrevious() {
        imgExit.setVisibility(View.GONE);
        quadrantFullSub1.setVisibility(View.GONE);
        quadrantFullSub2.setVisibility(View.GONE);
        switchTextTitle(mC.getResources().getString(R.string.quadrantGeneralTitle),"back");
        viewSwitcher.setInAnimation(mC,R.anim.infromleft);
        viewSwitcher.setOutAnimation(mC,R.anim.outtoright);
        viewSwitcher.showPrevious();
        fullscreen=false;
        unlockOrient();
    }

    private void switchTextTitle(final String s,String... mode){
        String modeSelected=mode.length > 0 ? mode[0] : "";  //parametre optionnel mode
        final TextView quadrantTitle=mainView.findViewById(R.id.quadrantGeneralTitle);
        quadrantTitle.clearAnimation();
        final Animation in;
        Animation out;
        if (modeSelected.equals("back"))
        {
            in = AnimationUtils.loadAnimation(mC, R.anim.infromleftfilled);
            out = AnimationUtils.loadAnimation(mC, R.anim.outtorightfilled);
        } else {
            in = AnimationUtils.loadAnimation(mC, R.anim.infromrightfilled);
            out = AnimationUtils.loadAnimation(mC, R.anim.outtoleftfilled);
        }
        out.setAnimationListener( new  Animation.AnimationListener(){
            @Override
            public void onAnimationEnd(Animation arg0) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        quadrantTitle.setText(s);
                        quadrantTitle.clearAnimation();
                        quadrantTitle.startAnimation(in);
                    }
                }, 50);
            }
            @Override
            public void onAnimationRepeat(Animation arg0) {         }
            @Override
            public void onAnimationStart(Animation arg0) { }
        });
        quadrantTitle.startAnimation(out);
    }

    public boolean isFullscreen() { return fullscreen;  }

    private void lockOrient() {
        if(mA instanceof MainActivity) {
            mA.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else if (mA instanceof PetActivity) {
            mA.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    private void unlockOrient() {     mA.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    }


}