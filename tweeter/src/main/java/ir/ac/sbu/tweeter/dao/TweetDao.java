package ir.ac.sbu.tweeter.dao;

import ir.ac.sbu.tweeter.dto.TweetSearchParamsDto;
import ir.ac.sbu.tweeter.entity.Tweet;
import ir.ac.sbu.tweeter.manager.Tweet.TweetNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class TweetDao {

    @PersistenceContext
    EntityManager entityManager;

    public Tweet save(Tweet tweet) {
        log.info("going to save tweet to db :{}", tweet);
        entityManager.persist(tweet);
        entityManager.flush();
        log.info("user is saved to database. Id is : {}", tweet.getId());
        return tweet;
    }

    public Tweet loadByUsername(String uuid) {
        log.debug("going to load Tweet with uuid number : {}", uuid);
        try {
            return doLoadByUsername(uuid);
        } catch (NoResultException e) {
            throw new TweetNotFoundException(uuid);
        }
    }

    private Tweet doLoadByUsername(String uuid) {
        String queryString = "select t from Tweet t where t.uuid = :uuid";
        TypedQuery<Tweet> query = entityManager.createQuery(queryString, Tweet.class);
        query.setParameter("uuid", uuid);
        return query.getSingleResult();
    }

    public int delete(String uuid) {
        Query query = entityManager.createQuery("delete " +
                "from Tweet t " + "where t.uuid = :uuid");
        query.setParameter("uuid", uuid);
        return query.executeUpdate();
    }



    public List<Tweet> search(TweetSearchParamsDto dto) {
        String queryExpression = createSearchQuery(dto);
        TypedQuery<Tweet> query = entityManager.createQuery(queryExpression, Tweet.class);
        if (StringUtils.hasText(dto.getOwnerUsername())) {
            query.setParameter("ownerUsername", dto.getOwnerUsername());
        }
        if (StringUtils.hasText(dto.getHashtag())) {
            query.setParameter("hashtag", dto.getHashtag());
        }
        if(Objects.nonNull(dto.getStartDate())){
            query.setParameter("startDate", dto.getStartDate());
        }
        if(Objects.nonNull(dto.getFinishDate())){
            query.setParameter("finishDate", dto.getFinishDate());
        }
        return query.getResultList();
    }

    private String createSearchQuery(TweetSearchParamsDto params) {
        String queryExpression = "select t from Tweet t inner join t.hashtags h ";
        queryExpression += addWhereClause(params);
        return queryExpression;

    }

    private String addWhereClause(TweetSearchParamsDto params) {
        String result ="where 1=1";
        if (StringUtils.hasText(params.getOwnerUsername())) {
            result += " and t.owner.username = :ownerUsername ";
        }
        if (StringUtils.hasText(params.getHashtag())) {
            result += " and :hashtag = h";
        }
        if(Objects.nonNull(params.getStartDate())){
            result += " and t.time >= :startDate ";
        }
        if(Objects.nonNull(params.getFinishDate())){
            result += " and t.time <= :finishDate ";
        }
        return result;
    }
}
