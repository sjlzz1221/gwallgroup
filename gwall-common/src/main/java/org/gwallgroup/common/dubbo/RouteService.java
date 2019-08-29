package org.gwallgroup.common.dubbo;

import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

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

    ResponseEntity<Object> save(String id, RouteDefinition route);

    ResponseEntity<Object> delete(String id);

    ResponseEntity<RouteDefinition> route(String id);

    HashMap<String, Object> combinedfilters(String id);
}
