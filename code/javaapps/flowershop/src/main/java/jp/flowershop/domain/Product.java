package jp.flowershop.domain;

import java.time.LocalDate;
import jp.flowershop.exception.InputException;
import jp.flowershop.exception.SystemException;
import jp.flowershop.util.DateTimeUtil;
import lombok.ToString;

/**
 * <p>
 * 商品エンティティ
 * </p>
 */
@ToString
public class Product {

    /** 商品ID最小サイズ */
    public static final int MIN_PRODUCT_ID_SIZE = 4;    
    /** 商品ID最大サイズ */
    public static final int MAX_PRODUCT_ID_SIZE = 20;   

    /** 商品名最小サイズ */
    public static final int MIN_PRODUCT_NAME_SIZE = 1;  
    /** 商品名最大サイズ */
    public static final int MAX_PRODUCT_NAME_SIZE = 50; 

    /** 単価最小額 */
    public static final int MIN_UNIT_PRICE = 1;         
    /** 単価最大額 */
    public static final int MAX_UNIT_PRICE = 999999;    

    /** 販売終了日初期値 */
    private static final String DEFAULT_END_DATE = "99991231";  

    private String productId = null;
    private String productName = null;
    private int unitPrice = 0;
    private LocalDate salesStartDate = null;
    private LocalDate salesEndDate = null;

    /**
     * <p>
     * 商品情報初期化
     * </p>
     * 
     * @param productId      商品ID
     * @param productName    商品名
     * @param unitPrice      単価
     * @param salesStartDate 販売開始日
     * @exception InputException 1.商品ID、商品名未指定 2.単価-999999未満または999999超える 3.販売開始日が有効でなかった場合
     */
    public Product(String productId, String productName, int unitPrice, String salesStartDate)
            throws InputException {

        if (productId == null || productName == null) {
            throw new InputException("商品ID、商品名は必ず指定ください");
        }

        if (productId.length() < MIN_PRODUCT_ID_SIZE || MAX_PRODUCT_ID_SIZE < productId.length()) {
            throw new InputException(
                    "商品IDは" + MIN_PRODUCT_ID_SIZE + "文字から" + MAX_PRODUCT_ID_SIZE + "文字の間で指定ください");
        }

        if (productName.length() < MIN_PRODUCT_NAME_SIZE
                || MAX_PRODUCT_NAME_SIZE < productName.length()) {
            throw new InputException(
                    "商品名は" + MIN_PRODUCT_NAME_SIZE + "文字から" + MAX_PRODUCT_NAME_SIZE + "文字の間で指定ください");
        }

        if (unitPrice < MIN_UNIT_PRICE || MAX_UNIT_PRICE < unitPrice) {
            throw new InputException("標準単価は" + MIN_UNIT_PRICE + "円から" + MAX_UNIT_PRICE + "円の間で指定ください");
        }

        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;

        try {
            this.salesStartDate = DateTimeUtil.toDate(salesStartDate);
            this.salesEndDate = DateTimeUtil.toDate(DEFAULT_END_DATE);
        } catch (SystemException e) {
            throw new InputException("販売開始日には有効な日付を指定ください", e);
        }
    }

    /**
     * <p>
     * 商品名変更
     * </p>
     * @param newName 変更後の商品名
     * @throws InputException 入力チェックエラー
     */
    public void changeName(String newName) throws InputException {

        if (newName == null) {
            throw new InputException("商品名は必ず指定ください");
        }

        if (newName.length() < MIN_PRODUCT_NAME_SIZE
                || MAX_PRODUCT_NAME_SIZE < newName.length()) {
            throw new InputException(
                "商品名は" + MIN_PRODUCT_NAME_SIZE + "文字から" + MAX_PRODUCT_NAME_SIZE + "文字の間で指定ください");
        }

        this.productName = newName;
    }

    /**
     * <p>
     * 商品標準価格変更
     * </p>
     * 
     * @param newUnitPrice 変更後の標準単価
     * @throws InputException 入力チェックエラー
     */
    public void changeUnitPrice(int newUnitPrice) throws InputException {

        if (newUnitPrice < MIN_UNIT_PRICE || MAX_UNIT_PRICE < newUnitPrice) {
            throw new InputException("標準単価は" + MIN_UNIT_PRICE + "円から" + MAX_UNIT_PRICE + "円の間で指定ください");
        }

        this.unitPrice = newUnitPrice;
    }

    /**
     * <p>
     * + 販売終了日設定
     * </p>
     * 
     * @param salesEndDate 販売終了日
     * @throws InputException 販売終了日が日付と認識できない場合
     */
    public void setSalesEndDate(String salesEndDate) throws InputException {

        try {
            this.salesEndDate = DateTimeUtil.toDate(salesEndDate);
        } catch (SystemException e) {
            throw new InputException("販売終了日には有効な日付を指定ください", e);
        }
    }

    /**
     * <p>
     * 商品ID取得
     * </p>
     * 
     * @return 商品IDを取得する
     */
    public String getProductId() {
        return this.productId;
    }

    /**
     * 商品名取得
     * 
     * @return 商品の名称を取得する
     */
    public String getProductName() {
        return this.productName;
    }

    /**
     * <p>
     * 商品単価取得
     * </p>
     * 
     * @return 商品の標準販売単価を取得する。
     */
    public int getUnitPrice() {
        return unitPrice;
    }

    /**
     * <p>
     * 販売可否判定
     * </p>
     * 
     * @return 商品の販売開始日が本日以降であり、商品の販売終了日が本日より前であれば有効としてtrue
     */
    public boolean isForSale() {

        LocalDate today = LocalDate.now();
        return (salesStartDate.isBefore(today) || salesStartDate.isEqual(today))
            && (salesEndDate.isEqual(today) || salesEndDate.isAfter(today));

    }
}
