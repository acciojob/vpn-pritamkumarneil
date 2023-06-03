package com.driver.services.impl;

import com.driver.Exceptions.CountryNotFoundException;
import com.driver.model.Country;
import com.driver.model.CountryName;
import com.driver.model.ServiceProvider;
import com.driver.model.User;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository3;
    @Autowired
    ServiceProviderRepository serviceProviderRepository3;
    @Autowired
    CountryRepository countryRepository3;

    @Override
    public User register(String username, String password, String countryName) throws Exception{
        //create a user of given country. The originalIp of the user should be "countryCode.userId" and return the user.
        // Note that right now user is not connected and thus connected would be false and maskedIp would be null
        //Note that the userId is created automatically by the repository layer

        countryName=countryName.toUpperCase();
        boolean countryFound=false;
        CountryName countryName1=CountryName.IND;
        for(CountryName name: CountryName.values()){
            if(name.toString().equals(countryName)){
                countryFound=true;
                countryName1=name;
                break;
            }
        }
        if(!countryFound){
            throw new CountryNotFoundException("Country not found");
        }

        Country country=null;
//        List<Country> countries=countryRepository3.findAll();
//        for(Country country1: countries){
//            if(country1.getCountryName().equals(countryName1)){
//                country=country1;
//                break;
//            }
//        }
        if(country==null){
            country=new Country();
            country.setCountryName(countryName1);
            country.setCode(countryName1.toCode());
        }
        User user= new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setCountry(country);
        country.setUser(user);
        User savedUser=userRepository3.save(user);
        savedUser.setOriginalIp(country.getCode()+"."+savedUser.getId());

        userRepository3.save(savedUser);


        return savedUser;
    }

    @Override
    public User subscribe(Integer userId, Integer serviceProviderId) {
        //subscribe to the serviceProvider by adding it to the list of providers and return updated User
        ServiceProvider serviceProvider=serviceProviderRepository3.findById(serviceProviderId).get();
        User user= userRepository3.findById(userId).get();

        serviceProvider.getUsers().add(user);
        user.getServiceProviders().add(serviceProvider);

        serviceProviderRepository3.save(serviceProvider);

        return user;
    }
}
