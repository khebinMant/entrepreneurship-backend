package com.project.emprendia.event.repository;

import com.project.emprendia.event.domain.Event;
import com.project.emprendia.event.domain.QEvent;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class EventQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<Event> search(String name, Long eventTypeId, Long eventVisibilityId,
                               LocalDateTime fromDate, LocalDateTime toDate) {
        QEvent event = QEvent.event;
        BooleanBuilder predicate = buildPredicate(name, eventTypeId, eventVisibilityId, fromDate, toDate);

        return queryFactory.selectFrom(event)
            .where(predicate)
            .orderBy(event.startDatetime.asc())
            .fetch();
    }

    public Page<Event> searchPaginated(String name, Long eventTypeId, Long eventVisibilityId,
                                        LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable) {
        QEvent event = QEvent.event;
        BooleanBuilder predicate = buildPredicate(name, eventTypeId, eventVisibilityId, fromDate, toDate);

        List<Event> results = queryFactory.selectFrom(event)
            .where(predicate)
            .orderBy(event.startDatetime.asc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long totalCount = queryFactory.select(event.count())
            .from(event)
            .where(predicate)
            .fetchOne();

        long total = totalCount != null ? totalCount : 0L;
        return new PageImpl<>(results, pageable, total);
    }

    public List<Event> findByCreator(Long userId, String name, Long eventTypeId, Long eventVisibilityId,
                                      LocalDateTime fromDate, LocalDateTime toDate) {
        QEvent event = QEvent.event;
        BooleanBuilder predicate = buildUserPredicate(userId, name, eventTypeId, eventVisibilityId, fromDate, toDate);

        return queryFactory.selectFrom(event)
            .where(predicate)
            .orderBy(event.startDatetime.asc())
            .fetch();
    }

    public Page<Event> findByCreatorPaginated(Long userId, String name, Long eventTypeId, Long eventVisibilityId,
                                               LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable) {
        QEvent event = QEvent.event;
        BooleanBuilder predicate = buildUserPredicate(userId, name, eventTypeId, eventVisibilityId, fromDate, toDate);

        List<Event> results = queryFactory.selectFrom(event)
            .where(predicate)
            .orderBy(event.startDatetime.asc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long totalCount = queryFactory.select(event.count())
            .from(event)
            .where(predicate)
            .fetchOne();

        long total = totalCount != null ? totalCount : 0L;
        return new PageImpl<>(results, pageable, total);
    }

    private BooleanBuilder buildPredicate(String name, Long eventTypeId, Long eventVisibilityId,
                                           LocalDateTime fromDate, LocalDateTime toDate) {
        QEvent event = QEvent.event;
        BooleanBuilder predicate = new BooleanBuilder();

        if (name != null && !name.isBlank()) {
            predicate.and(event.name.containsIgnoreCase(name));
        }
        if (eventTypeId != null) {
            predicate.and(event.eventTypeId.eq(eventTypeId));
        }
        if (eventVisibilityId != null) {
            predicate.and(event.eventVisibilityId.eq(eventVisibilityId));
        }
        applyDateFilters(predicate, event, fromDate, toDate);

        return predicate;
    }

    private BooleanBuilder buildUserPredicate(Long userId, String name, Long eventTypeId, Long eventVisibilityId,
                                               LocalDateTime fromDate, LocalDateTime toDate) {
        QEvent event = QEvent.event;
        BooleanBuilder predicate = new BooleanBuilder();

        predicate.and(event.createdByUserId.eq(userId));

        if (name != null && !name.isBlank()) {
            predicate.and(event.name.containsIgnoreCase(name));
        }
        if (eventTypeId != null) {
            predicate.and(event.eventTypeId.eq(eventTypeId));
        }
        if (eventVisibilityId != null) {
            predicate.and(event.eventVisibilityId.eq(eventVisibilityId));
        }
        applyDateFilters(predicate, event, fromDate, toDate);

        return predicate;
    }

    private void applyDateFilters(BooleanBuilder predicate, QEvent event,
                                   LocalDateTime fromDate, LocalDateTime toDate) {
        if (fromDate != null && toDate != null) {
            predicate.and(event.startDatetime.between(fromDate, toDate));
        } else if (fromDate != null) {
            predicate.and(event.startDatetime.goe(fromDate));
        } else if (toDate != null) {
            predicate.and(event.startDatetime.loe(toDate));
        }
    }
}
