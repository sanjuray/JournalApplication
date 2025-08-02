package net.engineeringdigest.journalApp.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWTUtil {

    private String SECRET_KEY = "luv#Illusion^Synonmous+1987$pops";

    private SecretKey getSigningKey(){
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    private String createToken(Map<String, Object> claims, String userName){
        return Jwts.builder()
                .claims(claims)
                .subject(userName)
                .header().empty().add("typ","JWT")
                .and()
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 5 * 60 * 1000))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateToken(String userName){
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userName);
    }

    public String extractUserName(String token){
        return extractAllClaims(token).getSubject();
    }

    public Date extractExpiration(String token){
        return extractAllClaims(token).getExpiration();
    }

    private Claims extractAllClaims(String token){
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private String extractClaim(String jwt, String claim){
        return extractAllClaims(jwt).get(claim, String.class);
    }



    private boolean isTokenExpired(String jwt){
        return extractExpiration(jwt).before(new Date());
    }

    public boolean validateToken(String jwt, String userName){
        final String extractedUserName = extractUserName(jwt);
        return (extractedUserName.equals(userName) && !isTokenExpired(jwt));
    }

}
