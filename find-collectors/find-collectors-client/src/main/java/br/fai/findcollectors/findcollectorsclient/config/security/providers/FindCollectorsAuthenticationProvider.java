package br.fai.findcollectors.findcollectorsclient.config.security.providers;


import br.fai.findcollectors.entities.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class FindCollectorsAuthenticationProvider implements AuthenticationProvider {

//    @Autowired
//    PersonService personService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

//        Person person = personService.validateLogin(username, password);

//        if (person == null) {
//            return null;
//        }

        grantedAuthorities.add(new SimpleGrantedAuthority(""));

        return new UsernamePasswordAuthenticationToken(new Person(), password, grantedAuthorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
