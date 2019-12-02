package stellarnear.wedge_companion.Perso;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jchatron on 04/01/2018.
 */

public class Form {
    private String name;
    private String type;
    private String size;
    private String descr;
    private String id;
    private List<FormCapacity> listPassiveCapacities= new ArrayList<>();
    private List<FormCapacity> listActivesCapacities= new ArrayList<>();

    private Context mC;

    public Form(String name, String type,String size, String descr, String id, Context mC){
        this.name=name;
        this.type=type;
        this.size=size;
        this.descr=descr;
        this.id=id;
        this.mC=mC;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getDescr() {
        return descr;
    }

    public String getId() {
        return id;
    }

    public String getSize() {
        return size;
    }

    public void setListActivesCapacities(List<FormCapacity> listActivesCapacities) {
        this.listActivesCapacities = listActivesCapacities;
    }

    public void setListPassiveCapacities(List<FormCapacity> listPassiveCapacities) {
        this.listPassiveCapacities = listPassiveCapacities;
    }

    public List<FormCapacity> getListActivesCapacities() {
        return listActivesCapacities;
    }

    public List<FormCapacity> getListPassiveCapacities() {
        return listPassiveCapacities;
    }
}

