package com.project.emprendia.user.repository;

import com.project.emprendia.user.domain.AppUser;
import com.project.emprendia.user.domain.QAppUser;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<AppUser> searchUsers(String firstName, String lastName) {
        QAppUser user = QAppUser.appUser;
        BooleanBuilder predicate = new BooleanBuilder();

        if (firstName != null && !firstName.isBlank()) {
            predicate.and(user.firstName.containsIgnoreCase(firstName));
        }
        if (lastName != null && !lastName.isBlank()) {
            predicate.and(user.lastName.containsIgnoreCase(lastName));
        }

        return queryFactory.selectFrom(user)
            .where(predicate)
            .orderBy(user.lastName.asc(), user.firstName.asc())
            .fetch();
    }
}
