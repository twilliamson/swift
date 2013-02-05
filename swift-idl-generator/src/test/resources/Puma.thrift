struct Row {
  1:i64           timestamp,
  2:list<string>  values,
}

struct Rows {
  1:list<string>  headers,
  2:list<Row>     rows,
}

exception PumaException {
  1:string  message,
}

struct Query {
  1: string             tableName,
  2: list<string>       selectList,
  3: map<string,string> filter
  4: i64                start
  5: i64                end,
  6: optional i32       rollupRows,
  7: optional bool      skipPartialValues,
}

/**
 * Query is done over the interval [start, end), i.e., including "start", but
 * excluding "end".  "start" and "end" must be in millis since epoch.
 *
 * The rollupRows parameter allows merging of values that would otherwise be
 * returned in separate rows.  For example, if you have a table storing daily
 * uniques but wanted to get the unique count for the last week, you would
 * specify a rollupRows value of 7.
 *
 * When skipPartialValues is set to true, values that might change will not be
 * returned.  For example, an hourly aggregation row that starts at 6:00am is a
 * partial value until puma has processed all the input data before 7:00am.
 */
service Puma {
 list<Rows> executeQueries(1:string appName, 2:list<Query> queries) throws (1:PumaException exception),
}
