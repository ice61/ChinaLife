package com.chinalife.auth.mapper;

import com.chinalife.auth.vo.Auth;
import tk.mybatis.mapper.additional.idlist.DeleteByIdListMapper;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

public interface AuthMapper extends Mapper<Auth>,SelectByIdListMapper<Auth,Integer>,DeleteByIdListMapper<Auth,Integer> {
}
