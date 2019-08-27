package org.gwallgroup.gwall.service.impl;

import com.mongodb.client.result.UpdateResult;
import java.util.List;
import javax.annotation.Resource;
import org.gwallgroup.gwall.entity.mongo.MongoRouteDefinition;
import org.gwallgroup.gwall.mapper.mongo.MongoRouteDefinitionMapper;
import org.gwallgroup.gwall.service.MongoRouteDefinitionService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
public class MongoRouteDefinitionServiceImpl implements MongoRouteDefinitionService {
  @Resource
  private MongoRouteDefinitionMapper mongoRouteDefinitionMapper;
  @Resource
  private MongoTemplate mongoTemplate;


  @Override
  public void deleteById(String id) {
    mongoRouteDefinitionMapper.deleteById(id);
  }

  @Override
  public MongoRouteDefinition createOrUpdate(MongoRouteDefinition mongoRouteDefinition) {
    if (mongoRouteDefinitionMapper.existsById(mongoRouteDefinition.getId())) {
      // do update
      Query query = new Query(Criteria.where("id").is(mongoRouteDefinition.getId()));
      Update update = new Update();
      update.set("_id", mongoRouteDefinition.getId());
      update.set("predicates", mongoRouteDefinition.getPredicates());
      update.set("filters", mongoRouteDefinition.getFilters());
      update.set("uri", mongoRouteDefinition.getUri());
      update.set("order", mongoRouteDefinition.getOrder());
      mongoTemplate.updateFirst(query, update, MongoRouteDefinition.class);
      return mongoRouteDefinition;
    } else {
      // do create
      return mongoRouteDefinitionMapper.save(mongoRouteDefinition);
    }
  }

  @Override
  public List<MongoRouteDefinition> queryAll() {
    return mongoRouteDefinitionMapper.findAll();
  }
}
