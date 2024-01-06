package com.todocode.bazarventa;



import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan(basePackages = "com.todocode.bazarventa") // Reemplaza con el paquete base correcto
public class BazarventaApplication {

	public static void main(String[] args) {
		SpringApplication.run(BazarventaApplication.class, args);
        }        

}
