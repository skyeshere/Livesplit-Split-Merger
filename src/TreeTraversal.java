import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TreeTraversal {
    Node found_node = null;
    String target = "";
    Boolean found = false;

    public TreeTraversal()
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try
        {
            //get document to parse   
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse("empty.lss");

            //set the root node to the root node of the xml tree
            Node root_node = doc.getElementsByTagName("Run").item(0);
            target = "BestSegmentTime";

            //find the node with name target
            findNode(root_node, target);

            //debug print all children of the found node
            for (int i = 0; i < found_node.getChildNodes().getLength(); i++)
            {
                System.out.println(found_node.getChildNodes().item(i) + " " + i);
            }
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args)
    {
        new TreeTraversal();
    }

    public  void findNode(Node n, String target)
    {
        if (n.getNodeName().equals("#text")) return;

        System.out.println(n.getNodeName() + " : target = " + target );
        if (n.getNodeName().equals(target)) //check if new root's name is equal to the target node
        {
            //do blah blah blah
            System.out.println("Found `" + target + "`");
            found_node = n;
        }

		NodeList children = n.getChildNodes(); //pull all child nodes of current root
		
		if (children.getLength() < 1) return; //return if no children

		for (int i = 0; i < children.getLength(); i++) //loop through and explore child nodes
		{
			findNode(children.item(i), target);
		}
    }
}
