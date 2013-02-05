package com.facebook.swift.generator.puma;

import com.facebook.swift.codec.ThriftConstructor;
import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;

@ThriftStruct
public class ReadSemanticException extends Exception {
  @ThriftConstructor
  public ReadSemanticException(String message) {
    super(message);
  }

  @Override
  @ThriftField(1)
  public String getMessage() {
    return super.getMessage();
  }
}

