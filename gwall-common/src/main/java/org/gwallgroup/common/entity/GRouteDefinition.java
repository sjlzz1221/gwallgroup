package org.gwallgroup.common.entity;

import com.google.common.collect.Lists;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.springframework.util.StringUtils.tokenizeToStringArray;

/**
 * @author jsen
 * @version 1.0
 * @date 2019/8/29 7:05 PM
 */
@Validated
public class GRouteDefinition implements Serializable {
    @NotEmpty
    private String id = UUID.randomUUID().toString();

    @NotEmpty
    @Valid
    private List<GPredicateDefinition> predicates = new ArrayList<>();

    @Valid
    private List<GFilterDefinition> filters = new ArrayList<>();

    @NotNull
    private URI uri;

    private int order = 0;

    public GRouteDefinition() {

    }
    public GRouteDefinition(RouteDefinition routeDefinition) {
        this.id = routeDefinition.getId();
        this.uri = routeDefinition.getUri();
        this.order = routeDefinition.getOrder();
        if (routeDefinition.getFilters() != null) {
            List<GFilterDefinition> fds = Lists.newArrayList();
            for (FilterDefinition item : routeDefinition.getFilters()) {
                GFilterDefinition fd = new GFilterDefinition();
                fd.setName(item.getName());
                fd.setArgs(item.getArgs());
                fds.add(fd);
            }
            setFilters(fds);
        }
        if (routeDefinition.getPredicates() != null) {
            List<GPredicateDefinition> pds = Lists.newArrayList();
            for (PredicateDefinition item : routeDefinition.getPredicates()) {
                GPredicateDefinition pd = new GPredicateDefinition();
                pd.setName(item.getName());
                pd.setArgs(item.getArgs());
                pds.add(pd);
            }
            setPredicates(pds);
        }
    }

    public GRouteDefinition(String text) {
        int eqIdx = text.indexOf('=');
        if (eqIdx <= 0) {
            throw new ValidationException("Unable to parse RouteDefinition text '" + text + "'" +
                    ", must be of the form name=value");
        }

        setId(text.substring(0, eqIdx));

        String[] args = tokenizeToStringArray(text.substring(eqIdx+1), ",");

        setUri(URI.create(args[0]));

        for (int i=1; i < args.length; i++) {
            this.predicates.add(new GPredicateDefinition(args[i]));
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<GPredicateDefinition> getPredicates() {
        return predicates;
    }

    public void setPredicates(List<GPredicateDefinition> predicates) {
        this.predicates = predicates;
    }

    public List<GFilterDefinition> getFilters() {
        return filters;
    }

    public void setFilters(List<GFilterDefinition> filters) {
        this.filters = filters;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GRouteDefinition GRouteDefinition = (GRouteDefinition) o;
        return Objects.equals(id, GRouteDefinition.id) &&
                Objects.equals(predicates, GRouteDefinition.predicates) &&
                Objects.equals(order, GRouteDefinition.order) &&
                Objects.equals(uri, GRouteDefinition.uri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, predicates, uri);
    }

    @Override
    public String toString() {
        return "RouteDefinition{" +
                "id='" + id + '\'' +
                ", predicates=" + predicates +
                ", filters=" + filters +
                ", uri=" + uri +
                ", order=" + order +
                '}';
    }

}
