package org.gwallgroup.common.dubbo;

import org.gwallgroup.common.entity.RouteDefinition;
import org.gwallgroup.common.utils.ResponseBase;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * route 操作接口 网关实现 guard 调用
 * @author jsen
 * @version 1.0
 * @date 2019/8/28 7:48 PM
 */
public interface RouteService {

    /**
     * 更新
     */
    void refresh();

    HashMap<String, Object> globalfilters();

    HashMap<String, Object> routefilers();

    List<Map<String, Object>> routes();

    boolean save(String id, RouteDefinition route);

    ResponseEntity<Object> delete(String id);

    ResponseEntity<org.springframework.cloud.gateway.route.RouteDefinition> route(String id);

    HashMap<String, Object> combinedfilters(String id);
}
