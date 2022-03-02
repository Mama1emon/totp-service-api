package com.khokhlov.totp.config

import org.springframework.context.annotation.Configuration
import org.springframework.http.CacheControl
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.resource.EncodedResourceResolver
import java.util.concurrent.TimeUnit

@Configuration
class ResourceConfig : WebMvcConfigurer {

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/", "/index.html")
            .addResourceLocations("classpath:/static/")
            .setCacheControl(CacheControl.noCache()).resourceChain(false)
            .addResolver(EncodedResourceResolver())
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/")
            .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS).cachePublic())
            .resourceChain(false).addResolver(EncodedResourceResolver())
    }

    override fun addViewControllers(registry: ViewControllerRegistry) {
        registry.addViewController("/").setViewName("redirect:/index.html")
    }
}