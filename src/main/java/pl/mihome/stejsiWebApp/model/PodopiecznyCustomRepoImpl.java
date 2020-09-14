package pl.mihome.stejsiWebApp.model;

import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.stereotype.Repository;
import pl.mihome.stejsiWebApp.adapter.PodopiecznyCustomRepo;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class PodopiecznyCustomRepoImpl implements PodopiecznyCustomRepo {

    private final EntityManager entityManager;

    public PodopiecznyCustomRepoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Podopieczny> elasticSearchFindByKeyWord(String keyWord) {

        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);

        QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder()
                .forEntity(Podopieczny.class)
                .get();

        Query luceneQuery = queryBuilder
                .bool()
                .should(
                        queryBuilder
                                .keyword()
                                .fuzzy()
                                .withEditDistanceUpTo(2)
                                .withPrefixLength(2)
                                .onFields("imie", "nazwisko", "email")
                                .matching(keyWord.toLowerCase())
                                .createQuery())
                .should(
                        queryBuilder
                                .keyword()
                                .wildcard()
                                .onFields("imie", "nazwisko", "email")
                                .matching("*" + keyWord.toLowerCase() + "*")
                                .createQuery())
                .createQuery();


        FullTextQuery jpaQuery = fullTextEntityManager.createFullTextQuery(luceneQuery, Podopieczny.class);

        jpaQuery.setMaxResults(15);

        return jpaQuery.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Podopieczny> elasticSearchFindByPhoneNumber(String keyWord) {
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);

        QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder()
                .forEntity(Podopieczny.class)
                .get();

        Query luceneQuery = queryBuilder
                .keyword()
                .wildcard()
                .onField("phoneNumber")
                .matching(Integer.parseInt(keyWord))
                .createQuery();


        FullTextQuery jpaQuery = fullTextEntityManager.createFullTextQuery(luceneQuery, Podopieczny.class);

        jpaQuery.setMaxResults(15);

        return jpaQuery.getResultList();
    }

}
