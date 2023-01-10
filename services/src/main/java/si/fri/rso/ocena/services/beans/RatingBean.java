package si.fri.rso.ocena.services.beans;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Timeout;
import si.fri.rso.ocena.lib.Rating;
import si.fri.rso.ocena.models.converters.RatingConverter;
import si.fri.rso.ocena.models.entities.RatingEntity;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.UriInfo;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RequestScoped
public class RatingBean {

    private Logger log = Logger.getLogger(RatingBean.class.getName());
    @Inject
    private RatingBean ratingBeanProxy;

    @Inject
    private EntityManager em;

    private Client httpClient;
    private String baseUrl;

    @PostConstruct
    private void init() {
        httpClient = ClientBuilder.newClient();
        baseUrl = "http://localhost:8082"; // only for demonstration
    }

    public List<Rating> getRatings() {
        TypedQuery<RatingEntity> query = em.createNamedQuery("RatingEntity.getAll", RatingEntity.class);

        List<RatingEntity> resultList = query.getResultList();

        return resultList.stream().map(RatingConverter::toDto).collect(Collectors.toList());
    }

    public List<Rating> getRatingsFilter(UriInfo uriInfo) {

        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery()).defaultOffset(0).build();

        return JPAUtils.queryEntities(em, RatingEntity.class, queryParameters).stream()
                .map(RatingConverter::toDto).collect(Collectors.toList());
    }

    public Rating getRatings(Integer id) {

        RatingEntity ratingEntity = em.find(RatingEntity.class, id);

        if (ratingEntity == null) {
            throw new NotFoundException();
        }

        Rating rating = RatingConverter.toDto(ratingEntity);

        ratingBeanProxy.getDelivererInfo(rating.getDeliveryPerson());
        return rating;
    }

    public Rating createRating(Rating rating) {

        RatingEntity ratingEntity = RatingConverter.toEntity(rating);

        try {
            beginTx();
            em.persist(ratingEntity);
            commitTx();
        }
        catch (Exception e) {
            rollbackTx();
        }

        if (ratingEntity.getId() == null) {
            throw new RuntimeException("Entity was not persisted");
        }

        return RatingConverter.toDto(ratingEntity);
    }

    public Rating putRating(Integer id, Rating rating) {

        RatingEntity r = em.find(RatingEntity.class, id);

        if (r == null) {
            return null;
        }

        RatingEntity updatedRatingEntity = RatingConverter.toEntity(rating);

        try {
            beginTx();
            updatedRatingEntity.setId(r.getId());
            updatedRatingEntity.setStars((r.getStars()*r.getRatingsCount()+updatedRatingEntity.getStars())/(r.getRatingsCount()+1));
            updatedRatingEntity.setRatingsCount(r.getRatingsCount()+1);
            updatedRatingEntity = em.merge(updatedRatingEntity);
            commitTx();
        }
        catch (Exception e) {
            rollbackTx();
        }

        return RatingConverter.toDto(updatedRatingEntity);
    }

    public boolean deleteRating(Integer id) {

        RatingEntity rating = em.find(RatingEntity.class, id);

        if (rating != null) {
            try {
                beginTx();
                em.remove(rating);
                commitTx();
            }
            catch (Exception e) {
                rollbackTx();
            }
        }
        else {
            return false;
        }

        return true;
    }


    //Klic druge mikrostoritve za napake in simulacijo tolerance napak, namenjeno za demonstracijo
    @Timeout(value = 2, unit = ChronoUnit.SECONDS)
    @CircuitBreaker(requestVolumeThreshold = 3)
    @Fallback(fallbackMethod = "getFallback")
    public Integer getDelivererInfo(Integer userId) {
        log.info("Calling delivery-person service: getting their id to add to count.");

        try {
            return httpClient
                    .target(baseUrl + "v1/dostavljalec/" + String.valueOf(userId))
                    .request().get(new GenericType<Integer>() {
                    });

        } catch (WebApplicationException | ProcessingException e) {
            log.severe(e.getMessage());
            throw new InternalServerErrorException(e);
        }
    }

    public Integer getFallback(Integer userId) {
        log.info("Fallback.");
        return null;
    }

    private void beginTx() {
        if (!em.getTransaction().isActive()) {
            em.getTransaction().begin();
        }
    }

    private void commitTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().commit();
        }
    }

    private void rollbackTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }

}
