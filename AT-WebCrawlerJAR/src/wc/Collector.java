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
import javax.ejb.Stateful;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import agent.AID;
import agent.Agent;
import agent.AgentManagerBean;
import agent.BaseAgent;
import message.ACLMessage;
import message.Performative;
import util.JSON;
import util.WSMessageCreator;

@Stateful
@Remote(Agent.class)
public class Collector extends BaseAgent {
	private static final long serialVersionUID = 1L;
	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";

	@EJB
	WSMessageCreator wsMessageCreator;
	@EJB
	AgentManagerBean agentManager;
	
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
			if (url.contains("city-nekretnine") || url.contains("info-nekretnine") || url.contains("021-nekretnine")) {
				ArrayList<Property> properties = scraperSameSite(url);
				if (properties != null) {
					saveToFile(properties, file);
				}
			}
			
			ACLMessage reply = msg.makeReply(Performative.INFORM);
			reply.content = this.getAid().getHost().getAddress();
			reply.sender = id;
			reply.ontology = file;
			agentManager.stopAgent(this.id);
			msm().post(reply);
			break;
		default:
			wsMessageCreator.log("Collector: invalid performative");
			agentManager.stopAgent(this.id);
			break;
		}

	}

	private ArrayList<Property> scraperSameSite(String url) {
		ArrayList<Property> properties = new ArrayList<Property>();
		try {
			Element nextPage = null;
			String nextPageUrl = url;
			// COMMENTED FOR DEV AND TESTING PURPOSES, uncomment when fully working project is done
			int i = 0;
			do {
				Connection connection = Jsoup.connect(nextPageUrl).userAgent(USER_AGENT);
				Document htmlDocument = connection.timeout(60 * 1000).get();
				Elements elements = htmlDocument.getElementsByClass("listing");
				//System.out.println(elements);

				for (Element element : elements) {
					String propertyUrl = element.select("a").first().absUrl("href");
					Property property = new Property();
					property.setUrl(propertyUrl);
					Connection connectionProperty = Jsoup.connect(propertyUrl).userAgent(USER_AGENT);
					Document htmlDocumentProperty = connectionProperty.timeout(60 * 1000).get();
					
					// Type
					String query = "Vrsta";
					Element type = htmlDocumentProperty.select("p:contains(" + query + ":)").first();
					if (type == null) {
						type = htmlDocumentProperty.select("p:contains(" + query.toUpperCase() + ":)").first();
					}
					if (type != null) {
						Element typeText = type.nextElementSibling();
						if (typeText != null && typeText.hasText()) {
							property.setType(typeText.text());
						}
					}
					// Area
					query = "Mesto";
					Element area = htmlDocumentProperty.select("p:contains(" + query + ":)").first();
					if (area == null) {
						area = htmlDocumentProperty.select("p:contains(" + query.toUpperCase() + ":)").first();
					}
					if (area != null) {
						Element areaText = area.nextElementSibling();
						if (areaText != null && areaText.hasText()) {
							property.setArea(areaText.text());
						}
					}
					// Location
					query = "Lokacija";
					Element location = htmlDocumentProperty.select("p:contains(" + query + ":)").first();
					if (location == null) {
						location = htmlDocumentProperty.select("p:contains(" + query.toUpperCase() + ":)").first();
					}
					if (location != null) {
						Element locationText = location.nextElementSibling();
						if (locationText != null && locationText.hasText()) {
							property.setLocation(locationText.text());
						}
					}
					// Size
					query = "Kvadratura";
					Element size = htmlDocumentProperty.select("p:contains(" + query + ":)").first();
					if (size == null) {
						size = htmlDocumentProperty.select("p:contains(" + query.toUpperCase() + ":)").first();
					}
					if (size != null) {
						Element sizeText = size.nextElementSibling();
						if (sizeText != null && sizeText.hasText()) {
							String text = sizeText.text();
							Double number = Double.parseDouble(text.substring(0, text.length()-2));
							property.setSize(number);
						}
					}
					// Price
					query = "Cena";
					Element price = htmlDocumentProperty.select("p:contains(" + query + ":)").first();
					if (price == null) {
						price = htmlDocumentProperty.select("p:contains(" + query.toUpperCase() + ":)").first();
					}
					if (price != null) {
						Element priceText = price.nextElementSibling();
						if (priceText != null && priceText.hasText()) {
							String text = priceText.text();
							Double number = Double.parseDouble(text.substring(0, text.length()-1));
							property.setPrice(number);
						}
					}
					// State
					query = "Stanje nekretnine";
					Element state = htmlDocumentProperty.select("p:contains(" + query + ":)").first();
					if (state == null) {
						state = htmlDocumentProperty.select("p:contains(" + query.toUpperCase() + ":)").first();
					}
					if (state != null) {
						Element stateText = state.nextElementSibling();
						if (stateText != null && stateText.hasText()) {
							property.setState(stateText.text());
						}
					}
					// Land
					query = "Plac";
					Element land = htmlDocumentProperty.select("p:contains(" + query + ":)").first();
					if (land == null) {
						land = htmlDocumentProperty.select("p:contains(" + query.toUpperCase() + ":)").first();
					}
					if (land != null) {
						Element landText = land.nextElementSibling();
						if (landText != null && landText.hasText()) {
							String text = landText.text();
							Double number = Double.parseDouble(text.substring(0, text.length()-2));
							property.setLand(number);
						}
					}
					
					// Views
					Element views = htmlDocumentProperty.getElementsByClass("property_view").first();
					if (views == null) {
						views = htmlDocumentProperty.getElementsByClass("property_views").first();
					}
					if (views != null) {
						String text = views.text().split(" ")[3];
						property.setViews(Integer.parseInt(text));
					}
					System.out.println(property);
					properties.add(property);
				}
				

				nextPage = htmlDocument.select("div.bottom_pagination > div > span > a").last();
				if (nextPage != null) {
					nextPageUrl = nextPage.absUrl("href");
					System.out.println("************ Next Page Url ************");
					System.out.println(nextPageUrl);
				}
				i++;
				System.out.println("--------- PAGE: " + i + " ------------");
			} while (i < 50); // Testing
//			} while (nextPage != null); // Real

			return properties;
		} catch (IOException e) {
			e.printStackTrace();
			return properties;
		}
	}

//	private ArrayList<Property> scrapeGreenAcresSite(String regionUrl) {
//		try {
//			ArrayList<Property> properties = new ArrayList<Property>();
//			Element nextPage = null;
//			String nextPageUrl = regionUrl;
//			// COMMENTED FOR DEV AND TESTING PURPOSES, uncomment when fully working project is done
//			int i = 0;
//			do {
//				Connection connection = Jsoup.connect(nextPageUrl).userAgent(USER_AGENT);
//				Document htmlDocument = connection.get();
//				Elements elements = htmlDocument.getElementsByTag("figure");
//				System.out.println(elements.size());
//
//				for (Element element : elements) {
//					String url = element.select("a").first().absUrl("href");
//					Property property = new Property();
//					property.setUrl(url);
//					Connection connectionProperty = Jsoup.connect(url).userAgent(USER_AGENT);
//					Document htmlDocumentProperty = connectionProperty.get();
//
//					Elements address = htmlDocumentProperty.select("#mainInfoAdvertPage > div > ul > li > a > p");
//					property.setAddress(address.text());
//
//					//Elements details = htmlDocumentProperty.select("#mainInfoAdvertPage > div > ul > li");
//					Elements details = htmlDocumentProperty.select("#mainInfoAdvertPage > div > ul > li > p");
//					for (Element el : details) {
//						String info = el.text();
//						
//						if (info.equals("Habitable area")) {
//							property.setHabitableArea(Double.parseDouble(((TextNode)el.nextSibling()).text().replaceAll(",", "")));
//						} else if (info.equals("Rooms")) {
//							property.setRooms(Double.parseDouble(((TextNode)el.nextSibling()).text().replaceAll(",", "")));
//						} else if (info.equals("Bedrooms")) {
//							property.setBedrooms(Double.parseDouble(((TextNode)el.nextSibling()).text().replaceAll(",", "")));
//						} else if (info.equals("Land")) {
//							property.setLand(Double.parseDouble(((TextNode)el.nextSibling()).text().replaceAll(",", "")));
//						} else {
//							System.out.println(info);
//						}
//					}
//
//					Element price = htmlDocumentProperty.select(
//							"#descriptionBlockAdvertPage > div.item-content-part.price.item-ecology > div > h2 > span")
//							.first();
//					property.setPrice(
//							Double.parseDouble(price.text().substring(2, price.text().length()).replaceAll(",", "")));
//
//					System.out.println(property);
//					properties.add(property);
//				}
//
//				nextPage = htmlDocument.getElementById("nextPage");
//				if (nextPage != null) {
//					nextPageUrl = nextPage.absUrl("href");
//				}
//				i++;
//			} while (i < 6);
//
//			return properties;
//		} catch (IOException e) {
//			e.printStackTrace();
//			return null;
//		}
//	}

	private void saveToFile(ArrayList<Property> properties, String file) throws IOException {
		int index = file.lastIndexOf("/");
		if (index != -1) {
			String dir = file.substring(0, index);
			File directory = new File(dir);
			if (!directory.exists()) {
				directory.mkdirs();
			}
		}

		try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"))) {
			writer.write(JSON.mapper.writeValueAsString(properties));
		}
	}
}
