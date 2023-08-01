package kr.co.tumble.common.context;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.BaseHierarchicalConfiguration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.tree.ImmutableNode;
import org.apache.commons.configuration2.tree.NodeModel;
import org.apache.commons.configuration2.tree.NodeTreeWalker;
import org.apache.commons.configuration2.tree.ReferenceNodeHandler;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.*;
import java.util.stream.IntStream;

/**
 * Yaml configuration Class
 * Yaml 파일을 읽어들이기 위해서 HierarchicalConfiguration extends yaml 데이터를 Map에 저장함.
 * 최종적으로 FileBasedConfiguration의 인터페이스를 그대로 구현한 후 데이터 구조만 저장한 Map으로 교체함.
 */
@Slf4j
public final class YamlConfiguration extends BaseHierarchicalConfiguration implements FileBasedConfiguration {

    private int multiConfigIndex = -1;

    private String keyName;

    private Object keyValue;

    public YamlConfiguration() {
        super();
    }

    public YamlConfiguration(HierarchicalConfiguration<ImmutableNode> c) {
        super(c);
    }

    public YamlConfiguration(NodeModel<ImmutableNode> model) {
        super(model);
    }

    public YamlConfiguration(int configIndex) {
        super();
        multiConfigIndex = configIndex;
    }

    public YamlConfiguration(String keyName, Object keyVal) {
        super();
        this.keyName = keyName;
        this.keyValue = keyVal;
    }

    public void write(Writer out) throws ConfigurationException, IOException {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        new Yaml(options).dump(fromNode(), out);
    }

    public void read(Reader in) throws ConfigurationException, IOException {
        ImmutableNode.Builder rootBuilder = new ImmutableNode.Builder();
        ImmutableNode root = rootBuilder.create();

        final Map<String, ?> load = load(in);
        Map<ImmutableNode, ?> elemRefMap = toNodeMap(load);
        ImmutableNode top = addChildrenToRoot(root, elemRefMap);
        getSubConfigurationParentModel().mergeRoot(top, "yaml", elemRefMap, null, this);
    }

    @SuppressWarnings("unchecked")
    private Map<String, ?> load(Reader in) {
        Yaml yaml = new Yaml();
        final Iterable<Object> objects = yaml.loadAll(in);

        if (multiConfigIndex >= 0) {
            final ArrayList<Object> out = new ArrayList<>();
            objects.forEach(out::add);
            return (Map<String, ?>) out.get(multiConfigIndex);
        }

        if (keyName != null && keyValue != null) {
            Map<String, ?> toReturn = new HashMap<>();
            final Iterator<Object> iterator = objects.iterator();

            while (iterator.hasNext()) {
                Map<String, ?> map = (Map<String, ?>) iterator.next();

                if (map.containsKey(keyName) && map.get(keyName).equals(keyValue)) {
                    toReturn = map;
                }
            }

            return toReturn;
        } else {
            return (Map<String, ?>) objects.iterator().next();
        }
    }

    ImmutableNode addChildrenToRoot(ImmutableNode root, Map<ImmutableNode, ?> elemRefMap) {
        ImmutableNode top = root;

        for (Map.Entry<ImmutableNode, ?> mapEntry : elemRefMap.entrySet()) {
            ImmutableNode node = mapEntry.getKey();
            if (elemRefMap.get(node) instanceof Map) {
                top = top.addChild(addChildrenToRoot(node, (Map<ImmutableNode, ?>) elemRefMap.get(node)));
            }
            else if (isNodeCollection(elemRefMap.get(node))) {
                Collection<Map<ImmutableNode, ?>> n = (Collection<Map<ImmutableNode, ?>>) elemRefMap.get(node);

                for (Map<ImmutableNode, ?> mapValue : n) {
                    top = top.addChild(addChildrenToRoot(node, mapValue));
                }
            }
            else {
                top = top.addChild(node);
            }
        }

        return top;
    }

    private static boolean isNodeCollection(Object o) {
        if (o instanceof Collection<?> collention) {
            return !collention.isEmpty() && collention.iterator().next() instanceof Map;
        } else {
            return false;
        }
    }

    private Map<String, Object> fromNode() {
        final YamlBuilder visitor = new YamlBuilder();
        NodeTreeWalker.INSTANCE.walkBFS(getNodeModel().getRootNode(), visitor, getNodeModel().getNodeHandler());
        return visitor.getDocument();
    }

    Map<ImmutableNode, Object> toNodeMap(Object load) {
        final Map<String, ?> map = (Map<String, ?>) load;
        final Map<ImmutableNode, Object> nodeMap = new HashMap<>();

        for (Map.Entry<String, ?> mapEntry : map.entrySet()) {
            String key = mapEntry.getKey();
            Object value = mapEntry.getValue();

            if (value instanceof Map) {
                ImmutableNode currentNode = new ImmutableNode.Builder().name(key).value(value).create();
                nodeMap.put(currentNode, toNodeMap(value));
            } else if (isNodeCollection(value)) {
                ImmutableNode currentNode = new ImmutableNode.Builder().name(key).value(value).create();
                Collection<Map<String, ?>> valueMaps = (Collection<Map<String, ?>>) value;
                nodeMap.put(currentNode, valueMaps.stream().map(this::toNodeMap).toList());
            } else {
                Object outVal = (value != null ? value : new TreeMap<>());
                ImmutableNode currentNode = new ImmutableNode.Builder().name(key).value(outVal).create();
                nodeMap.put(currentNode, outVal);
            }
        }

        return nodeMap;
    }

    private final class YamlBuilder extends BuilderVisitor {

        final Map<String, Object> document = new TreeMap<>();

        final List<Shadow<?>> documentShadow = new ArrayList<>();

        YamlBuilder() {
            super();
            documentShadow.add(new ShadowMapNode("yaml", "yaml", null));
        }

        @Override
        protected void insert(final ImmutableNode node, final ImmutableNode parent,
                              final ImmutableNode siblingBefore, final ImmutableNode siblingAfter,
                              final ReferenceNodeHandler handler) {

            final String nodeName = node.getNodeName();
            final String nodeNameEscaped = node.getNodeName().replaceAll("[.]", "\\^");
            final String longNodeName = toLongName(parent, handler, nodeNameEscaped);
            final Object value = node.getValue();

            Shadow<?> shadow = documentShadow.stream().filter(s -> s.longName.equals(longNodeName)).findFirst().orElseGet(() -> makeShadow(parent, handler, nodeName, longNodeName, value));
            shadow.syncDocument(value);
        }

        @Override
        protected void update(ImmutableNode node, Object value, ReferenceNodeHandler handler) {
            final ImmutableNode parent = handler.getParent(node);
            final String nodeName = node.getNodeName();
            final String nodeNameEscaped = node.getNodeName().replaceAll("[.]", "\\^");
            final String longNodeName = toLongName(parent, handler, nodeNameEscaped);

            Shadow<?> shadow = documentShadow.stream().filter(s -> s.longName.equals(longNodeName)).findFirst().orElseGet(() -> makeShadow(parent, handler, nodeName, longNodeName, value));
            shadow.syncDocument(value);
        }

        <T> Shadow<T> makeShadow(ImmutableNode parent, ReferenceNodeHandler handler,
                                 String nodeName, String longNodeName, Object value) {
            final Shadow<?> shadowParent = documentShadow.stream().filter(s -> s.longName.equals(longNodeName.substring(0, longNodeName.lastIndexOf(".")))).findFirst().orElse(null);
            final Shadow<?> newShadow;

            if (isNodeCollection(value)) {
                final int count = handler.getChildrenCount(parent, nodeName);
                newShadow = shadowCollection(longNodeName, nodeName, count, shadowParent);
            } else if (value instanceof Map) {
                newShadow = shadowMap(longNodeName, nodeName, shadowParent);
            } else {
                newShadow = shadowLeaf(longNodeName, nodeName, shadowParent);
            }

            documentShadow.add(newShadow);
            return (Shadow<T>) newShadow;
        }

        String toLongName(ImmutableNode node, ReferenceNodeHandler handler, String init) {
            if (node != null) {
                final String nodePath = node.getNodeName().replaceAll("[.]", "\\^") + "." + init;
                return toLongName(handler.getParent(node), handler, nodePath);
            } else {
                return init;
            }
        }

        Map<String, Object> getDocument() {
            return document;
        }

        <T> Shadow<T> shadowMap(String longName, String name, Shadow<?> parent) {
            return (Shadow<T>) new ShadowMapNode(longName, name, parent);
        }

        <T> Shadow<T> shadowLeaf(String longName, String name, Shadow<?> parent) {
            return (Shadow<T>) new ShadowLeafNode(longName, name, parent);
        }

        <T> Shadow<T> shadowCollection(String longName, String name, int size, Shadow<?> parent) {
            return (Shadow<T>) new ShadowCollectionNode(longName, name, size, parent);
        }

        abstract class Shadow<T> {

            final String name;
            final String longName;
            final Shadow<?> parent;
            final T reference;
            int pointer = 0;

            Shadow(String longName, String name, Shadow<?> parent, T reference) {
                this.name = name;
                this.longName = longName;
                this.parent = parent;
                this.reference = reference;
            }

            abstract void syncDocument(Object value);

            @Override
            public String toString() {
                return "Shadow@" + this.hashCode() + "{" +
                        "longName='" + longName + '\'' +
                        '}';
            }
        }

        final class ShadowCollectionNode extends Shadow<List<Map<String, Object>>> {

            ShadowCollectionNode(String longName, String name, int size, Shadow<?> parent) {
                super(longName, name, parent, new ArrayList<>());
                IntStream.range(0, size).forEach(i -> this.reference.add(new TreeMap<>()));

                if (parent instanceof ShadowMapNode shadowMapNode) {
                    shadowMapNode.reference.put(name, this.reference);
                } else if (parent instanceof ShadowCollectionNode shadowCollectionNode) {
                    shadowCollectionNode.reference.get(pointer).put(name, this.reference);
                    pointer++;
                }
            }

            @Override
            void syncDocument(Object value) {
                log.debug("syncDocument");
            }

            @Override
            public String toString() {
                return "Collection" + super.toString();
            }
        }

        final class ShadowMapNode extends Shadow<Map<String, Object>> {

            ShadowMapNode(String longName, String name, Shadow<?> parent) {
                super(longName, name, parent, parent == null ? document : new TreeMap<>());

                if (parent instanceof ShadowMapNode shadowMapNode) {
                    shadowMapNode.reference.put(name, this.reference);
                } else if (parent instanceof ShadowCollectionNode shadowCollectionNode) {
                    shadowCollectionNode.reference.get(pointer).put(name, this.reference);
                    pointer++;
                }
            }

            @Override
            void syncDocument(Object value) {
                log.debug("syncDocument");
            }

            @Override
            public String toString() {
                return "Map" + super.toString();
            }
        }

        final class ShadowLeafNode extends Shadow<Object> {

            ShadowLeafNode(String longName, String name, Shadow<?> parent) {
                super(longName, name, parent, null);
            }

            @Override
            void syncDocument(Object value) {
                if (this.parent instanceof ShadowCollectionNode parent) {

                    if (parent.reference.size() <= pointer) {
                        parent.reference.add(pointer, new TreeMap<>());
                    }

                    parent.reference.get(pointer).put(name, value);
                    pointer++;
                } else {
                    ((ShadowMapNode) this.parent).reference.put(name, value);
                }
            }

            @Override
            public String toString() {
                return "Leaf" + super.toString();
            }
        }
    }
}