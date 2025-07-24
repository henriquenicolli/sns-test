package com.sns_test.demo;

import com.sns_test.demo.sns.producer.SnsProducer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		//SpringApplication.run(DemoApplication.class, args);

		SnsProducer producer = new SnsProducer();

		int threadCount = 500;

		Runnable task = () -> {
			while (true) {
				producer.sendMessage();
			}
		};

		for (int i = 0; i < threadCount; i++) {
			new Thread(task).start();
		}
	}

}
