package com.vuog.telebotmanager.application.specification;

import com.vuog.telebotmanager.domain.entity.Command;
import com.vuog.telebotmanager.presentation.dto.query.CommandQuery;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class CommandSpecification {
    public static Specification<Command> withFilter(CommandQuery query) {
        return (root, queryBuilder, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (query == null) {
                return criteriaBuilder.conjunction();
            }

            if (query.getBotId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("bot").get("id"), query.getBotId()));
            }

            if (query.getType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("type"), query.getType()));
            }

            if (query.getTrigger() != null) {
                predicates.add(criteriaBuilder.equal(root.get("trigger"), query.getTrigger()));
            }

            if (query.getEnabled() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isEnabled"), query.getEnabled()));
            }

            if (query.getCommandContains() != null && !query.getCommandContains().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("command")), 
                        "%" + query.getCommandContains().toLowerCase() + "%"));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
