package com.facebook.swift.generator.puma;

import com.facebook.swift.codec.ThriftConstructor;
import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.annotation.concurrent.Immutable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * name: name of the logical table
 * starttime: start time from where to start reading (unix time) in milliseconds
 * endtime: end time till when to read (unix time) in milliseconds
 * numSteps: number of times the aggregation needs to be merged
 * filter: values of the columns (logical) specified - this may include a
 * subset of the logical columns. The column names should be present
 * in the logical table.
 * selectList: the list of columns that need to be retrieved. The list of
 * columns specified should match the list of columns in one and only one
 * aggregation in the pql
 */
@Immutable
@ThriftStruct
public class ReadQueryInfo {
  private final String name;
  private final long starttime;
  private final long endtime;
  private final int windowSize;
  private final Map<String, String> filter;
  private final List<String> selectList;

  @ThriftConstructor
  public ReadQueryInfo(
    @JsonProperty("name") String name,
    @JsonProperty("starttime") long starttime,
    @JsonProperty("endtime") long endtime,
    @JsonProperty("numSteps") int windowSize,
    @JsonProperty("filter") Map<String, String> filter,
    @JsonProperty("selectList") List<String> selectList
  ) {
    this.name = name;
    this.starttime = starttime;
    this.endtime = endtime;
    this.windowSize = windowSize;
    this.filter = filter == null ?
      ImmutableMap.<String, String>of() : Collections.unmodifiableMap(filter);
    this.selectList = selectList == null ?
      ImmutableList.<String>of() : Collections.unmodifiableList(selectList);
  }

  @ThriftField(1)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  @ThriftField(2)
  @JsonProperty("starttime")
  public long getStarttime() {
    return starttime;
  }

  @ThriftField(3)
  @JsonProperty("endtime")
  public long getEndtime() {
    return endtime;
  }

  @ThriftField(4)
  @JsonProperty("numSteps")
  public int getWindowSize() {
    return windowSize;
  }

  @ThriftField(5)
  @JsonProperty("filter")
  public Map<String, String> getFilter() {
    return filter;
  }

  @ThriftField(6)
  @JsonProperty("selectList")
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

    ReadQueryInfo that = (ReadQueryInfo) o;

    return starttime == that.starttime &&
      endtime == that.endtime &&
      windowSize == that.windowSize &&
      Objects.equals(name, that.name) &&
      Objects.equals(filter, that.filter) &&
      Objects.equals(selectList, that.selectList);
  }

  @Override
  @SuppressWarnings("NumericCastThatLosesPrecision")
  public int hashCode() {
    int result = name != null ? name.hashCode() : 0;

    result = 31 * result + (int) (starttime ^ (starttime >>> 32));
    result = 31 * result + (int) (endtime ^ (endtime >>> 32));
    result = 31 * result + windowSize;
    result = 31 * result + (filter != null ? filter.hashCode() : 0);
    result = 31 * result + (selectList != null ? selectList.hashCode() : 0);

    return result;
  }

  @Override
  public String toString() {
    return "ReadQueryInfo{" +
      "name='" + name + '\'' +
      ", starttime=" + starttime +
      ", endtime=" + endtime +
      ", windowSize=" + windowSize +
      ", filter=" + filter +
      ", selectList=" + selectList +
      '}';
  }
}

