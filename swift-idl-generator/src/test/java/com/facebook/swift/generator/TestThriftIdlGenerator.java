package com.facebook.swift.generator;

import com.facebook.swift.codec.ThriftDocumentation;
import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import com.facebook.swift.codec.metadata.ThriftCatalog;
import com.facebook.swift.generator.puma.PumaService;
import com.facebook.swift.service.ThriftException;
import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import com.google.common.io.CharStreams;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.inject.Named;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class TestThriftIdlGenerator
{
    @Test
    public void testIdlGeneration() throws Exception
    {
        ThriftIdlGenerator generator = new ThriftIdlGenerator(SomeService.class, new ThriftCatalog());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        generator.generate(new PrintStream(out));

        String actual = out.toString();
        String expected = CharStreams.toString(new InputStreamReader(getClass().getResourceAsStream("MyTestService.thrift")));

        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testPumaIdlGeneration() throws Exception
    {
        ThriftIdlGenerator generator = new ThriftIdlGenerator(PumaService.class, new ThriftCatalog());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        generator.addInclude("common/fb303/if/fb303.thrift")
                .setExtendsClass("fb303.FacebookService")
                .addNamespace("cpp", "facebook.puma")
                .addNamespace("java", "com.facebook.puma.thrift.generated");
        generator.generate(new PrintStream(out));

        String actual = out.toString();
        String expected = CharStreams.toString(new InputStreamReader(getClass().getResourceAsStream("PumaReadService.thrift")));

        Assert.assertEquals(actual, expected);
    }

    @ThriftService("MyTestService")
    @ThriftDocumentation("This is a test service")
    @SuppressWarnings({"unused", "InterfaceNeverImplemented"})
    public static interface SomeService
    {
        @ThriftMethod
        public long simple();

        @ThriftMethod
        public float throwsException() throws SomeException;

        @ThriftMethod
        @ThriftDocumentation({"This method has arguments", "(and a multi-line comment)"})
        public String hasArguments(@Named("foo") int foo, @Named("bar") String bar);

        @ThriftMethod(
                exception = {
                        @ThriftException(id = 1, type = SomeException.class),
                        @ThriftException(id = 4, type = SomeOtherException.class, name = "Other")
                }
        )
        public SomeStruct complex(@Named("foo") SomeStruct foo, @Named("bar") SomeEnum bar, @Named("hello") int hello);
    }

    /**
     * This is a test struct
     */
    @ThriftStruct
    @SuppressWarnings("unused")
    public static class SomeStruct
    {
        @ThriftField(1)
        public String getFoo()
        {
            return null;
        }

        @ThriftField(2)
        public double getBar()
        {
            return 0;
        }
    }

    @ThriftStruct
    @SuppressWarnings("unused")
    public static class SomeException extends Exception
    {
        @ThriftField(1)
        public String getMessage()
        {
            return null;
        }
    }

    @ThriftStruct
    @SuppressWarnings("unused")
    public static class SomeOtherException extends RuntimeException
    {
        /**
         * Some other message
         *
         * @return a message
         */
        @ThriftField(1)
        public String getMessage()
        {
            return null;
        }
    }

    @ThriftStruct
    @SuppressWarnings("unused")
    public static enum SomeEnum
    {
        OPTION_1,
        OPTION_A,
    }
}
