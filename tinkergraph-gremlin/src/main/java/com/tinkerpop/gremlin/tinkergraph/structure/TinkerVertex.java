package com.tinkerpop.gremlin.tinkergraph.structure;

import com.tinkerpop.gremlin.process.graph.GraphTraversal;
import com.tinkerpop.gremlin.structure.Direction;
import com.tinkerpop.gremlin.structure.Edge;
import com.tinkerpop.gremlin.structure.MetaProperty;
import com.tinkerpop.gremlin.structure.Property;
import com.tinkerpop.gremlin.structure.Vertex;
import com.tinkerpop.gremlin.structure.util.ElementHelper;
import com.tinkerpop.gremlin.structure.util.StringFactory;
import com.tinkerpop.gremlin.tinkergraph.process.graph.TinkerElementTraversal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class TinkerVertex extends TinkerElement implements Vertex {

    protected Map<String, Set<Edge>> outEdges = new HashMap<>();
    protected Map<String, Set<Edge>> inEdges = new HashMap<>();
    protected Map<String, List<MetaProperty>> metaProperties = new HashMap<>();


    protected TinkerVertex(final Object id, final String label, final TinkerGraph graph) {
        super(id, label, graph);
    }

    @Override
    public <V> Property<V> property(final String key, final V value) {
        if (this.graph.graphView != null && this.graph.graphView.getInUse()) {
            return this.graph.graphView.setProperty(this, key, value);
        } else {
            ElementHelper.validateProperty(key, value);
            final Property oldProperty = super.property(key);
            final Property newProperty = new TinkerProperty<>(this, key, value);
            this.properties.put(key, newProperty);
            this.graph.vertexIndex.autoUpdate(key, value, oldProperty.isPresent() ? oldProperty.value() : null, this);
            return newProperty;
        }
    }

    @Override
    public <V> Iterator<MetaProperty<V>> metaProperties(final String... metaPropertyKeys) {
        return (Iterator) this.metaProperties.entrySet().stream()
                .filter(entry -> metaPropertyKeys.length == 0 || Arrays.binarySearch(metaPropertyKeys, entry.getKey()) >= 0)
                .flatMap(entry -> entry.getValue().stream())
                .iterator();
    }

    @Override
    public <V> MetaProperty<V> metaProperty(final String key, final V value, final Object... propertyKeyValues) {
        final MetaProperty<V> metaProperty = new TinkerMetaProperty<>(this, key, value, propertyKeyValues);
        if (this.metaProperties.containsKey(key)) {
            this.metaProperties.get(key).add(metaProperty);
        } else {
            final List<MetaProperty> list = new ArrayList<>();
            list.add(metaProperty);
            this.metaProperties.put(key, list);
        }
        return metaProperty;
    }

    public String toString() {
        return StringFactory.vertexString(this);
    }

    @Override
    public Edge addEdge(final String label, final Vertex vertex, final Object... keyValues) {
        return TinkerHelper.addEdge(this.graph, this, (TinkerVertex) vertex, label, keyValues);
    }

    @Override
    public void remove() {
        this.bothE().forEach(Edge::remove);
        this.properties.clear();
        this.graph.vertexIndex.removeElement(this);
        this.graph.vertices.remove(this.id);
    }

    //////////////////////

    @Override
    public GraphTraversal<Vertex, Vertex> start() {
        return new TinkerElementTraversal<>(this, this.graph);
    }

    @Override
    public Iterator<Edge> edges(final Direction direction, final int branchFactor, final String... labels) {
        return (Iterator) TinkerHelper.getEdges(this, direction, branchFactor, labels);
    }

    @Override
    public Iterator<Vertex> vertices(final Direction direction, final int branchFactor, final String... labels) {
        return (Iterator) TinkerHelper.getVertices(this, direction, branchFactor, labels);
    }
}
