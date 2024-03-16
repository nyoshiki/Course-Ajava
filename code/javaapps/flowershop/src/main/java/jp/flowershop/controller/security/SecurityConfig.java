package jp.flowershop.controller.security;

/**
 * <p>
 * セキュリティポリシー設定
 * </p>
 */
public interface SecurityConfig {
    
    /** 認証済みユーザのロール */
    public static final String ROLE_USER = "USER";

    /** 認証可能なユーザID ※初期のアプリケーションはユーザ管理をテーブルでせず1ユーザのみメモリで保持 */
    public static final String APPROVED_USER_ID = "tencyo";
    /** passwordは初期は"ohanayasan"をjp.flowershop.util.PasswordEncodeUtilでBCryptでエンコードしている */
    public static final String APPROVED_PASS =
            "{bcrypt}$2a$10$lbM.05foPkIkjGgq6f2TpuE/Wp2d3gY3Oi5y4x8PMpBVkMEs8IwW.";

    /** APIログインURI */
    public static final String API_LOGIN_URI = "/api/login";
    /** APIログインURI・ユーザ名キーワード */
    public static final String CREDENTIAL_USERNAME = "username";
    /** APIログインURI・パスワードキーワード */
    public static final String CREDENTIAL_PASSWORD = "password";

    /** JWT認証トークンセキュアキー */
    public static final String TOKEN_SECRET_VALUE = "flowershopsecret";
    /** JWT認証ヘッダー項目 */
    public static final String AUTH_HEADER_STRING = "Authorization";
    /** JWT認証トークン項目プレフィックス */
    public static final String TOKEN_PREFIX = "Bearer ";
    /** JWT認証トークン有効時間 */
    public static final long TOKEN_EXPIRATION_TIME = 60 * 60 * 24 * 1000; // 24hours
    /** JWT認証済みロール項目 */
    public static final String AUTHORITIES_KEY = "auth";

}
