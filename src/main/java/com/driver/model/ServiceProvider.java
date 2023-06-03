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
    private String providerName;

    // navigational properties
    @ManyToOne
    @JoinColumn
    Admin admin;
    @OneToMany(mappedBy = "serviceProvider",cascade = CascadeType.ALL)
    List<Connection> connections=new ArrayList<>();

    @OneToMany(mappedBy="serviceProvider",cascade = CascadeType.ALL)
    List<Country> countries=new ArrayList<>();

    @ManyToMany(mappedBy = "serviceProviders",cascade = CascadeType.ALL)
    List<User> users= new ArrayList<>();

    // getter and setter / constructor
    public ServiceProvider(int id, String providerName, Admin admin, List<Connection> connections, List<Country> countries, List<User> users) {
        this.id = id;
        this.providerName = providerName;
        this.admin = admin;
        this.connections = connections;
        this.countries = countries;
        this.users = users;
    }

    public ServiceProvider() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    public List<Connection> getConnections() {
        return connections;
    }

    public void setConnections(List<Connection> connections) {
        this.connections = connections;
    }

    public List<Country> getCountries() {
        return countries;
    }

    public void setCountries(List<Country> countries) {
        this.countries = countries;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
