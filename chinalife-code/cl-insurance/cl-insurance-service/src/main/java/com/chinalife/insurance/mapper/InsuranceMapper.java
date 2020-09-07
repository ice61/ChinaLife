package com.chinalife.insurance.mapper;

import com.chinalife.insurance.po.Insurance;
import tk.mybatis.mapper.additional.idlist.DeleteByIdListMapper;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

public interface InsuranceMapper extends Mapper<Insurance>,DeleteByIdListMapper<Insurance,Long>,SelectByIdListMapper<Insurance,Long> {
}
