public class App 
{
    public static void main(String[] args)
    {
        /*if(args.length < 2)
        {
            System.err.println("Please provide two or more split files to merge");
            System.exit(1);
        }
            */

        for(String file : args)
        {
            SplitPuller sp = new SplitPuller(file); 
        }
    }
}
