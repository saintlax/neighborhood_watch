package Objects;

public class OrganisationStaff {

    public int id;
    public int user_id;
    public int organisation_id;
    public String organisation,status,date;

    public void setId(int id) {
        this.id = id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setOrganisation_id(int organisation_id) {
        this.organisation_id = organisation_id;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
