public class App 
{
    public App(String [] args)
    {
        SplitMerger sm = new SplitMerger();

        for(String file : args)
        {
            SplitPuller sp = new SplitPuller(file); 
            sm.queueAdd(sp.pullSplitDetails());
        }

        sm.printQueue();
        sm.mergeSplits();
    }
    public static void main(String[] args)
    {
        if(args.length < 2)
        {
            System.err.println("Usage: [two or more .lss files]" + "\n" +
                               "e.g. 'game1.lss game2.lss'");
            System.exit(1);
        }
        
        new App(args);
    }
}
