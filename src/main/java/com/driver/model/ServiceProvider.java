package com.driver.model;

import org.apache.logging.log4j.util.PerformanceSensitive;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ServiceProvider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "provide_name")
    private String name;

    // navigational properties
    @ManyToOne
    @JoinColumn
    Admin admin;
    @OneToMany(mappedBy = "serviceProvider",cascade = CascadeType.ALL)
    List<Connection> connectionList=new ArrayList<>();

    @OneToMany(mappedBy="serviceProvider",cascade = CascadeType.ALL)
    List<Country> countryList=new ArrayList<>();

    @ManyToMany(mappedBy = "serviceProviderList",cascade = CascadeType.ALL)
    List<User> userList= new ArrayList<>();

    // getter and setter / constructor
    public ServiceProvider(int id, String name, Admin admin, List<Connection> connections, List<Country> countries, List<User> users) {
        this.id = id;
        this.name = name;
        this.admin = admin;
        this.connectionList = connections;
        this.countryList = countries;
        this.userList = users;
    }

    public ServiceProvider() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    public List<Connection> getConnectionList() {
        return connectionList;
    }

    public void setConnectionList(List<Connection> connections) {
        this.connectionList = connections;
    }

    public List<Country> getCountryList() {
        return countryList;
    }

    public void setCountryList(List<Country> countries) {
        this.countryList = countries;
    }

    public List<User> getUsers() {
        return userList;
    }

    public void setUsers(List<User> users) {
        this.userList = users;
    }
}
