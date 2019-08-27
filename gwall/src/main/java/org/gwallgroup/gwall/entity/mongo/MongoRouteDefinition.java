package org.gwallgroup.gwall.entity.mongo;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "route")
public class MongoRouteDefinition extends RouteDefinition {
  @Id
  @Field("_id")
  private String id = UUID.randomUUID().toString();

  @Override
  public String getId() {
    return id;
  }

  @Override
  public void setId(String id) {
    super.setId(id);
    this.id = id;
  }
}
