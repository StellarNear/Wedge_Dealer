package stellarnear.wedge_companion.Perso;

import android.content.Context;

/**
 * Created by jchatron on 26/12/2017.
 */

public class CanalisationCapacity {
    private Context mC;
    private String name;
    private int cost;
    private String feat;
    private String descr;
    private String shortdescr;
    private String id;

    public CanalisationCapacity(String name, int cost, String feat, String descr, String shortdescr, String id, Context mC)
    {
        this.mC = mC;
        this.name=name;
        this.cost=cost;
        this.feat=feat;
        this.descr=descr;
        if(shortdescr.equalsIgnoreCase("")){
            this.shortdescr=descr;
        } else {
            this.shortdescr=shortdescr;
        }
        this.id=id;
    }

    public String getName() {
        return name;
    }

    public int getCost() {
        return cost;
    }

    public String getFeat() {
        return feat;
    }

    public String getDescr() {
        return descr;
    }

    public String getShortdescr() {
        return shortdescr;
    }

    public String getId(){return id;}
}
