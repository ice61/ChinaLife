package com.chinalife.pan.interceptor;

import com.chinalife.common.utils.CookieUtils;
import com.chinalife.manauth.entity.ClerkAuthInfo;
import com.chinalife.manauth.entity.ClerkInfo;
import com.chinalife.manauth.utils.JwtUtils;
import com.chinalife.pan.config.JwtProperties;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class PanFileInterceptor implements HandlerInterceptor {

    private JwtProperties prop;

    private StringRedisTemplate redisTemplate;

    private static final ThreadLocal<String> idTl = new ThreadLocal<>();

    private static final ThreadLocal<Boolean> authTl = new ThreadLocal<>();

    private static final String KEY_PREFIX_AUTH = "clerk:login:auth";

    private static final String TARGET_AUTH = "/api/pan/manage";

    public PanFileInterceptor(JwtProperties prop, StringRedisTemplate redisTemplate) {
        this.prop = prop;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取cookie中相应的cookie
        String token = CookieUtils.getCookieValue(request, prop.getCookieName());
        // 解析token
        try {
            ClerkInfo clerkInfo = JwtUtils.getInfoFromToken(token, prop.getPublicKey());
            String id = clerkInfo.getId();
            idTl.set(id);
            String authToken = redisTemplate.opsForValue().get(KEY_PREFIX_AUTH + id);
            ClerkAuthInfo authInfo = JwtUtils.getAuthFromToken(authToken, prop.getPublicKey());
            List<String> auths = authInfo.getAuths();
            Boolean ans = false;
            for (String auth : auths) {
                if(TARGET_AUTH.equals(auth)) {
                    ans = true;
                    break;
                }
            }
            authTl.set(ans);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        idTl.remove();
        authTl.remove();
    }

    public static String getId() {
        return idTl.get();
    }

    public static Boolean getAuth() {
        return authTl.get();
    }

}
