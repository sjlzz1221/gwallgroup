package org.gwallgroup.authentication.entity.po;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @author jsen
 * @version 1.0
 * @date 2019/8/30 11:09 AM
 */
@Entity(name = "gwall_account")
@Data
@Accessors(chain = true)
public class GwallAccount {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    /**
     * 登录账户
     */
    private String name;
    /**
     * 登录密码
     */
    private String password;

}
