package org.example.worktest.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "currencies")
public class Currency  {

    @Id
    private String code;  // 幣別代碼，如 USD、EUR、GBP

    @Column(nullable = false)
    private String name;  // 幣別中文名稱

    public Currency() {}

    public Currency(String code, String name) {
        this.code = code;
        this.name = name;
    }

    // Getter 與 Setter
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
