package wc;

import java.io.Serializable;
import java.util.HashMap;

import wc.Master.Statistic;

public class StatisticDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private HashMap<String, Integer> type;
	private HashMap<String, Integer> area;
	private HashMap<String, Integer> location;
	private HashMap<String, Integer> size;
	private HashMap<String, Integer> land;
	private HashMap<String, Integer> price;
	private HashMap<String, Integer> state;

	public StatisticDTO() {
		super();
	}

	public HashMap<String, Integer> getType() {
		return type;
	}

	public void setType(HashMap<String, Integer> type) {
		this.type = type;
	}

	public HashMap<String, Integer> getArea() {
		return area;
	}

	public void setArea(HashMap<String, Integer> area) {
		this.area = area;
	}

	public HashMap<String, Integer> getLocation() {
		return location;
	}

	public void setLocation(HashMap<String, Integer> location) {
		this.location = location;
	}

	public HashMap<String, Integer> getSize() {
		return size;
	}

	public void setSize(HashMap<String, Integer> size) {
		this.size = size;
	}

	public HashMap<String, Integer> getLand() {
		return land;
	}

	public void setLand(HashMap<String, Integer> land) {
		this.land = land;
	}

	public HashMap<String, Integer> getPrice() {
		return price;
	}

	public void setPrice(HashMap<String, Integer> price) {
		this.price = price;
	}

	public HashMap<String, Integer> getState() {
		return state;
	}

	public void setState(HashMap<String, Integer> state) {
		this.state = state;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "StatisticDTO [type=" + type + ", area=" + area + ", location=" + location + ", size=" + size + ", land="
				+ land + ", price=" + price + ", state=" + state + "]";
	}
}
