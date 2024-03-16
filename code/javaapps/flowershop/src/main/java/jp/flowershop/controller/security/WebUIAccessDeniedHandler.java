package jp.flowershop.controller.security;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.csrf.MissingCsrfTokenException;
import jp.flowershop.controller.common.URLBuilderUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * Webアクセス拒否ハンドラー
 * </p>
 */
@Slf4j // ログ処理が宣言なしでlog変数で利用可能
public class WebUIAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {

        log.info("[ACCESS DENIED]:" + accessDeniedException.toString());

        if (accessDeniedException instanceof MissingCsrfTokenException) {

            if (URLBuilderUtil.isForwarded(request)) {
                // リクエストヘッダの値で認証関連の処理で発生するリダイレクトをしてしまうと
                // ヘッダ偽装のフィッシングが可能となるためスキーマとサーバを固定値でリダイレクトする
                // 通常の認証後のリダイレクトはjp.flowershop.Application#ForwardedHeaderFilterで自動補完
                log.info("[ACCESS DENIED] exception {} redirect to HTTPS LOGIN URL", 
                        accessDeniedException.getMessage());
    
                response.sendRedirect(URLBuilderUtil.REVERSE_PROXY_ROOT 
                        + request.getContextPath() + "/start");
    
            } else {
                log.info("[ACCESS DENIED] exception {} redirect to NORMAL LOGIN URL", 
                        accessDeniedException.getMessage());
            
                DefaultRedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
                redirectStrategy.sendRedirect(request, response, "/start");
            }

        } else {

            log.info("[ACCESS DENIED] load ACCESS ERROR PAGE");

            request.getRequestDispatcher("/accesserror").forward(request, response);
        }
    }

}
