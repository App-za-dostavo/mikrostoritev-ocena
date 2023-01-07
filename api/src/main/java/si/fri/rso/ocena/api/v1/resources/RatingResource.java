package si.fri.rso.ocena.api.v1.resources;

import si.fri.rso.ocena.lib.Rating;
import si.fri.rso.ocena.services.beans.RatingBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@ApplicationScoped
@Path("/ocena")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RatingResource {

    @Inject
    private RatingBean ratingBean;

    @Context
    protected UriInfo uriInfo;

    @GET
    public Response getRating() {

        List<Rating> rating = ratingBean.getRatingsFilter(uriInfo);

        return Response.status(Response.Status.OK).entity(rating).build();
    }

    @GET
    @Path("/{id}")
    public Response getRating(@PathParam("id") Integer id) {

        Rating rating = ratingBean.getRatings(id);

        if (rating == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(rating).build();
    }

    @POST
    public Response createRating(Rating rating) {

        if (rating.getDeliveryPerson() == null || rating.getStars() == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else {
            if (rating.getRatingsCount() == null) {
                rating.setRatingsCount(1);
            }
            rating = ratingBean.createRating(rating);
        }

        return Response.status(Response.Status.CONFLICT).entity(rating).build();
    }

    @PUT
    @Path("{id}")
    public Response putRating(@PathParam("id") Integer id, Rating rating) {

        rating = ratingBean.putRating(id, rating);

        if (rating == null) {
            return Response.status(Response.Status.NOT_MODIFIED).build();
        }
        return Response.status(Response.Status.OK).entity(rating).build();
    }

    @DELETE
    @Path("{id}")
    public Response deleteRating(@PathParam("id") Integer id, Rating rating) {

        boolean deleted = ratingBean.deleteRating(id);

        if (deleted) {
            return  Response.status(Response.Status.OK).build();
        } else return Response.status(Response.Status.NOT_FOUND).entity(rating).build();
    }
}

