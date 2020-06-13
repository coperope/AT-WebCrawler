package wc;

public class Property {
	
	private String address;
	private double habitableArea;
	private double rooms;
	private double bedrooms;
	private double price;
	
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
	@Override
	public String toString() {
		return "Property [address=" + address + ", habitableArea=" + habitableArea + ", rooms=" + rooms + ", bedrooms="
				+ bedrooms + ", price=" + price + "]";
	}

	
}
