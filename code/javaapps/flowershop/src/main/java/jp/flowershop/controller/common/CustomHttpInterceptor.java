package jp.flowershop.controller.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * HTTPリクエストハンドル処理
 * </p>
 * すべてのリクエストでHTTPリクエストの内容をログに出力する。
 */
@Slf4j // ログ処理が宣言なしでlog変数で利用可能
public class CustomHttpInterceptor implements HandlerInterceptor {

    /**
     * <p>
     * HTTPリクエストハンドル処理・ログ出力処理
     * </p>
     * すべてのリクエストでHTTPリクエストの内容をログに出力する。
     * @param request HTTPリクエスト
     * @param response HTTPレスポンス
     * @param handler HTTPメソッド
     * @return boolean true:処理継続
     * @exception Exception 処理例外発生時
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) throws Exception {

        if (handler instanceof HandlerMethod) {

            HandlerMethod handlerMethod = (HandlerMethod) handler;

            String handlerClassName = handlerMethod.getMethod().getDeclaringClass().getSimpleName();
            String handlerMethodName = handlerMethod.getMethod().getName();

            Authentication authentication = (Authentication) request.getUserPrincipal();

            if (authentication == null) {
                log.info("[PRE CONTROLLER HANDLE] NO AUTH ACCESS TO {}#{}", handlerClassName,
                        handlerMethodName);
            } else {
                log.info("[PRE CONTROLLER HANDLE] AUTH ACCESS TO {}#{}", handlerClassName,
                        handlerMethodName);
            }
        }

        log.info("[PRE CONTROLLER HANDLE]CALL DEFAULT PRE HANDLE");
        return true;

    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {

        if (modelAndView == null) {
            log.info("[POST CONTROLLER HANDLE]CALL DEFAULT POST HANDLE no model");
        } else {
            log.info("[POST CONTROLLER HANDLE]CALL DEFAULT POST HANDLE [{}}]",
                    modelAndView.toString());
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
            Object handler, Exception ex) throws Exception {

        log.info("[AFTER COMPLETION]CALL DEFAULT AFTER COMPLETION");
    }
}
