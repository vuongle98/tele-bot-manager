package com.vuog.telebotmanager.application.specification;

import com.vuog.telebotmanager.domain.entity.BotPlugin;
import com.vuog.telebotmanager.presentation.dto.query.PluginQuery;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class PluginSpecification {
    public static Specification<BotPlugin> withFilter(PluginQuery query) {
        return (root, queryBuilder, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (query == null) {
                return criteriaBuilder.conjunction();
            }

            if (query.getNameContains() != null && !query.getNameContains().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")), 
                        "%" + query.getNameContains().toLowerCase() + "%"));
            }

            if (query.getType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("type"), query.getType()));
            }

            if (query.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), query.getStatus()));
            }

            if (query.getActive() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isActive"), query.getActive()));
            }

            if (query.getAuthor() != null && !query.getAuthor().trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("author"), query.getAuthor()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
