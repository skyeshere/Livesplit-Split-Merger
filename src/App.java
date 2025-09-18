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
        System.out.println("Welcome to the split merger! To get started, enter how many split files you will be merging!");
        System.out.println("Please note: This isn't the be-all-end-all solution. You will need to format your splits afterwards: ");

        int number_of_splits = Integer.parseInt(input.nextLine());

        for(int i = 0; i < number_of_splits; i++)
        {
            System.out.println("Enter the name of split file (" + (i + 1) + "), including the .lss file extention [file1.lss]");
            args.add(input.nextLine());
        }
        
        return args;
    }
}
