package si.fri.rso.ocena.services.beans;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import si.fri.rso.ocena.lib.Rating;
import si.fri.rso.ocena.models.converters.RatingConverter;
import si.fri.rso.ocena.models.entities.RatingEntity;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.stream.Collectors;

@RequestScoped
public class RatingBean {

    @Inject
    private EntityManager em;

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
