package jp.flowershop.controller.viewmodel;

import java.io.Serializable;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Data;

/**
 * <p>
 * 商品選択ビューモデル
 * </p>
 * ユーザインタフェースからの商品選択リクエスト<br/>
 * および、ユーザインタフェースへの表示用レスポンスを扱う。
 */
@Data
public class SelectProductModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /** HTTPテンプレート用モデル保管キーワード */
    public static final String MODEL_KEY = "selectProductModel";

    /** 販売伝票番号 */
    @NotEmpty(message = "{SelectProductModel.salesNo.Rule}")
    @Pattern(regexp = "^F[0-9]{14}", message = "{SelectProductModel.salesNo.Rule}")
    private String salesNo;

    /** 商品ID */
    // 商品は商品マスタに存在することが前提であることを示すカスタムメッセージにする
    // message={}で指定したキーでValidationMessage.propertiesに記載
    @NotEmpty(message = "{SelectProductModel.productId.NotEmpty}")
    @Size(min = 1, max = 20, message = "{SelectProductModel.productId.Size}")
    private String productId;

    /** 販売数 */
    @NotNull
    @Min(-999)
    @Max(999)
    private Integer qty;

    /** 販売単価 */
    @NotNull
    @Min(-999999)
    @Max(999999)
    private Integer unitPrice;

}
