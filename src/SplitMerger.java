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

public class SplitMerger 
{
    ArrayList<SplitsContainer> splits_queue;   
    Node split_copy; 
	Node found_node = null;
    String target = "";

    public SplitMerger()
    {
        this.splits_queue = new ArrayList<>();
    }

    public void mergeSplits()
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try
        {
            /*
            * 1. remove initial segment from blank split file
            * 2. add new segment nodes to split file
            * 3. after each segment, add a gameswitch segment if we are not on the last split in the queue
            */
            
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse("empty.lss");

            Node root_node = doc.getElementsByTagName("Run").item(0);
            Node segments_node = null; // storing this reference to where the segments start saves going back through the split file to find it again
            target = "Segments";

            //find the segments tag
			findNode(root_node, target);
            segments_node = found_node;
            if (found_node == null) // this shouldnt occur however it's here just to be safe...
            {
                System.err.println("The target attribute in the lss file was not found! exitting...");
                System.exit(1);
            }

			/*remove initial segment from empty lss file */
            /**
             * This will use proprietery bit of code to specifically deal with a determined outcome from line 41 and may not be the best
             */

            Node lone_segment = found_node.getChildNodes().item(1);
            if (!lone_segment.getNodeName().equals("Segment"))        
            {
                System.err.println("An error occurred...");
                System.exit(1);
            }
            //copy the empty segment
            split_copy = lone_segment.cloneNode(true);
            //remove empty segment from the split file
            found_node.removeChild(lone_segment);

            //set back to null for future use
            found_node = null;

            /* merge all split files, in order, into the empty split file */
            String tmp_name = "";
            String tmp_gold = "";
            for(int i = 0; i < splits_queue.size(); i++) //for every game
            {
                ArrayList<Split> splits = splits_queue.get(i).getContainer();
                for(int j = 0; j < splits.size(); j++) //for every split in that game
                {
                    tmp_name = splits.get(j).getSplitName();
                    tmp_gold = splits.get(j).getSplitGold(); 

                    System.out.println("Merging " + tmp_name);

                    //combing the split_copy node for the right places to put the name and gold time
                    for(int l = 0; l < split_copy.getChildNodes().getLength(); l++)
                    {
                        Node child = split_copy.getChildNodes().item(l);
                        if(child.getNodeName().equals("Name"))
                        {
                            child.setTextContent(tmp_name);
                        }

                        else if(child.getNodeName().equals("BestSegmentTime"))
                        {
                            for(int m = 0; m < child.getChildNodes().getLength(); m++)
                            {
                                Node grandchild = child.getChildNodes().item(m);
                                if(grandchild.getNodeName().equals("RealTime"))
                                {
                                    grandchild.setTextContent(tmp_gold);
                                }
                            }
                        }
                    }         
                    segments_node.appendChild(split_copy.cloneNode(true)); //add the new split to the segments node    
                    
                    if(j == splits.size() - 1 && i != splits_queue.size() - 1) //if we are at the last split of a game, but not the last game
                    {
                        //add a gameswitch segment
                        for(int n = 0; n < split_copy.getChildNodes().getLength(); n++)
                        {
                            Node child = split_copy.getChildNodes().item(n);
                            if(child.getNodeName().equals("Name"))
                            {
                                child.setTextContent("Game Switch");
                            }
                            else if(child.getNodeName().equals("BestSegmentTime"))
                            {
                                for(int m = 0; m < child.getChildNodes().getLength(); m++)
                                {
                                    Node grandchild = child.getChildNodes().item(m);
                                    if(grandchild.getNodeName().equals("RealTime"))
                                    {
                                        grandchild.setTextContent("00:01:00.000"); //1 minute seems resonable imo
                                    }
                                }
                            }
                        }         
                        segments_node.appendChild(split_copy.cloneNode(true)); //add the new split to the segments node    
                    }
                }
            }
            
            /* saves new splits file */
            TransformerFactory tff = TransformerFactory.newInstance();
            Transformer tf = tff.newTransformer();
            DOMSource source = new DOMSource(doc);

            StreamResult result = new StreamResult("merged.lss");
            tf.transform(source, result);
            System.out.println("Merging complete, file saved as merged.lss");
            
        }
        catch(Exception e)
        {   
            System.err.println("An error has occurred in the merging process, please try again.");
            e.printStackTrace();
        }
    }

    public void queueAdd(SplitsContainer s)
    {
        this.splits_queue.add(s);
    }

    //debugging print method
    public void printQueue()
    {
        if(splits_queue != null)
        {
            for(SplitsContainer sc : splits_queue)
            {
                for(Split s : sc.getContainer())
                {
                    System.out.println(s.getSplitName() + " : " + s.getSplitGold());
                }
            }
        }
    }

    public ArrayList<Split> getIndexedSplits(int i)
    {
        return this.splits_queue.get(i).getContainer();
    }

    public void findNode(Node n, String target)
    {

        if (n.getNodeName().equals("#text")) return; //skips redundant fake ass nodes
        
        System.out.println(n.getNodeName() + " : target = " + target );
        if (n.getNodeName().equals(target)) //check if new root's name is equal to the target node
        {
            //do blah blah blah
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