package com.slyak.es.config;

import com.slyak.es.controller.BaseController;
import com.slyak.es.util.RequestUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.List;
import java.util.Map;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        exceptionResolvers.add((request, response, handler, ex) -> {
            Map<String, Object> ret = BaseController.fail(ex).toMap();
            if (RequestUtils.isRestRequest(request, handler)) {
                return new ModelAndView(new MappingJackson2JsonView(), ret);
            }
            ret.put("ex", ex);
            return new ModelAndView("error", ret);
        });
    }
}
