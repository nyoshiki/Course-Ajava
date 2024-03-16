package jp.flowershop.controller.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * Webアプリケーションログアウト処理
 * </p>
 */
@Slf4j // ログ処理が宣言なしでlog変数で利用可能
public class WebUIAuthLogoutHandler implements LogoutHandler {

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) {

        log.info("[LOGOUT]");

    }

}
