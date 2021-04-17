package com.example.jwt.repository;

import com.example.jwt.entity.UserEntity;
import com.example.jwt.response.UserResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import static com.example.jwt.entity.QUserEntity.userEntity;
import static com.example.jwt.entity.QTokenEntity.tokenEntity;

@Repository
public class UserRepositorySupport extends QuerydslRepositorySupport {

    private final JPAQueryFactory queryFactory;

    public UserRepositorySupport(JPAQueryFactory queryFactory) {
        super(UserEntity.class);
        this.queryFactory = queryFactory;
    }


    public UserResponse.LoginResponse getTokenInfo(String userId) {
        return queryFactory
                .select(Projections.constructor(UserResponse.LoginResponse.class, userEntity.id, tokenEntity.accessToken, tokenEntity.refreshToken))
                .from(userEntity)
                .join(tokenEntity).on(userEntity.id.eq(tokenEntity.userIdx))
                .where(userEntity.userId.eq(userId))
                .fetchOne();
    }

    public UserResponse.UserInfoDto getUserInfo(String userId) {
        return queryFactory
                .select(Projections.constructor(UserResponse.UserInfoDto.class, userEntity.userId, userEntity.userName, userEntity.email))
                .from(userEntity)
                .where(userEntity.userId.eq(userId))
                .fetchOne();

    }

    public Long getUserIdx(String userId) {
        return queryFactory
                .select(userEntity.id)
                .from(userEntity)
                .where(userEntity.userId.eq(userId))
                .fetchOne();
    }
}
