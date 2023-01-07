package si.fri.rso.ocena.models.converters;

import si.fri.rso.ocena.lib.Rating;
import si.fri.rso.ocena.models.entities.RatingEntity;

public class RatingConverter {

    public static Rating toDto(RatingEntity entity) {

        Rating dto = new Rating();
        dto.setId(entity.getId());
        dto.setDeliveryPerson(entity.getDeliveryPerson());
        dto.setStars(entity.getStars());
        dto.setRatingsCount(entity.getRatingsCount());

        return dto;
    }

    public static RatingEntity toEntity(Rating dto) {

        RatingEntity entity = new RatingEntity();
        entity.setId(dto.getId());
        entity.setDeliveryPerson(dto.getDeliveryPerson());
        entity.setStars(dto.getStars());
        entity.setRatingsCount(dto.getRatingsCount());

        return entity;
    }
}
