package com.chinalife.gateway.filters;

import com.chinalife.common.enums.ExceptionEnum;
import com.chinalife.common.exception.ClException;
import com.chinalife.common.utils.CookieUtils;
import com.chinalife.gateway.config.FilterProperties;
import com.chinalife.gateway.config.JwtProperties;
import com.chinalife.manauth.entity.ClerkAuthInfo;
import com.chinalife.manauth.entity.ClerkInfo;
import com.chinalife.manauth.utils.JwtUtils;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Component
@EnableConfigurationProperties({JwtProperties.class, FilterProperties.class})
public class AuthFilter extends ZuulFilter {

    private static final String KEY_PREFIX_AUTH = "clerk:login:auth";

    @Autowired
    private JwtProperties prop;

    @Autowired
    private FilterProperties filterProp;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.PRE_DECORATION_FILTER_ORDER - 1;
    }

    @Override
    public boolean shouldFilter() {
        //获取上下文
        RequestContext currentContext = RequestContext.getCurrentContext();
        //获取request
        HttpServletRequest request = currentContext.getRequest();
        //获取url路径
        String path = request.getRequestURI();
        boolean allow = isAllowPath(path,filterProp.getAllowPaths());
        return !allow;
    }

    private boolean isAllowPath(String path, List<String> allowPahts) {
        for (String allowPath : allowPahts) {
            if (path.startsWith(allowPath)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletRequest request = currentContext.getRequest();
        String infoToken = CookieUtils.getCookieValue(request, prop.getCookieName());
        try {
            ClerkInfo info = JwtUtils.getInfoFromToken(infoToken, prop.getPublicKey());
            String clerkId = info.getId();
            String key = KEY_PREFIX_AUTH + clerkId;
            String authToken = redisTemplate.opsForValue().get(key);
            ClerkAuthInfo auth = JwtUtils.getAuthFromToken(authToken, prop.getPublicKey());
            if (!StringUtils.equals(auth.getId(),clerkId)) {
                throw new ClException(ExceptionEnum.UNAUTHORIZED);
            }
            String path = request.getRequestURI();
            if (!isAllowPath(path,auth.getAuths())) {
                throw new ClException(ExceptionEnum.UNAUTHORIZED);
            }
        } catch (Exception e) {
            // 未登陆或无权限
            currentContext.setSendZuulResponse(false);
            currentContext.setResponseStatusCode(HttpStatus.FORBIDDEN.value());
        }
        return null;
    }
}
