import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class SplitMerger 
{
    ArrayList<ArrayList<Split>> splits_queue;    

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
            Document doc = builder.parse("src/empty.lss");

            NodeList run = doc.getElementsByTagName("Run");
            
            for(int q = 0; q < run.getLength(); q++)
            {
                NodeList segments_list = run.item(q).getChildNodes();
                
                for(int p = 0; p < segments_list.getLength(); p++)
                {
                    if(segments_list.item(p).getNodeName().equals("Segments"))
                    {
                        //remove the initial segment node
                        for(int r = 0; r < segments_list.item(p).getChildNodes().getLength(); r++)
                        {
                            Node s = segments_list.item(p).getChildNodes().item(r);
                            if(segments_list.item(p).getChildNodes().item(r).getNodeName().equals("Segment"))
                            {
                                segments_list.item(p).removeChild(s); 
                                break;
                            }
                        }
                        break;
                    }
                }
            }


            for(int i = 0; i < splits_queue.size(); i++)
            {
                for(int j = 0; j < getIndexedSplits(i).size(); j++)
                {
                    //adds segments to a blank livesplit splits file

            
                }

                //if we are on any split in the queue that isnt the last
                if(i != splits_queue.size() - 1)
                {
                    //add a gameswitch segment
                }
            }

            /* 
            ransformerFactory tff = TransformerFactory.newInstance();
            Transformer tf = tff.newTransformer();
            DOMSource source = new DOMSource(doc);

            StreamResult result = new StreamResult("src/empty.lss");
            tf.transform(source, result);
            */
        }
        catch(Exception e)
        {
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
