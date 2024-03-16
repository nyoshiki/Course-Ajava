package jp.flowershop.controller;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * <p>
 * ログイン画面表示コントローラ
 * </p>
 */
@Controller
@EnableAutoConfiguration
public class AuthController {

    /**
     * <p>
     * ログイン画面表示処理(Query)
     * </p>
     * @param error Spring Security のユーザ認証で認証エラーとなる場合に引き渡される
     * @param model ログイン画面出力用マップデータ
     * @return ログイン画面のテンプレートファイル名
     */
    @GetMapping("start")
    public String service(@RequestParam(required = false, name = "error") String error, Model model) {

        // SpringではSpring Securityの機構を利用する。
        // jp.flowershop.controller.config.WebUISecurityConfig参照
        // AuthenticateManagerの認証の結果エラーが発生した場合メッセージを表示する。
        model.addAttribute("iserror", error != null);

        // templateフォルダ配下の拡張子.htmlのファイルを読み込み
        return "loginview";
    }
}
