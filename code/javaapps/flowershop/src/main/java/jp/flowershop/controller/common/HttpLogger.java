package jp.flowershop.controller.common;

import java.util.Enumeration;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * HTTPリクエストログ処理
 * </p>
 * すべてのリクエストでHTTPリクエストの内容をログに出力する。
 */
@Slf4j
public class HttpLogger {

    private static final String SEP = "|";

    /**
     * <p>
     * HTTPリクエストをログ出力する
     * </p>
     * ログ出力はslf4jにより標準のログ出力
     * 
     * @param message メッセージ
     * @param request ログ対象HTTPリクエスト
     * @return ログ計測時間
     */
    public static long logRequest(String message, HttpServletRequest request) {

        StringBuffer logvalue = new StringBuffer();

        logvalue.append("REMOTE:" + request.getRemoteAddr() + SEP);
        logvalue.append("SCHEME:" + request.getScheme() + SEP);
        logvalue.append("METHOD:" + request.getMethod() + SEP);
        logvalue.append("URL:" + request.getRequestURL() + SEP);
        logvalue.append("SERVER:" + request.getServerName() + SEP);
        logvalue.append("SERVER PORT:" + request.getServerPort() + SEP);
        logvalue.append("LOCAL PORT:" + request.getLocalPort() + SEP);
        logvalue.append("ROOT:" + request.getContextPath() + SEP);
        logvalue.append("URI:" + request.getRequestURI() + SEP);
        logvalue.append("PATH:" + request.getPathInfo() + SEP);
        logvalue.append("SERVLET PATH:" + request.getServletPath() + SEP);
        logvalue.append("QUERY STRING:" + request.getQueryString() + SEP);
        logvalue.append("SID:" + request.getRequestedSessionId() + SEP);

        String key = null;

        logvalue.append("HEADERS<<" + SEP);

        Enumeration<String> e = request.getHeaderNames();
        while (e.hasMoreElements()) {
            key = (String) e.nextElement();
            logvalue.append(String.format("%s:%s " + SEP, key, request.getHeader(key)));
        }

        logvalue.append(">>" + SEP);

        logvalue.append("PARAMS<<" + SEP);

        Map<String, String[]> paramsMap = request.getParameterMap();

        for (Map.Entry<String, String[]> entry : paramsMap.entrySet()) {

            String[] vals = entry.getValue();

            for (int i = 0; i < vals.length; i++) {
                logvalue.append(String.format("%s[%d]:%s " + SEP, entry.getKey(), i, vals[i]));
            }
        }

        logvalue.append(">>" + SEP);

        log.info("== HTTP LOG REQUEST ==[{}]== {} {}== LOG END ==", message, SEP, logvalue);

        return System.currentTimeMillis();
    }


    /**
     * <p>
     * HTTP レスポンスをログ出力する
     * </p>
     * ログ出力はslf4jにより標準のログ出力
     * 
     * @param message  メッセージ
     * @param response ログ対象HTTPレスポンス
     */
    public static void logResponse(String message, HttpServletResponse response) {
        logResponse(message, response, 0);
    }

    /**
     * <p>
     * HTTP レスポンスをログ出力する
     * </p>
     * ログ出力はslf4jにより標準のログ出力
     * 
     * @param message   メッセージ
     * @param response  ログ対象HTTPレスポンス
     * @param startTime 指定された場合(0出ない場合)現在時間からの差を出力する
     */
    public static void logResponse(String message, HttpServletResponse response, long startTime) {

        StringBuffer logvalue = new StringBuffer();

        logvalue.append("HEADERS<<" + SEP);

        for (String key : response.getHeaderNames()) {
            logvalue.append(String.format("%s:%s %s", key, response.getHeader(key), SEP));
        }

        if (startTime == 0) {
            log.info(
                    "== HTTP LOG RESPONSE ==[REQUEST ID]{}{} {} [TIME]{}(ms) [STATUS]{} == LOG END ==",
                    message, SEP, logvalue, System.currentTimeMillis() - startTime,
                    response.getStatus());
        } else {
            log.info(String.format(
                    "== HTTP LOG RESPONSE ==[REQUEST ID]{}{} {} [STATUS]{} == LOG END ==", message,
                    SEP, logvalue, response.getStatus()));
        }
    }
}
