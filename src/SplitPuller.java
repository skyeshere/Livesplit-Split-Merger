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
        this.segments = getSplits(file);
    }

    public NodeList getSplits(String file)
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try 
        {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file); //parse split file

            NodeList run = doc.getElementsByTagName("Run").item(0).getChildNodes();
            for(int i = 0; i < run.getLength(); i++) //find segments node
            {
                if(run.item(i).getNodeName().equals("Segments"))
                {
                    return run.item(i).getChildNodes(); //return segments node list
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

                //pull split name
                if(s.getNodeType() == Node.ELEMENT_NODE && s.getNodeName().equals("Name"))
                {
                    String str = s.getTextContent();
                    split.setSplitName(str);
                }

                //pull split gold
                else if(s.getNodeType() == Node.ELEMENT_NODE && s.getNodeName().equals("BestSegmentTime"))
                {
                    NodeList golds = s.getChildNodes();

                    for(int k = 0; k < golds.getLength(); k++)
                    {
                        if(golds.item(k).getNodeName().equals("RealTime"))
                        {
                            String str = golds.item(k).getTextContent();
                            split.setSplitGold(str);
                        }
                    }
                }
            }
            
            //hodge-podge fix to empty splits being added to the splits array, but it works! 
            if(!(split.getSplitName().equals("") || split.getSplitGold().equals("")))
            {
                splits_array.add(split);
            }
        }

        return cleanSplitNames(splits_array);
    }

    public ArrayList<Split> cleanSplitNames(ArrayList<Split> splits)
    {
        for(int i = 0; i < splits.size(); i++)
        {
            StringBuffer name = new StringBuffer(splits.get(i).getSplitName());
            if(name.charAt(0) != '-' && name.charAt(0) != '{')
            {
                if(i < splits.size() - 1)
                {
                    splits.get(i).setSplitName("-" + name.toString());
                }
                else
                {
                    splits.get(i).setSplitName("{Subsplit}" + name.toString());
                }
            }
            else if(name.charAt(0) == '{' && i < splits.size() - 1)
            {
                int end = name.indexOf("}");
                String cleaned = "-" + name.substring(end + 1).trim(); //remove the {subsplit} from the start of the split name
                splits.get(i).setSplitName(cleaned);
            }
            else if(name.charAt(0) == '-' && i == splits.size() - 1)
            {
                String cleaned = "{Subsplit}" + name.substring(1).trim(); //add the {subsplit} to the start of the split name
                splits.get(i).setSplitName(cleaned);
            }
        }
        return splits;
    }
}
