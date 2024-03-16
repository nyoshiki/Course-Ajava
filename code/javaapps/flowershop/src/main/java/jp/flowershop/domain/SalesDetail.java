package jp.flowershop.domain;

// FOR JPA START
import java.io.Serializable;
// FOR JPA END
// FOR JPA START
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;
// FOR JPA END
import lombok.ToString;

/**
 * <p>
 * 販売伝票明細エンティティ
 * </p>
 * <ul>
 * <li>販売伝票明細はお客様との販売において商品ごとの値引きなど個別の販売単価、販売点数、小計を扱う。
 * <li>一度の販売において1つの商品で複数の販売単価は扱えない。従って一度の販売で同じ商品が複数の明細に記録されることはない。
 * <li>商品の増減、販売単価の変更、販売点数の増減に合わせ販売伝票明細の小計を自動再計算する。
 * </ul>
 */
@Entity
@ToString
public class SalesDetail implements Serializable {

    private static final long serialVersionUID = -297387477402407049L;

    /** 販売伝票明細番号 */
    @Getter
    @Setter
    @Id
    @Column(unique = true, nullable = false)
    private String salesDetailNo;

    /** 販売伝票番号 */
    @Getter
    @Setter
    @Column(nullable = false)
    private String salesNo;

    /** 商品ID */
    @Getter
    @Setter
    @Column(nullable = false)
    private String productId;

    /** 商品名 */
    @Getter
    @Setter
    @Column(nullable = false)
    private String productName;

    /** 販売単価 */
    @Getter
    @Setter
    @Column(nullable = false)
    private Integer unitPrice;

    /** 販売数 */
    @Getter
    @Setter
    @Column(nullable = false)
    private Integer qty = 0;

    /** 販売額小計 */
    @Getter
    @Setter
    @Column(nullable = false)
    private Integer amount = 0;

    /**
     * <p>
     * 販売伝票明細生成
     * </p>
     */
    public SalesDetail() {

    }

    /**
     * <p>
     * 販売明細エンティティ生成
     * </p>
     * 
     * @param salesNo       販売伝票番号
     * @param salesDetailNo 販売伝票明細番号
     * @param productId     購入対象商品の商品ID
     * @param productName   購入対象商品の商品名
     * @param unitPrice     購入対象商品の販売単価 ※商品の定価ではなく値引き等適用済みの販売単価
     * @param qty           販売数
     */
    public SalesDetail(String salesNo, String salesDetailNo, String productId, String productName,
            int unitPrice, int qty) {

        this.salesNo = salesNo;
        this.salesDetailNo = salesDetailNo;
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.qty = qty;
        this.amount = qty * unitPrice;

    }

    /**
     * <p>
     * 購入数を増やす
     * </p>
     * 対象商品の購入数を増やし、増やした購入数で明細の小計を再計算する。 <br/>
     * 計算結果はgetAmount()で参照できる。
     * 
     * @param increased 増加数
     */
    public void addQty(int increased) {
        this.qty += increased;
        reCalc();
    }

    /**
     * <p>
     * 購入数を減らす
     * </p>
     * 対象商品の購入数を減らし、減らした購入数で明細の小計を再計算する。<br/>
     * 計算結果はgetAmount()で参照できる。
     * 
     * @param decreased 減少数
     */
    public void reduceQty(int decreased) {
        this.qty -= decreased;
        reCalc();
    }

    /**
     * <p>
     * 購入数を変更する
     * </p>
     * 対象商品の購入数を指定した数に変更し、変更した購入数で明細の小計を再計算する。<br/>
     * 計算結果はgetAmount()で参照できる。
     * 
     * @param newQty 変更後の購入数
     */
    public void changeQty(int newQty) {
        this.qty = newQty;
        reCalc();
    }

    /**
     * <p>
     * 販売伝票明細再計算
     * </p>
     * 変更された販売単価、販売点数で明細の小計を再計算する。
     */
    private void reCalc() {
        this.amount = this.qty * this.unitPrice;
    }
}
