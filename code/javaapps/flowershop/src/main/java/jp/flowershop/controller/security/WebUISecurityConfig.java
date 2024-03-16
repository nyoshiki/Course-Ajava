package jp.flowershop.controller.security;

import static jp.flowershop.controller.security.SecurityConfig.APPROVED_PASS;
import static jp.flowershop.controller.security.SecurityConfig.APPROVED_USER_ID;
import static jp.flowershop.controller.security.SecurityConfig.ROLE_USER;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * Webアプリケーションの共通セキュリティ設定
 * </p>
 * https://spring.pleiades.io/spring-security/site/docs/5.0.13.RELEASE/reference/html/headers.html
 */
@Configuration
@EnableWebSecurity
@Order(SecurityProperties.BASIC_AUTH_ORDER + 2)
@Slf4j 
public class WebUISecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(WebSecurity webSecurity) throws Exception {
        webSecurity.debug(false) // デバッグを無効にする
                .ignoring() // セキュリティコントロール除外
                .antMatchers("/images/**", "/js/**", "/css/**"); // 静的コンテンツは認証対象外
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {

        // antMatchersで指定したURIは全てのユーザにアクセスを許可し
        // それ以外の全てのURIは認証を要求する。(ログイン画面を自動で表示する。)
        httpSecurity.authorizeRequests() // 認証要否の指定
                .antMatchers("/", "/about", "/start", "/login", "/swagger-ui/**") // 認証不要URLの指定
                    .permitAll() // 全てのユーザでアクセス可能
                // SwaggerのAPIリファレンスへのアクセスは許可する
                // mvcMatchersは/swagger-uiを指定した時に/swagger-ui/や/swagger-ui/bbbも対象となる
                .mvcMatchers("/swagger-ui", "/swagger-resources", "/v3/api-docs") // 認証不要URLの指定
                    .permitAll() // 全てのユーザでアクセス可能
                .mvcMatchers("/**").hasRole(ROLE_USER) // アクセス可能ロール指定
                    .anyRequest().authenticated(); // 上記以外は認証を要求する

        httpSecurity.formLogin() // Spring Securityによる認証の有効化
                .loginPage("/start") // ログイン画面
                .loginProcessingUrl("/login") // ログイン処理URL ※デフォルトも/login
                .usernameParameter("userId") // ユーザIDのパラメータ名指定
                .passwordParameter("password") // パスワードのパラメータ名指定
                .successHandler(new WebUIAuthSuccessHandler()) // 認証成功時処理クラス指定
                .failureHandler(new WebUIAuthFailureHandler()) // 認証失敗時処理クラス指定
                .permitAll(); // 全てのユーザにアクセスを許可

        httpSecurity.logout() // ログアウトの有効化
                .invalidateHttpSession(true) // ログイン時にセッションを無効化する
                .deleteCookies("JESSIONID") // セッションをCookieからクリア
                .addLogoutHandler(new WebUIAuthLogoutHandler())
                .logoutSuccessHandler(new WebUIAuthLogoutSuccessHandler()).permitAll(); // 全てのユーザにアクセスを許可

        httpSecurity.exceptionHandling() // セキュリティ例外処理
                .accessDeniedHandler(new WebUIAccessDeniedHandler()) // 権限エラー処理クラス指定
                .authenticationEntryPoint(new WebUIAuthEntryPoint("/start")); // 未認証アクセスエラー処理クラス指定

        httpSecurity.headers() // HTTP Headerのセキュリティ設定
                .defaultsDisabled() // デフォルトのヘッダー設定を一度無効にして以降の設定のみにする
                .frameOptions() // 同じドメインからのリクエストを許可する
                .sameOrigin() // ドメインの異なるFrameの埋め込みを許可しないクリックジャック防止
                .cacheControl(); // キャッシュを無効にすることでブラウザ履歴からの認証を不可とする

        httpSecurity.csrf().csrfTokenRepository(new CookieCsrfTokenRepository());
    }

    /**
     * <p>
     * 認証処理サービス生成
     * </p>
     * 当初は店長のみ利用のためインメモリ認証の簡易実装とし、将来店員やお客様からのアクセスが増加した場合に拡張。<br/>
     * パスワードは安全性の高いBCryptを利用。 <br/>
     * 生成時はjp.flowershop.util.PasswordEncodeUtilを参考に生成する。。
     * 
     * @return UserDetailsService 認証を処理するサービスを許可するユーザID・パスワードを定義したInMemoryUserDetailsManagerで返却
     */
    @Bean
    public UserDetailsService users() {

        log.info("WebUISecurityConfig 一般WebユーザUserDetailsService設定");

        UserDetails user = User.builder().username(APPROVED_USER_ID).password(APPROVED_PASS)
                .roles(ROLE_USER).build();
        return new InMemoryUserDetailsManager(user);
    }

}
