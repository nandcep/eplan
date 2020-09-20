package com.eplan.user.util;

import io.jsonwebtoken.*;
import org.apache.commons.codec.binary.Base64;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
* @author  Adinandra Dharmasurya
* @version 1.0
* @since   2020-09-19
*/
@Component
public class UserUtility {

    @Value("${eplan.jwt.expiration}")
    private Integer eplanJwtExpiration;
    
    @Value("${eplan.jwt.secret}")
    private String eplanJwtSecret;

    private static final String REGEX_PWD = "(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,20}$";
    private static Pattern pattern;
    private static Matcher matcher;

    public Boolean isValidPasswordStrength(String password){
        pattern = Pattern.compile(REGEX_PWD);
        matcher = pattern.matcher(password);
        matcher = pattern.matcher(password);
        if(!matcher.matches()
                || password.toLowerCase().contains("password")
                || password.toLowerCase().contains("p@ssw0rd")
                || password.toLowerCase().contains("admin")
                || password.toLowerCase().contains("administrator"))
        {
            return false;
        }
        return true;
    }

    public String getNewJwt(Authentication authentication) {
    	UserPrinciple userPrincipal = (UserPrinciple) authentication.getPrincipal();
    	Base64 base64 = new Base64();
    	String username = new String(base64.encode(userPrincipal.getUsername().getBytes()));
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(new Date())
            .setExpiration(new Date((new Date()).getTime() + eplanJwtExpiration * 1000))
            .signWith(SignatureAlgorithm.HS512, eplanJwtSecret)
            .compact();
    }

    public Jws<Claims> getJwtClaim(String token){
        if(StringUtils.isEmpty(token)){
            return null;
        }
        return Jwts.parser().setSigningKey(eplanJwtSecret).parseClaimsJws(token);
    }
    
}