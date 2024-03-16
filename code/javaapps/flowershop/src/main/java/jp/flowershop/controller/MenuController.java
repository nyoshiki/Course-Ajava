package jp.flowershop.controller;

import java.time.LocalDateTime;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jp.flowershop.exception.InputException;
import jp.flowershop.exception.StatusException;
import jp.flowershop.exception.SystemException;
import jp.flowershop.util.DateTimeUtil;

/**
 * <p>
 * メニュー表示コントローラ
 * </p>
 */
@Controller
@EnableAutoConfiguration
public class MenuController {

    /**
     * <p>
     * メニュー表示処理(Query)
     * </p>
     * @param model テンプレート表示用マップ
     * @return メニュー画面のテンプレートファイル名
     * @throws InputException 入力チェックエラー
     * @throws StatusException ステータスエラー
     * @throws SystemException システム例外
     */
    @GetMapping("menu")
    protected String service(Model model) throws InputException, StatusException, SystemException {

        // 修正 viewInterfaceData.set -> model.addAttribute
        model.addAttribute("userName", "店長");
        model.addAttribute("currDate", DateTimeUtil.toViewJpString(LocalDateTime.now()));

        // templateフォルダの配下の拡張子.htmlのファイルをテンプレートに利用
        return "menuview";
    }

}
