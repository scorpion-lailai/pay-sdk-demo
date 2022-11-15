package com.baidu.base.swagger;

import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.Swagger;
import io.swagger.models.properties.Property;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author xzll
 * @version 1.0
 * @date 2021/5/10 14:59
 */
@Aspect
@Component
public class SwaggerAop {

    @Pointcut(value = "execution(public * springfox.documentation.swagger2.mappers.ServiceModelToSwagger2MapperImpl.mapDocumentation(..))")
    public void point() {

    }

    @Around("point()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Swagger swagger = (Swagger) proceedingJoinPoint.proceed();

        Map<String, Model> returnValue = swagger.getDefinitions();

        returnValue.putAll(ModelCache.extra_cache);

        ModelCache.specified_cache.forEach((key, value) -> {

            String model_name = value.v1;

            String[] fields = value.v2;

            Model model = returnValue.get(model_name);

            if (model == null) {
                throw new RuntimeException("不存在的类型" + model_name);
            }

            Map<String, Property> properties = model.getProperties();

            ModelImpl newModel = new ModelImpl();

            newModel.setDescription(model.getDescription());
            for (String field : fields) {
                Property property = properties.get(field);
                if (property == null) {
                    throw new RuntimeException("不存在的属性" + field);
                }
                newModel.property(field, property);
            }

            returnValue.put(key, newModel);

        });
        return swagger;
    }
}