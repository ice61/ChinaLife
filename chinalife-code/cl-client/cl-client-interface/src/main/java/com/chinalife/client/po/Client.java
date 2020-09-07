package com.chinalife.client.po;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
import java.util.Objects;

@Data
@Table(name = "client")
public class Client {
    @Id
    private String id;
    private String name;
    private Date birthday;
    private Boolean sex;
    private String phone;
    private String image;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Client client = (Client) o;
        return Objects.equals(id, client.id) &&
                Objects.equals(name, client.name) &&
                Objects.equals(birthday, client.birthday) &&
                Objects.equals(sex, client.sex) &&
                Objects.equals(phone, client.phone) &&
                Objects.equals(image, client.image);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), id, name, birthday, sex, phone, image);
    }
}
