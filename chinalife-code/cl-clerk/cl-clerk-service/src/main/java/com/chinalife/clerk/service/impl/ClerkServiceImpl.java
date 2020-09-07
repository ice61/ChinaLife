package com.chinalife.clerk.service.impl;

import com.chinalife.clerk.config.JwtProperties;
import com.chinalife.clerk.mapper.ClerkMapper;
import com.chinalife.clerk.po.Clerk;
import com.chinalife.clerk.service.ClerkService;
import com.chinalife.clerk.service.ScoreService;
import com.chinalife.clerk.utils.CodecUtils;
import com.chinalife.clerk.utils.ImageCode;
import com.chinalife.clerk.vo.SearchClerk;
import com.chinalife.clerk.vo.SimClerk;
import com.chinalife.common.enums.ExceptionEnum;
import com.chinalife.common.exception.ClException;
import com.chinalife.common.utils.NumberUtils;
import com.chinalife.manauth.entity.ClerkInfo;
import com.chinalife.manauth.utils.JwtUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@Service
@EnableConfigurationProperties(JwtProperties.class)
public class ClerkServiceImpl implements ClerkService {

    @Autowired
    private ScoreService scoreService;

    @Autowired
    private ClerkMapper clerkMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private JwtProperties prop;

    private static final String KEY_PREFIX_REGISTER = "clerk:register:verify:phone";
    private static final String KEY_PREFIX_LOGIN = "clerk:login:verify:id";
    private static final String KEY_PREFIX_FINDPASSWORD = "clerk:findPassword:verify:phone";

    // 返回false证明已经注册过
    @Override
    public Boolean checkData(String id) {
        Clerk clerk = new Clerk();
        clerk.setId(id);
        return clerkMapper.selectCount(clerk) == 0;
    }

    @Override
    public void sendCode(String phone) {
        //生成redis中的key
        String key = KEY_PREFIX_REGISTER + phone;
        // 借助工具类生成验证码
        String code = NumberUtils.generateCode(6);
        // 创建一个Map将电话号码和生成的验证码通过消息队列发出
        HashMap<String, String> msg = new HashMap<>();
        msg.put("phone", phone);
        msg.put("code", code);
        //通过消息队列发出
        // 依次为交换机，口令，要发送的信息
        amqpTemplate.convertAndSend("cl.sms.exchange", "sms.verify.code", msg);

        //将验证码保存在redis中并设置有效时间为5分钟
        redisTemplate.opsForValue().set(key, code, 5, TimeUnit.MINUTES);

    }

    @Override
    public void register(SimClerk simClerk, String code) {
        String cacheCode = redisTemplate.opsForValue().get(KEY_PREFIX_REGISTER + simClerk.getPhone());
        if (StringUtils.isBlank(cacheCode)) {
            throw new ClException(ExceptionEnum.OVERDUE_VERIFY_CODE);
        }
        if (!StringUtils.equals(cacheCode, code)) {
            throw new ClException(ExceptionEnum.INVALID_VERIFY_CODE);
        }
        if (!checkData(simClerk.getId())) {
            throw new ClException(ExceptionEnum.INVALID_CLERK_ID);
        }
        redisTemplate.delete(KEY_PREFIX_REGISTER + simClerk.getPhone());

        Clerk clerk = new Clerk();
        clerk.setId(simClerk.getId());
        clerk.setInstiution(simClerk.getInstiution());
        clerk.setName(simClerk.getName());
        clerk.setSex(simClerk.getSex());
        clerk.setBirthday(simClerk.getBirthday());
        clerk.setPhone(simClerk.getPhone());
        clerk.setImage(simClerk.getImage());
        String salt = CodecUtils.generateSalt();
        String password = CodecUtils.md5Hex(simClerk.getPassword(), salt);
        clerk.setSalt(salt);
        clerk.setPassword(password);
        Date date = new Date();
        clerk.setUpdated(date);
        clerk.setCreated(date);
        clerkMapper.insert(clerk);
        scoreService.insert(clerk.getId());
        amqpTemplate.convertAndSend("cl.clerk.exchange", "clerk.insert", clerk.getId());
        amqpTemplate.convertAndSend("cl.pan.exchange", "clerk.register", clerk.getId());
    }

    @Override
    public SearchClerk findClerkById(String id) {
        Clerk clerk = new Clerk();
        clerk.setId(id);
        Clerk result = clerkMapper.selectOne(clerk);
        if (result == null) {
            throw new ClException(ExceptionEnum.FIND_CLERK_FAIL);
        }
        return getSearchClerk(result);
    }

    private SearchClerk getSearchClerk(Clerk clerk) {
        SearchClerk searchClerk = new SearchClerk();
        searchClerk.setId(clerk.getId());
        searchClerk.setName(clerk.getName());
        searchClerk.setSex(clerk.getSex());
        searchClerk.setInstiution(clerk.getInstiution());
        searchClerk.setBirthday(clerk.getBirthday());
        searchClerk.setPhone(clerk.getPhone());
        searchClerk.setImage(clerk.getImage());
        searchClerk.setCreated(clerk.getCreated());
        searchClerk.setUpdated(clerk.getUpdated());
        return searchClerk;
    }

    @Override
    public void deleteClerkById(String id) {
        Clerk clerk = clerkMapper.selectByPrimaryKey(id);
        if (clerk == null) {
            throw new ClException(ExceptionEnum.FIND_CLERK_FAIL);
        }
        int i = clerkMapper.deleteByPrimaryKey(id);
        if (i != 1) {
            throw new ClException(ExceptionEnum.DELETE_CLERK_ERROR);
        }
        scoreService.delete(id);
        amqpTemplate.convertAndSend("cl.clerk.exchange", "clerk.delete", id);
        amqpTemplate.convertAndSend("cl.pan.exchange","clerk.del",id);
    }

    @Override
    public BufferedImage imageCode(String id) {
        if (StringUtils.isBlank(id)) {
            throw new ClException(ExceptionEnum.NO_CLERK_ID);
        }
        ImageCode imageCode = new ImageCode();
        BufferedImage image = imageCode.getImage();
        String code = imageCode.getText();
        String key = KEY_PREFIX_LOGIN + id;
        redisTemplate.opsForValue().set(key, code, 2, TimeUnit.MINUTES);
        return image;
    }

    @Override
    public SearchClerk query(String id, String password, String code) {
        SearchClerk searchClerk = new SearchClerk();
        String cacheCode = redisTemplate.opsForValue().get(KEY_PREFIX_LOGIN + id);
        if (StringUtils.isBlank(cacheCode)) {
            return searchClerk;
            //throw new ClException(ExceptionEnum.OVERDUE_VERIFY_CODE);
        }
        if (!StringUtils.equalsIgnoreCase(cacheCode, code)) {
            return searchClerk;
            //throw new ClException(ExceptionEnum.INVALID_VERIFY_CODE);
        }
        Clerk clerk = clerkMapper.selectByPrimaryKey(id);
        if (clerk == null) {
            return null;
        }
        password = CodecUtils.md5Hex(password, clerk.getSalt());
        if (!StringUtils.equals(password, clerk.getPassword())) {
            return null;
        }
        redisTemplate.delete(KEY_PREFIX_LOGIN + id);
        return getSearchClerk(clerk);

    }

    @Override
    public void updateClerk(SimClerk simClerk, String token) {
        if (!StringUtils.isBlank(token)) {
            try {
                ClerkInfo info = JwtUtils.getInfoFromToken(token, prop.getPublicKey());
                if (!StringUtils.equals(simClerk.getId(), info.getId())) {
                    throw new ClException(ExceptionEnum.UNAUTHORIZED);
                }
            } catch (Exception e) {
                throw new ClException(ExceptionEnum.UNAUTHORIZED);
            }
        }
        Clerk clerk = new Clerk();
        clerk.setId(simClerk.getId());
        Clerk one = clerkMapper.selectOne(clerk);
        if (one == null) {
            throw new ClException(ExceptionEnum.FIND_CLERK_FAIL);
        }
        one.setPhone(simClerk.getPhone());
        one.setImage(simClerk.getImage());
        one.setInstiution(simClerk.getInstiution());
        one.setBirthday(simClerk.getBirthday());
        clerkMapper.updateByPrimaryKey(one);
        amqpTemplate.convertAndSend("cl.clerk.exchange", "clerk.update", one.getId());
    }

    @Override
    public void sendCodeUP(String phone) {
        //生成redis中的key
        String key = KEY_PREFIX_FINDPASSWORD + phone;
        // 借助工具类生成验证码
        String code = NumberUtils.generateCode(6);
        // 创建一个Map将电话号码和生成的验证码通过消息队列发出
        HashMap<String, String> msg = new HashMap<>();
        msg.put("phone", phone);
        msg.put("code", code);
        //通过消息队列发出
        // 依次为交换机，口令，要发送的信息
        amqpTemplate.convertAndSend("cl.sms.exchange", "sms.password.code", msg);
        //将验证码保存在redis中并设置有效时间为5分钟
        redisTemplate.opsForValue().set(key, code, 5, TimeUnit.MINUTES);

    }

    @Override
    public void updatePassword(String clerkId, String oldPassword, String newPassword, String code, String phone) {
        Clerk newClerk = null;
        if (StringUtils.isBlank(oldPassword) && StringUtils.isBlank(code)) {
            throw new ClException(ExceptionEnum.UNAUTHORIZED);
        } else if (StringUtils.isNotBlank(oldPassword)) {
            Clerk clerk = clerkMapper.selectByPrimaryKey(clerkId);
            if (clerk == null) {
                throw new ClException(ExceptionEnum.FIND_CLERK_FAIL);
            }
            oldPassword = CodecUtils.md5Hex(oldPassword, clerk.getSalt());
            if (!StringUtils.equals(oldPassword, clerk.getPassword())) {
                throw new ClException(ExceptionEnum.PASSWORD_ERROR);
            }
            newClerk = clerk;
        } else if (StringUtils.isNotBlank(code) && StringUtils.isNotBlank(phone)) {
            Clerk clerkTest = new Clerk();
            clerkTest.setPhone(phone);
            clerkTest.setId(clerkId);
            Clerk clerk = clerkMapper.selectOne(clerkTest);
            if (clerk == null) {
                throw new ClException(ExceptionEnum.FIND_CLERK_FAIL);
            }
            String cacheCode = redisTemplate.opsForValue().get(KEY_PREFIX_FINDPASSWORD + phone);
            if (!StringUtils.equals(cacheCode, code)) {
                throw new ClException(ExceptionEnum.INVALID_VERIFY_CODE);
            }
            redisTemplate.delete(KEY_PREFIX_FINDPASSWORD + phone);
            newClerk = clerk;
        }
        String salt = CodecUtils.generateSalt();
        newPassword = CodecUtils.md5Hex(newPassword, salt);
        newClerk.setSalt(salt);
        newClerk.setPassword(newPassword);
        newClerk.setUpdated(new Date());
        clerkMapper.updateByPrimaryKey(newClerk);
        //amqpTemplate.convertAndSend("cl.clerk.exchange", "clerk.update", newClerk.getId());
        amqpTemplate.convertAndSend("cl.clerk.exchange", "clerk.password.update", newClerk.getId());
    }

}
