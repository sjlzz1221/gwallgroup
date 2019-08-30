package org.gwallgroup.guard;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDubbo
@EnableDiscoveryClient
public class Boot {

  public static void main(String[] args) {
    SpringApplication.run(Boot.class, args);
  }

}
