package org.gwallgroup.gwall.service.impl;

import com.google.common.collect.Lists;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.dubbo.config.annotation.Service;
import org.gwallgroup.common.dubbo.RouteService;
import org.gwallgroup.common.utils.ResponseBase;
import org.gwallgroup.common.utils.ResponseHelp;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.*;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.core.Ordered;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.net.URI;
import java.util.*;

/**
 * @author jsen
 * @version 1.0
 * @date 2019/8/28 7:50 PM
 */
@Service
public class RouteServiceImpl implements RouteService, ApplicationEventPublisherAware {
    private static final Log log = LogFactory.getLog(RouteServiceImpl.class);
    @Resource
    private RouteDefinitionLocator routeDefinitionLocator;
    @Resource
    private List<GlobalFilter> globalFilters;
    @Resource
    private List<GatewayFilterFactory> gatewayFilters;
    @Resource
    private RouteDefinitionWriter routeDefinitionWriter;
    @Resource
    private RouteLocator routeLocator;
    private ApplicationEventPublisher publisher;

    @Override
    public void refresh() {
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
    }

    @Override
    public HashMap<String, Object> globalfilters() {
        return this.getNamesToOrders(this.globalFilters);
    }

    @Override
    public HashMap<String, Object> routefilers() {
        return this.getNamesToOrders(this.gatewayFilters);
    }

    @Override
    public List<Map<String, Object>> routes() {
        Mono<Map<String, RouteDefinition>> routeDefs = this.routeDefinitionLocator.getRouteDefinitions().collectMap(RouteDefinition::getId);
        Mono<List<Route>> routes = this.routeLocator.getRoutes().collectList();
        return Mono.zip(routeDefs, routes).map((tuple) -> {
            Map<String, RouteDefinition> defs = (Map)tuple.getT1();
            List<Route> routeList = (List)tuple.getT2();
            List<Map<String, Object>> allRoutes = new ArrayList();
            routeList.forEach((route) -> {
                HashMap<String, Object> r = new HashMap();
                r.put("route_id", route.getId());
                r.put("order", route.getOrder());
                if (defs.containsKey(route.getId())) {
                    r.put("route_definition", defs.get(route.getId()));
                } else {
                    HashMap<String, Object> obj = new HashMap();
                    obj.put("predicate", route.getPredicate().toString());
                    if (!route.getFilters().isEmpty()) {
                        ArrayList<String> filters = new ArrayList();
                        Iterator var6 = route.getFilters().iterator();

                        while(var6.hasNext()) {
                            GatewayFilter filter = (GatewayFilter)var6.next();
                            filters.add(filter.toString());
                        }

                        obj.put("filters", filters);
                    }

                    if (!obj.isEmpty()) {
                        r.put("route_object", obj);
                    }
                }

                allRoutes.add(r);
            });
            return allRoutes;
        }).block();
    }

    @Override
    public boolean save(String id, org.gwallgroup.common.entity.RouteDefinition route) {
        RouteDefinition rd = new RouteDefinition();
        rd.setId(route.getId());
        rd.setOrder(route.getOrder());
        String url = route.getUri().toString();
        rd.setUri(URI.create(url));
        route.setId(id);
        if (route.getFilters() != null) {
            List<FilterDefinition> fds = Lists.newArrayList();
            for (org.gwallgroup.common.entity.FilterDefinition item : route.getFilters()) {
                FilterDefinition fd = new FilterDefinition();
                fd.setName(item.getName());
                fd.setArgs(item.getArgs());
                fds.add(fd);
            }
            rd.setFilters(fds);
        }
        if (route.getPredicates() != null) {
            List<PredicateDefinition> pds = Lists.newArrayList();
            for (org.gwallgroup.common.entity.PredicateDefinition item : route.getPredicates()) {
                PredicateDefinition pd = new PredicateDefinition();
                pd.setName(item.getName());
                pd.setArgs(item.getArgs());
                pds.add(pd);
            }
            rd.setPredicates(pds);
        }

        log.debug("Saving route: " + route);
        this.routeDefinitionWriter.save(Mono.just(rd)).then(Mono.defer(() -> {
            return Mono.just(ResponseEntity.created(URI.create("/routes/" + id)).build());
        })).block();
        return true;
    }

  public static void main(String[] args) {
    //
      URI uri = URI.create("lb://sdfsdf");
      System.out.println(uri);
  }

    @Override
    public ResponseEntity<Object> delete(String id) {
        return this.routeDefinitionWriter.delete(Mono.just(id)).then(Mono.defer(() -> {
            return Mono.just(ResponseEntity.ok().build());
        })).onErrorResume((t) -> {
            return t instanceof NotFoundException;
        }, (t) -> {
            return Mono.just(ResponseEntity.notFound().build());
        }).block();
    }

    @Override
    public ResponseEntity<RouteDefinition> route(String id) {
        return this.routeDefinitionLocator.getRouteDefinitions().filter((route) -> {
            return route.getId().equals(id);
        }).singleOrEmpty().map(ResponseEntity::ok).switchIfEmpty(Mono.just(ResponseEntity.notFound().build())).block();
    }

    @Override
    public HashMap<String, Object> combinedfilters(String id) {
        return this.routeLocator.getRoutes().filter((route) -> {
            return route.getId().equals(id);
        }).reduce(new HashMap<String, Object>(), this::putItem).block();
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }

    private <T> HashMap<String, Object> getNamesToOrders(List<T> list) {
        return Flux.fromIterable(list).reduce(new HashMap<String, Object>(), this::putItem).block();
    }

    private HashMap<String, Object> putItem(HashMap<String, Object> map, Object o) {
        Integer order = null;
        if (o instanceof Ordered) {
            order = ((Ordered)o).getOrder();
        }

        map.put(o.toString(), order);
        return map;
    }
}
