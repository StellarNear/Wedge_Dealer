package stellarnear.wedge_dealer.SettingsFraments;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ViewSwitcher;

import stellarnear.wedge_dealer.R;
import stellarnear.wedge_dealer.Tools;

public class DisplayStatsScreenFragment {
    private Activity mA;
    private Context mC;

    private Tools tools=new Tools();
    private View mainView;
    private ViewSwitcher panel;
    private boolean page2=false;

    private DisplayStatsScreenFragmentAtk fragAtk;
    private DisplayStatsScreenFragmentDmg fragDmg;

    public DisplayStatsScreenFragment(Activity mA, Context mC) {
        this.mA=mA;
        this.mC=mC;
    }

    public void addStatsScreen() {
        ViewGroup window = mA.findViewById(android.R.id.content);
        LayoutInflater inflater = LayoutInflater.from(mC);
        mainView = inflater.inflate(R.layout.stats_charts, null);

        fragAtk = new DisplayStatsScreenFragmentAtk(mainView,mC);
        fragDmg = new DisplayStatsScreenFragmentDmg(mainView,mC);

        buttonSetup();
        window.addView(mainView);
    }

    private void buttonSetup() {
        panel = mainView.findViewById(R.id.stats_switcher);
        final FloatingActionButton fabAtk = mainView.findViewById(R.id.fab_stat_atk);
        final FloatingActionButton fabDmg = mainView.findViewById(R.id.fab_stat_dmg);

        fabAtk.animate().alpha(0).setDuration(0).scaleX(2f).scaleY(2f).start();
        fabAtk.animate().alpha(1).scaleX(1f).scaleY(1f).setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
        fabDmg.animate().alpha(0).setDuration(0).translationY(100).start();
        fabDmg.animate().setStartDelay(1000).setDuration(500).alpha(1).translationY(0).setInterpolator(new DecelerateInterpolator()).start();
        fabAtk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                movePanelTo("atk");
                popIn(fabAtk);
                fragAtk.reset();
            }
        });

        fabDmg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

    private void movePanelTo(String position) {
        panel.clearAnimation();
        Animation in;Animation out;
        switch (position) {
            case "atk":
                   if (page2) {
                    panel.clearAnimation();
                    in = AnimationUtils.loadAnimation(mC, R.anim.infromleft);
                    out = AnimationUtils.loadAnimation(mC, R.anim.outtoright);
                    panel.setInAnimation(in);
                    panel.setOutAnimation(out);
                    panel.showPrevious();
                }
                break;

            case "dmg":
                if (!page2) {
                    panel.clearAnimation();
                    in = AnimationUtils.loadAnimation(mC, R.anim.infromright);
                    out = AnimationUtils.loadAnimation(mC, R.anim.outtoleft);
                    panel.setInAnimation(in);
                    panel.setOutAnimation(out);
                    panel.showNext();
                    fragDmg.initSubs();
                    break;
                }
        }
        page2=position.equalsIgnoreCase("dmg");
    }

}
