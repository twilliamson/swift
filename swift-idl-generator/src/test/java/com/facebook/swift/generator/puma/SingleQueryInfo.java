package com.facebook.swift.generator.puma;

import com.facebook.swift.codec.ThriftConstructor;
import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import javax.annotation.concurrent.Immutable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Immutable
@ThriftStruct
public class SingleQueryInfo {
  private final String logicalTableName;
  private final Map<String, String> filter;
  private final List<String> selectList;

  @ThriftConstructor
  public SingleQueryInfo(
    String logicalTableName, Map<String, String> filter, List<String> selectList
  ) {
    this.logicalTableName = logicalTableName;
    this.filter = filter == null ?
      ImmutableMap.<String, String>of() : Collections.unmodifiableMap(filter);
    this.selectList = selectList == null ?
      ImmutableList.<String>of() : Collections.unmodifiableList(selectList);
  }

  @ThriftField(1)
  public String getLogicalTableName() {
    return logicalTableName;
  }

  @ThriftField(2)
  public Map<String, String> getFilter() {
    return filter;
  }

  @ThriftField(3)
  public List<String> getSelectList() {
    return selectList;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    SingleQueryInfo that = (SingleQueryInfo) o;

    return Objects.equals(logicalTableName, that.logicalTableName) &&
      Objects.equals(filter, that.filter) &&
      Objects.equals(selectList, that.selectList);
  }

  @Override
  public int hashCode() {
    int result = logicalTableName != null ? logicalTableName.hashCode() : 0;

    result = 31 * result + (filter != null ? filter.hashCode() : 0);
    result = 31 * result + (selectList != null ? selectList.hashCode() : 0);

    return result;
  }

  @Override
  public String toString() {
    return "SingleQueryInfo{" +
      "logicalTableName='" + logicalTableName + '\'' +
      ", filter=" + filter +
      ", selectList=" + selectList +
      '}';
  }
}

