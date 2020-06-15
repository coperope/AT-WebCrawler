package wc;

import java.io.Serializable;

public class Property implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String address;
	private double habitableArea;
	private double rooms;
	private double bedrooms;
	private double price;
	private double land;
	private String url;
	
	public Property() {
		
	}
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public double getHabitableArea() {
		return habitableArea;
	}
	public void setHabitableArea(double habitableArea) {
		this.habitableArea = habitableArea;
	}
	public double getRooms() {
		return rooms;
	}
	public void setRooms(double rooms) {
		this.rooms = rooms;
	}
	public double getBedrooms() {
		return bedrooms;
	}
	public void setBedrooms(double bedrooms) {
		this.bedrooms = bedrooms;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public double getLand() {
		return land;
	}
	public void setLand(double land) {
		this.land = land;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "Property [address=" + address + ", habitableArea=" + habitableArea + ", rooms=" + rooms + ", bedrooms="
				+ bedrooms + ", price=" + price + ", land=" + land + ", url=" + url + "]";
	}
}
