package com.chinalife.auth.vo;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Data
@Table(name = "auth")
public class Auth {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Auth auth = (Auth) o;
        return Objects.equals(id, auth.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), id);
    }

    @Id
    @KeySql(useGeneratedKeys = true)
    private Integer id;

    private String alias;

    private String url;

    private Integer groupId;

}
