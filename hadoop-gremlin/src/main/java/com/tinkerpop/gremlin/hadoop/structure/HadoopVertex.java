package com.tinkerpop.gremlin.hadoop.structure;

import com.tinkerpop.gremlin.hadoop.process.graph.HadoopElementTraversal;
import com.tinkerpop.gremlin.process.graph.GraphTraversal;
import com.tinkerpop.gremlin.structure.Direction;
import com.tinkerpop.gremlin.structure.Edge;
import com.tinkerpop.gremlin.structure.Element;
import com.tinkerpop.gremlin.structure.Vertex;
import com.tinkerpop.gremlin.structure.VertexProperty;
import com.tinkerpop.gremlin.structure.util.wrapped.WrappedVertex;
import com.tinkerpop.gremlin.tinkergraph.structure.TinkerVertex;
import com.tinkerpop.gremlin.tinkergraph.structure.TinkerVertexProperty;
import com.tinkerpop.gremlin.util.StreamFactory;

import java.util.Iterator;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class HadoopVertex extends HadoopElement implements Vertex, Vertex.Iterators, WrappedVertex<TinkerVertex> {

    protected HadoopVertex() {
    }

    public HadoopVertex(final TinkerVertex vertex, final HadoopGraph graph) {
        super(vertex, graph);
    }

    @Override
    public <V> VertexProperty<V> property(final String key) {
        final VertexProperty<V> vertexProperty = getBaseVertex().<V>property(key);
        return vertexProperty.isPresent() ?
                new HadoopVertexProperty<>((TinkerVertexProperty<V>) ((Vertex) this.tinkerElement).property(key), this) :
                VertexProperty.<V>empty();
    }

    @Override
    public <V> VertexProperty<V> property(final String key, final V value) {
        throw Element.Exceptions.propertyAdditionNotSupported();
    }

    @Override
    public Edge addEdge(final String label, final Vertex inVertex, final Object... keyValues) {
        throw Vertex.Exceptions.edgeAdditionsNotSupported();
    }

    @Override
    public GraphTraversal<Vertex, Vertex> start() {
        return new HadoopElementTraversal<>(this, this.graph);
    }

    @Override
    public TinkerVertex getBaseVertex() {
        return (TinkerVertex) this.tinkerElement;
    }

    @Override
    public Vertex.Iterators iterators() {
        return this;
    }

    @Override
    public Iterator<Vertex> vertexIterator(final Direction direction, final String... labels) {
        return StreamFactory.stream(getBaseVertex().iterators().vertexIterator(direction, labels)).map(v -> this.graph.v(v.id())).iterator();
    }

    @Override
    public Iterator<Edge> edgeIterator(final Direction direction, final String... edgeLabels) {
        return StreamFactory.stream(getBaseVertex().iterators().edgeIterator(direction, edgeLabels)).map(e -> this.graph.e(e.id())).iterator();
    }

    @Override
    public <V> Iterator<VertexProperty<V>> propertyIterator(final String... propertyKeys) {
        return (Iterator) StreamFactory.stream(getBaseVertex().iterators().propertyIterator(propertyKeys))
                .map(property -> new HadoopVertexProperty<>((TinkerVertexProperty<V>) property, this)).iterator();
    }
}
