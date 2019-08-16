package Objects;

public class UserNeighborhood {

    public int id,user_id,state_id,district_id,lga_id,street_id,building_id,apartment_id,apartment_type_id;
    public String state_name;
    public String lga_name;
    public String district_name;
    public String date;
    public String street_name;
    public String building_name;
    public String apartment_name;
    public String user_type;

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public void setApartment_type_id(int apartment_type_id) {
        this.apartment_type_id = apartment_type_id;
    }

    public void setApartment_type_name(String apartment_type_name) {
        this.apartment_type_name = apartment_type_name;
    }

    public String apartment_type_name;

    public void setStreet_id(int street_id) {
        this.street_id = street_id;
    }

    public void setBuilding_id(int building_id) {
        this.building_id = building_id;
    }

    public void setApartment_id(int apartment_id) {
        this.apartment_id = apartment_id;
    }

    public void setStreet_name(String street_name) {
        this.street_name = street_name;
    }

    public void setBuilding_name(String building_name) {
        this.building_name = building_name;
    }

    public void setApartment_name(String apartment_name) {
        this.apartment_name = apartment_name;
    }

    public void setLga_id(int lga_id) {
        this.lga_id = lga_id;
    }

    public void setLga_name(String lga_name) {
        this.lga_name = lga_name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setState_id(int state_id) {
        this.state_id = state_id;
    }

    public void setDistrict_id(int district_id) {
        this.district_id = district_id;
    }

    public void setState_name(String state_name) {
        this.state_name = state_name;
    }

    public void setDistrict_name(String district_name) {
        this.district_name = district_name;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
