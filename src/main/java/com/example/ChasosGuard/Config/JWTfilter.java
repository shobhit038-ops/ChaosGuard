//package com.example.ChasosGuard.Config;
//import com.example.ChasosGuard.Service.JWTservice;
//import com.example.ChasosGuard.Service.MyUserDetailsService;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationContext;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Component
//public class JWTfilter extends OncePerRequestFilter {
//    @Autowired
//    JWTservice jwtService;
//    @Autowired
//    ApplicationContext context;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        String authheader = request.getHeader("Authorization");
//        String token = null;
//        String username = null;
//
//        if(authheader!=null && authheader.startsWith("Bearer ")){
//            token = authheader.substring(7);
//            username = jwtService.extractUserName(token);
//        }
//
//        if(username!=null && SecurityContextHolder.getContext().getAuthentication()==null){
//            UserDetails userDetails = context.getBean(MyUserDetailsService.class).loadUserByUsername(username);
//            if(jwtService.validateToken(token,userDetails)){
//                UsernamePasswordAuthenticationToken authtoken =
//                        new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
//                authtoken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                SecurityContextHolder.getContext().setAuthentication(authtoken);
//            }
//        }
//        filterChain.doFilter(request,response);
//    }
//}

package com.example.ChasosGuard.Config;

import com.example.ChasosGuard.Service.JWTservice;
import com.example.ChasosGuard.Service.MyUserDetailsService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTfilter extends OncePerRequestFilter {

    @Autowired
    private JWTservice jwtService;

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            String username = jwtService.extractUserName(token);

            if (username != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(username);

                if (jwtService.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    SecurityContextHolder.getContext()
                            .setAuthentication(authToken);
                }
            }

        } catch (JwtException | IllegalArgumentException |
                 UsernameNotFoundException ex) {
            SecurityContextHolder.clearContext();
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid or expired JWT token"
            );
            return;
        }

        filterChain.doFilter(request, response);
    }
}
