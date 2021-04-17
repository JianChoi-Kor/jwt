package com.example.jwt.repository;

import com.example.jwt.entity.TokenEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import static com.example.jwt.entity.QTokenEntity.tokenEntity;

@Repository
public class TokenRepositorySupport extends QuerydslRepositorySupport {

    private JPAQueryFactory queryFactory;

    public TokenRepositorySupport(JPAQueryFactory queryFactory) {
        super(TokenEntity.class);
        this.queryFactory = queryFactory;
    }


    public TokenEntity getTokenInfo(String accessToken) {
        return queryFactory
                .selectFrom(tokenEntity)
                .where(tokenEntity.accessToken.eq(accessToken))
                .fetchOne();
    }

}
