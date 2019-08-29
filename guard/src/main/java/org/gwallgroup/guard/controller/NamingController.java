package org.gwallgroup.guard.controller;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.ListView;
import org.gwallgroup.common.utils.ResponseBase;
import org.gwallgroup.common.utils.ResponseHelp;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author jsen
 * @version 1.0
 * @date 2019/8/28 5:01 PM
 */
@RestController
@RequestMapping("/gwall-naming")
public class NamingController {

    private NamingService namingService;
    @Resource
    private NacosDiscoveryProperties nacosDiscoveryProperties;
    @PostConstruct
    public void init() {
        this.namingService = nacosDiscoveryProperties.namingServiceInstance();
    }


    @GetMapping("instances")
    public ResponseBase listServices(@RequestParam("pageNo") Integer pageNo, @RequestParam("pageSize") Integer pageSize) {
        try {
            ListView<String> result = namingService.getServicesOfServer(pageNo, pageSize);
            return ResponseHelp.simpleSucceed().data(result);
        } catch (NacosException e) {
            return ResponseHelp.simpleFailed(e.getErrMsg());
        }
    }



}
