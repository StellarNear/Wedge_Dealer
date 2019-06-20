package stellarnear.wedge_dealer.SettingsFraments.DisplayStatsScreenFragment;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ViewFlipper;

import stellarnear.wedge_dealer.R;
import stellarnear.wedge_dealer.Tools;

public class DSSF {
    private Activity mA;
    private Context mC;

    private Tools tools=new Tools();
    private View mainView;
    private ViewFlipper panel;

    private DSSFAtk fragAtk;
    private DSSFGraph fragGraph;
    private DSSFDmg fragDmg;
    private String position="atk";


    public DSSF(Activity mA, Context mC) {
        this.mA=mA;
        this.mC=mC;
    }

    public void addStatsScreen() {
        ViewGroup window = mA.findViewById(android.R.id.content);
        LayoutInflater inflater = LayoutInflater.from(mC);
        mainView = inflater.inflate(R.layout.stats_charts, null);
        fragAtk = new DSSFAtk(mainView,mC);
        buttonSetup();
        window.addView(mainView);
    }

    private void buttonSetup() {
        panel = mainView.findViewById(R.id.stats_flipper);
        final FloatingActionButton fabAtk = mainView.findViewById(R.id.fab_stat_atk);
        final FloatingActionButton fabGraph= mainView.findViewById(R.id.fab_stat_graph);
        final FloatingActionButton fabDmg = mainView.findViewById(R.id.fab_stat_dmg);

        fabAtk.animate().alpha(0).setDuration(0).scaleX(2f).scaleY(2f).start();
        fabAtk.animate().alpha(1).scaleX(1f).scaleY(1f).setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
        fabAtk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                movePanelTo("atk");
                popIn(fabAtk);
                fragAtk.reset();
            }
        });

        fabGraph.animate().alpha(0).setDuration(0).translationY(100).start();
        fabGraph.animate().setStartDelay(1000).setDuration(500).alpha(1).translationY(0).setInterpolator(new DecelerateInterpolator()).start();
        fabGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fragGraph==null){fragGraph=new DSSFGraph(mainView,mC);}
                movePanelTo("graph");
                popIn(fabGraph);
                fragGraph.reset();
            }
        });

        fabDmg.animate().alpha(0).setDuration(0).translationX(100).start();
        fabDmg.animate().setStartDelay(1000).setDuration(500).alpha(1).translationX(0).setInterpolator(new DecelerateInterpolator()).start();
        fabDmg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fragDmg==null){  fragDmg = new DSSFDmg(mainView,mC);}
                movePanelTo("dmg");
                popIn(fabDmg);
                fragDmg.reset();
            }
        });
    }

    private void popIn(final FloatingActionButton fab) {
        fab.animate().setStartDelay(0).scaleX(1.5f).scaleY(1.5f).setDuration(100).withEndAction(new Runnable() {
            @Override
            public void run() {
                fab.animate().setStartDelay(0).scaleX(1f).scaleY(1f).setDuration(400);
            }
        });
    }

    private void movePanelTo(String toPosition) {
        if(!this.position.equalsIgnoreCase(toPosition)) {
            Animation in=null;Animation out=null;
            int indexChild=0;
            switch (this.position) {
                case "atk":
                    out = AnimationUtils.loadAnimation(mC, R.anim.outtoleft);
                    break;

                case "graph":
                    out = AnimationUtils.loadAnimation(mC, R.anim.outtobot);
                    break;

                case "dmg":
                    out = AnimationUtils.loadAnimation(mC, R.anim.outtoright);
                    break;
            }

            switch (toPosition) {
                case "atk":
                    in = AnimationUtils.loadAnimation(mC, R.anim.infromleft);
                    indexChild=0;

                    break;

                case "graph":
                    in = AnimationUtils.loadAnimation(mC, R.anim.infrombot);
                    indexChild=1;
                    break;

                case "dmg":
                    in = AnimationUtils.loadAnimation(mC, R.anim.infromright);
                    indexChild=2;
                    break;
            }

            panel.clearAnimation();
            panel.setInAnimation(in);
            panel.setOutAnimation(out);
            panel.setDisplayedChild(indexChild);
            this.position = toPosition;
        }
    }

}
