package myapps.solutions.huddil.utils;

public class UserType {

	public static int serviceprovider = 7;
	public static int advisor = 6;
	public static int administrator = 5;
	public static int consumer = 8;

	public static int isValidUserType(String userType) {
		switch (userType.toLowerCase()) {
			case "administrator":
				return administrator;
			case "advisor":
				return advisor;
			case "service provider":
				return serviceprovider;
			case "consumer":
				return consumer;
			default:
				return 0;
		}
	}
}
