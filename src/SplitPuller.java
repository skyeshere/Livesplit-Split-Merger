import java.io.IOException;
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
    String target_name = "";
    String target_attribute = "";
    NodeList segments = null;
    Split split;
    Node found_node = null;
    TreeTraversal tt = new TreeTraversal();
    Boolean import_pb = false;

    public SplitPuller(String file, Boolean ip)
    {
        this.file = file;
        this.segments = getSplits(file);
        this.import_pb = ip;
    }

    public NodeList getSplits(String file)
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try 
        {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file); //parse split file

            NodeList run = doc.getElementsByTagName("Run").item(0).getChildNodes();

            for (int i = 0; i < run.getLength(); i++) //find segments node
            {
                //gets game name from spits
                if (run.item(i).getNodeName().equals("GameName"))
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
        SplitsContainer splits_container = new SplitsContainer();
        splits_container.setGame(game); //this is lowk an extremely arbitery place to set this but it works :p 

        for (int i = 0; i < segments.getLength(); i++) //first for loop here can be changed into for-each
        {
            Split split = new Split();
            Node single_segment = segments.item(i); //root node to be searched
            if (single_segment.getNodeName().equals("#text")) continue; //this line fixes  the issue of it crashing :/
            
            target_name = "Name";
            tt.findNode(single_segment, target_name, "");
            found_node = tt.getFoundNode();
            tt.setFoundNodeNull();

            if (found_node.getTextContent() != null)
            {
                split.setSplitName(found_node.getTextContent());
            }
            else //why arent you naming your splits silly boy
            {
                split.setSplitName("NAME THIS SPLIT, MAN");
            }
            
            found_node = null;

            target_name = "BestSegmentTime";
            tt.findNode(single_segment, target_name, "");
            found_node = tt.getFoundNode().getChildNodes().item(1);

            if (found_node != null)
            {
                split.setSplitGold(found_node.getTextContent());
            }

            tt.setFoundNodeNull();

            found_node = null;

            target_name = "Icon";
            tt.findNode(single_segment, target_name, "");
            found_node = tt.getFoundNode();

            if (found_node.getTextContent() != null)
            {
                split.setSplitIcon(found_node.getTextContent());
            }

            found_node = null;
            tt.setFoundNodeNull();

            if (import_pb) //if import_pb == true
            {
                target_name = "SplitTime";
                target_attribute = "Personal Best";

                tt.findNode(single_segment, target_name, target_attribute);
                found_node = tt.getFoundNode().getChildNodes().item(1); //this should be RealTime

                tt.setFoundNodeNull();

                if (found_node == null)
                {
                    split.setSplitPB("");
                }
                else
                {
                    split.setSplitPB(found_node.getTextContent());
                }

                found_node = null;
            }
            
            //should filter out completely empty records being read from the split file and
            //keep records with a name and empty gold  
            if (!split.getSplitName().equals(""))
            {
                splits_container.addSplit(split);
            }
        }

        return cleanSplitNames(splits_container);
    }

    public SplitsContainer cleanSplitNames(SplitsContainer splits)
    {
        System.out.println("Cleaning subsplits...");
        for (int i = 0; i < splits.getContainer().size(); i++)
        {
            StringBuffer name = new StringBuffer(splits.getContainer().get(i).getSplitName()); //StringBuffer to reduce load on stack/heap
            if (name.charAt(0) != '-' && name.charAt(0) != '{')
            {
                if (i < splits.getContainer().size() - 1)
                {
                    splits.getContainer().get(i).setSplitName("-" + name.toString());
                }
                
                else
                {
                    if (splits.getGame().equals(""))
                    {   //if no game name attached to splits, use this default
                        splits.getContainer().get(i).setSplitName("{Subsplit}" + name.toString());
                    } 

                    else
                    {   //if does have name, use it
                        splits.getContainer().get(i).setSplitName("{" + splits.getGame() + "}" + name.toString());
                    }
                }
            }

            else if (name.charAt(0) == '{' && i < splits.getContainer().size() - 1)
            {
                int end = name.indexOf("}");
                //remove the {subsplit} from the start of the split name
                splits.getContainer().get(i).setSplitName("-" + name.substring(end + 1).trim());
            }

            else if (name.charAt(0) == '-' && i == splits.getContainer().size() - 1)
            {
                //String cleaned = "{Subsplit}" + name.substring(1).trim(); //add the {subsplit} to the start of the split name
                if (splits.getGame().equals(""))
                    splits.getContainer().get(i).setSplitName("{Subsplit}" + name.substring(1).trim());

                else
                    splits.getContainer().get(i).setSplitName("{" + splits.getGame() + "}" + name.substring(1).trim());
            }

            else if (name.charAt(0) == '{' && i == splits.getContainer().size() - 1)
            {
                int end = name.indexOf("}");
                String subsplit = name.substring(1, end);

                if (!subsplit.equals(splits.getGame()))
                    splits.getContainer().get(i).setSplitName("{" + splits.getGame() + "}" + name.substring(end + 1));
            }
        }
        return splits;
    }
}