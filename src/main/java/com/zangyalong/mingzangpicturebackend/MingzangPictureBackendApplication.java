package com.zangyalong.mingzangpicturebackend;

import org.apache.shardingsphere.spring.boot.ShardingSphereAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication(exclude = {ShardingSphereAutoConfiguration.class})
@MapperScan("com.zangyalong.mingzangpicturebackend.infrastructure.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
public class MingzangPictureBackendApplication {

	public static void main(String[] args) {

		SpringApplication.run(MingzangPictureBackendApplication.class, args);
	}

}
