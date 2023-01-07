package si.fri.rso.ocena.lib;

public class Rating {
    private Integer id;
    private Integer deliveryPerson;
    Float stars;
    Integer ratingsCount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDeliveryPerson() {
        return deliveryPerson;
    }

    public void setDeliveryPerson(Integer client) {
        this.deliveryPerson = deliveryPerson;
    }

    public Float getStars() {
        return stars;
    }

    public void setStars(Float stars) {
        this.stars = stars;
    }

    public Integer getRatingsCount() {
        return ratingsCount;
    }

    public void setRatingsCount(Integer stars) {
        this.ratingsCount = ratingsCount;
    }

}
