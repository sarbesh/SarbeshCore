package com.sarbesh.core.security;

import com.sarbesh.core.config.JwtConfig;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RefreshScope
public class JwtHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtHelper.class);

    @Autowired
    private JwtConfig jwtConfig;

    public String extractUserName(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    public List<String> extractRole(String token){
        return extractClaim(token, this::getRole);
    }

    public Collection<? extends GrantedAuthority> extractGrantedAuthorities(String token){
        return extractRole(token).stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(jwtConfig.getSIGNING_KEY()).parseClaimsJws(token).getBody();
    }

    public boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(UserDetails userDetails){
        Map<String, Object> claims = new HashMap<>();
        claims.put(jwtConfig.getAUTHORITIES_KEY(),userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        LOGGER.debug("generated claims: {} for {}",claims,subject);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+(jwtConfig.getTOKEN_VALIDITY()*1000)))
                .signWith(SignatureAlgorithm.HS512,jwtConfig.getSIGNING_KEY()).compact();
    }

    public boolean validateToken(String token, UserDetails userDetails){
        String username = extractUserName(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public UsernamePasswordAuthenticationToken getAuthenticationToken(String token, Authentication existingAuth, UserDetails userDetails) {

        JwtParser jwtParser = Jwts.parser().setSigningKey(jwtConfig.getSIGNING_KEY());

        Jws<Claims> claimsJws = jwtParser.parseClaimsJws(token);

        Claims claims = claimsJws.getBody();

        Collection<? extends GrantedAuthority> authorities = getRole(claims)
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
    }

    private List<String> getRole(Claims claims) {
        List<String> collect = (List<String>) claims.get(jwtConfig.getAUTHORITIES_KEY());
        LOGGER.debug("#JwtHelper extracted clain: {}",collect);
        return collect;
    }
}
