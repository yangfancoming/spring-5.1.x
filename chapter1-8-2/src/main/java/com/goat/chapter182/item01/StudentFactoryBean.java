package com.goat.chapter182.item01;

import org.springframework.beans.factory.FactoryBean;

/**
 * Created by 64274 on 2019/8/17.
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/8/17---19:41
 */
public class StudentFactoryBean implements FactoryBean<Student> {

	private String studentInfo;

	public void setStudentInfo(String studentInfo) {
		this.studentInfo = studentInfo;
	}

	// 这个Bean是我们自己new的，这里我们就可以控制Bean的创建过程了
	@Override
	public Student getObject() {
		if (studentInfo == null) throw new IllegalArgumentException("'studentInfo' is required");
		// 分割属性
		String[] splitStudentInfo = studentInfo.split(",");
		if (null == splitStudentInfo || splitStudentInfo.length != 3) {
			throw new IllegalArgumentException("'studentInfo' config error");
		}
		// 创建Student并填充属性
		Student student = new Student();
		student.setName(splitStudentInfo[0]);
		student.setAge(Integer.valueOf(splitStudentInfo[1]));
		student.setClassName(splitStudentInfo[2]);
		return student;
	}

	@Override
	public Class<?> getObjectType() {
		return StudentFactoryBean.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
}
