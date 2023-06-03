package com.driver.model;

import javax.persistence.*;

@Entity
// Note: Do not write @Enumerated annotation above CountryName in this model.
public class Country{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    private CountryName countryName;

    String code;

    // navigational properties
    @ManyToOne
    @JoinColumn(nullable = true)
    ServiceProvider serviceProvider;

    @OneToOne
    @JoinColumn
    User user;

    public Country(int id, CountryName countryName, String code, ServiceProvider serviceProvider, User user) {
        this.id = id;
        this.countryName = countryName;
        this.code = code;
        this.serviceProvider = serviceProvider;
        this.user = user;
    }

    public Country() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CountryName getCountryName() {
        return countryName;
    }

    public void setCountryName(CountryName countryName) {
        this.countryName = countryName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ServiceProvider getServiceProvider() {
        return serviceProvider;
    }

    public void setServiceProvider(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}