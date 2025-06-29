import java.util.ArrayList;

public class SplitMerger 
{
    ArrayList<ArrayList<Split>> splits_queue;    

    public SplitMerger()
    {
        this.splits_queue = new ArrayList<>();
    }

    public void mergeSplits()
    {
        for(int i = 0; i < splits_queue.size(); i++)
        {
            for(int j = 0; j < getIndexedSplits(i).size(); j++)
            {
                //adds segments to a blank livesplit splits file
            }

            //if we are on any split in the queue that isnt the last
            if(i != splits_queue.size() - 1)
            {
                //add a gameswitch segment
            }
        }
    }

    public void queueAdd(ArrayList<Split> s)
    {
        this.splits_queue.add(s);
    }

    public void printQueue()
    {
        if(splits_queue != null)
        {
            for(ArrayList<Split> s_s : splits_queue)
            {
                for(Split s : s_s)
                {
                    System.out.println(s.getSplitName() + " : " + s.getSplitGold());
                }
            }
        }
    }

    public ArrayList<Split> getIndexedSplits(int i)
    {
        return this.splits_queue.get(i);
    }
}
