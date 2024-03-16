package jp.flowershop.controller.common;

import java.io.IOException;
import java.util.UUID;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

/**
 * <p>
 * HTTPリクエストログ出力対象フィルタリング処理
 * </p>
 * 静的コンテンツの場合はHTTPリクエストのログを出力しないようにフィルタリングする。
 */
@Component
public class HttpLoggingFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = ((HttpServletRequest) request);

        if (isStatic(httpRequest.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }

        String requestId = "REQID:" + UUID.randomUUID();

        long start = HttpLogger.logRequest(requestId, httpRequest);

        chain.doFilter(request, response);

        HttpLogger.logResponse(requestId, ((HttpServletResponse) response), start);

    }

    private boolean isStatic(String uri) {
        return uri.contains("/js/") || uri.contains("/css/") || uri.contains("/fonts/")
                || uri.contains("/images/");
    }

}
