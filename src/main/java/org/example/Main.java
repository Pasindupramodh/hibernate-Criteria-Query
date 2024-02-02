package org.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import org.example.entity.Customer;
import org.example.persistence.PersistenceUnitInfo;
import org.hibernate.jpa.HibernatePersistenceProvider;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {

        Map<String, String> props = new HashMap<>();

        props.put("hibernate.show_sql", "true");
        props.put("hibernate.hbm2ddl.auto", "none");


//        EntityManagerFactory emf = Persistence.createEntityManagerFactory("my-persistence-unit");
        EntityManagerFactory emf = new HibernatePersistenceProvider()
                .createContainerEntityManagerFactory(new PersistenceUnitInfo(), props);
        EntityManager entityManager = emf.createEntityManager();//represent the context
        try {
            entityManager.getTransaction().begin();

            CriteriaBuilder builder = entityManager.getCriteriaBuilder();

            CriteriaQuery<Object[]> cq = builder.createQuery(Object[].class);

            Root<Customer> customerRoot = cq.from(Customer.class);

//            cq.select(customerRoot); //SELECT c FROM Customer c

//            cq.select(customerRoot.get("name")); //SELECT c.name FROM Customer c

            cq.multiselect(customerRoot.get("name"),customerRoot.get("id"))
                    .where(builder.like(customerRoot.get("name"),"%u%"))
                    .orderBy(builder.asc(customerRoot.get("name")));

            TypedQuery<Object[]> query = entityManager.createQuery(cq);

            query.getResultList().forEach(customer -> System.out.println(customer[0]+" "+customer[1]));

            entityManager.getTransaction().commit();
        } finally {
            entityManager.close();
        }
    }
}