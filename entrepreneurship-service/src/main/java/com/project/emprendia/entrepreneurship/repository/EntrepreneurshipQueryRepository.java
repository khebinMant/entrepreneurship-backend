package com.project.emprendia.entrepreneurship.repository;

import com.project.emprendia.entrepreneurship.domain.Entrepreneurship;
import com.project.emprendia.entrepreneurship.domain.QEntrepreneurship;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class EntrepreneurshipQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<Entrepreneurship> search(String name, Long categoryId, Boolean isPhysical, Boolean isDigital) {
        QEntrepreneurship entrepreneurship = QEntrepreneurship.entrepreneurship;
        BooleanBuilder predicate = buildPredicate(name, categoryId, isPhysical, isDigital);

        return queryFactory.selectFrom(entrepreneurship)
            .join(entrepreneurship.category).fetchJoin()
            .where(predicate)
            .orderBy(entrepreneurship.name.asc())
            .fetch();
    }

    public Page<Entrepreneurship> searchPaginated(String name, Long categoryId, Boolean isPhysical, Boolean isDigital, Pageable pageable) {
        QEntrepreneurship entrepreneurship = QEntrepreneurship.entrepreneurship;
        BooleanBuilder predicate = buildPredicate(name, categoryId, isPhysical, isDigital);

        List<Entrepreneurship> results = queryFactory.selectFrom(entrepreneurship)
            .join(entrepreneurship.category).fetchJoin()
            .where(predicate)
            .orderBy(entrepreneurship.name.asc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long totalCount = queryFactory.select(entrepreneurship.count())
            .from(entrepreneurship)
            .join(entrepreneurship.category)
            .where(predicate)
            .fetchOne();

        long total = totalCount != null ? totalCount : 0L;

        return new PageImpl<>(results, pageable, total);
    }

    private BooleanBuilder buildPredicate(String name, Long categoryId, Boolean isPhysical, Boolean isDigital) {
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

        return predicate;
    }
}
