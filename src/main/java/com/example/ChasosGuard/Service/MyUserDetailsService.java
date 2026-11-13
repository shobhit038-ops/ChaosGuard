package com.example.ChasosGuard.Service;

import com.example.ChasosGuard.Model.User;
import com.example.ChasosGuard.Model.UserPrincipal;
import com.example.ChasosGuard.Repositry.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        User user = userRepo.findByName(name);
        if(user==null){
            System.out.println("user 404");
            throw new UsernameNotFoundException("user 404");
        }
        return new UserPrincipal(user);
    }
}
