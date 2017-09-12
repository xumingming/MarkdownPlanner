package jash;

import java.io.File;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
  public static final String ROOT = new File("").getAbsolutePath();
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
