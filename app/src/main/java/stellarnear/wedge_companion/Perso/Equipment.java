package stellarnear.wedge_companion.Perso;
import android.content.Context;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by jchatron on 04/01/2018.
 */


public class Equipment {
    private String name;
    private String descr;
    private String value;
    private String slotId;
    private List<String> tags=new ArrayList<>();
    private String img_path;
    private Boolean equiped;

    public Equipment(String name, String descr, String value, String imgIdTxt, String slotId, Boolean equiped) {
        this.name = name;
        this.descr = descr;
        if (value.equalsIgnoreCase("")){
            this.value="-";
        } else {
            this.value=value;
        }
        this.img_path=imgIdTxt;
        this.slotId = slotId;
        this.equiped=equiped;
    }

    public Equipment(String name, String descr, String value, List<String> tags) { //for bag items
        this.name = name;
        this.descr = descr;
        if (value.equalsIgnoreCase("")){
            this.value="-";
        } else {
            this.value=value;
        }
        for(String tag:tags){
            if(!tag.equalsIgnoreCase("")){
                this.tags.add(tag);
            }
        }
    }

    public String getName() {
        return name;
    }

    public String getDescr() {
        return descr;
    }

    public String getSlotId() {
        return slotId;
    }

    public Drawable getImg(Context mC){  //pour la sauvegarde dans SHaredSetting on ne peut pas serialisé le contexte donc on le redonne à l'exec
        Drawable draw =null;
        try {
            int imgId = mC.getResources().getIdentifier(this.img_path, "drawable", mC.getPackageName());
            draw = mC.getDrawable(imgId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return draw;
    }

    public String getValue() {return this.value;}

    public boolean isEquiped(){
        Boolean val;
        if (this.equiped!=null){
            val=this.equiped;
        } else {
            val=false;
        }
        return val;
    }

    public void setEquiped(Boolean equiped) {
        this.equiped = equiped;
    }

    public List<String> getTags() {
        return tags;
    }
}

