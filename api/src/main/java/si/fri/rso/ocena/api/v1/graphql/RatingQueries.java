package si.fri.rso.narocilo.api.v1.graphql;

import com.kumuluz.ee.graphql.annotations.GraphQLClass;
import com.kumuluz.ee.graphql.classes.Filter;
import com.kumuluz.ee.graphql.classes.Pagination;
import com.kumuluz.ee.graphql.classes.PaginationWrapper;
import com.kumuluz.ee.graphql.classes.Sort;
import com.kumuluz.ee.graphql.utils.GraphQLUtils;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;
import si.fri.rso.ocena.lib.Rating;
import si.fri.rso.ocena.services.beans.RatingBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@GraphQLClass
@ApplicationScoped
public class RatingQueries {

    @Inject
    private RatingBean ratingBean;

    @GraphQLQuery
    public PaginationWrapper<Rating> allRatings(@GraphQLArgument(name = "pagination") Pagination pagination,
                                              @GraphQLArgument(name = "sort") Sort sort,
                                              @GraphQLArgument(name = "filter") Filter filter) {

        return GraphQLUtils.process(ratingBean.getRatings(), pagination, sort, filter);
    }

    @GraphQLQuery
    public Rating getRatings(@GraphQLArgument(name = "id") Integer id) {
        return ratingBean.getRatings(id);
    }

}