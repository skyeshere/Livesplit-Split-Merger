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
        if((int)(Math.random()*10) % 2 == 0) //Shoutout to MarvJungs for thinking of this idea!!!!! PLEASE DONATE IM BEGGIUNG
        {
            System.out.println("Consider donating! You can do that on https://streamelements.com/skyeves/tip");
        }

        System.out.println("Welcome to the spit merger! Please note: You may have to format your splits after merging depending on your needs. \n");
        System.out.println("Please enter the names of the split files you want to merge (including the `.lss` file extension). Press 'Enter' with no input to continue.");

        String result = "";
        int counter = 1;
        do
        {
            System.out.print("item " + counter + ": ");
            result = input.nextLine();

            if(result.length() >= 5) //if the resulting string is at a length to be a possible split file
            {
                if(result.substring(result.length() - 4).toLowerCase().equals(".lss")) //check for file extension
                {
                    args.add(result);
                }
            }
            else //this is not a great solution to be honest
            {
                break;
            }

            counter++;
        }
        while(result.substring(result.length() - 4).toLowerCase().equals(".lss"));

        if(args.size() <= 1)
        {
            System.out.println("Please enter at least 2 split files to merge.");
            System.out.println("You entered: " + args.size() + " splits");
            System.exit(1);
        }

        return args;
    }
}
