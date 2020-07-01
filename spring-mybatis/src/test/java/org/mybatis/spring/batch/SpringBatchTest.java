

package org.mybatis.spring.batch;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.batch.domain.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

@SpringJUnitConfig(locations = { "classpath:org/mybatis/spring/batch/applicationContext.xml" })
class SpringBatchTest {

  @Autowired
  @Qualifier("pagingNoNestedItemReader")
  private MyBatisPagingItemReader<Employee> pagingNoNestedItemReader;

  @Autowired
  @Qualifier("pagingNestedItemReader")
  private MyBatisPagingItemReader<Employee> pagingNestedItemReader;

  @Autowired
  @Qualifier("cursorNoNestedItemReader")
  private MyBatisCursorItemReader<Employee> cursorNoNestedItemReader;

  @Autowired
  @Qualifier("cursorNestedItemReader")
  private MyBatisCursorItemReader<Employee> cursorNestedItemReader;

  @Autowired
  private MyBatisBatchItemWriter<Employee> writer;

  @Autowired
  private SqlSession session;

  @Test
  @Transactional
  void shouldDuplicateSalaryOfAllEmployees() throws Exception {
    List<Employee> employees = new ArrayList<>();
    Employee employee = pagingNoNestedItemReader.read();
    while (employee != null) {
      employee.setSalary(employee.getSalary() * 2);
      employees.add(employee);
      employee = pagingNoNestedItemReader.read();
    }
    writer.write(employees);

    assertThat((Integer) session.selectOne("checkSalarySum")).isEqualTo(20000);
    assertThat((Integer) session.selectOne("checkEmployeeCount")).isEqualTo(employees.size());
  }

  @Test
  @Transactional
  void checkPagingReadingWithNestedInResultMap() throws Exception {
    // This test is here to show that PagingReader can return wrong result in case of nested result maps
    List<Employee> employees = new ArrayList<>();
    Employee employee = pagingNestedItemReader.read();
    while (employee != null) {
      employee.setSalary(employee.getSalary() * 2);
      employees.add(employee);
      employee = pagingNestedItemReader.read();
    }
    writer.write(employees);

    // Assert that we have a WRONG employee count
    assertThat((Integer) session.selectOne("checkEmployeeCount")).isNotEqualTo(employees.size());
  }

  @Test
  @Transactional
  void checkCursorReadingWithoutNestedInResultMap() throws Exception {
    cursorNoNestedItemReader.doOpen();
    try {
      List<Employee> employees = new ArrayList<>();
      Employee employee = cursorNoNestedItemReader.read();
      while (employee != null) {
        employee.setSalary(employee.getSalary() * 2);
        employees.add(employee);
        employee = cursorNoNestedItemReader.read();
      }
      writer.write(employees);

      assertThat((Integer) session.selectOne("checkSalarySum")).isEqualTo(20000);
      assertThat((Integer) session.selectOne("checkEmployeeCount")).isEqualTo(employees.size());
    } finally {
      cursorNoNestedItemReader.doClose();
    }
  }

  @Test
  @Transactional
  void checkCursorReadingWithNestedInResultMap() throws Exception {
    cursorNestedItemReader.doOpen();
    try {
      List<Employee> employees = new ArrayList<>();
      Employee employee = cursorNestedItemReader.read();
      while (employee != null) {
        employee.setSalary(employee.getSalary() * 2);
        employees.add(employee);
        employee = cursorNestedItemReader.read();
      }
      writer.write(employees);

      assertThat((Integer) session.selectOne("checkSalarySum")).isEqualTo(20000);
      assertThat((Integer) session.selectOne("checkEmployeeCount")).isEqualTo(employees.size());
    } finally {
      cursorNestedItemReader.doClose();
    }
  }
}
