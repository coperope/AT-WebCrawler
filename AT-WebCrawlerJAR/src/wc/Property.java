package wc;

import java.io.Serializable;

public class Property implements Serializable {
	private static final long serialVersionUID = 1L;
	
	
	private String type;
	private String area;
	private String location;
	private Double size;
	private Double land;
	private Double price;
	private String state;
	private String url;
	private Integer views;
	
	public Property() {
		
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Double getSize() {
		return size;
	}

	public void setSize(Double size) {
		this.size = size;
	}

	public Double getLand() {
		return land;
	}

	public void setLand(Double land) {
		this.land = land;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getViews() {
		return views;
	}

	public void setViews(Integer views) {
		this.views = views;
	}

	@Override
	public String toString() {
		return "Property [type=" + type + ", area=" + area + ", location=" + location + ", size=" + size + ", land="
				+ land + ", price=" + price + ", state=" + state + ", url=" + url + ", views=" + views + "]";
	}


}
