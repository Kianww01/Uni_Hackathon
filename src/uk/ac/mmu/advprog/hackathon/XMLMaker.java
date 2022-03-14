package uk.ac.mmu.advprog.hackathon;

import java.io.StringWriter;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Handles the XML Document creation for task 3
 * @author Kian Wilson 19069770
 */
public class XMLMaker
{
	/**
	 * Creates and returns an empty XML document
	 * @return An empty XML document
	 */
	private static Document makeDoc() {
		try {
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			
			return db.newDocument();
		}
		catch(ParserConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Uses a transformer and a writer to convert a provided XML document into a valid string for output
	 * @param doc An XML document created by one of the methods within the class
	 * @return A string representation of the XML provided
	 */
	private static String formatDocument(Document doc) {
		StringWriter writer = new StringWriter();
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.transform(new DOMSource(doc), new StreamResult(writer));						
		}
		catch(TransformerFactoryConfigurationError | TransformerException e) {
			e.printStackTrace();
		}
		return writer.toString();
	}
	
	/**
	 * Creates an XML document which stores the provided signal groups within a <group> tag which is stored within a <groups> tag
	 * @param results ArrayList of strings which contains signal groups
	 * @return A formatted XML document which contains the signal groups stored as XML
	 */
	public static String getSignalGroups(ArrayList<String> results) {
		Document doc = makeDoc();
		
		Element Groups = doc.createElement("Groups");
		doc.appendChild(Groups);
	
		for(String s : results) {
			Element Group = doc.createElement("Group");
			// Assigns value of a textnode to a value at the current index of the results arraylist
			Group.appendChild(doc.createTextNode(s));
			
			Groups.appendChild(Group);
		}		
		return formatDocument(doc);
	}
	
	/**
	 * Creates an XML document which stores the provided signal ids, values and datetimes within their respective tags,
	 * <ID>, <Value>, <DateSet> - These are stored within a <Signal> tag. There is one of these for every GroupTime POJO stored within
	 * the groupTimes ArrayList. Surrounding all <Signal> tags is a <Signals> tag (the root)
	 * @param groupTimes An ArrayList of GroupTime POJOs
	 * @return A formatted XML document which contains the signal id, values and datetimes stored as XML
	 */
	public static String getSignalsByGroupTime(ArrayList<GroupTime> groupTimes) {
		Document doc = makeDoc();
		
		Element Signals = doc.createElement("Signals");
		doc.appendChild(Signals);
		
		for(GroupTime gt : groupTimes) {
			Element Signal = doc.createElement("Signal");
			Signals.appendChild(Signal);
			
			Element ID = doc.createElement("ID");
			Element DateSet = doc.createElement("DateSet");
			Element Value = doc.createElement("Value");
			Signal.appendChild(ID);
			Signal.appendChild(DateSet);
			Signal.appendChild(Value);
			
			ID.appendChild(doc.createTextNode(gt.getSignalID()));
			DateSet.appendChild(doc.createTextNode(gt.getDateTime()));
			Value.appendChild(doc.createTextNode(gt.getSignalValue()));
		}
		return formatDocument(doc);
	}
}
