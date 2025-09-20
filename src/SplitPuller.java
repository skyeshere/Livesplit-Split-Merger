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
    String game;
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
                //gets game name from spits
                if(run.item(i).getNodeName().equals("GameName"))
                {
                    game = run.item(i).getTextContent();
                }

                else if(run.item(i).getNodeName().equals("Segments"))
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

    public SplitsContainer pullSplitDetails()
    {
        //ArrayList<Split> splits_array = new ArrayList<>();
        SplitsContainer splits_container = new SplitsContainer();
        splits_container.setGame(game); //this is lowk an extremely arbitery place to set this but it works :p 

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
            
            //should filter out completely empty records being read from the split file and
            //keep records with a name and empty gold  
            if(!split.getSplitName().equals(""))
            {
                splits_container.addSplit(split);
            }
        }

        return cleanSplitNames(splits_container);
    }

    public SplitsContainer cleanSplitNames(SplitsContainer splits)
    {
        System.out.println("Cleaning subsplits...");
        for(int i = 0; i < splits.getContainer().size(); i++)
        {
            StringBuffer name = new StringBuffer(splits.getContainer().get(i).getSplitName());
            if(name.charAt(0) != '-' && name.charAt(0) != '{')
            {
                if(i < splits.getContainer().size() - 1)
                {
                    splits.getContainer().get(i).setSplitName("-" + name.toString());
                }
                else
                {
                    if(splits.getGame().equals(""))
                    {   //if no game name attached to splits, use this default
                        splits.getContainer().get(i).setSplitName("{Subsplit}" + name.toString());
                    } 
                    else
                    {   //if does have name, use it
                        splits.getContainer().get(i).setSplitName("{" + splits.getGame() + "}" + name.toString());
                    }
                }
            }
            else if(name.charAt(0) == '{' && i < splits.getContainer().size() - 1)
            {
                int end = name.indexOf("}");
                //remove the {subsplit} from the start of the split name
                splits.getContainer().get(i).setSplitName("-" + name.substring(end + 1).trim());
            }
            else if(name.charAt(0) == '-' && i == splits.getContainer().size() - 1)
            {
                //String cleaned = "{Subsplit}" + name.substring(1).trim(); //add the {subsplit} to the start of the split name
                if(splits.getGame().equals(""))
                {
                    splits.getContainer().get(i).setSplitName("{Subsplit}" + name.substring(1).trim());
                }
                else
                {
                    splits.getContainer().get(i).setSplitName("{" + splits.getGame() + "}" + name.substring(1).trim());
                }
            }

            else if(name.charAt(0) == '{' && i == splits.getContainer().size() - 1)
            {
                int end = name.indexOf("}");
                String subsplit = name.substring(1, end);
                if(!subsplit.equals(splits.getGame()))
                {
                    splits.getContainer().get(i).setSplitName("{" + splits.getGame() + "}" + name.substring(end + 1));
                }
            }
        }
        return splits;
    }
}
