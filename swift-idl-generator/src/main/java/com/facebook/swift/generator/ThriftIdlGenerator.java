package com.facebook.swift.generator;

import com.facebook.swift.codec.metadata.ThriftCatalog;
import com.facebook.swift.codec.metadata.ThriftFieldMetadata;
import com.facebook.swift.codec.metadata.ThriftType;
import com.facebook.swift.service.metadata.ThriftMethodMetadata;
import com.facebook.swift.service.metadata.ThriftServiceMetadata;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.StringRenderer;

import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;

public class ThriftIdlGenerator
{
    private final ThriftServiceMetadata metadata;
    private final STGroup group;
    private final List<String> includes = new ArrayList<>();
    private final Map<String, String> namespaces = new LinkedHashMap<>();

    private volatile String name;
    private volatile String extendsClass;

    public ThriftIdlGenerator(Class<?> serviceClass, ThriftCatalog catalog)
    {
        this(new ThriftServiceMetadata(serviceClass, catalog));
    }

    public ThriftIdlGenerator(ThriftServiceMetadata metadata)
    {
        this.metadata = metadata;
        name = metadata.getName();

        URL resource = getClass().getResource("ThriftIdlGenerator.stg");

        group = new STGroup();
        group.loadGroupFile("", resource.toExternalForm());
        group.registerRenderer(
                Object.class,
                new StringRenderer()
                {
                    @Override
                    public String toString(Object o, String formatString, Locale locale)
                    {
                        return super.toString(o == null ? "" : o.toString(), formatString, locale);
                    }
                }
        );
    }

    public ThriftIdlGenerator setName(String name)
    {
        this.name = name;

        return this;
    }

    public ThriftIdlGenerator setExtendsClass(String extendsClass)
    {
        this.extendsClass = extendsClass;

        return this;
    }

    public ThriftIdlGenerator addInclude(String include)
    {
        includes.add(include);

        return this;
    }

    public ThriftIdlGenerator addNamespace(String language, String namespace)
    {
        namespaces.put(language, namespace);

        return this;
    }

    public void generate(PrintStream out)
    {
        ST template = group.getInstanceOf("generate");
        Collection<ThriftMethodMetadata> methods = getMethods();

        template.add("name", name);
        template.add("extendsClass", extendsClass);
        template.add("includes", includes);
        template.add("namespaces", namespaces);
        template.add("methods", methods);
//        template.add("documentation", metadata.getDocumentation());
        template.add("types", getTypes(methods));
        out.println(template.render());
    }

    private Collection<ThriftMethodMetadata> getMethods()
    {
        // methods are returned in some non-deterministic order, so alphabetize them
        Collection<ThriftMethodMetadata> methods = new TreeSet<>(
                new Comparator<ThriftMethodMetadata>()
                {
                    @Override
                    public int compare(ThriftMethodMetadata lhs, ThriftMethodMetadata rhs)
                    {
                        return lhs.getName().compareTo(rhs.getName());
                    }
                }
        );

        methods.addAll(metadata.getMethods().values());

        return methods;
    }

    private Collection<ThriftType> getTypes(Collection<ThriftMethodMetadata> methods)
    {
        LinkedHashSet<ThriftType> result = new LinkedHashSet<>();

        for (ThriftMethodMetadata method : methods) {
            walkType(method.getReturnType(), result);

            for (ThriftType exceptionType : method.getExceptions().values()) {
                walkType(exceptionType, result);
            }

            for (ThriftFieldMetadata fieldMetadata : method.getParameters()) {
                walkType(fieldMetadata.getType(), result);
            }
        }

        return result;
    }

    private void walkType(ThriftType type, final Set<ThriftType> alreadyWalked)
    {
        if (void.class.equals(type.getJavaType())) {
            return;
        }

        if (alreadyWalked.add(type)) {
            try {
                switch (type.getProtocolType()) {
                    case LIST:
                    case SET:
                        walkType(type.getValueType(), alreadyWalked);
                        break;
                    case MAP:
                        walkType(type.getKeyType(), alreadyWalked);
                        walkType(type.getValueType(), alreadyWalked);
                        break;
                    case STRUCT:
                        for (ThriftFieldMetadata fieldMetadata : type.getStructMetadata().getFields()) {
                            walkType(fieldMetadata.getType(), alreadyWalked);
                        }
                        break;
                    case UNKNOWN:
                        throw new IllegalArgumentException("Unknown thrift type: " + type);
                }
            }
            catch (RuntimeException e) {
                throw new IllegalStateException("Error walking type: " + type, e);
            }
        }
    }

    public static void main(String... args)
    {
        if (args.length < 1) {
            System.err.println("USAGE: [-i <include>] [-e <extends-class>] [-n <language> <namespace>] [-s <service_name>] class");
            System.err.println("EXAMPLE: ");
            System.err.println("    -i common/fb303/if/fb303.thrift \\");
            System.err.println("    -e fb303.FacebookService \\");
            System.err.println("    -n cpp facebook.puma \\");
            System.err.println("    -n java com.facebook.puma.thrift.generated \\");
            System.err.println("    -s PumaReadService \\");
            System.err.println("    com.facebook.swift.generator.puma.PumaService");
            return;
        }

        String className = args[args.length - 1];
        Class<?> serviceClass;

        try {
            serviceClass = ThriftIdlGenerator.class.getClassLoader().loadClass(className);
        }
        catch (ClassNotFoundException e) {
            System.err.println("Couldn't find class: " + className);
            System.err.println("Please make sure it's on the classpath.");
            return;
        }

        ThriftIdlGenerator generator = new ThriftIdlGenerator(serviceClass, new ThriftCatalog());
        Iterator<String> iterator = Arrays.asList(args).subList(0, args.length - 1).iterator();

        while (iterator.hasNext()) {
            String flag = iterator.next();

            try {
                switch (flag) {
                    case "-i":
                        generator.addInclude(iterator.next());
                        break;
                    case "-e":
                        generator.setExtendsClass(iterator.next());
                        break;
                    case "-n":
                        generator.addNamespace(iterator.next(), iterator.next());
                        break;
                    case "-s":
                        generator.setName(iterator.next());
                        break;
                    default:
                        System.err.println("Unexpected argument: " + flag);
                        return;
                }
            }
            catch (NoSuchElementException e) {
                System.err.println("Missing for last flag: " + flag);
            }
        }

        generator.generate(System.out);
    }
}
