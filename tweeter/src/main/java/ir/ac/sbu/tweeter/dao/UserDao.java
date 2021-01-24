package ir.ac.sbu.tweeter.dao;

import ir.ac.sbu.tweeter.dto.UserSearchDto;
import ir.ac.sbu.tweeter.entity.User;
import ir.ac.sbu.tweeter.manager.User.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.List;

@Slf4j
@Component
public class UserDao {

    @PersistenceContext
    EntityManager entityManager;

    public void save(User user) {
        log.info("going to save user to db :{}", user);
        entityManager.persist(user);
        entityManager.flush();
        log.info("user is saved to database. Id is : {}", user.getId());
    }

    public User loadByUsername(String username) {
        log.debug("going to load product with serial number : {}", username);
        try {
            return doLoadByUsername(username);
        } catch (NoResultException e) {
            throw new UserNotFoundException(username);
        }
    }

    private User doLoadByUsername(String username) {
        String queryString = "select u from User u where u.username = :username";
        TypedQuery<User> query = entityManager.createQuery(queryString, User.class);
        query.setParameter("username", username);
        return query.getSingleResult();
    }

    public int delete(String username) {
        Query query = entityManager.createQuery("delete " +
                "from User u " + "where u.username = :username");
        query.setParameter("username", username);
        return query.executeUpdate();
    }

    public void update(User user) {
        entityManager.merge(user);
        entityManager.flush();
        log.debug("user with username : {} updated", user.getUsername());
    }

    public List<User> search(UserSearchDto dto) {
        String queryExpression = createSearchQuery(dto);
        TypedQuery<User> query = entityManager.createQuery(queryExpression, User.class);
        if (StringUtils.hasText(dto.getName())) {
            query.setParameter("name", dto.getName());
        }
        if (StringUtils.hasText(dto.getUsernameValue())) {
            query.setParameter("usernameValue", dto.getUsernameValue());
        }
        return query.getResultList();
    }

    private String createSearchQuery(UserSearchDto params) {
        String queryExpression = "select u from User u ";
        queryExpression += addWhereClause(params.getName(), params.getUsernameValue());
        return queryExpression;

    }

    private String addWhereClause(String name, String usernameValue) {
        String result ="where 1=1";
        if (StringUtils.hasText(name)) {
            result += " and u.name = :name ";
        }
        if (StringUtils.hasText(usernameValue)) {
            result += " and u.username = :usernameValue ";
        }
        return result;
    }


}