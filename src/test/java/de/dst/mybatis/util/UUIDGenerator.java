package de.dst.mybatis.util;

import java.util.Random;
import java.util.UUID;

public class UUIDGenerator {
	private UUIDGenerator() {
	}

	public static void main(String[] args) {
		Random rand = new Random();
		for (int i = 0; i < 10; i++) {
			UUID uuid = new UUID(rand.nextLong(), rand.nextLong());
			System.out.println(uuid.toString());
		}
	}

}
