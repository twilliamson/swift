include "common/fb303/if/fb303.thrift"

namespace cpp facebook.puma
namespace java com.facebook.puma.thrift.generated

/**
  * starttimeResultWindow: start time of the resulting window
  * <p/>
  * columnNameValueMap: map of results for the above window. The column names
  * are the keys in the map and their corresponding values are values in the map
  */
struct ReadResultQueryInfo {
  1: i64 starttimeResultWindow,
  2: map<string, string> columnNameValueMap
}

exception ReadSemanticException {
  1: string message,
}

struct SingleQueryInfo {
  1: string logicalTableName,
  2: map<string, string> filter,
  3: list<string> selectList
}

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
struct ReadQueryInfo {
  1: string name,
  2: i64 starttime,
  3: i64 endtime,
  4: i32 numSteps,
  5: map<string, string> filter,
  6: list<string> selectList,
}

/**
* Some explanation on the timezone.
* In the log line, the time is always specified in UTC (number of milliseconds from ...)
* The user also has the option of specifying his local timezone.
* Consider the following scenario:
*  Based on the time specified in UTC, the time is 2011-08-02 2:00  -- 2am August 2nd
*  However, the local timezone is 4 hours behind UTC, so based on that, the time should be
*  2011-07-01 22:00   -- 10pm August 1st in the local timezone.
* The aggregation will take this into account, and the above entry will be counted in Aug 1st,
* if it was a daily aggregation. As far as the aggregation is concerned, the entry is
* 2011-07-01 22:00 in UTC
*
* On the read side, the user cannot specify the timezone. It is assumed that the time is in UTC
**/

struct ReadQueryInfoTimeString {
  1: string name,
  2: string starttime,
  3: string endtime,
  4: i32 numSteps,
  5: map<string, string> filter,
  6: list<string> selectList,
}

/**
  * starttimeResultWindow: start time of the resulting window
  *
  * columnNameValueMap: map of results for the above window. The column names
  * are the keys in the map and their corresponding values are values in the map
  */
struct ReadResultQueryInfoTimeString {
  1: string starttimeResultWindow,
  2: map<string, string> columnNameValueMap
}

struct MergeAggregationQueryInfo {
  1: list<SingleQueryInfo> queries,
  2: i64 starttime,
  3: i64 endtime,
  4: i32 numSteps
}

service PumaReadService extends fb303.FacebookService {
  list<list<ReadResultQueryInfo>> getResult(1: list<ReadQueryInfo> reader)
    throws (1: ReadSemanticException e);

  list<list<ReadResultQueryInfoTimeString>> getResultTimeString(
    1: list<ReadQueryInfoTimeString> reader)
     throws (1: ReadSemanticException e);

  /**
    * Runs query on the list of aggregations and merges the result.
    * The ReadResultQueryInfo will use the names of the columns
    * in the last SingleQueryInfo.
    * The aggregation column in the ReadResultQueryInfo will be
    * named __MERGED_RESULT__
    */
  list<ReadResultQueryInfo> mergeQueryAggregation(
    1: MergeAggregationQueryInfo mergeAggregationQueryInfo)
    throws (1: ReadSemanticException e);

  /**
   * An approximate estimate of the latest timestamp to which puma has
   * finished processing
   * This can be used to figure out the endtime for ReadQueryInfo for a
   * particular application and scribe category combination.
   *
   * @param category: The scribe category your application is operating on
   * @param appName: The name of your application. It is currently specified
   * via the 'app_name' or 'stream_name' parameters in the config
   * @param bucketNumbers: The scribe category buckets. If you are
   * querying for data which you know goes to a particular scribe buckets,
   * this will allow you to gauge the progress of puma for those bucket.
   * This is useful in cases where you data is bucketed such that you can
   * publish results for one/few buckets independent of the other buckets
   * Specify an empty list to find the latestQueryableTime over all buckets
   *
   * @return A single timestamp (seconds since epoch) which represents the
   * minimum queryable time over all the buckets specified. A return value of 0
   * indicates no data is available yet for at least one of the buckets
   * specified
   */
   i64 latestQueryableTime(
    1: string category, 2: string appName, 3: list<i32> bucketNumbers)
     throws (1: ReadSemanticException e);

  /**
   * An approximate estimate of the latest timestamp to which puma has
   * finished processing
   * This can be used to figure out the endtime for ReadQueryInfo for a
   * particular application and scribe category combination.
   *
   * @param category: The scribe category your application is operating on
   * @param appName: The name of your application. It is currently specified
   * via the 'app_name' or 'stream_name' parameters in the config
   * @param bucketNumbers: The scribe category buckets. If you are
   * querying for data which you know goes to a particular scribe buckets,
   * this will allow you to gauge the progress of puma for those bucket.
   * This is useful in cases where you data is bucketed such that you can
   * publish results for one/few buckets independent of the other buckets
   * Specify an empty list to find the latestQueryableTime over all buckets
   *
   * @return A list of timestamps (seconds since epoch), one corresponding to
   * each bucket specified, which represents the minimum queryable time for the
   * bucket. If no data is available for a bucket the corresponding timestamp
   * returned is 0
   */
   list<i64> latestQueryableTimes(
    1: string category, 2: string appName, 3: list<i32> bucketNumbers)
     throws (1: ReadSemanticException e);


   /**
    * Get results for life time aggregations.
    */
   list<list<ReadResultQueryInfo>> getLifetimeResult(1: list<SingleQueryInfo> queries)
     throws (1: ReadSemanticException e);

   /**
    * Lifetime aggregation: merge query results from multiple sources
    *
    */
    list<ReadResultQueryInfo> mergeLifetimeResult(1: list<SingleQueryInfo> queries)
      throws (1: ReadSemanticException e);
}
