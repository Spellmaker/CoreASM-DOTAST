
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.Map;
import org.coreasm.engine.CoreASMEngineFactory;
import org.coreasm.engine.EngineProperties;
import org.coreasm.engine.Specification;
import org.coreasm.engine.Engine;
import org.coreasm.engine.interpreter.ASTNode;
import org.coreasm.engine.interpreter.Node;

public class Main {
	/**
	 * args[0] = filename
	 * @param args
	 */
	
	public static int nodecount = 0;
	
	public static void main(String[] args) throws Exception{
		Engine cae = (Engine)CoreASMEngineFactory.createEngine();
		//cae.setProperty(EngineProperties.PLUGIN_FOLDERS_PROPERTY, "C:\\Users\\Spellmaker\\git\\coreasm-plugins\\org.coreasm.plugins.assertion\\target");
		cae.initialize();
		cae.loadSpecification(args[0]);
		while(cae.isBusy());
		cae.terminate();
		System.out.println("Engine has finished");
		Specification s = cae.getSpec();
		System.out.println(s.getRootNode().unparseTree());
		System.out.println(s.getRootNode().getPluginName());
		System.out.println("======================================");
		System.out.println("The tree is:");
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(args[1]));
		
		printDot(s, bw, Integer.parseInt(args[2]));		
		
		bw.close();		
	}
	
	public static void printNode(Node n, int depth){
		if(depth == 0) return;
		
		System.out.println("Node Type is: " + n.getConcreteNodeType());
		System.out.println("Child count is: " + n.getNumberOfChildren());
		System.out.println("Plugin is: " + n.getPluginName());
		System.out.println("printing children: ");
		for(int i = 0; i < n.getNumberOfChildren(); i++){
			System.out.println("Child " + i + ":");
			printNode((Node)n.getChildNodes().get(i), depth - 1);
		}
	}
	
	public static void printDot(Specification s, BufferedWriter bw, int depth) throws Exception{
		bw.write("digraph " + s.getName() + "{");bw.newLine();
		bw.write("ratio=fill");bw.newLine();
		bw.write("size=\"8.27,11.7\"");bw.newLine();
		bw.write("ranksep=0.05");bw.newLine();
		bw.write("nodesep=0.05");bw.newLine();
		bw.write("page=\"8.5,11\"");bw.newLine();
		nodecount = 0;
		printDotNode((ASTNode)s.getRootNode(), bw, depth);
		bw.write("}");bw.newLine();
	}
	
	public static int printDotNode(ASTNode n, BufferedWriter bw, int depth) throws Exception{
		if(depth == 0) return 0;
		
		int mycount = nodecount;
		
		String label = "n" + nodecount + 
				"[label=\"" + 
				"Node Type: " + n.getConcreteNodeType() + "\n" + 
				"Token: " + n.getToken() + "\n" + 
				"Plugin Name: " + n.getPluginName() + "\n" +
				"Grammar Class: " + n.getGrammarClass() + "\n" + 
				"Rule: " + n.getGrammarRule() + "\n";
		try{
			Map<String, Object> m = n.getProperties();
			for(Iterator<String> it = m.keySet().iterator(); it.hasNext(); ){
				String o = it.next();
				label = label + "Key: " + o.toString() + " | Value: " + m.get(o).toString() + "\n";
			}
		}
		catch(NullPointerException npe){
			label = label + "No properties\n";
		}

		label = label + "\"; fontsize=\"28\"]";
		
		bw.write(label);
		bw.newLine();
		for(int i = 0; i < n.getAbstractChildNodes().size(); i++){
			nodecount++;
			int tmp = printDotNode((ASTNode)n.getAbstractChildNodes().get(i), bw, depth - 1);
			bw.write("n" + mycount + " -> " + "n" + tmp); bw.newLine();
		}
		
		return mycount;
	}
}
