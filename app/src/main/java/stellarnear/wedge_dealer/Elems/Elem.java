package stellarnear.wedge_dealer.Elems;


public class Elem {

    private int colorId;
    private String name;
    private String key;

    public Elem(String key, String name, int colorId){
        this.key=key;
        this.name=name;
        this.colorId=colorId;
    }

    public int getColorId() {
        return colorId;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }
}

