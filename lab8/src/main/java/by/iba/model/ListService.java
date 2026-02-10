package by.iba.model;

import java.sql.*;
import java.util.*;

public class ListService {

    // Получить всех студентов из БД
    public static List<Person> retrieveList() {

        List<Person> list = new ArrayList<>();

        try (Connection c = DBUtil.getConnection()) {

            Statement st = c.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM person");

            while (rs.next()) {
                list.add(new Person(
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("email")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // Добавить студента в БД
    public static void addPerson(Person p) {

        try (Connection c = DBUtil.getConnection()) {

            PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO person(name, phone, email) VALUES (?, ?, ?)");

            ps.setString(1, p.getName());
            ps.setString(2, p.getPhone());
            ps.setString(3, p.getEmail());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void assignLab(String lab) {
        try (Connection c = DBUtil.getConnection()) {

            // создаём лабораторную, если её ещё нет
            PreparedStatement labStmt =
                    c.prepareStatement("INSERT INTO lab(title) VALUES (?)",
                            Statement.RETURN_GENERATED_KEYS);
            labStmt.setString(1, lab);
            labStmt.executeUpdate();

            ResultSet keys = labStmt.getGeneratedKeys();
            int labId = 0;
            if (keys.next()) labId = keys.getInt(1);

            // привязываем эту лабу ко всем студентам
            Statement st = c.createStatement();
            ResultSet rs = st.executeQuery("SELECT id FROM person");

            while (rs.next()) {
                int pid = rs.getInt("id");

                PreparedStatement ps =
                        c.prepareStatement(
                                "INSERT INTO person_lab(person_id, lab_id) VALUES (?,?)");

                ps.setInt(1, pid);
                ps.setInt(2, labId);
                ps.executeUpdate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
