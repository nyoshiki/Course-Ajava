package jp.flowershop.controller.security;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import jp.flowershop.controller.common.URLBuilderUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * Webアプリケーション認証失敗時ハンドリング処理
 * </p>
 * ログイン認証が失敗したらログイン画面に遷移する。
 */
@Slf4j // ログ処理が宣言なしでlog変数で利用可能
public class WebUIAuthFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException {

        if (URLBuilderUtil.isForwarded(request)) {
            // リクエストヘッダの値で認証関連の処理で発生するリダイレクトをしてしまうと
            // ヘッダ偽装のフィッシングが可能となるためスキーマとサーバを固定値でリダイレクトする
            // 通常の認証後のリダイレクトはjp.flowershop.Application#ForwardedHeaderFilterで自動補完
            log.info("[AUTH FAILURE] exception {} redirect to HTTPS LOGIN URL", exception.getMessage());

            response.sendRedirect(URLBuilderUtil.REVERSE_PROXY_ROOT 
                    + request.getContextPath() + "/start?error=invalid");

        } else {
            log.info("[AUTH FAILURE] exception {} redirect to LOGIN URL", exception.getMessage());

            getRedirectStrategy().sendRedirect(request, response, "/start?error=invalid");
        }
    }
}
