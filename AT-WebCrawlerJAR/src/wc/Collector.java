package wc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import agent.AID;
import agent.Agent;
import agent.BaseAgent;
import message.ACLMessage;
import util.JSON;
import util.WSMessageCreator;

@Stateless
@Remote(Agent.class)
public class Collector extends BaseAgent {
	private static final long serialVersionUID = 1L;
	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";

	@EJB
	WSMessageCreator wsMessageCreator;

	@Override
	public void init(AID id) throws IOException {
		this.id = id;
		wsMessageCreator.log("Collector agent created");
	}

	@Override
	public void handleMessage(ACLMessage msg) throws IOException {
		switch (msg.performative) {

		case REQUEST:
			String content = msg.content;
			String url = content.split(" ")[0];
			String file = content.split(" ")[1];
			System.out.println(url);
			System.out.println(file);
			if (url.contains("green-acres")) {
				ArrayList<Property> properties = scrapeGreenAcresSite(url);
				if (properties != null) {
					saveToFile(properties, file);
				}
			}
			break;
		default:
			wsMessageCreator.log("Collector: invalid performative");
			break;
		}

	}

	private ArrayList<Property> scrapeGreenAcresSite(String regionUrl) {
		try {
			ArrayList<Property> properties = new ArrayList<Property>();
			Element nextPage = null;
			String nextPageUrl = regionUrl;
			do {
				Connection connection = Jsoup.connect(nextPageUrl).userAgent(USER_AGENT);
				Document htmlDocument = connection.get();
				Elements elements = htmlDocument.getElementsByTag("figure");
				System.out.println(elements.size());

				for (Element element : elements) {
					String url = element.select("a").first().absUrl("href");
					System.out.println(url);
					Property property = new Property();

					Connection connectionProperty = Jsoup.connect(url).userAgent(USER_AGENT);
					Document htmlDocumentProperty = connectionProperty.get();

					Elements address = htmlDocumentProperty.select("#mainInfoAdvertPage > div > ul > li > a > p");
					property.setAddress(address.text());

					Elements details = htmlDocumentProperty.select("#mainInfoAdvertPage > div > ul > li");
					int i = 0;
					for (Element el : details) {
						for (Node child : el.childNodes()) {
							if (child instanceof TextNode && !((TextNode) child).isBlank()) {
								if (i == 0) {
									if (Double.parseDouble(((TextNode) child).text()) > 10) {
										property.setHabitableArea(Double.parseDouble(((TextNode) child).text()));
									} else { // No habitable area specified
										property.setRooms(Double.parseDouble(((TextNode) child).text()));
										break;
									}

								} else if (i == 1) {
									property.setRooms(Double.parseDouble(((TextNode) child).text()));
								} else if (i == 2) {
									property.setBedrooms(Double.parseDouble(((TextNode) child).text()));
								}
								i++;
							}
						}
					}
					Element price = htmlDocumentProperty.select(
							"#descriptionBlockAdvertPage > div.item-content-part.price.item-ecology > div > h2 > span")
							.first();
					property.setPrice(
							Double.parseDouble(price.text().substring(2, price.text().length()).replaceAll(",", "")));

					System.out.println(property);
					properties.add(property);
				}

				nextPage = htmlDocument.getElementById("nextPage");
				if (nextPage != null) {
					nextPageUrl = nextPage.absUrl("href");
				}

			} while (nextPage != null);

			return properties;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private void saveToFile(ArrayList<Property> properties, String file) throws IOException {
		int index = file.lastIndexOf("/");
		if (index != -1) {
			String dir = file.substring(0, index);
			System.out.println(dir);
			File directory = new File(dir);
			if (!directory.exists()) {
				directory.mkdirs();
			}
		}

		try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"))) {
			writer.write(JSON.mapper.writeValueAsString(properties));
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
