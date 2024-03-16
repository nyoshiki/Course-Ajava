package jp.flowershop.controller.security;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import jp.flowershop.controller.common.URLBuilderUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * Webアプリケーション認証成功時ハンドリング処理
 * </p>
 * ログイン認証が成功したらメニュー画面に遷移する。
 */
@Slf4j // ログ処理が宣言なしでlog変数で利用可能
public class WebUIAuthSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        if (URLBuilderUtil.isForwarded(request)) {
            // リクエストヘッダの値で認証関連の処理で発生するリダイレクトをしてしまうと
            // ヘッダ偽装のフィッシングが可能となるためスキーマとサーバを固定値でリダイレクトする
            // 通常の認証後のリダイレクトはjp.flowershop.Application#ForwardedHeaderFilterで自動補完
            log.info("[AUTH SUCCESS] redirect to HTTPS MENU URL"); 

            response.sendRedirect(URLBuilderUtil.REVERSE_PROXY_ROOT + request.getContextPath() + "/menu");

        } else {
            log.info("[AUTH SUCCESS] redirect to NORMAL MENU URL");  

            DefaultRedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
            redirectStrategy.sendRedirect(request, response, "/menu");
        }
    }

}
