import java.util.ArrayList;

public class SplitsContainer {
    
    ArrayList<Split> container; //Arraylist containing all splits for a game
    String game; //String containing what game, from the split file

    public SplitsContainer()
    {
        container = new ArrayList<>();
        game = "";
    }

    public void addSplit(Split s)
    {
        container.add(s);
    }

    public void setGame(String g)
    {
        game = g; 
    }

    public ArrayList<Split> getContainer()
    {
        return container;
    }

    public String getGame()
    {
        return game;
    }
}

