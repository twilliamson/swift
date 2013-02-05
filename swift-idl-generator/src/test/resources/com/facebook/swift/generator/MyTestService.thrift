/**
 * This is a test struct
 */
struct SomeStruct {
  1: string foo,
  2: double bar
}

exception SomeException {
  1: string message
}

exception SomeOtherException {
  /**
   * Some other message
   *
   * @return a message
   */
  1: string message
}

enum SomeEnum {
  OPTION_1 = 1,
  OPTION_A = 2
}

/**
 * This is a test service
 */
service MyTestService {
  SomeStruct complex(1: SomeStruct foo, 2: SomeEnum bar, 3: i32 hello)
    throws (1: SomeException e1, 4: Other e4);

  /**
   * This method has arguments
   * (and a multi-line comment)
   */
  string hasArguments(1: i32 foo, 2: string bar);

  i64 simple();

  double throwsException()
    throws (1: SomeException e1);
}
