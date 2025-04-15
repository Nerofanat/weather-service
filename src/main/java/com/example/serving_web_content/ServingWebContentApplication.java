package com.example.serving_web_content;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// 1. Основная аннотация Spring Boot, которая включает:
//    - @Configuration (позволяет использовать @Bean)
//    - @EnableAutoConfiguration (автоконфигурация)
//    - @ComponentScan (сканирование компонентов)
public class ServingWebContentApplication {

	public static void main(String[] args) {
		// 2. Точка входа в приложение
		// SpringApplication.run запускает Spring Boot приложение
		// Первый параметр - текущий класс
		// Второй параметр - аргументы командной строки
		SpringApplication.run(ServingWebContentApplication.class, args);
	}

}

