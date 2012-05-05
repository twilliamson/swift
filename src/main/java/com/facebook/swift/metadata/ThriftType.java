/*
 * Copyright 2004-present Facebook. All Rights Reserved.
 */
package com.facebook.swift.metadata;

import com.facebook.swift.ThriftProtocolFieldType;
import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;

import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Set;

import static com.facebook.swift.ThriftProtocolFieldType.STRUCT;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class ThriftType {
  public static final ThriftType BOOL = new ThriftType(ThriftProtocolFieldType.BOOL, boolean.class);
  public static final ThriftType BYTE = new ThriftType(ThriftProtocolFieldType.BYTE, byte.class);
  public static final ThriftType DOUBLE =
      new ThriftType(ThriftProtocolFieldType.DOUBLE, double.class);
  public static final ThriftType I16 = new ThriftType(ThriftProtocolFieldType.I16, short.class);
  public static final ThriftType I32 = new ThriftType(ThriftProtocolFieldType.I32, int.class);
  public static final ThriftType I64 = new ThriftType(ThriftProtocolFieldType.I64, long.class);
  public static final ThriftType STRING =
      new ThriftType(ThriftProtocolFieldType.STRING, ByteBuffer.class);

  public static ThriftType struct(ThriftStructMetadata<?> structMetadata) {
    return new ThriftType(structMetadata);
  }

  public static <K, V> ThriftType map(ThriftType keyType, ThriftType valueType) {
    checkNotNull(keyType, "keyType is null");
    checkNotNull(valueType, "valueType is null");

    Type javaType = new TypeToken<Map<K, V>>() {}
        .where(new TypeParameter<K>() {}, (TypeToken<K>) TypeToken.of(keyType.getJavaType()))
        .where(new TypeParameter<V>() {}, (TypeToken<V>) TypeToken.of(valueType.getJavaType()))
        .getType();
    return new ThriftType(ThriftProtocolFieldType.MAP, javaType, keyType, valueType);
  }

  public static <E> ThriftType set(ThriftType valueType) {
    Preconditions.checkNotNull(valueType, "valueType is null");

    Type javaType = new TypeToken<Set<E>>() {}
        .where(new TypeParameter<E>() {}, (TypeToken<E>) TypeToken.of(valueType.getJavaType()))
        .getType();
    return new ThriftType(ThriftProtocolFieldType.SET, javaType, null, valueType);
  }

  public static <E> ThriftType list(ThriftType valueType) {
    checkNotNull(valueType, "valueType is null");

    Type javaType = new TypeToken<Set<E>>() {}
        .where(new TypeParameter<E>() {}, (TypeToken<E>) TypeToken.of(valueType.getJavaType()))
        .getType();
    return new ThriftType(ThriftProtocolFieldType.LIST, javaType, null, valueType);
  }

  // public static final ThriftType ENUM = new ThriftFieldType(ThriftProtocolFieldType.ENUM);

  private final ThriftProtocolFieldType protocolType;
  private final Type javaType;
  private final ThriftType keyType;
  private final ThriftType valueType;
  private final ThriftStructMetadata<?> structMetadata;
  private final ThriftType uncoercedType;

  private ThriftType(ThriftProtocolFieldType protocolType, Type javaType) {
    Preconditions.checkNotNull(protocolType, "protocolType is null");
    Preconditions.checkNotNull(javaType, "javaType is null");

    this.protocolType = protocolType;
    this.javaType = javaType;
    keyType = null;
    valueType = null;
    structMetadata = null;
    uncoercedType = null;
  }

  private ThriftType(
      ThriftProtocolFieldType protocolType,
      Type javaType,
      ThriftType keyType,
      ThriftType valueType
  ) {
    Preconditions.checkNotNull(protocolType, "protocolType is null");
    Preconditions.checkNotNull(javaType, "javaType is null");
    Preconditions.checkNotNull(valueType, "valueType is null");

    this.protocolType = protocolType;
    this.javaType = javaType;
    this.keyType = keyType;
    this.valueType = valueType;
    this.structMetadata = null;
    this.uncoercedType = null;
  }

  private ThriftType(ThriftStructMetadata<?> structMetadata) {
    Preconditions.checkNotNull(structMetadata, "structMetadata is null");

    this.protocolType = STRUCT;
    this.javaType = structMetadata.getStructClass();
    keyType = null;
    valueType = null;
    this.structMetadata = structMetadata;
    this.uncoercedType = null;
  }

  public ThriftType(ThriftType uncoercedType, Type javaType) {

    this.javaType = javaType;
    this.uncoercedType = uncoercedType;

    this.protocolType = uncoercedType.getProtocolType();
    keyType = null;
    valueType = null;
    structMetadata = null;
  }

  public Type getJavaType() {
    return javaType;
  }

  public ThriftProtocolFieldType getProtocolType() {
    return protocolType;
  }

  public ThriftType getKeyType() {
    checkState(keyType != null, "%s does not have a key", protocolType);
    return keyType;
  }

  public ThriftType getValueType() {
    checkState(valueType != null, "%s does not have a value", protocolType);
    return valueType;
  }

  public ThriftStructMetadata<?> getStructMetadata() {
    checkState(structMetadata != null, "%s does not have struct metadata", protocolType);
    return structMetadata;
  }

  public boolean isCoerced() {
    return uncoercedType != null;
  }

  public ThriftType coerceTo(Type javaType) {
    if (javaType == this.javaType) {
      return this;
    }

    Preconditions.checkState(
        protocolType != ThriftProtocolFieldType.STRUCT &&
            protocolType != ThriftProtocolFieldType.SET &&
            protocolType != ThriftProtocolFieldType.LIST &&
            protocolType != ThriftProtocolFieldType.MAP,
        "Coercion is not supported for %s", protocolType );
    return new ThriftType(this, javaType);
  }

  public ThriftType getUncoercedType() {
    return uncoercedType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    final ThriftType that = (ThriftType) o;

    if (javaType != null ? !javaType.equals(that.javaType) : that.javaType != null) {
      return false;
    }
    if (protocolType != that.protocolType) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = protocolType != null ? protocolType.hashCode() : 0;
    result = 31 * result + (javaType != null ? javaType.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("ThriftType");
    sb.append("{");
    sb.append(protocolType).append(" ").append(javaType);
    if (structMetadata != null) {
      sb.append(" ").append(structMetadata.getStructClass().getName());
    } else if (keyType != null) {
      sb.append(" keyType=").append(keyType);
      sb.append(", valueType=").append(valueType);
    } else if (valueType != null) {
      sb.append(" valueType=").append(valueType);
    }
    sb.append('}');
    return sb.toString();
  }
}
