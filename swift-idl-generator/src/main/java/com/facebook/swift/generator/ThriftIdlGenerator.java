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
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

public class ThriftIdlGenerator
{
    private final ThriftServiceMetadata metadata;
    private final STGroup group;

    public ThriftIdlGenerator(Class<?> serviceClass, ThriftCatalog catalog)
    {
        this(new ThriftServiceMetadata(serviceClass, catalog));
    }

    public ThriftIdlGenerator(ThriftServiceMetadata metadata)
    {
        this.metadata = metadata;

        URL resource = getClass().getResource("/com/facebook/swift/generator/ThriftIdlGenerator.stg");

        group = new STGroup();
        group.loadGroupFile("", resource.toExternalForm());
        group.registerRenderer(Object.class, new StringRenderer() {
            @Override
            public String toString(Object o, String formatString, Locale locale)
            {
                return super.toString(o == null ? "" : o.toString(), formatString, locale);
            }
        });
    }

    public void generate(PrintStream out)
    {
        ST template = group.getInstanceOf("generate");
        Collection<ThriftMethodMetadata> methods = getMethods();

        template.add("name", metadata.getName());
        template.add("methods", methods);
        template.add("documentation", metadata.getDocumentation());
        template.add("types", getTypes(methods));
        out.println(template.render());
    }

    private Collection<ThriftMethodMetadata> getMethods()
    {
        // methods are returned in some non-deterministic order, so alphabetize them
        Collection<ThriftMethodMetadata> methods = new TreeSet<>(new Comparator<ThriftMethodMetadata>()
        {
            @Override
            public int compare(ThriftMethodMetadata lhs, ThriftMethodMetadata rhs)
            {
                return lhs.getName().compareTo(rhs.getName());
            }
        });

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
        if (alreadyWalked.add(type)) {
            switch (type.getProtocolType()) {
                case LIST:
                case SET:
                    walkType(type.getValueType(), alreadyWalked);
                    break;
                case MAP:
                    walkType(type.getKeyType(), alreadyWalked);
                    walkType(type.getValueType(), alreadyWalked);
                    break;
                case ENUM:
                case STRUCT:
                    for (ThriftFieldMetadata fieldMetadata : type.getStructMetadata().getFields()) {
                        walkType(fieldMetadata.getType(), alreadyWalked);
                    }
                    break;
                case UNKNOWN:
                    throw new IllegalArgumentException("Unknown thrift type: " + type);
            }
        }
    }
}
