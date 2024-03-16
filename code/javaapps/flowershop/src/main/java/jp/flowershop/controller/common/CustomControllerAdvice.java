package jp.flowershop.controller.common;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * コントローラ共通処理
 * </p>
 */
@ControllerAdvice("jp.flowershop.controller")
@Slf4j // ログ処理が宣言なしでlog変数で利用可能
public class CustomControllerAdvice {

    /**
     * コントローラ共通のExceptionクラスの例外ハンドリング
     * 
     * @param e     コントローラで発生した例外
     * @param model テンプレート編集用マップ
     * @return エラーテンプレートページ（.htmlは自動で付与される）
     */
    @ExceptionHandler({Exception.class})
    public String handleException(Exception e, Model model) {

        log.error("[ERROR]Exception Handler エラーが発生しました ", e);

        model.addAttribute("errorMessage", e.getMessage());

        return "error";
    }

}
