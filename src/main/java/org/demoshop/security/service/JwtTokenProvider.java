package org.demoshop.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.demoshop.exceptions.InvalidJwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Service
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.lifetime}")
    private long jwtLifeTime;

    public String createToken(String userName){
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtLifeTime);

        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());


        return Jwts.builder()
                .setSubject(userName)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key,SignatureAlgorithm.HS256)
                .compact();
    }


    public boolean validateToken(String token){
        try {
            byte[] keyByte = jwtSecret.getBytes(StandardCharsets.UTF_8);
            Key key = new SecretKeySpec(keyByte,SignatureAlgorithm.HS256.getJcaName());

            Jwts
                    .parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e){
            throw new InvalidJwtException("Invalid JWT token: " + e.getMessage());
        }
    }

    public String getUserNameFromJWT(String token){

        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

}
