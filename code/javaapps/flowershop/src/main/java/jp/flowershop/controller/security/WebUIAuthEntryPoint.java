package jp.flowershop.controller.security;

import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import jp.flowershop.controller.common.URLBuilderUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * Webアプリケーション認証エントリポイント拡張
 * </p>
 * Webアプリケーション認証エントリポイントの処理を拡張する
 */
@Slf4j // ログ処理が宣言なしでlog変数で利用可能
public class WebUIAuthEntryPoint extends LoginUrlAuthenticationEntryPoint {

    /**
     * 認証処理拡張
     * 
     * @param loginFormUrl ログインURL
     */
    public WebUIAuthEntryPoint(String loginFormUrl) {

        super(loginFormUrl);

        log.info("[AUTH ENTRYPOINT]LOGIN FORM:" + loginFormUrl);

    }

    /**
     * <p>
     * 認証要求ページで未認証であった場合のログインページ遷移
     * </p>
     * 認証が要求されるページのリクエストで認証が完了していない場合ログインページに遷移する。
     * @param request HTTPリクエスト
     * @param response HTTPレスポンス
     * @param exception 未認証例外
     * @return ログインページURI
     */
    protected String determineUrlToUseForThisRequest(HttpServletRequest request,
            HttpServletResponse response, AuthenticationException exception) {

        log.info("[AUTH ENTRYPOINT]DETERMINE LOGIN URL", exception);

        String redirectUrl = null;

        if (URLBuilderUtil.isForwarded(request)) {
            // リクエストヘッダの値で認証関連の処理で発生するリダイレクトをしてしまうと
            // ヘッダ偽装のフィッシングが可能となるためスキーマとサーバを固定値でリダイレクトする
            // 通常の認証後のリダイレクトはjp.flowershop.Application#ForwardedHeaderFilterで自動補完
            log.info("[AUTH FAILURE] exception {} redirect to HTTPS LOGIN URL", exception.getMessage());

            redirectUrl = URLBuilderUtil.REVERSE_PROXY_ROOT + request.getContextPath() + "/start";

        } else {

            redirectUrl = URLBuilderUtil.getAbsoluteURL(request, "/start");

        }

        return redirectUrl;

    }

    /**
     * <p>
     * ログインページURI生成
     * </p>
     * 検証環境と開発環境でログインページのURIを可変で設定する。
     * @param request HTTPリクエスト
     * @param response HTTPレスポンス
     * @param authException 認証エラー
     * @return リダイレクト先ログインページURI
     */
    protected String buildRedirectUrlToLoginPage(HttpServletRequest request,
            HttpServletResponse response, AuthenticationException authException) {

        log.info("[AUTH ENTRYPOINT]BUILD REDIRECT LOGIN URL", authException);

        String redirectUrl = request.getContextPath() + "/start";

        return redirectUrl;
    }
}
