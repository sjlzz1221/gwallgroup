package org.gwallgroup.guard.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.dubbo.config.annotation.Reference;
import org.gwallgroup.common.dubbo.RouteService;
import org.gwallgroup.common.entity.RouteDefinition;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jsen
 * @version 1.0
 * @date 2019/8/29 9:33 AM
 */
@RestController
@RequestMapping("/gwall-gateway")
public class GatewayController {
    @Reference(check = false, lazy = true)
    private RouteService routeService;

    private static final Log log = LogFactory.getLog(GatewayController.class);

    @PostMapping("/refresh")
    public void refresh() {
        routeService.refresh();
    }

    @GetMapping("/globalfilters")
    public HashMap<String, Object> globalfilters() {
        return routeService.globalfilters();
    }

    @GetMapping("/routefilters")
    public HashMap<String, Object> routefilers() {
        return routeService.routefilers();
    }

    @GetMapping("/routes")
    public List<Map<String, Object>> routes() {
        return routeService.routes();
    }

    @PostMapping("/routes/{id}")
    @SuppressWarnings("unchecked")
    public ResponseEntity<Object> save(@PathVariable String id, @RequestBody RouteDefinition route) {
        return routeService.save(id, route);
    }

    @DeleteMapping("/routes/{id}")
    public ResponseEntity<Object> delete(@PathVariable String id) {
        return routeService.delete(id);
    }

    @GetMapping("/routes/{id}")
    public ResponseEntity<RouteDefinition> route(@PathVariable String id) {
        return routeService.route(id);
    }

    @GetMapping("/routes/{id}/combinedfilters")
    public HashMap<String, Object> combinedfilters(@PathVariable String id) {
        return routeService.combinedfilters(id);
    }

}
