package org.gwallgroup.common.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author jsen
 * @version 1.0
 * @date 2019/8/28 6:28 PM
 */
@Data
@Accessors(chain = true)
public class LoginCheck {
    /**
     * http code
     */
    private int code;

    /**
     * permissions, split with \0
     */
    private String permissions;
}
