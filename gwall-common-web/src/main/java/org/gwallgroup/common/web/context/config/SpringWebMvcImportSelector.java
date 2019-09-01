package org.gwallgroup.common.web.context.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

public class SpringWebMvcImportSelector implements ImportSelector {

  @Override
  public String[] selectImports(@NotNull AnnotationMetadata importingClassMetadata) {
    boolean webmvcPresent =
        ClassUtils.isPresent(
            "org.springframework.web.servlet.DispatcherServlet", getClass().getClassLoader());
    return webmvcPresent
        ? new String[] {"org.gwallgroup.common.web.context.config.WebMvcSecurityConfiguration"}
        : new String[] {};
  }
}
