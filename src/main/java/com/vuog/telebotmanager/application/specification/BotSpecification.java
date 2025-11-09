package com.vuog.telebotmanager.application.specification;

import com.vuog.telebotmanager.domain.entity.Bot;
import com.vuog.telebotmanager.presentation.dto.query.BotQuery;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class BotSpecification {
    public static Specification<Bot> withFilter(BotQuery query) {
        return (root, queryBuilder, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (query.getActive() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isActive"), query.getActive()));
            }

            if (query.getName() != null) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("botName")),
                        "%" + query.getName().toLowerCase() + "%"));
            }

            if (query.getUsername() != null) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("botUsername")), 
                        "%" + query.getUsername().toLowerCase() + "%"));
            }

            if (query.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), query.getStatus()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
