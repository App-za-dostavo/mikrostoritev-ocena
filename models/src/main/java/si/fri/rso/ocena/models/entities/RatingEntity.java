package si.fri.rso.ocena.models.entities;

import javax.persistence.*;

@Entity
@Table(name = "ocena")
@NamedQueries(value =
        {
                @NamedQuery(name = "RatingEntity.getAll", query = "SELECT rating FROM RatingEntity rating"),
                @NamedQuery(name = "RatingEntity.getById", query = "SELECT rating FROM RatingEntity rating WHERE rating.id=:id")
        })
public class RatingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "deliveryPerson")
    private Integer deliveryPerson;

    @Column(name = "stars")
    private Float stars;

    @Column(name = "ratingsCount")
    private Integer ratingsCount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDeliveryPerson() {
        return deliveryPerson;
    }

    public void setDeliveryPerson(Integer deliveryPerson) {
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

    public void setRatingsCount(Integer ratingsCount) {
        this.ratingsCount = ratingsCount;
    }

}
