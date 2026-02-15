package ru.restaurant.server.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import ru.restaurant.common.model.Dish;
import ru.restaurant.server.util.HibernateUtil;

import java.util.List;
import java.util.UUID;

public class DishDAO {
    
    // CREATE
    public Dish create(Dish dish) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            
            if (dish.getId() == null || dish.getId().isEmpty()) {
                dish.setId(UUID.randomUUID().toString());
            }
            
            session.persist(dish);
            transaction.commit();
            
            System.out.println("[DAO] Создано блюдо: " + dish.getName());
            return dish;
            
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Ошибка создания блюда", e);
        }
    }
    
    // READ ONE
    public Dish findById(String id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Dish.class, id);
        }
    }
    
    // READ ALL
    public List<Dish> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Dish ORDER BY name", Dish.class).list();
        }
    }
    
    // UPDATE
    public Dish update(Dish dish) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            
            Dish updated = session.merge(dish);
            transaction.commit();
            
            System.out.println("[DAO] Обновлено блюдо: " + updated.getName());
            return updated;
            
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Ошибка обновления блюда", e);
        }
    }
    
    // DELETE
    public boolean delete(String id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            
            Dish dish = session.get(Dish.class, id);
            if (dish != null) {
                session.remove(dish);
                transaction.commit();
                
                System.out.println("[DAO] Удалено блюдо: " + dish.getName());
                return true;
            }
            
            return false;
            
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Ошибка удаления блюда", e);
        }
    }
    
    // FILTER BY TYPE
    public List<Dish> findByType(String type) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Dish> query = session.createQuery(
                "FROM Dish WHERE type = :type ORDER BY name", Dish.class);
            query.setParameter("type", type);
            return query.list();
        }
    }
    
    // FILTER BY PRICE RANGE
    public List<Dish> findByPriceRange(Double minPrice, Double maxPrice) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Dish> query = session.createQuery(
                "FROM Dish WHERE price BETWEEN :min AND :max ORDER BY price", Dish.class);
            query.setParameter("min", minPrice);
            query.setParameter("max", maxPrice);
            return query.list();
        }
    }
    
    // FILTER BY CALORIES RANGE
    public List<Dish> findByCaloriesRange(Integer minCalories, Integer maxCalories) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Dish> query = session.createQuery(
                "FROM Dish WHERE calories BETWEEN :min AND :max ORDER BY calories", Dish.class);
            query.setParameter("min", minCalories);
            query.setParameter("max", maxCalories);
            return query.list();
        }
    }
    
    // SORT BY NAME
    public List<Dish> findAllSortedByName(boolean ascending) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String order = ascending ? "ASC" : "DESC";
            return session.createQuery("FROM Dish ORDER BY name " + order, Dish.class).list();
        }
    }
    
    // SORT BY PRICE
    public List<Dish> findAllSortedByPrice(boolean ascending) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String order = ascending ? "ASC" : "DESC";
            return session.createQuery("FROM Dish ORDER BY price " + order, Dish.class).list();
        }
    }
    
    // SORT BY CALORIES
    public List<Dish> findAllSortedByCalories(boolean ascending) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String order = ascending ? "ASC" : "DESC";
            return session.createQuery("FROM Dish ORDER BY calories " + order, Dish.class).list();
        }
    }
    
    // TOP EXPENSIVE
    public List<Dish> findTopExpensive(int limit) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Dish> query = session.createQuery("FROM Dish ORDER BY price DESC", Dish.class);
            query.setMaxResults(limit);
            return query.list();
        }
    }
    
    // STATISTICS
    public Object[] getStatistics() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Object[]> query = session.createQuery(
                "SELECT AVG(price), MIN(price), MAX(price), " +
                "AVG(calories), MIN(calories), MAX(calories), COUNT(*) " +
                "FROM Dish", Object[].class);
            return query.uniqueResult();
        }
    }
}
