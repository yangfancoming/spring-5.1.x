package com.goat.chapter201.cyclicdependence;

/**
 * Created by 64274 on 2019/8/16.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/8/16---15:26
 */
public class Room {
	private String television;
	private String airConditioner;
	private String refrigerator;
	private String washer;
	// 省略 getter/setter


	public String getTelevision() {
		return television;
	}

	public void setTelevision(String television) {
		this.television = television;
	}

	public String getAirConditioner() {
		return airConditioner;
	}

	public void setAirConditioner(String airConditioner) {
		this.airConditioner = airConditioner;
	}

	public String getRefrigerator() {
		return refrigerator;
	}

	public void setRefrigerator(String refrigerator) {
		this.refrigerator = refrigerator;
	}

	public String getWasher() {
		return washer;
	}

	public void setWasher(String washer) {
		this.washer = washer;
	}

	@Override
	public String toString() {
		return "Room{" + "television='" + television + '\'' + ", airConditioner='" + airConditioner + '\'' + ", refrigerator='" + refrigerator + '\'' + ", washer='" + washer + '\'' + '}';
	}
}
