package com.project.emprendia.shared.repository;

import com.project.emprendia.shared.domain.CatalogueValue;
import com.project.emprendia.shared.domain.QCatalogueValue;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CatalogueValueQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<CatalogueValue> findByTypeCode(String typeCode) {
        QCatalogueValue catalogueValue = QCatalogueValue.catalogueValue;
        return queryFactory.selectFrom(catalogueValue)
            .join(catalogueValue.catalogueType).fetchJoin()
            .where(catalogueValue.catalogueType.code.eq(typeCode))
            .orderBy(catalogueValue.name.asc())
            .fetch();
    }

    public Optional<CatalogueValue> findByTypeCodeAndCode(String typeCode, String code) {
        QCatalogueValue catalogueValue = QCatalogueValue.catalogueValue;
        return Optional.ofNullable(
            queryFactory.selectFrom(catalogueValue)
                .join(catalogueValue.catalogueType).fetchJoin()
                .where(catalogueValue.catalogueType.code.eq(typeCode)
                    .and(catalogueValue.code.eq(code)))
                .fetchOne()
        );
    }

    public List<CatalogueValue> findChildrenByParentId(Long parentId) {
        QCatalogueValue catalogueValue = QCatalogueValue.catalogueValue;
        return queryFactory.selectFrom(catalogueValue)
            .where(catalogueValue.parentValue.catalogueValueId.eq(parentId))
            .orderBy(catalogueValue.name.asc())
            .fetch();
    }
}
