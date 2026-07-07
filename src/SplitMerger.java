import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.regex.*;
import java.util.Scanner;

public class SplitMerger 
{
    ArrayList<SplitsContainer> splits_queue;   
    Node split_copy; 
	Node found_node = null;
    String target = "";

    Scanner input = new Scanner(System.in);

    public SplitMerger()
    {
        this.splits_queue = new ArrayList<>();
    }

    public Document mergeSplits()
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try
        {  
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse("empty.lss");

            Node root_node = doc.getElementsByTagName("Run").item(0);
            Node segments_node = null; // storing this reference to where the segments start saves going back through the split file to find it again
            target = "Segments";

            //find the segments tag
			findNode(root_node, target);
            checkNode(found_node, target);

            Node lone_segment = found_node.getChildNodes().item(1); //item 1 will always be the Segment node from Segments
            checkNode(lone_segment, "Segment"); //check that lone segment is correct. Again, this should never occur     

            //copy the empty segment
            split_copy = lone_segment.cloneNode(true);
            //remove empty segment from the split file
            found_node.removeChild(lone_segment);

            //set back to null for future use
            segments_node = found_node;
            found_node = null;

            /* merge all split files, in order, into the empty split file */
            String tmp_name = "";
            String tmp_gold = "";

            for (int i = 0; i < splits_queue.size(); i++) //need index from this loop
            {
                SplitsContainer sc = splits_queue.get(i);
                ArrayList<Split> splits = sc.getContainer();
                for (Split split : splits)
                {
                    tmp_name = split.getSplitName();
                    tmp_gold = split.getSplitGold();
                    
                    System.out.println("Merging: " + tmp_name);

                    /* search for both name and gold nodes from split_copy */
                    //set name
                    target = "Name";
                    findNode(split_copy, target); //sets found_node to target when found
                    checkNode(found_node, target);                  
                    found_node.setTextContent(tmp_name);

                    found_node = null;
                    //set gold
                    
                    //find the BestSegmentTime
                    target = "BestSegmentTime";
                    findNode(split_copy, target);
                    checkNode(split_copy, target);
                    found_node = found_node.getChildNodes().item(1); //item one is real time where the time should be inputted
                    
                    found_node.setTextContent(tmp_gold);
                    
                    found_node = null;

                    //set the new segment node into the xml tree
                    segments_node.appendChild(split_copy.cloneNode(true)); //add the new split to the segments node    
                }
                if (i != splits_queue.size() -1) //if not the last game in the queue, add game switch segment between games
                {
                    target = "Name";
                    findNode(split_copy, target);
                    checkNode(split_copy, target);
                    found_node.setTextContent("Game Switch");

                    found_node = null;

                    target = "BestSegmentTime";
                    findNode(split_copy, target);
                    checkNode(split_copy, target);
                    found_node = found_node.getChildNodes().item(1); //item 1 is real time where the time should be inputted
                    found_node.setTextContent("00:01:00.000"); //1 minute seems resonable imo

                    found_node = null;

                    segments_node.appendChild(split_copy.cloneNode(true)); //add the new split to the segments node  
                }
            }

            //extra info from user
            // String file_name = "";
            // System.out.println("\nMerge successful! \nWhat would you like to name the file? (default= merged)");
            // file_name = input.nextLine();

            // if (file_name == "") //default case
            // {
            //     file_name = "merged";
            // }
            // else if(file_name.length() >= 4 && file_name.substring(file_name.length() - 4).toLowerCase().equals(".lss")) //if the file name is long enough and has the .lss extension
            // {
            //     file_name = file_name.substring(0, file_name.length() - 4); //remove the .lss if the user has inputted it
            // }

            /* saves new splits file */
            //TransformerFactory tff = TransformerFactory.newInstance();
            // Transformer tf = tff.newTransformer();
            // DOMSource source = new DOMSource(doc);

            // StreamResult result = new StreamResult(file_name + ".lss");
            // tf.transform(source, result);
            // System.out.println("Merging complete, file saved as " + file_name + ".lss");

            System.out.println("\nMerge successful!");
            return doc;
        }
        catch(Exception e)
        {   
            System.err.println("An error has occurred in the merging process, please try again.");
            e.printStackTrace();
            return null;
        }
    }

    public void queueAdd(SplitsContainer s)
    {
        this.splits_queue.add(s);
    }

    public void findNode(Node n, String target)
    {
        String curr_node = n.getNodeName();
        if (curr_node.equals("#text")) return; //skips redundant fake ass nodes
        
        if (curr_node.equals(target)) //check if new root's name is equal to the target node
        {
			found_node = n;
			return;
        }

		NodeList children = n.getChildNodes(); //pull all child nodes of current root
		
		if (children.getLength() < 1) return; //return if no children

		for (int i = 0; i < children.getLength(); i++) //loop through and explore child nodes
		{
			if (found_node != null) return;
			findNode(children.item(i), target);
		}
    }

    //n is the node being checked, t is the target string
    public void checkNode(Node n, String t)
    {
        if (found_node == null || !found_node.getNodeName().equals(target))
        {
            System.err.println("targetted node not found :/ make sure your split files aren't missing certain tags");
            System.exit(1);
        }       
    }
}