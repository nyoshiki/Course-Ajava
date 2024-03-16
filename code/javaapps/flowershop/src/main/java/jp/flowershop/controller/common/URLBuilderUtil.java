package jp.flowershop.controller.common;

import java.net.URI;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.util.UriComponentsBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * 環境別URL組み立て共通処理クラス
 * </p>
 */
@Slf4j
public class URLBuilderUtil {

    /** リバースプロキシルートURL */
    // リクエストヘッダの値で認証関連の処理で発生するリダイレクトをしてしまうと
    // ヘッダ偽装のフィッシングが可能となるためスキーマとサーバを固定値でリダイレクトする
    // 通常の認証後のリダイレクトはjp.flowershop.Application#ForwardedHeaderFilterで自動補完
    public static final String REVERSE_PROXY_ROOT = "https://flowershop.local";
    
    /** Forwarded for HTTPヘッダ項目 */
    private static final String X_FORWARDED_FOR = "x-forwarded-for";

    /**
     * URL生成
     * 
     * @param request HTTPリクエスト
     * @param uri コンテキストルート以下のURI
     * @return リバースプロキシも考慮した絶対パスのURL
     */
    public static String getAbsoluteURL(HttpServletRequest request, String uri) {

        // フィッシング対策で別ドメインへリダイレクトされることを防止するためURI生成を利用する
        UriComponentsBuilder builder = UriComponentsBuilder.newInstance();

        URI location = builder.scheme(request.getScheme()).host(request.getServerName())
                .path(request.getContextPath() + uri).build().toUri();

        log.info("URL BUILD {}", location.toString());

        return location.toString();

    }

    /**
     * <p>
     * リバースプロキシ実行判定
     * </p>
     * HTTPリクエストヘッダにX-Forwarded-Forが含まれる場合リバースプロキシ経由と判断
     * @param request HTTPリクエスト
     * @return リバースプロキシ経由の場合True 
     */
    public static boolean isForwarded(HttpServletRequest request) {

        return request.getHeader(X_FORWARDED_FOR) != null;
    }
}
