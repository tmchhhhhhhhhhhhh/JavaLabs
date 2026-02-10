package by.iba.model;

import java.util.ArrayList;
import java.util.List;

public class Person {
    private String name;
    private String phone;
    private String email;
    private List<String> labs = new ArrayList<>();

    public Person(String name, String phone, String email) {
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public Person(Person p) {
        this.name = p.name;
        this.phone = p.phone;
        this.email = p.email;
        this.labs = p.labs;
    }

    public void addLab(String lab) {
        labs.add(lab);
    }

    public List<String> getLabs() {
        return labs;
    }

    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }

    public void setName(String name) { this.name = name; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
}
