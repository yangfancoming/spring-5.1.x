

package org.mybatis.spring.scan;

import org.apache.ibatis.jdbc.SQL;

import java.util.function.Supplier;

public class ScanClass1 {

  public static class StaticInnerClass {

  }

  public class InnerClass {

  }

  public enum InnerEnum {

  }

  public @interface InnerAnnotation {

  }

  public String createSqlUsingAnonymousClass() {
    return new SQL() {
      {
        SELECT("a");
        FROM("test1");
      }
    }.toString();
  }

  public Supplier<String> createSqlSupplier() {
    return () -> "SELECT a FROM test1";
  }

}
