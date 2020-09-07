package com.chinalife.auth.mapper;

import com.chinalife.auth.vo.AuthGroup;
import com.chinalife.auth.vo.AuthGroupAll;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface AuthGroupMapper extends Mapper<AuthGroup>,SelectByIdListMapper<AuthGroup,Integer> {
}
