package com.project.emprendia.entrepreneurship.repository;

import com.project.emprendia.entrepreneurship.domain.Entrepreneurship;
import com.project.emprendia.entrepreneurship.domain.QEntrepreneurship;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class EntrepreneurshipQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<Entrepreneurship> search(String name, Long categoryId, Boolean isPhysical, Boolean isDigital) {
        QEntrepreneurship entrepreneurship = QEntrepreneurship.entrepreneurship;
        BooleanBuilder predicate = new BooleanBuilder();

        if (name != null && !name.isBlank()) {
            predicate.and(entrepreneurship.name.containsIgnoreCase(name));
        }
        if (categoryId != null) {
            predicate.and(entrepreneurship.category.categoryId.eq(categoryId));
        }
        if (isPhysical != null) {
            predicate.and(entrepreneurship.isPhysical.eq(isPhysical));
        }
        if (isDigital != null) {
            predicate.and(entrepreneurship.isDigital.eq(isDigital));
        }

        return queryFactory.selectFrom(entrepreneurship)
            .join(entrepreneurship.category).fetchJoin()
            .where(predicate)
            .orderBy(entrepreneurship.name.asc())
            .fetch();
    }
}
