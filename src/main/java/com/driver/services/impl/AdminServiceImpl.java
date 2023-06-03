package com.driver.services.impl;

import com.driver.Exceptions.CountryNotFoundException;
import com.driver.model.Admin;
import com.driver.model.Country;
import com.driver.model.CountryName;
import com.driver.model.ServiceProvider;
import com.driver.repository.AdminRepository;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    AdminRepository adminRepository1;

    @Autowired
    ServiceProviderRepository serviceProviderRepository1;

    @Autowired
    CountryRepository countryRepository1;

    @Override
    public Admin register(String username, String password) {
        //create an admin and return
        Admin admin=new Admin();
        admin.setUsername(username);
        admin.setPassword(password);
        adminRepository1.save(admin);
        return admin;
    }

    @Override
    public Admin addServiceProvider(int adminId, String providerName) {
        //add a serviceProvider under the admin and return updated admin
        ServiceProvider serviceProvider=new ServiceProvider();
        serviceProvider.setName(providerName);
        Admin admin=adminRepository1.findById(adminId).get();
        // making relation between admin and serviceProvider
        serviceProvider.setAdmin(admin);
        admin.getServiceProviders().add(serviceProvider);
        // saving the admin(parent)
        adminRepository1.save(admin);
        return admin;
    }

    @Override
    public ServiceProvider addCountry(int serviceProviderId, String countryName) throws Exception{
        //add a country under the serviceProvider and return respective service provider
        //country name would be a 3-character string out of ind, aus, usa, chi, jpn.
        // Each character can be in uppercase or lowercase. You should create a new Country object based on
        // the given country name and add it to the country list of the service provider.
        // Note that the user attribute of the country in this case would be null.
        //In case country name is not amongst the above mentioned strings, throw "Country not found" exception

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
//        List<Country> countries=countryRepository1.findAll();
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

        // find the ServiceProvider
        ServiceProvider serviceProvider=serviceProviderRepository1.findById(serviceProviderId).get();

        serviceProvider.getCountryList().add(country);
        country.setServiceProvider(serviceProvider);
        serviceProviderRepository1.save(serviceProvider);
        return serviceProvider;
    }
}
