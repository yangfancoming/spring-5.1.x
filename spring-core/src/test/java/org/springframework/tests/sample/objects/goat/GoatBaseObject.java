package org.springframework.tests.sample.objects.goat;

/**
 * Created by Administrator on 2021/6/27.
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2021/6/27---23:21
 */
public class GoatBaseObject   {

	public String baseName;

	protected Integer baseAge;

	private Long baseLength;

	public String getBaseName() {
		return baseName;
	}

	public void setBaseName(String baseName) {
		this.baseName = baseName;
	}

	public Integer getBaseAge() {
		return baseAge;
	}

	public void setBaseAge(Integer baseAge) {
		this.baseAge = baseAge;
	}

	public Long getBaseLength() {
		return baseLength;
	}

	public void setBaseLength(Long baseLength) {
		this.baseLength = baseLength;
	}
}
