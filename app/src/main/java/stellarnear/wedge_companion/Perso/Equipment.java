package stellarnear.wedge_companion.Perso;
import android.content.Context;
import android.graphics.drawable.Drawable;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stellarnear.wedge_companion.Tools;


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
    private Boolean equiped=false;
    private Boolean limitedMaxDex=false;
    private int maxDexMod;
    private int armor=0;
    private Map<String,Integer> mapAbilityUp=null;
    private Map<String,Integer> mapSkillUp=null;

    public Equipment(String name, String descr, String value, String imgIdTxt, String slotId, Boolean equiped,int armor) {
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
        this.armor=armor;
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

    public void setMaxDexMod(Integer toInt) {
        maxDexMod=toInt;
        limitedMaxDex=true;
    }

    public boolean isLimitedMaxDex() {
        return limitedMaxDex;
    }

    public int getMaxDexMod() {
        return maxDexMod;
    }

    public int getArmor() {
        return armor;
    }

    public void setAbilityUp(Element element) {
        try {//try car il peut ne pas y avoir de abilityUp
            NodeList abilityUp = element.getElementsByTagName("abilityUp").item(0).getChildNodes();
            Tools tools =Tools.getTools();
            for (int i = 0; i < abilityUp.getLength(); i++) {
                Node node = abilityUp.item(i);
                if(node.getNodeType() == Node.ELEMENT_NODE) {
                    String name = node.getNodeName();
                    int val = tools.toInt(node.getChildNodes().item(0).getNodeValue());
                    if(name !=null && !name.equalsIgnoreCase("")&& val!=0) {
                        if (mapAbilityUp == null) {
                            mapAbilityUp = new HashMap<>();
                        }
                        mapAbilityUp.put(name, val);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setSkillUp(Element element) {
        try { //try car il peut ne pas y avoir de skillUp
            NodeList skillUp = element.getElementsByTagName("skillUp").item(0).getChildNodes();
            Tools tools =Tools.getTools();
            for (int i = 0; i < skillUp.getLength(); i++) {
                Node node = skillUp.item(i);
                if(node.getNodeType() == Node.ELEMENT_NODE) {
                    String name = node.getNodeName();
                    int val = tools.toInt(node.getChildNodes().item(0).getNodeValue());
                    if(name !=null && !name.equalsIgnoreCase("")&& val!=0) {
                        if (mapSkillUp == null) {
                            mapSkillUp = new HashMap<>();
                        }
                        mapSkillUp.put(name, val);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, Integer> getMapAbilityUp() {
        return mapAbilityUp;
    }

    public Map<String, Integer> getMapSkillUp() {
        return mapSkillUp;
    }

    public void addMapAbilityUp(Map<String, Integer> abiMap) {
        if (mapAbilityUp == null) {
            mapAbilityUp = new HashMap<>();
        }
        for(Map.Entry<String,Integer> entry : abiMap.entrySet()) {
            mapAbilityUp.put(entry.getKey(),entry.getValue());
        }
    }

    public void addMapSkillUp(Map<String, Integer> skillMap) {
        if (mapSkillUp == null) {
            mapSkillUp = new HashMap<>();
        }
        for(Map.Entry<String,Integer> entry : skillMap.entrySet()) {
            mapSkillUp.put(entry.getKey(),entry.getValue());
        }
    }
}

