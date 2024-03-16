package jp.flowershop.controller;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * <p>
 * システムについて表示コントローラ
 * </p>
 */
@Controller
@EnableAutoConfiguration
public class AboutController {

    /**
     * <p>
     * システムについて画面表示処理(Query)
     * </p>
     * @return システムについて画面のテンプレートファイル名
     */
    @GetMapping("about")
    protected String service() {

        // templateフォルダの拡張子.htmlのファイルをテンプレートする
        return "aboutview";

    }
}
