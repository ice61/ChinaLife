package com.chinalife.manauth.service.impl;

import com.chinalife.auth.vo.Auth;
import com.chinalife.auth.vo.AuthGroup;
import com.chinalife.clerk.vo.SearchClerk;
import com.chinalife.common.enums.ExceptionEnum;
import com.chinalife.common.exception.ClException;
import com.chinalife.common.utils.CookieUtils;
import com.chinalife.manauth.client.AuthClient;
import com.chinalife.manauth.client.AuthGroupClient;
import com.chinalife.manauth.client.ClerkClient;
import com.chinalife.manauth.config.JwtProperties;
import com.chinalife.manauth.entity.ClerkAuthInfo;
import com.chinalife.manauth.entity.ClerkInfo;
import com.chinalife.manauth.mapper.ClerkAuthMapper;
import com.chinalife.manauth.service.ManauthService;
import com.chinalife.manauth.utils.JwtUtils;
import com.chinalife.manauth.po.ClerkAuth;
import com.chinalife.manauth.vo.ClerkAuthPageResult;
import com.chinalife.manauth.vo.GroupWithAuth;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@EnableConfigurationProperties(JwtProperties.class)
@Slf4j
public class ManauthServiceImpl implements ManauthService {

    @Autowired
    private JwtProperties prop;

    @Autowired
    private ClerkClient clerkClient;

    @Autowired
    private ClerkAuthMapper clerkAuthMapper;

    @Autowired
    private AuthClient authClient;

    @Autowired
    private AuthGroupClient authGroupClient;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX_AUTH = "clerk:login:auth";
    private static final String KEY_PREFIX_INFO = "clerk:login:info";

    private static final String CLERK_AUTH_HAVED = "hava";
    private static final String CLERK_AUTH_WITHOUT = "without";

    @Override
    public String login(String id, String password, String code) {
        SearchClerk searchClerk = clerkClient.query(id, password, code);
        if (searchClerk == null) {
            throw new ClException(ExceptionEnum.LOGIN_FAIL);
        }
        if (searchClerk.getId() == null) {
            throw new ClException(ExceptionEnum.INVALID_VERIFY_CODE);
        }
        ClerkAuthInfo clerkAuthInfo = new ClerkAuthInfo();
        ClerkInfo clerkInfo = new ClerkInfo();
        processAuthAndInfoBySearchClerk(clerkInfo, clerkAuthInfo, searchClerk);
        // 生成token
        try {
            String authToken = JwtUtils.generateAuthToken(clerkAuthInfo, prop.getPrivateKey(), prop.getExpire());
            String infoToken = JwtUtils.generateInfoToken(clerkInfo, prop.getPrivateKey(), prop.getExpire());
            String clerkId = searchClerk.getId();
            redisTemplate.opsForValue().set(KEY_PREFIX_AUTH + clerkId, authToken, 30, TimeUnit.MINUTES);
            redisTemplate.opsForValue().set(KEY_PREFIX_INFO + clerkId, infoToken, 30, TimeUnit.MINUTES);
            return infoToken;
        } catch (Exception e) {
            log.error("[授权中心] 生成token失败");
            throw new ClException(ExceptionEnum.GENERTE_TOKEN_FAIL);
        }

    }

    @Override
    public void logout(String token, HttpServletRequest request, HttpServletResponse response) {
        try {
            ClerkInfo clerkInfo = JwtUtils.getInfoFromToken(token, prop.getPublicKey());
            String clerkId = clerkInfo.getId();
            redisTemplate.delete(KEY_PREFIX_AUTH + clerkId);
            redisTemplate.delete(KEY_PREFIX_INFO + clerkId);
            CookieUtils.deleteCookie(request, response, prop.getCookieName());
        } catch (Exception e) {
            throw new ClException(ExceptionEnum.UNAUTHORIZED);
        }
    }

    @Override
    public ClerkInfo verify(String token, HttpServletResponse response, HttpServletRequest request) {
        if (StringUtils.isBlank(token)) {
            throw new ClException(ExceptionEnum.UNAUTHORIZED);
        }
        try {
            ClerkInfo clerkInfo = JwtUtils.getInfoFromToken(token, prop.getPublicKey());
            String cacheInfoToken = redisTemplate.opsForValue().get(KEY_PREFIX_INFO + clerkInfo.getId());
            // 如果token相同则证明权限模块未发生改变
            if (!StringUtils.equals(token, cacheInfoToken)) {
                clerkInfo = JwtUtils.getInfoFromToken(cacheInfoToken, prop.getPublicKey());
            }
            String infoToken = JwtUtils.generateInfoToken(clerkInfo, prop.getPrivateKey(), prop.getExpire());
            CookieUtils.setCookie(request, response, prop.getCookieName(), infoToken);
            redisTemplate.opsForValue().set(KEY_PREFIX_INFO + clerkInfo.getId(), infoToken, 30, TimeUnit.MINUTES);
            String authToken = redisTemplate.opsForValue().get(KEY_PREFIX_AUTH + clerkInfo.getId());
            ClerkAuthInfo auth = JwtUtils.getAuthFromToken(authToken, prop.getPublicKey());
            String newAuthToken = JwtUtils.generateAuthToken(auth, prop.getPrivateKey(), prop.getExpire());
            redisTemplate.opsForValue().set(KEY_PREFIX_AUTH + clerkInfo.getId(), newAuthToken, 30, TimeUnit.MINUTES);
            return clerkInfo;
        } catch (Exception e) {
            throw new ClException(ExceptionEnum.UNAUTHORIZED);
        }
    }

    @Override
    public void addAuthToClerk(String id, List<Integer> ids) {
        ClerkAuth clerkAuth = new ClerkAuth();
        Date date = new Date();
        clerkAuth.setClerkId(id);
        clerkAuth.setTime(date);
        List<Auth> auths = authClient.findAuthByIds(ids);
        for (Auth auth : auths) {
            clerkAuth.setId(null);
            clerkAuth.setAuthAlias(auth.getAlias());
            clerkAuth.setAuthId(auth.getId());
            clerkAuthMapper.insert(clerkAuth);
        }
        updateToken(id);
    }

    @Override
    public void delAuthFromClerk(String id, List<Integer> ids) {
        ClerkAuth clerkAuth = new ClerkAuth();
        clerkAuth.setClerkId(id);
        for (Integer authId : ids) {
            clerkAuth.setAuthId(authId);
            clerkAuthMapper.delete(clerkAuth);
        }
        updateToken(id);
    }

    @Override
    public ClerkAuthPageResult findAuthPage(Integer page, Integer rows, String sortBy, Boolean desc, String key, Boolean time) {

        PageHelper.startPage(page, rows);

        Example example = new Example(ClerkAuth.class);
        if (StringUtils.isNotBlank(key)) {
            if (time == false) {
                example.createCriteria().orLike("authAlias", "%" + key + "%")
                        .orEqualTo("clerkId", key);
            } else {
                example.createCriteria().orLike("time", "%" + key + "%");
            }
        }
        if (StringUtils.isNotBlank(sortBy)) {
            String orderClause = sortBy + (desc ? " DESC" : " ASC");
            example.setOrderByClause(orderClause);
        }
        List<ClerkAuth> clerkAuths = clerkAuthMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(clerkAuths)) {
            throw new ClException(ExceptionEnum.CLERK_AUTH_NOT_FOUND);
        }
        PageInfo<ClerkAuth> info = new PageInfo<>(clerkAuths);
        ClerkAuthPageResult result = new ClerkAuthPageResult();
        result.setData(info.getList());
        result.setTotal(info.getTotal());
        result.setTotalPage(info.getPages());
        return result;
    }

    @Override
    public Map<String, List<GroupWithAuth>> findHaveAndWithout(String id) {
        List<Integer> authIdsHad = new LinkedList<>();
        ClerkAuth example = new ClerkAuth();
        example.setClerkId(id);
        authIdsHad = clerkAuthMapper.select(example).stream().map(c -> c.getAuthId()).collect(Collectors.toList());
        LinkedList<Auth> authHad = new LinkedList<>();
        List<Auth> allAuths = authClient.findAll();
        for (Auth auth : allAuths) {
            if (authIdsHad.contains(auth.getId())) {
                authHad.add(auth);
            }
        }
        allAuths.removeAll(authHad);


        // 得到已有权限的权限组id
        Set<Integer> authGroupHadIds = authHad.stream().map(a -> a.getGroupId()).collect(Collectors.toSet());

        // 得到未有权限的权限组id
        Set<Integer> authGroupWithOutIds = allAuths.stream().map(a -> a.getGroupId()).collect(Collectors.toSet());

        // 得到已有权限组信息
        List<Integer> groupHadIds = new ArrayList<>(authGroupHadIds);
        LinkedList<GroupWithAuth> had = new LinkedList<>();
        if (!CollectionUtils.isEmpty(groupHadIds)) {
            List<AuthGroup> authGroupHad = authGroupClient.findAuthGroupByIds(groupHadIds);
            for (AuthGroup authGroup : authGroupHad) {
                GroupWithAuth groupWithAuth = getGroupWithAuth(authGroup, authHad);
                had.add(groupWithAuth);
            }
        }

        // 得到未有权限组信息
        LinkedList<GroupWithAuth> without = new LinkedList<>();
        if (!CollectionUtils.isEmpty(authGroupWithOutIds)) {
            List<AuthGroup> authGroupWithout = authGroupClient.findAuthGroupByIds(new ArrayList<>(authGroupWithOutIds));
            for (AuthGroup authGroup : authGroupWithout) {
                GroupWithAuth groupWithAuth = getGroupWithAuth(authGroup, allAuths);
                without.add(groupWithAuth);
            }
        }


        HashMap<String, List<GroupWithAuth>> map = new HashMap<>();
        map.put(CLERK_AUTH_HAVED, had);
        map.put(CLERK_AUTH_WITHOUT, without);
        return map;
    }

    private GroupWithAuth getGroupWithAuth(AuthGroup authGroup, List<Auth> authHad) {
        Integer authGroupId = authGroup.getId();
        GroupWithAuth groupWithAuth = new GroupWithAuth();
        groupWithAuth.setAlias(authGroup.getAlias());
        groupWithAuth.setId(authGroup.getId());
        // 准备获取groupwithauth中的成员变量
        LinkedList<Auth> auths = new LinkedList<>();
        for (Auth auth : authHad) {
            if (auth.getGroupId() == authGroupId) {
                auths.add(auth);
            }
        }
        groupWithAuth.setAuths(auths);
        return groupWithAuth;
    }

    @Override
    public void delAuthFromClerks(Integer id) {
        ClerkAuth clerkAuth = new ClerkAuth();
        clerkAuth.setAuthId(id);
        List<String> clerkIds = clerkAuthMapper.select(clerkAuth).stream().map(c -> c.getClerkId()).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(clerkIds)) {
            clerkAuthMapper.delete(clerkAuth);
            for (String clerkId : clerkIds) {
                updateToken(clerkId);
            }
        }
    }

    @Override
    public List<ClerkAuth> findAuthByClerkId(String token) {
        try {
            ClerkInfo clerkInfo = JwtUtils.getInfoFromToken(token, prop.getPublicKey());
            ClerkAuth clerkAuth = new ClerkAuth();
            clerkAuth.setClerkId(clerkInfo.getId());
            List<ClerkAuth> result = clerkAuthMapper.select(clerkAuth);
            if (CollectionUtils.isEmpty(result)) {
                throw new ClException(ExceptionEnum.AUTH_NOT_FOUND);
            }
            return result;
        } catch (Exception e) {
            throw new ClException(ExceptionEnum.UNAUTHORIZED);
        }
    }


    public void updateToken(String id) {
        String infoToken = redisTemplate.opsForValue().get(KEY_PREFIX_INFO + id);
        String authToken = redisTemplate.opsForValue().get(KEY_PREFIX_AUTH + id);
        if (StringUtils.isBlank(infoToken) || StringUtils.isBlank(authToken)) {
            return;
        }
        SearchClerk searchClerk = clerkClient.findClerkById(id);
        ClerkInfo clerkInfo = new ClerkInfo();
        ClerkAuthInfo clerkAuthInfo = new ClerkAuthInfo();
        processAuthAndInfoBySearchClerk(clerkInfo, clerkAuthInfo, searchClerk);
        try {
            infoToken = JwtUtils.generateInfoToken(clerkInfo, prop.getPrivateKey(), prop.getExpire());
            redisTemplate.opsForValue().set(KEY_PREFIX_INFO + id, infoToken, 30, TimeUnit.MINUTES);
            authToken = JwtUtils.generateAuthToken(clerkAuthInfo, prop.getPrivateKey(), prop.getExpire());
            redisTemplate.opsForValue().set(KEY_PREFIX_AUTH + id, authToken, 30, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("[授权中心] 生成token失败");
            throw new ClException(ExceptionEnum.GENERTE_TOKEN_FAIL);
        }

    }

    @Override
    public void delToken(String clerkId) {
        String authToken = redisTemplate.opsForValue().get(KEY_PREFIX_AUTH + clerkId);
        if (StringUtils.isNotBlank(authToken)) {
            redisTemplate.delete(KEY_PREFIX_AUTH + clerkId);
        }
        String infoToken = redisTemplate.opsForValue().get(KEY_PREFIX_INFO + clerkId);
        if (StringUtils.isNotBlank(infoToken)) {
            redisTemplate.delete(KEY_PREFIX_INFO + clerkId);
        }

    }

    @Override
    public void updateClerkAuth(Integer authId) {
        ClerkAuth clerkAuth = new ClerkAuth();
        clerkAuth.setAuthId(authId);
        List<ClerkAuth> clerkAuths = clerkAuthMapper.select(clerkAuth);
        Auth auth = authClient.findAuthById(authId);
        String alias = auth.getAlias();
        for (ClerkAuth clerkAuth1 : clerkAuths) {
            clerkAuth1.setAuthAlias(alias);
            clerkAuthMapper.updateByPrimaryKey(clerkAuth1);
            updateToken(clerkAuth1.getClerkId());
        }
    }

    private void processAuthAndInfoBySearchClerk(ClerkInfo clerkInfo, ClerkAuthInfo clerkAuthInfo, SearchClerk searchClerk) {
        clerkInfo.setId(searchClerk.getId());
        clerkInfo.setInstiution(searchClerk.getInstiution());
        clerkInfo.setName(searchClerk.getName());
        clerkInfo.setImage(searchClerk.getImage());
        clerkInfo.setBirthday(searchClerk.getBirthday());
        clerkInfo.setCreated(searchClerk.getCreated());
        clerkInfo.setUpdated(searchClerk.getUpdated());
        clerkInfo.setPhone(searchClerk.getPhone());
        clerkAuthInfo.setId(searchClerk.getId());
        // 创建查找实例去搜索权限
        ClerkAuth clerkAuth = new ClerkAuth();
        clerkAuth.setClerkId(searchClerk.getId());
        List<ClerkAuth> clerkAuths = clerkAuthMapper.select(clerkAuth);
        // 如果当前用户无任何权限
        if (CollectionUtils.isEmpty(clerkAuths)) {
            clerkInfo.setAuthModel(new LinkedList<>());
            clerkAuthInfo.setAuths(new LinkedList<>());
        } else {
            List<Integer> authIds = clerkAuths.stream().map(a -> a.getAuthId()).collect(Collectors.toList());
            List<Auth> auths = authClient.findAuthByIds(authIds);
            List<String> authUrls = auths.stream().map(a -> a.getUrl()).collect(Collectors.toList());
            clerkAuthInfo.setAuths(authUrls);
            Set<Integer> authModels = auths.stream().map(a -> a.getGroupId()).collect(Collectors.toSet());
            //clerkInfo.setAuthModel(authModels);
            List<AuthGroup> authGroups = authGroupClient.findAuthGroupByIds(new LinkedList<>(authModels));
            clerkInfo.setAuthModel(authGroups);

        }
    }


}
