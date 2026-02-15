package ru.tmchhhhhhhhhhhhh.javalabs.lab7client.server.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import ru.tmchhhhhhhhhhhhh.javalabs.lab7client.common.model.Dish;
import ru.tmchhhhhhhhhhhhh.javalabs.lab7client.server.model.Order;
import ru.tmchhhhhhhhhhhhh.javalabs.lab7client.server.util.HibernateUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderDAO {
    
    // CREATE ORDER
    public Order createOrder(Dish dish) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            
            Order order = new Order(dish);
            session.persist(order);
            
            transaction.commit();
            
            System.out.println("[DAO] Создан заказ для: " + dish.getName());
            return order;
            
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Ошибка создания заказа", e);
        }
    }
    
    // GET ORDER COUNT BY DISH
    public Map<String, Long> getOrderCountByDish() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT d.name, COUNT(o.id) " +
                        "FROM Order o JOIN o.dish d " +
                        "GROUP BY d.name " +
                        "ORDER BY COUNT(o.id) DESC";
            
            List<Object[]> results = session.createQuery(hql, Object[].class).list();
            
            Map<String, Long> orderCounts = new HashMap<>();
            for (Object[] row : results) {
                orderCounts.put((String) row[0], (Long) row[1]);
            }
            
            return orderCounts;
        }
    }
    
    // GET TOTAL ORDERS
    public Long getTotalOrders() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Long> query = session.createQuery("SELECT COUNT(o) FROM Order o", Long.class);
            return query.uniqueResult();
        }
    }
    
    // GET TOP POPULAR DISHES
    public List<Object[]> getTopPopularDishes(int limit) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT d, COUNT(o.id) as orderCount " +
                        "FROM Order o JOIN o.dish d " +
                        "GROUP BY d " +
                        "ORDER BY orderCount DESC";
            
            Query<Object[]> query = session.createQuery(hql, Object[].class);
            query.setMaxResults(limit);
            
            return query.list();
        }
    }
}
