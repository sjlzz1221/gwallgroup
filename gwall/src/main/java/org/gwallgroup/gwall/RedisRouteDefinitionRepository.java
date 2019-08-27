package org.gwallgroup.gwall;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import org.gwallgroup.gwall.entity.mongo.MongoRouteDefinition;
import org.gwallgroup.gwall.service.MongoRouteDefinitionService;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class RedisRouteDefinitionRepository implements RouteDefinitionRepository {

//  @Resource(name = "redisTemplate")
//  private HashOperations<String, String, Object> hashOperations;
//  @Resource
//  private MongoTemplate mongoTemplate;


  @Resource
  private MongoRouteDefinitionService mongoRouteDefinitionService;
  private final Map<String, RouteDefinition> routes = new ConcurrentSkipListMap<>();

  @PostConstruct
  public void init() {
    routes.putAll(mongoRouteDefinitionService.queryAll().stream().collect(Collectors.toMap(MongoRouteDefinition::getId, e -> e)));
  }

  @Override
  public Flux<RouteDefinition> getRouteDefinitions() {
    return Flux.fromIterable(this.routes.values());
  }

  @Override
  public Mono<Void> save(Mono<RouteDefinition> route) {
    return route.flatMap((r) -> {
      MongoRouteDefinition mongoRouteDefinition = new MongoRouteDefinition();
      mongoRouteDefinition.setId(r.getId());
      mongoRouteDefinition.setPredicates(r.getPredicates());
      mongoRouteDefinition.setFilters(r.getFilters());
      mongoRouteDefinition.setUri(r.getUri());
      mongoRouteDefinition.setOrder(r.getOrder());
      mongoRouteDefinitionService.createOrUpdate(mongoRouteDefinition);
      this.routes.put(r.getId(), r);
      return Mono.empty();
    });
  }

  @Override
  public Mono<Void> delete(Mono<String> routeId) {
    return routeId.flatMap((id) -> {
      if (this.routes.containsKey(id)) {
        mongoRouteDefinitionService.deleteById(id);
        this.routes.remove(id);
        return Mono.empty();
      } else {
        return Mono.defer(() -> Mono.error(new NotFoundException("RouteDefinition not found: " + routeId)));
      }
    });
  }
}
