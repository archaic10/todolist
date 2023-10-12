package br.com.vitornunes.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.vitornunes.todolist.user.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {
    @Autowired
    IUserRepository userRepository;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {        
        var authorization = request.getHeader("Authorization");
        
        var userEncoded = authorization.substring("Basic".length()).trim();
        byte[] authDecoded = Base64.getDecoder().decode(userEncoded);
        var authString = new String(authDecoded);
        String[] credentials = authString.split(":");
        String userName = credentials[0];
        String password = credentials[1];
        var user = this.userRepository.findByUserName(userName);
        if(user == null){
            response.sendError(401);
        }
        
        var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
        
        if(passwordVerify.verified){
            filterChain.doFilter(request, response);
        }
    }

    
    
}
