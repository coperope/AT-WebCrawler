package wc;

import java.io.IOException;
import java.util.ArrayList;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import agent.AID;
import agent.Agent;
import agent.BaseAgent;
import message.ACLMessage;
import util.WSMessageCreator;

@Stateless
@Remote(Agent.class)
public class Collector extends BaseAgent{
	private static final long serialVersionUID = 1L;
	private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";

	@EJB
	WSMessageCreator wsMessageCreator;
	
	@Override
	public void init(AID id) throws IOException {
		this.id = id;
		wsMessageCreator.log("InitiatorContractNet agent created");
	}
	
	@Override
	public void handleMessage(ACLMessage msg) throws IOException {
		//if (msg.content.contains("green-acres")) {
			scrapeGreenAcresSite();
		//}
	
	}
	
	private void scrapeGreenAcresSite() {
		try {
			for (String region: greenAcresRegions()) {
				Connection connection = Jsoup.connect(region).userAgent(USER_AGENT);
		        Document htmlDocument = connection.get();
				
		        Elements elements = htmlDocument.getElementsByTag("figure");
				System.out.println(elements.size());
		
				for (Element element: elements) {
					String url = element.select("a").first().absUrl("href");
					System.out.println(url); 
					
					Connection connectionProperty = Jsoup.connect("https://www.green-acres.fr/property-for-sale/paris").userAgent(USER_AGENT);
			        Document htmlDocumentProperty = connectionProperty.get();
			        
			        // Extract data, create json, append to document
		
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private ArrayList<String> greenAcresRegions() {
		ArrayList<String> regions = new ArrayList<String>();
		regions.add("https://www.green-acres.fr/property-for-sale/paris");
		regions.add("https://www.green-acres.fr/property-for-sale/hauts-de-seine");
		regions.add("https://www.green-acres.fr/property-for-sale/val-de-marne");
		regions.add("https://www.green-acres.fr/property-for-sale/seine-saint-denis");
		regions.add("https://www.green-acres.fr/property-for-sale/seine-et-marne");
		regions.add("https://www.green-acres.fr/property-for-sale/val-d-oise");
		regions.add("https://www.green-acres.fr/property-for-sale/yvelines");
		regions.add("https://www.green-acres.fr/property-for-sale/essonne");
		return regions;
	}
}
