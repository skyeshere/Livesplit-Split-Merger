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

            //pretty print the document to a string
            InputStream is = getClass().getResourceAsStream("/empty.lss");
            String  xmlString = new String(is.readAllBytes());

            System.out.println(prettyPrintByTransformer(xmlString, 2, true));

            NodeList run = doc.getElementsByTagName("Run");
            
            for(int q = 0; q < run.getLength(); q++)
            {
                NodeList segments_list = run.item(q).getChildNodes();
                
                for(int p = 0; p < segments_list.getLength(); p++)
                {
                    if(segments_list.item(p).getNodeName().equals("Segments"))
                    {
                        //remove the initial segment node
                        segments_list.item(p).getParentNode().removeChild(segments_list.item(p));
                        System.out.println("found empty segment");
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

    public static String prettyPrintByTransformer(String xmlString, int indent, boolean ignoreDeclaration) {

    try {
        InputSource src = new InputSource(new StringReader(xmlString));
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(src);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setAttribute("indent-number", indent);
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, ignoreDeclaration ? "yes" : "no");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        Writer out = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(out));
        return out.toString();
    } catch (Exception e) {
        throw new RuntimeException("Error occurs when pretty-printing xml:\n" + xmlString, e);
    }
}
}
