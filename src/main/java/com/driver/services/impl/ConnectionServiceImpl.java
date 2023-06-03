package com.driver.services.impl;

import com.driver.Exceptions.AlreadyConnectedException;
import com.driver.Exceptions.AlreadyDisconnectedException;
import com.driver.Exceptions.ConnectionFailException;
import com.driver.Exceptions.UnableToConnectException;
import com.driver.model.*;
import com.driver.repository.ConnectionRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ConnectionServiceImpl implements ConnectionService {
    @Autowired
    UserRepository userRepository2;
    @Autowired
    ServiceProviderRepository serviceProviderRepository2;
    @Autowired
    ConnectionRepository connectionRepository2;

    @Override
    public User connect(int userId, String countryName) throws Exception{
        //Connect the user to a vpn by considering the following priority order.
        //1. If the user is already connected to any service provider, throw "Already connected" exception.
        //2. Else if the countryName corresponds to the original country of the user, do nothing.
        // This means that the user wants to connect to its original country, for which we do not require a connection.
        // Thus, return the user as it is.
        //3. Else, the user should be subscribed under a serviceProvider having option to connect to the given country.
        //If the connection can not be made (As user does not have a serviceProvider or serviceProvider does not have given
        // country, throw "Unable to connect" exception.
        //Else, establish the connection where the maskedIp is "updatedCountryCode.serviceProviderId.userId" and
        // return the updated user. If multiple service providers allow you to connect to the country,
        // use the service provider having smallest id.
        countryName=countryName.toUpperCase();

        User user =userRepository2.findById(userId).get();
        //1. If the user is already connected to any service provider, throw "Already connected" exception.
        if(user.isConnected()){
            throw new AlreadyConnectedException("Already connected");
        }
        Country country=user.getCountry();
        // 2. Else if the countryName corresponds to the original country of the user, do nothing.
        // This means that the user wants to connect to its original country, for which we do not require a connection.
        // Thus, return the user as it is.
        if(country.getCountryName().toString().equals(countryName)){
            return user;
        }
        //3. Else, the user should be subscribed under a serviceProvider having option to connect to the given country.
        //If the connection can not be made (As user does not have a serviceProvider or serviceProvider does not have given
        // country, throw "Unable to connect" exception.

        List<ServiceProvider> userServiceProviders=user.getServiceProviders();
        if(userServiceProviders==null||userServiceProviders.size()==0){
            throw new UnableToConnectException("Unable to connect");
        }
        boolean providerNotInCountry=false;
        ServiceProvider currentServiceProvider=null;
        for(ServiceProvider provider: userServiceProviders){
            for(Country providerCountry: provider.getCountries()){
                if(providerCountry.getCountryName().toString().equals(countryName)){
                    providerNotInCountry=true;
                    if(currentServiceProvider==null){
                        currentServiceProvider=provider;
                    }
                    else if (currentServiceProvider.getId()>provider.getId()){
                        currentServiceProvider=provider;
                    }
                }
            }
        }
        if(!providerNotInCountry){
            throw new UnableToConnectException("Unable to connect");
        }
        //Else, establish the connection where the maskedIp is "updatedCountryCode.serviceProviderId.userId" and
        // return the updated user. If multiple service providers allow you to connect to the country,
        // use the service provider having smallest id.
        Connection connection=new Connection();

        connection.setServiceProvider(currentServiceProvider);
        currentServiceProvider.getConnections().add(connection);

        ServiceProvider savedServiceProvider=serviceProviderRepository2.save(currentServiceProvider);
        Connection savedConnection=savedServiceProvider.getConnections().get(savedServiceProvider.getConnections().size()-1);

        user.setMaskedIp(country.getCode()+"."+savedServiceProvider.getId()+"."+userId);
        user.setConnected(true);
        savedConnection.setUser(user);
        user.getConnections().add(savedConnection);
        userRepository2.save(user);

        return user;

    }
    @Override
    public User disconnect(int userId) throws Exception {
        //If the given user was not connected to a vpn, throw "Already disconnected" exception.
        //Else, disconnect from vpn, make masked Ip as null, update relevant attributes and return updated user.

        User user=userRepository2.findById(userId).get();
        if(!user.isConnected()){
            throw new AlreadyDisconnectedException("Already disconnected");
        }
        user.setMaskedIp(null);
        user.setConnected(false);
        for(Connection connection: user.getConnections()){
            connection.setServiceProvider(null);
            connection.setUser(null);
            user.getConnections().remove(connection);
        }
        user.setConnections(new ArrayList<>());
        return userRepository2.save(user);
    }
    @Override
    public User communicate(int senderId, int receiverId) throws Exception {
        //Establish a connection between sender and receiver users
        //To communicate to the receiver, sender should be in the current country of the receiver.
        //If the receiver is connected to a vpn, his current country is the one he is connected to.

        //If the receiver is not connected to vpn, his current country is his original country.
        //The sender is initially not connected to any vpn. If the sender's original country does not match
        // receiver's current country,

        // we need to connect the sender to a suitable vpn. If there are multiple options,
        // connect using the service provider having smallest id

        //If the sender's original country matches receiver's current country, we do not need to do anything as they can
        // communicate. Return the sender as it is.

        //If communication can not be established due to any reason, throw "Cannot establish communication" exception

        User receiver=userRepository2.findById(receiverId).get();
        Country receiverCountry=receiver.getCountry();

        User sender=userRepository2.findById(senderId).get();
        Country senderCountry=sender.getCountry();

        if(!receiver.isConnected() && receiverCountry.getCountryName().equals(senderCountry.getCountryName())){
            return sender;
        }

        //The sender is initially not connected to any vpn. If the sender's original country does not match
        // receiver's current country,
        for(Connection connection: receiver.getConnections()){
            for(Country country: connection.getServiceProvider().getCountries()){
                if(senderCountry.getCountryName().equals(country.getCountryName())){
                    return sender;
                }
            }
        }
        // we need to connect the sender to a suitable vpn. If there are multiple options,
        // connect using the service provider having smallest id

        // here we cant get new connection because sender is not subscribed to any provider
        if(sender.getServiceProviders().size()==0){
            throw new ConnectionFailException("Cannot establish communication");
        }
        ServiceProvider commonProvider=null;
        boolean foundCommonProvider=false;
        for(ServiceProvider senderProvider: sender.getServiceProviders()){
            for(Connection receiverConnection: receiver.getConnections()){
                if(senderProvider.equals(receiverConnection.getServiceProvider())){
                    foundCommonProvider=true;
                    if(commonProvider==null){
                        commonProvider=senderProvider;
                    }
                    else if(commonProvider.getId()>senderProvider.getId()){
                        commonProvider=senderProvider;
                    }
                }
            }
        }
        if(!foundCommonProvider){
            throw new ConnectionFailException("Cannot establish communication");
        }

        Connection connection=new Connection();
        connection.setServiceProvider(commonProvider);
        commonProvider.getConnections().add(connection);
        ServiceProvider savedCommonProvider= serviceProviderRepository2.save(commonProvider);

        Connection savedConnection=savedCommonProvider.getConnections().get(savedCommonProvider.getConnections().size()-1);
        sender.getConnections().add(savedConnection);
        savedConnection.setUser(sender);
        userRepository2.save(sender);
        return sender;
    }
}
