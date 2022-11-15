package com.baidu.base.swagger;


import com.baidu.base.exception.Errors;
import com.baidu.base.language.LocalSource;
import com.google.common.collect.Lists;
import io.swagger.models.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.AlternateTypeRules;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Configuration  //声明这是一个注解类
@EnableSwagger2
@Component
public class SwaggerConfig {

// 访问地址：http://项目实际地址/swagger-ui.html

    @Value("${swagger.enable}")
    private Boolean enable ;

    @Autowired
    private LocalSource localSource;

    public static final String AUTHORIZATION_HEADER = "token";

    @Bean
    public Docket customDocket() {
        ParameterBuilder ticketPar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<>();
        //Token 以及Authorization 为自定义的参数，session保存的名字是哪个就可以写成那个
        ticketPar.name(AUTHORIZATION_HEADER).description("user ticket")
                .modelRef(new ModelRef("string"))
                .parameterType("header")
                .required(false).build(); //header中的ticket参数非必填，传空也可以
        pars.add(ticketPar.build());    //根据每个方法名也知道当前方法在设置什么参数

        //添加全局响应状态码
        List<ResponseMessage> responseMessageList = new ArrayList<>();
        Arrays.stream(Errors.values()).forEach(error -> responseMessageList.add(
                new ResponseMessageBuilder().code(Integer.parseInt(error.getCode())).
                        message(localSource.getMessage(error.getCode())).responseModel(
                        new ModelRef(localSource.getMessage(error.getCode()))).build()
        ));

        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                // 添加全局响应状态码
                .globalResponseMessage(RequestMethod.GET, responseMessageList)
                .globalResponseMessage(RequestMethod.POST, responseMessageList)
                .globalResponseMessage(RequestMethod.PUT, responseMessageList)
                .globalResponseMessage(RequestMethod.DELETE, responseMessageList)

                .apiInfo(apiInfo())
                // 配置API名称
                .groupName("API")
                // 配置是否启用swagger 如果是false，在浏览器将无法访问，默认是true
                .enable(enable)
                .select()
                //apis： 添加过滤条件,
//                .apis(RequestHandlerSelectors
//                        .basePackage("com.shaolin.comm"))
                .paths(PathSelectors.any())
                .build()
                .securityContexts(Lists.newArrayList(securityContext()))
                .securitySchemes(Lists.newArrayList(apiKey()));

        docket.alternateTypeRules(AlternateTypeRules.newMapRule(String.class, List.class));
        docket.alternateTypeRules(AlternateTypeRules.newMapRule(List.class, Model.class));

        return docket;
    }

    private ApiKey apiKey() {
        return new ApiKey(AUTHORIZATION_HEADER, AUTHORIZATION_HEADER, "header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                //.forPaths(PathSelectors.regex(DEFAULT_INCLUDE_PATTERN))
                .forPaths(PathSelectors.regex("^(?!auth).*$"))
                .build();
    }

    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope
                = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Lists.newArrayList(
                new SecurityReference(AUTHORIZATION_HEADER, authorizationScopes));
    }

    private ApiInfo apiInfo() {
        Contact contact = new Contact("credit",
                "localhost",
                "xxx@163.com");
        return new ApiInfoBuilder()
                .title("xxx")
                .description("接口描述")
                .contact(contact)
                .version("1.0.0")
                .license("许可：Apach 2.0")
                .build();
    }
}
