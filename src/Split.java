public class Split 
{    
    String split_name;
    String split_gold;
    String split_icon;

    public Split()
    {
        this.split_name = "";
        this.split_gold = "";
    }

    public String getSplitName()
    {
        return this.split_name;
    }

    public String getSplitGold()
    {
        return this.split_gold;
    }

    public void setSplitName(String name)
    { 
        split_name = name;
    }

    public void setSplitGold(String gold)
    {
        split_gold = gold;
    }

}
