package stellarnear.wedge_companion.Elems;


public class Elem {

    private int colorId;
    private int colorIdRecent;
    private String name;
    private String key;
    private int drawableId;

    public Elem(String key, String name, int colorId,int colorIdRecent,int drawableId){
        this.key=key;
        this.name=name;
        this.colorId=colorId;
        this.colorIdRecent=colorIdRecent;
        this.drawableId=drawableId;
    }

    public Elem(String key, String name, int colorId,int colorIdRecent){
        this.key=key;
        this.name=name;
        this.colorId=colorId;
        this.colorIdRecent=colorIdRecent;
    }

    public int getColorId() {
        return colorId;
    }

    public int getColorIdDark() {
        return colorIdRecent;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public int getDrawableId() {
        return this.drawableId;
    }
}

