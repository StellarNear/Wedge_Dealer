package stellarnear.wedge_companion.Spells;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by jchatron on 28/11/2017.
 */

public class MetaList {
    private List<Metamagic> listMeta = new ArrayList<>();

    public MetaList(){
        listMeta = new ArrayList<>();
    }

    public MetaList(MetaList metaList){ //pour clonner la metalist
        this.listMeta = new ArrayList<>();
        for (Metamagic meta : metaList.listMeta){
            this.listMeta.add(new Metamagic(meta));
        }
    }

    public void add(Metamagic metamagic){
        listMeta.add(metamagic);
    }

    public Metamagic getMetaByID(String id){
        Metamagic metaAnswer = null;
        for (Metamagic meta : listMeta){
            if (meta.getId().equalsIgnoreCase(id)){
                metaAnswer=meta;
            }
        }
        return metaAnswer;
    }

    public List<Metamagic> asList(){
        return listMeta;
    }

    public MetaList filterMaxRank(int maxRank) {
        MetaList metaList = new MetaList();
        for (Metamagic meta : this.listMeta){
            if(meta.getUprank()<=maxRank){
                metaList.add(meta);
            }
        }
        return metaList;
    }

    public MetaList filterActive() {
        MetaList metaList = new MetaList();
        for (Metamagic meta : this.listMeta){
            if(meta.isActive()){
                metaList.add(meta);
            }
        }
        return metaList;
    }

    public boolean metaIdIsActive(String metaId){
        boolean val=false;
        for (Metamagic meta : this.listMeta){
            if(meta.getId().equalsIgnoreCase(metaId) && meta.isActive()){
                val=true;
            }
        }
        return val;
    }

    public void remove(Metamagic meta) {
        this.listMeta.remove(meta);
    }

    public boolean hasAnyMetaActive() {
        boolean val=false;
        for (Metamagic meta : this.listMeta){
            if( meta.isActive()){
                val=true;
                break;
            }
        }
        return val;
    }

    public MetaList getAllActivesMetas() {
        MetaList listMetaActives = new MetaList();
        for (Metamagic meta : this.listMeta){
            if( meta.isActive()){
                listMetaActives.add(meta);
            }
        }
        return listMetaActives;
    }
}
