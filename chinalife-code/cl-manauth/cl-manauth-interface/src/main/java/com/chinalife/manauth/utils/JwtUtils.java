package com.chinalife.manauth.utils;

import com.chinalife.common.utils.JsonUtils;
import com.chinalife.manauth.entity.ClerkAuthInfo;
import com.chinalife.manauth.entity.ClerkInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.joda.time.DateTime;

import java.security.PrivateKey;
import java.security.PublicKey;

public class JwtUtils {

    public static String generateInfoToken(ClerkInfo clerkInfo, PrivateKey privateKey, int expireMinutes) throws Exception{
        return Jwts.builder()
                .claim(JwtConstans.JWT_KEY_CLERK_INFO, JsonUtils.serialize(clerkInfo))
                .setExpiration(DateTime.now().plusMinutes(expireMinutes).toDate())
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .compact();
    }

    public static String generateInfoToken(ClerkInfo clerkInfo, byte[] privateKey, int expireMinutes) throws Exception {
        return Jwts.builder()
                .claim(JwtConstans.JWT_KEY_CLERK_INFO, JsonUtils.serialize(clerkInfo))
                .setExpiration(DateTime.now().plusMinutes(expireMinutes).toDate())
                .signWith(SignatureAlgorithm.RS256, RsaUtils.getPrivateKey(privateKey))
                .compact();
    }

    public static String generateAuthToken(ClerkAuthInfo ClerkAuthInfo, PrivateKey privateKey, int expireMinutes) throws Exception{
        return Jwts.builder()
                .claim(JwtConstans.JWT_KEY_CLERK_AUTH,JsonUtils.serialize(ClerkAuthInfo))
                .setExpiration(DateTime.now().plusMinutes(expireMinutes).toDate())
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .compact();
    }

    public static String generateAuthToken(ClerkAuthInfo ClerkAuthInfo, byte[] privateKey, int expireMinutes) throws Exception {
        return Jwts.builder()
                .claim(JwtConstans.JWT_KEY_CLERK_AUTH,ClerkAuthInfo)
                .setExpiration(DateTime.now().plusMinutes(expireMinutes).toDate())
                .signWith(SignatureAlgorithm.RS256, RsaUtils.getPrivateKey(privateKey))
                .compact();
    }


    public static Jws<Claims> parserToken(String token, PublicKey publicKey) {
        return Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token);
    }

    private static Jws<Claims> parserToken(String token, byte[] publicKey) throws Exception {
        return Jwts.parser().setSigningKey(RsaUtils.getPublicKey(publicKey))
                .parseClaimsJws(token);
    }

    public static ClerkInfo getInfoFromToken(String token, PublicKey publicKey) throws Exception{
        Jws<Claims> claimsJws = parserToken(token, publicKey);
        Claims body = claimsJws.getBody();
        String clerkInfoJson = ObjectUtils.toString(body.get(JwtConstans.JWT_KEY_CLERK_INFO));
        return JsonUtils.parse(clerkInfoJson, ClerkInfo.class);
    }

    public static ClerkInfo getInfoFromToken(String token, byte[] publicKey) throws Exception {
        Jws<Claims> claimsJws = parserToken(token, publicKey);
        Claims body = claimsJws.getBody();
        String clerkInfoJson = ObjectUtils.toString(body.get(JwtConstans.JWT_KEY_CLERK_INFO));
        return JsonUtils.parse(clerkInfoJson, ClerkInfo.class);
    }

    public static ClerkAuthInfo getAuthFromToken(String token, PublicKey publicKey) throws Exception{
        Jws<Claims> claimsJws = parserToken(token, publicKey);
        Claims body = claimsJws.getBody();
        String clerkAuthJson = ObjectUtils.toString(body.get(JwtConstans.JWT_KEY_CLERK_AUTH));
        return JsonUtils.parse(clerkAuthJson,ClerkAuthInfo.class);
    }

    public static ClerkAuthInfo getAuthFromToken(String token, byte[] publicKey) throws Exception {
        Jws<Claims> claimsJws = parserToken(token, publicKey);
        Claims body = claimsJws.getBody();
        String clerkAuthJson = ObjectUtils.toString(body.get(JwtConstans.JWT_KEY_CLERK_AUTH));
        return JsonUtils.parse(clerkAuthJson,ClerkAuthInfo.class);
    }
}
