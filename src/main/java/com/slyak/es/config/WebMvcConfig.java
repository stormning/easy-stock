package com.slyak.es.config;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.slyak.es.controller.BaseController;
import com.slyak.es.util.RequestUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.List;
import java.util.Map;

@Configuration
@Slf4j
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        exceptionResolvers.add((request, response, handler, ex) -> {
            log.error("Error occurred :", ex);
            Map<String, Object> ret = BaseController.fail(ex).toMap();
            if (RequestUtils.isRestRequest(request, handler)) {
                return new ModelAndView(new MappingJackson2JsonView(), ret);
            }
            ret.put("ex", ex);
            return new ModelAndView("error", ret);
        });
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        Hibernate5Module h5module = new Hibernate5Module();
        h5module.disable(Hibernate5Module.Feature.USE_TRANSIENT_ANNOTATION);
        h5module.enable(Hibernate5Module.Feature.FORCE_LAZY_LOADING);
        for (HttpMessageConverter<?> mc : converters){
            if (mc instanceof MappingJackson2HttpMessageConverter || mc instanceof MappingJackson2XmlHttpMessageConverter) {
                ((AbstractJackson2HttpMessageConverter) mc).getObjectMapper().registerModule(h5module);
            }
        }
    }
}
