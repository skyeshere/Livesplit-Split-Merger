import java.util.Scanner;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class MetadataInput
{
	Scanner input = new Scanner(System.in);
	Document merged_file;
	Node found_node = null; 

	public MetadataInput(Document f)
	{
		merged_file = f;
	}

	public void inputMetadata()
	{
	    String file_name = nameSplitFile();
	    merged_file = nameGameAndCategory(merged_file);

	    saveSplitsFile(merged_file, file_name);
	}

	public Document nameGameAndCategory(Document doc)
	{ 
		String game_name = "";
		String cat_name = "";

		System.out.print("What game is this for? (default= blank): ");
		game_name = input.nextLine();

		System.out.print("\nWhat category is this for? (default= blank): ");
		cat_name = input.nextLine();

		//search tree and put in data if needed
		Node root_node = doc.getElementsByTagName("Run").item(0);
		String target = "";
		if(!game_name.equals(""))
		{
			//search and replace
			target = "GameName";
			findNode(root_node, target);
			found_node.setTextContent(game_name);
			found_node = null;
		}

		if(!cat_name.equals(""))
		{
			//search and replace
			target = "CategoryName";
			findNode(root_node, target);
			found_node.setTextContent(cat_name);
			found_node = null;
		}

		return doc;
	}

	public String nameSplitFile()
	{
		String file_name = "";
		System.out.print("\nEnter the name you want for this file (default= merged):");
		file_name = input.nextLine();

	    if (file_name == "") //default case
	    {
	    	file_name = "merged";
	    }
	    else if(file_name.length() >= 4 && file_name.substring(file_name.length() - 4).toLowerCase().equals(".lss")) //if the file name is long enough and has the .lss extension
	    {
	    	file_name = file_name.substring(0, file_name.length() - 4); //remove the .lss if the user has inputted it
	    }

		return file_name;
	}

	public void saveSplitsFile(Document doc, String fn)
	{
		try
		{
			//save new merged split file!! 
			TransformerFactory tff = TransformerFactory.newInstance();
	        Transformer tf = tff.newTransformer();
	        DOMSource dom_merged = new DOMSource(doc);

	        StreamResult result = new StreamResult(fn + ".lss");
	        tf.transform(dom_merged, result);
	        System.out.println("Merging complete, file saved as " + fn + ".lss");
		}

		catch(Exception e)
		{
			System.err.println("error while saving splits: ");
			System.err.println(e);
		}
	}

	public void findNode(Node n, String target)
    {
        String curr_node = n.getNodeName();
        if (curr_node.equals("#text")) return; //skips redundant fake ass nodes
        
        if (curr_node.equals(target)) //check if new root's name is equal to the target node
        {
			found_node = n;
			return;
        }

		NodeList children = n.getChildNodes(); //pull all child nodes of current root
		
		if (children.getLength() < 1) return; //return if no children

		for (int i = 0; i < children.getLength(); i++) //loop through and explore child nodes
		{
			if (found_node != null) return;
			findNode(children.item(i), target);
		}
    }
}