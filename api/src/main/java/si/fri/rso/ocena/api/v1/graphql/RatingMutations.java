package si.fri.rso.ocena.api.v1.graphql;

import com.kumuluz.ee.graphql.annotations.GraphQLClass;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import si.fri.rso.ocena.lib.Rating;
import si.fri.rso.ocena.services.beans.RatingBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@GraphQLClass
@ApplicationScoped
public class RatingMutations {

    @Inject
    private RatingBean ratingBean;

    @GraphQLMutation
    public Rating addRating(@GraphQLArgument(name = "rating") Rating rating) {
        ratingBean.createRating(rating);
        return rating;
    }

    @GraphQLMutation
    public DeleteResponse deleteRating(@GraphQLArgument(name = "id") Integer id) {
        return new DeleteResponse(ratingBean.deleteRating(id));
    }

}