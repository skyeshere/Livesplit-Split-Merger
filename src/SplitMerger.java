import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SplitMerger 
{
    ArrayList<ArrayList<Split>> splits_queue;   
    Node split_copy; 

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
            * 2. build copy of the empty split node
            * 3. add new segment nodes to split file
            * 4. after each segment, add a gameswitch segment if we are not on the last split in the queue
            */
            
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse("empty.lss");
            System.out.println(doc);

            NodeList run = doc.getElementsByTagName("Run").item(0).getChildNodes();
            Node segments_node = null;

            /*removes initial segment */
            for(int i = 0; i < run.getLength(); i++)
            {
                Node segs = run.item(i);
                if(segs.getNodeName().equals("Segments"))
                {
                    NodeList segment_list = segs.getChildNodes();
                    segments_node = segs;
                    for(int j = 0; j < segment_list.getLength(); j++)
                    {
                        Node seg = segment_list.item(j);
                        if(seg.getNodeName().equals("Segment"))
                        {
                            System.out.println("bah");
                            split_copy = seg.cloneNode(true);
                            System.out.println(split_copy.getChildNodes().getLength());
                            segs.removeChild(seg);
                            break;
                        }
                    }
                    break;
                }
            }

            /* build copy of the empty split node */
            


            /* merge all split files, in order, into the empty split file */
            
            String tmp_name = "";
            String tmp_gold = "";
            for(int i = 0; i < splits_queue.size(); i++) //for every game
            {

                ArrayList<Split> splits = splits_queue.get(i);
                for(int j = 0; j < splits.size(); j++) //for every split in that game
                {
                    tmp_name = splits.get(j).getSplitName();
                    tmp_gold = splits.get(j).getSplitGold(); 

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
                                        grandchild.setTextContent("00:05:00.000");
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

            StreamResult result = new StreamResult("empty.lss");
            tf.transform(source, result);
            
        }
        catch(Exception e)
        {   
            System.err.println("An error has occurred in the merging process, please try again.");
            e.printStackTrace();
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
