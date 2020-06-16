package wc;

import java.io.Serializable;
import java.util.ArrayList;

public class DataRequestDTO implements Serializable{

	private static final long serialVersionUID = 1L;
	private ArrayList<String> content;
	private String ontology;
	
	public DataRequestDTO() {
		super();
	}

	public ArrayList<String> getContent() {
		return content;
	}

	public void setContent(ArrayList<String> content) {
		this.content = content;
	}

	public String getOntology() {
		return ontology;
	}

	public void setOntology(String ontology) {
		this.ontology = ontology;
	}
}
