package jp.flowershop.controller.security;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import jp.flowershop.controller.common.URLBuilderUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * Webアプリケーションログアウト成功時ハンドリング処理
 * </p>
 * ログアウトが成功したらログイン画面に遷移する。
 */
@Slf4j // ログ処理が宣言なしでlog変数で利用可能
public class WebUIAuthLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        if (URLBuilderUtil.isForwarded(request)) {
            // リクエストヘッダの値で認証関連の処理で発生するリダイレクトをしてしまうと
            // ヘッダ偽装のフィッシングが可能となるためスキーマとサーバを固定値でリダイレクトする
            // 通常の認証後のリダイレクトはjp.flowershop.Application#ForwardedHeaderFilterで自動補完
            log.info("[LOGOUT SUCCESS HANDLER] redirect to HTTPS LOGIN URL");

            response.sendRedirect(URLBuilderUtil.REVERSE_PROXY_ROOT + request.getContextPath() + "/start?logout");

        } else {
            log.info("[LOGOUT SUCCESS HANDLER] redirect to LOGIN URL");

            DefaultRedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
            redirectStrategy.sendRedirect(request, response, "/start?logout");
        }
    }

}
