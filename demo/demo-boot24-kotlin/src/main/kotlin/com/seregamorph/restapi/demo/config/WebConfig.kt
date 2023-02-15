package com.seregamorph.restapi.demo.config

import com.seregamorph.restapi.partial.JacksonBootConfig
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@Import(JacksonBootConfig::class)
class WebConfig : WebMvcConfigurer
