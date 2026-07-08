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
    String target_name = "";
    String target_attribute = "";
    Boolean import_pb = false;

    TreeTraversal tt = new TreeTraversal();
    Scanner input = new Scanner(System.in);

    public SplitMerger(Boolean ip)
    {
        this.splits_queue = new ArrayList<>();
        this.import_pb = ip;
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
            target_name = "Segments";

            //find the segments tag
			tt.findNode(root_node, target_name, "");
            found_node = tt.getFoundNode();
            tt.setFoundNodeNull();

            Node lone_segment = found_node.getChildNodes().item(1); //item 1 will always be the Segment node from Segments

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
            String tmp_pb   = "";

            for (int i = 0; i < splits_queue.size(); i++) //need index from this loop
            {
                SplitsContainer sc = splits_queue.get(i);
                ArrayList<Split> splits = sc.getContainer();
                for (Split split : splits)
                {
                    tmp_name = split.getSplitName();
                    tmp_gold = split.getSplitGold();
                    tmp_pb   = split.getSplitPB();
                    
                    System.out.println("Merging: " + tmp_name);

                    /* search for both name and gold nodes from split_copy */
                    //set name
                    target_name = "Name";
                    tt.findNode(split_copy, target_name, ""); //sets found_node to target when found             
                    found_node = tt.getFoundNode();
                    tt.setFoundNodeNull();

                    found_node.setTextContent(tmp_name);
                    found_node = null;
                    
                    //set gold
                    target_name = "BestSegmentTime";
                    tt.findNode(split_copy, target_name, "");
                    found_node = tt.getFoundNode().getChildNodes().item(1); //item one is real time where the time should be inputted
                    tt.setFoundNodeNull();

                    found_node.setTextContent(tmp_gold);
                    found_node = null;

                    if (import_pb)
                    {
                        target_name = "SplitTime";
                        target_attribute = "Sum of PBs";

                        tt.findNode(split_copy, target_name, target_attribute);
                        found_node = tt.getFoundNode().getChildNodes().item(1);
                        tt.setFoundNodeNull();

                        found_node.setTextContent(tmp_pb);
                        found_node = null;
                    }

                    //set the new segment node into the xml tree
                    segments_node.appendChild(split_copy.cloneNode(true)); //add the new split to the segments node    
                }
                
                if (i != splits_queue.size() -1) //if not the last game in the queue, add game switch segment between games
                {
                    target_name = "Name";
                    tt.findNode(split_copy, target_name, "");
                    found_node = tt.getFoundNode();
                    tt.setFoundNodeNull();

                    found_node.setTextContent("Game Switch");
                    found_node = null;

                    target_name = "BestSegmentTime";
                    tt.findNode(split_copy, target_name, "");
                    found_node = tt.getFoundNode().getChildNodes().item(1); //item 1 is real time where the time should be inputted
                    tt.setFoundNodeNull();

                    found_node.setTextContent("00:01:00.000"); //1 minute seems resonable imo
                    found_node = null;

                    if (import_pb)
                    {
                        target_name = "SplitTime";
                        target_attribute = "Sum of PBs";

                        tt.findNode(split_copy, target_name, target_attribute);
                        found_node = tt.getFoundNode().getChildNodes().item(1);
                        tt.setFoundNodeNull();

                        //found_node.setTextContent("00:01:00.000");
                        found_node = null;
                    }

                    segments_node.appendChild(split_copy.cloneNode(true)); //add the new split to the segments node  
                }
            }

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
}