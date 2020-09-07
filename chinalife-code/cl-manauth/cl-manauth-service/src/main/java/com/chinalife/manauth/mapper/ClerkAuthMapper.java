package com.chinalife.manauth.mapper;

import com.chinalife.manauth.po.ClerkAuth;
import tk.mybatis.mapper.additional.idlist.DeleteByIdListMapper;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

public interface ClerkAuthMapper extends Mapper<ClerkAuth>,DeleteByIdListMapper<ClerkAuth,Long>{
}
