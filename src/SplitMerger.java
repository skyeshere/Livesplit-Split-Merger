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
            * 2. add new segment nodes to split file
            * 3. after each segment, add a gameswitch segment if we are not on the last split in the queue
            */
            
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse("empty.lss");
            System.out.println(doc);

            NodeList run = doc.getElementsByTagName("Run").item(0).getChildNodes();

            /*removes initial segment */
            for(int i = 0; i < run.getLength(); i++)
            {
                Node segs = run.item(i);
                if(segs.getNodeName().equals("Segments"))
                {
                    NodeList segment_list = segs.getChildNodes();
                    for(int j = 0; j < segment_list.getLength(); j++)
                    {
                        Node seg = segment_list.item(j);
                        if(seg.getNodeName().equals("Segment"))
                        {
                            System.out.println("bah");
                            split_copy = seg.cloneNode(false);
                            System.out.println(split_copy.getChildNodes().getLength());
                            segs.removeChild(seg);
                            break;
                        }
                    }
                    break;
                }
            }

            /* merge all split files, in order, into the empty split file */
            for(int i = 0; i < splits_queue.size(); i++)
            {
                ArrayList<Split> splits = splits_queue.get(i);

                for(int j = 0; j < splits.size(); j++)
                {
                    Split split = splits.get(j);
                    
                    for(int k = 0; k < run.getLength(); k++)
                    {
                        Node segs = run.item(k);
                        if(segs.getNodeName().equals("Segments"))
                        {
                            String tmpname = split.getSplitName();
                            String tmpgold = split.getSplitGold();

                            Node newseg = split_copy.cloneNode(false);

                            /*
                            newseg.getAttributes().getNamedItem("Name").setTextContent(tmpname);
                            newseg.getChildNodes().item(1).getChildNodes().item(1).setTextContent(tmpgold);
                            segs.appendChild(newseg);
                            

                            for(int l = 0; l < split_copy.getChildNodes().getLength(); l++)
                            {
                                Node child = split_copy.getChildNodes().item(l);
                                if(child.getNodeName().equals("Name"))
                                {
                                    child.setTextContent(tmpname);
                                }

                                else if(child.getNodeName().equals("BestSegmentTime"))
                                {
                                    for(int m = 0; m < child.getChildNodes().getLength(); m++)
                                    {
                                        Node grandchild = child.getChildNodes().item(m);
                                        if(grandchild.getNodeName().equals("RealTime"))
                                        {
                                            grandchild.setTextContent(tmpgold);
                                            break;
                                        }
                                    }
                                }
                            }
                                /*
                                 * So turns out the copy of the empty segment node doesnt contain the childnodes, need to add them on their own (lame)
                                 * literally like this
                                 * <Segment>
                                 * 
                                 * </Segment>
                                 * AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
                                 * 't'is will be okay ! 
                                 */
                            
                            segs.appendChild(newseg);
                                                        
                            System.out.println("Added split: " + tmpname + " with gold time: " + tmpgold);
                            //merged split?
                        }
                    }
                    
                }

                //if we are on any split in the queue that isnt the last
                if(i != splits_queue.size() - 1)
                {
                    //add a gameswitch segment
                    System.out.println("game switch!"); 
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
