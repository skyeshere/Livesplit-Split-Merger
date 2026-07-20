import org.w3c.dom.Node;

public class Split 
{    
    String split_name;
    String split_gold;
    String split_pb;
    String split_icon;

    public Split()
    {
        this.split_name = "";
        this.split_gold = "";
        this.split_pb   = "";
        this.split_icon = "";
    }

    //GETTERS
    public String getSplitName()
    {
        return this.split_name;
    }

    public String getSplitGold()
    {
        return this.split_gold;
    }

    public String getSplitPB()
    {
        return this.split_pb;
    }

    public String getSplitIcon()
    {
        return this.split_icon;
    }

    //SETTERS -- i know `this.` isnt needed (i think, )
    public void setSplitName(String name)
    { 
        this.split_name = name;
    }

    public void setSplitGold(String gold)
    {
        this.split_gold = gold;
    }

    public void setSplitPB(String pb)
    {
        this.split_pb = pb;
    }

    public void setSplitIcon(String icon)
    {
        this.split_icon = icon;
    }
}
