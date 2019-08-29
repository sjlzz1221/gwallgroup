package org.gwallgroup.guard.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.dubbo.config.annotation.Reference;
import org.gwallgroup.common.dubbo.RouteService;
import org.gwallgroup.common.entity.GRouteDefinition;
import org.gwallgroup.common.utils.ResponseBase;
import org.gwallgroup.common.utils.ResponseHelp;
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
    public ResponseBase refresh() {
        routeService.refresh();
        return ResponseHelp.prefabSimpleSucceed();
    }

    @GetMapping("/globalfilters")
    public ResponseBase globalfilters() {
        return ResponseHelp.prefabSimpleSucceedData(routeService.globalfilters());
    }

    @GetMapping("/routefilters")
    public ResponseBase routefilers() {
        return ResponseHelp.prefabSimpleSucceedData(routeService.routefilers());
    }

    @GetMapping("/routes")
    public ResponseBase routes() {
        return ResponseHelp.prefabSimpleSucceedData(routeService.routes());
    }

    @PostMapping("/routes/{id}")
    @SuppressWarnings("unchecked")
    public ResponseBase save(@PathVariable String id, @RequestBody GRouteDefinition route) {
        return ResponseHelp.prefabSimpleSucceedData(routeService.save(id, route));
    }

    @DeleteMapping("/routes/{id}")
    public ResponseBase delete(@PathVariable String id) {
        return ResponseHelp.prefabSimpleSucceedData(routeService.delete(id));
    }

    @GetMapping("/routes/{id}")
    public ResponseBase route(@PathVariable String id) {
        return ResponseHelp.prefabSimpleSucceedData(routeService.route(id));
    }

    @GetMapping("/routes/{id}/combinedfilters")
    public ResponseBase combinedfilters(@PathVariable String id) {
        return ResponseHelp.prefabSimpleSucceedData(routeService.combinedfilters(id));
    }

}
