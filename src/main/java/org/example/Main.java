package org.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.example.entity.Author;
import org.example.entity.Book;
import org.example.entity.BookShop;
import org.example.entity.Customer;
import org.example.persistence.PersistenceUnitInfo;
import org.hibernate.jpa.HibernatePersistenceProvider;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {

        Map<String, String> props = new HashMap<>();

        props.put("hibernate.show_sql", "true");
        props.put("hibernate.hbm2ddl.auto", "update");


//        EntityManagerFactory emf = Persistence.createEntityManagerFactory("my-persistence-unit");
        EntityManagerFactory emf = new HibernatePersistenceProvider()
                .createContainerEntityManagerFactory(new PersistenceUnitInfo(), props);
        EntityManager entityManager = emf.createEntityManager();//represent the context
        try {
            entityManager.getTransaction().begin();

//            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
//
//            CriteriaQuery<Object[]> cq = builder.createQuery(Object[].class);
//
//            Root<Customer> customerRoot = cq.from(Customer.class);

//            cq.select(customerRoot); //SELECT c FROM Customer c

//            cq.select(customerRoot.get("name")); //SELECT c.name FROM Customer c

//
//            cq.multiselect(customerRoot.get("name"),customerRoot.get("id"));
//            cq.where(builder.like(customerRoot.get("name"),"%a%"));
//            cq.groupBy(customerRoot.get("id"));
//            cq.orderBy(builder.asc(customerRoot.get("name")));
//
//
//
//            TypedQuery<Object[]> query = entityManager.createQuery(cq);
//
//            query.getResultList().forEach(customer -> System.out.println(customer[0]+" "+customer[1]));


            /*----- Joins  -----*/

            CriteriaBuilder builder = entityManager.getCriteriaBuilder();

//            CriteriaQuery<Tuple> cq = builder.createTupleQuery();
//
//            //Author , Book
//            Root<Book> bookRoot = cq.from(Book.class);//select b from book b
//            Join<Book, Author> authorJoin = bookRoot.join("authorList",JoinType.LEFT);
//            Join<Book, BookShop> bookShopJoin = bookRoot.join("bookShopList",JoinType.INNER);
//
//            cq.multiselect(bookRoot,authorJoin,bookShopJoin); //select b,a from Book b inner join Author a
//
//            TypedQuery<Tuple> query = entityManager.createQuery(cq);
//
//            query.getResultStream()
//                    .forEach(tuple ->
//                            System.out.println(tuple.get(0)+" "+tuple.get(1)+" "+tuple.get(2))
//                    );

            //Sub Queries


            //SELECT a, (SELECT count(b) FROM Book b JOIN Author on b.id IN a.bookList) n FROM Author a WHERE n > 2
            CriteriaQuery<Author> mainQuery = builder.createQuery(Author.class);

            Root<Author> authorRoot = mainQuery.from(Author.class);

            Subquery<Long> subquery = mainQuery.subquery(Long.class);
            Root<Author> subRootAuthor = subquery.correlate(authorRoot);
            Join<Author,Book> authorBookJoin = subRootAuthor.join("bookList");

            subquery.select(builder.count(authorBookJoin));

            mainQuery.select(authorRoot)
                    .where(builder.greaterThan(subquery,1L));

            TypedQuery<Author> query = entityManager.createQuery(mainQuery);

            query.getResultStream().forEach(author -> System.out.println(author));

            entityManager.getTransaction().commit();
        } finally {
            entityManager.close();
        }
    }
}