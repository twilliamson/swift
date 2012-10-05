package com.facebook.swift.generator;

import com.facebook.swift.codec.ThriftDocumentation;
import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import com.facebook.swift.codec.metadata.ThriftCatalog;
import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.inject.Named;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class TestThriftIdlGenerator
{
    @Test
    public void testIdlGeneration()
    {
        ThriftIdlGenerator generator = new ThriftIdlGenerator(SomeService.class, new ThriftCatalog());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        generator.generate(new PrintStream(out));

        String actual = out.toString();
        String expected = "/**\n" +
                " * This is a test struct\n" +
                " */\n" +
                "struct SomeStruct {\n" +
                "  1: string foo,\n" +
                "  2: double bar\n" +
                "}\n" +
                "\n" +
                "exception SomeException {\n" +
                "  1: string message\n" +
                "}\n" +
                "\n" +
                "/**\n" +
                " * This is a test service\n" +
                " */\n" +
                "service MyTestService {\n" +
                "  SomeStruct complex(1: SomeStruct foo, 2: string bar, 3: i32 hello);\n" +
                "\n" +
                "  /**\n" +
                "   * This method has arguments\n" +
                "   * (and a multi-line comment)\n" +
                "   */\n" +
                "  string hasArguments(1: i32 foo, 2: string bar);\n" +
                "\n" +
                "  i64 simple();\n" +
                "\n" +
                "  double throwsException()\n" +
                "    throws (1: SomeException e1);\n" +
                "}\n";

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

        @ThriftMethod
        public SomeStruct complex(@Named("foo") SomeStruct foo, @Named("bar") String bar, @Named("hello") int hello);
    }

    @ThriftStruct
    @ThriftDocumentation("This is a test struct")
    @SuppressWarnings("unused")
    public static class SomeStruct
    {
        @ThriftField(1)
        public String getFoo()
        {
            return null;
        }

        @ThriftField(2)
        public double getbar()
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
}
