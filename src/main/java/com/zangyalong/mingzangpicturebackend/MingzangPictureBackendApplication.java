package com.zangyalong.mingzangpicturebackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan("com.zangyalong.mingzangpicturebackend.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
public class MingzangPictureBackendApplication {

	public static void main(String[] args) {

		SpringApplication.run(MingzangPictureBackendApplication.class, args);
	}

}
