import org.w3c.dom.Node;

public class Split 
{    
    String split_name;
    String split_gold;
    String split_pb;
    Node split_icon;

    public Split()
    {
        this.split_name = "";
        this.split_gold = "";
        this.split_pb   = "";
        this.split_icon = null;
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

    public Node getSplitIcon()
    {
        return this.split_icon;
    }

    //SETTERS
    public void setSplitName(String name)
    { 
        split_name = name;
    }

    public void setSplitGold(String gold)
    {
        split_gold = gold;
    }

    public void setSplitPB(String pb)
    {
        split_pb = pb;
    }

    public void setSplitIcon(Node icon)
    {
        split_icon = icon;
    }
}
