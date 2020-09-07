package com.chinalife.insurance.interceptor;

import com.chinalife.common.utils.CookieUtils;
import com.chinalife.insurance.config.JwtProperties;
import com.chinalife.manauth.entity.ClerkInfo;
import com.chinalife.manauth.utils.JwtUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ClerkInterceptor implements HandlerInterceptor {

    private JwtProperties prop;

    private static final ThreadLocal<ClerkInfo> tl = new ThreadLocal<>();

    public ClerkInterceptor(JwtProperties prop) {
        this.prop = prop;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取cookie中相应的cookie
        String token = CookieUtils.getCookieValue(request, prop.getCookieName());
        // 解析token
        try {
            ClerkInfo clerkInfo = JwtUtils.getInfoFromToken(token, prop.getPublicKey());
            tl.set(clerkInfo);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        tl.remove();
    }

    public static ClerkInfo getClerkInfo() {
        return tl.get();
    }
}
