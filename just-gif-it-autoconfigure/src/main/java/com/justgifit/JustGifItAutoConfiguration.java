package com.justgifit;

import com.justgifit.services.ConverterService;
import com.justgifit.services.GifEncoderService;
import com.justgifit.services.VideoDecoderService;
import com.madgag.gif.fmsware.AnimatedGifEncoder;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.FormContentFilter;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.filter.RequestContextFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.inject.Inject;

@Configuration
@ConditionalOnClass({FFmpegFrameGrabber.class, AnimatedGifEncoder.class})
@EnableConfigurationProperties(JustGifItProperties.class)
public class JustGifItAutoConfiguration {

    @Inject
    private JustGifItProperties properties;

    @Bean
    @ConditionalOnProperty(prefix = "com.justgifit", value = "create-result-dir")
    public Boolean createResultDirectory() {

        if (!properties.getGifLocation().exists()) {
            properties.getGifLocation().mkdir();
        }

        return true;
    }

    @Bean
    @ConditionalOnMissingBean({VideoDecoderService.class})
    public VideoDecoderService videoDecoderService() {
        return new VideoDecoderService();
    }

    @Bean
    @ConditionalOnMissingBean({GifEncoderService.class})
    public GifEncoderService gifEncoderService() {
        return new GifEncoderService();
    }

    @Bean
    @ConditionalOnMissingBean({ConverterService.class})
    public ConverterService converterService() {
        return new ConverterService();
    }


    @Configuration
    @ConditionalOnWebApplication
    public  class WebConfiguration {


        @Bean
        public WebMvcConfigurer webMvcConfigurer() {
            return new WebMvcConfigurerAdapter() {
                @Override
                public void addResourceHandlers(ResourceHandlerRegistry registry) {
                    registry.addResourceHandler("/gif/**")
                            .addResourceLocations("file:" + properties.getGifLocation());
                    super.addResourceHandlers(registry);
                }
            };
        }

            @Bean
            @ConditionalOnProperty(prefix = "com.justgifit", name = "optimize")
            public FilterRegistrationBean deregisterHiddenHttpMethodFilter (HiddenHttpMethodFilter filter){
                FilterRegistrationBean bean = new FilterRegistrationBean(filter);
                bean.setEnabled(false);
                return bean;
            }

            @Bean
            @ConditionalOnProperty(prefix = "com.justgifit", name = "optimize")
            public FilterRegistrationBean deregisterFormContentFilter (FormContentFilter filter){
                FilterRegistrationBean bean = new FilterRegistrationBean(filter);
                bean.setEnabled(false);
                return bean;
            }

            @Bean
            @ConditionalOnProperty(prefix = "com.justgifit", name = "optimize")
            public FilterRegistrationBean deregisterRequestContextFilter (RequestContextFilter filter){
                FilterRegistrationBean bean = new FilterRegistrationBean(filter);
                bean.setEnabled(false);
                return bean;
            }
        }
}

