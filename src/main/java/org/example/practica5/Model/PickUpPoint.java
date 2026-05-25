package org.example.practica5.Model;

public class PickUpPoint {

   public int id;
   public String city;
   public  String street;
   public int building;

    public PickUpPoint(int id, String city, String street, int building) {
        this.id=id;
        this.city=city;
        this.street=street;
        this.building=building;
    }

    public int getIndex() { return id; }
    public String getCity() { return city; }
    public String getStreet() { return street; }
    public int getBuildingNumber() { return building; }

}
