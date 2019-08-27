package org.gwallgroup.gwall.service;

import java.util.List;
import org.gwallgroup.gwall.entity.mongo.MongoRouteDefinition;

public interface MongoRouteDefinitionService {

  /**
   * 删除
   * @param id
   */
  void deleteById(String id);

  /**
   * 更新或者创建
   * @param mongoRouteDefinition r
   * @return r
   */
  MongoRouteDefinition createOrUpdate(MongoRouteDefinition mongoRouteDefinition);

  List<MongoRouteDefinition> queryAll();

}
