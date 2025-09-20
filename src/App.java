import java.util.ArrayList;
import java.util.Scanner;

public class App 
{
    ArrayList<String> args = new ArrayList<>();
    Scanner input = new Scanner(System.in);

    public App()
    {
        args = userInput();

        SplitMerger sm = new SplitMerger();

        for(String file : args)
        {
            SplitPuller sp = new SplitPuller(file); 
            sm.queueAdd(sp.pullSplitDetails());
        }

        sm.mergeSplits();
    }
    public static void main(String args[])
    {
        new App();
    }

    public ArrayList<String> userInput()
    {
        System.out.println("Welcome to the spit merger! Please note: You may have to format your splits after merging depending on your needs.");
        System.out.println("Please enter the names of the split files you want to merge (including the `.lss` file extension). Enter a non-lss file string to continue to the merge");

        String result = "";
        
        do
        {
            result = input.nextLine();

            if(result.toLowerCase().contains(".lss"))
            {
                args.add(result);
            }
        }
        while(result.toLowerCase().contains(".lss"));

        return args;
    }
}
