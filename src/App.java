import java.util.ArrayList;
import java.util.Scanner;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;

public class App 
{
    ArrayList<String> args = new ArrayList<>();
    Scanner input = new Scanner(System.in);
    Document merged_splits;
    Boolean import_pb = false;

    public App()
    {
        args = userInput();

        SplitMerger sm = new SplitMerger(import_pb);

        for (String file : args)
        {
            SplitPuller sp = new SplitPuller(file, import_pb); 
            sm.queueAdd(sp.pullSplitDetails());
        }

        merged_splits = sm.mergeSplits();

        MetadataInput mi = new MetadataInput(merged_splits);
        mi.inputMetadata();
    }
    
    public static void main(String args[])
    {
        new App();
    }

    public ArrayList<String> userInput()
    {
        if ((int)(Math.random()*10) % 2 == 0) //Shoutout to MarvJungs for thinking of this idea!!!!! PLEASE DONATE IM BEGGIUNG
        {
            System.out.println("Consider donating! You can do that on https://streamelements.com/skyeves/tip");
        }

        System.out.println("Welcome to the spit merger! Please note: You may have to format your splits after merging depending on your needs. \n");
        System.out.println("Please enter the names of the split files you want to merge (including the `.lss` file extension). Press 'Enter' with no input to continue.");
        
        int number_of_splits = 0;
        String result = "";

        while (number_of_splits <= 1) //will automatically go into while loop since n_o_s is initalised as one
        {
            System.out.print("Enter the <<NUMBER>> of splits you will be merging (number must be > 1): ");
            result = input.nextLine();

            try //try parse input as an integer
            {
                number_of_splits = Integer.parseInt(result);
            }
            catch (Exception e) //catch the error and allow user to try again.
            {
                System.err.println(e + ": Please enter a number, silly.");
            }
        }

        result = ""; //reset result for next input

        System.out.println("\nEnter the <<FILE NAMES>> for your splits! Keeping them simple is the best");
        System.out.println("e.g: randomsplitname.lss\n");

        for (int i = 0; i < number_of_splits; i++)
        {
            System.out.print("File " + (i + 1) + ": "); //what file number the user is on (1 -> numb splits)
            result = input.nextLine();

            while (result.length() <= 4 || !result.substring(result.length() - 4).toLowerCase().equals(".lss")) //entry validation
            {
                System.out.println("Please enter a valid split file name (ending in .lss): ");
                result = input.nextLine();
            }

            args.add(result);
            result = "";
        }

        System.out.print("Do you want to import your pbs under a custom comparison? (y/n): ");
        result = input.nextLine();

        while (!result.toLowerCase().equals("y") && !result.toLowerCase().equals("n")) //validate yes or no
        {
            System.out.println("Please enter 'y' or 'n' to confirm your option\n");
            System.out.print("Do you want to import your pbs under a custom comparison? (y/n): ");
            result = input.nextLine();
        }

        import_pb = result.matches("y"); //turn true if matches 'y'. i am lowk being an asshole by making them type n instead of literally anything else that isnt y lmfao :p

        System.out.print("\n");
        return args;
    }
}