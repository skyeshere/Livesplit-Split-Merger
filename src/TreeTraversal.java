import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TreeTraversal {
    Node found_node = null;
    String target_name = "";
    String target_attribute = "";

    // public TreeTraversal()
    // {
    //     DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    //     try
    //     {
    //         //get document to parse   
    //         DocumentBuilder builder = factory.newDocumentBuilder();
    //         Document doc = builder.parse("16star.lss");

    //         //set the root node to the root node of the xml tree
    //         Node root_node = doc.getElementsByTagName("Run").item(0);
    //         target_name = "SplitTime";
    //         target_attribute = "Personal Best";

    //         //find the node with name target
    //         findNode(root_node, target_name, target_attribute);

    //         // //debug print all children of the found node
    //         // for (int i = 0; i < found_node.getChildNodes().getLength(); i++)
    //         // {
    //         //     System.out.println(found_node.getChildNodes().item(i) + " " + i);
    //         // }
            
    //     }
    //     catch(Exception e)
    //     {
    //         e.printStackTrace();
    //         System.exit(1);
    //     }
    // }

    public void findNode(Node n, String target_name, String target_attribute)
    {
        String curr_name = n.getNodeName();

        if (curr_name.equals("#text")) return;

        // System.out.println(n.getNodeName() + " : target = " + target );
        if (curr_name.equals(target_name)) //check if new root's name is equal to the target node
        {
            if (!target_attribute.equals("") && n.getAttributes().item(0).getTextContent().equals(target_attribute)) //item(0) should be enough. no node has more than one attribute i THINK
            {
                //check if attribute is the same as the one we are looking for
                found_node = n;
                return;
            }

            else if (target_attribute.equals(""))
            {
                //if we're not looking for an attribute, go as normal 
                found_node = n;
                return;
            }
        }

		NodeList children = n.getChildNodes(); //pull all child nodes of current root

		if (children.getLength() < 1) return; //return if no children

		for (int i = 0; i < children.getLength(); i++) //loop through and explore child nodes
		{
			findNode(children.item(i), target_name, target_attribute);	      
        }
    }

    public Node getFoundNode()
    {
        return this.found_node;
    }

    public void setFoundNodeNull()
    {
        this.found_node = null;
    }
}