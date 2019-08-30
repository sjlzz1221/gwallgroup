package org.gwallgroup.authentication.service;

import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.ConcurrentMap;

/**
 * @author jsen
 * @version 1.0
 * @date 2019/8/30 10:46 AM
 */
@Service
public class AuthenticationServiceManager {
    @Resource
    @Qualifier("AuthenticationServiceDefault")
    private AuthenticationService innerAuthenticationService;
    private static AuthenticationService authenticationService;
    @PostConstruct
    public void init() {
        AuthenticationServiceManager.authenticationService = this.innerAuthenticationService;
    }

    private static ConcurrentMap<String, AuthenticationService> pool = Maps.newConcurrentMap();
    public static void register(String serviceType, AuthenticationService authenticationService) {
        pool.putIfAbsent(serviceType, authenticationService);
    }
    public static AuthenticationService get(String key) {
        return pool.getOrDefault(key, authenticationService);
    }

}
