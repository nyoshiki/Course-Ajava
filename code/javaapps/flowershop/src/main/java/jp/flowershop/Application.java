package jp.flowershop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.filter.ForwardedHeaderFilter;

/**
 * <p>
 * Flowershop Spring Bootアプリケーション
 * </p>
 */
@SpringBootApplication
public class Application {

    /**
     * <p>
     * Flowershop Spring Bootアプリケーションメイン処理
     * </p>
     * @param args 起動パラメータ
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * <p>
     * リバースプロキシのX-Forwardによる自動制御
     * </p>
     * HTTPのヘッダー情報をリバースプロキシから設定された情報があれば上書きする Spring Securityを利用した認証処理におけるリダイレクト処理は
     * jp.flowershop.controller.security.WebUIAuthSuccess/FailureHandlerで実装
     */
    @Bean
    FilterRegistrationBean<ForwardedHeaderFilter> forwardedHeaderFilter() {

        FilterRegistrationBean<ForwardedHeaderFilter> bean = new FilterRegistrationBean<>();

        // Spring Securityのフィルターより最上位
        bean.setFilter(new ForwardedHeaderFilter());
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);

        return bean;
    }

    /**
     * <p>
     * 共通リクエストログフィルタ
     * </p>
     * @return 共通リクエストログフィルタ
     */
    @Bean
    public CommonsRequestLoggingFilter logFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(10000);
        filter.setIncludeHeaders(false);
        filter.setAfterMessagePrefix("REQUEST DATA : ");
        return filter;
    }
}
