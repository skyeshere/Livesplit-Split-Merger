import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class SplitPuller 
{
    String file;
    NodeList segments = null;
    Split split;

    public SplitPuller(String file)
    {
        this.file = file;
        this.segments = getSplits();

        if(segments == null)
        {
            System.err.println("Could not find segments in provided file!");
            System.exit(1);
        }

        pullSplitDetails();
    }

    public NodeList getSplits()
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        try 
        {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);

            NodeList run = doc.getElementsByTagName("Run");

            for(int i = 0; i < run.getLength(); i++)
            {
                Node r = run.item(i);
                if(r.getNodeType() == Node.ELEMENT_NODE)
                {
                    NodeList segments_list = r.getChildNodes();

                    for(int j = 0; j < segments_list.getLength(); j++)
                    {
                        Node s = segments_list.item(j);

                        if(s.getNodeType() == Node.ELEMENT_NODE && s.getNodeName().equals("Segments"))
                            return s.getChildNodes();
                    }
                }
            }
        } 
        catch (ParserConfigurationException | SAXException | IOException e) 
        {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<Split> pullSplitDetails()
    {
        ArrayList<Split> splits_array = new ArrayList<>();

        for(int i = 0; i < segments.getLength(); i++)
        {
            Split split = new Split();
            NodeList single_segment = segments.item(i).getChildNodes();

            for(int j = 0; j < single_segment.getLength(); j ++)
            {
                Node s = single_segment.item(j);
                if(s.getNodeType() == Node.ELEMENT_NODE && s.getNodeName().equals("Name"))
                {
                    String str = s.getTextContent();
                    split.setSplitName(str);
                    System.out.println(str);
                }

                else if(s.getNodeType() == Node.ELEMENT_NODE && s.getNodeName().equals("BestSegmentTime"))
                {
                    NodeList golds = s.getChildNodes();

                    for(int k = 0; k < golds.getLength(); k++)
                    {
                        if(golds.item(k).getNodeName().equals("RealTime"))
                        {
                            
                            String str = golds.item(k).getTextContent();
                            split.setSplitGold(str);
                            System.out.println(str);
                            break;
                        }
                    }
                }
            }
            //hodge-podge fix to empty splits being added to the splits array
            if(!(split.getSplitName().equals("") || split.getSplitGold().equals("")))
            {
                splits_array.add(split);
            }
        }

        for(int i = 0; i < splits_array.size(); i++)
        {
            System.out.println(splits_array.get(i).getSplitName() + " : " + splits_array.get(i).getSplitGold());
        }
    }
}
