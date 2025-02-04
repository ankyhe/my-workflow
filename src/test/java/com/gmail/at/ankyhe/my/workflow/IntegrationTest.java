package com.gmail.at.ankyhe.my.workflow;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.gmail.at.ankyhe.my.workflow.config.TestConfiguration;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = { Application.class, TestConfiguration.class })
@TestPropertySource(properties = "spring.config.location=classpath:/application-test.properties")
public @interface IntegrationTest {

}
