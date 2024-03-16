package jp.flowershop.controller.common;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * <p>
 * HTTPアクセスカスタマイズ設定
 * </p>
 * すべてのリクエストでCustomHttpInterceptorによる
 * HTTPリクエストのログ処理を有効にする。
 */
@Configuration
public class CustomHttpConfigurator implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // CustomHttpInterceptorをすべてのリクエストで有効にし、HTTPリクエストのログを自動取得する。
        registry.addInterceptor(new CustomHttpInterceptor()).addPathPatterns("/**");
    }

}
