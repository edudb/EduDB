package net.edudb.engine;

import java.util.UUID;

public class Utility {
	
	public static String generateUUID() {
		UUID uuid = UUID.randomUUID();
        return uuid.toString();
	}
	
	public static void main(String[] args) {
		System.out.println(generateUUID());
	}

}
