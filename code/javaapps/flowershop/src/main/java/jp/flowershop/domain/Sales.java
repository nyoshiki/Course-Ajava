package jp.flowershop.domain;

// FOR JPA START
import java.io.Serializable;
// FOR JPA END
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
// FOR JPA START
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import jp.flowershop.exception.InputException;
import jp.flowershop.exception.StatusException;
import jp.flowershop.exception.SystemException;
import jp.flowershop.util.DateTimeUtil;
import lombok.Getter;
import lombok.Setter;
// FOR JPA END
import lombok.ToString;

/**
 * <p>
 * 販売伝票エンティティ
 * </p>
 * <ul>
 * <li>販売伝票は１回のお客様との販売取引を扱う。
 * <li>商品ごとに値引きなど個別の販売単価と販売点数を販売伝票明細(SalesDetail)として扱う。
 * <li>販売を確定する前までは商品の増減、販売単価の変更、販売点数の増減ができる。
 * <li>商品の増減、販売単価の変更、販売点数の増減に合わせ販売伝票の合計点数、合計金額を自動再計算する。
 * <li>販売を確定したあとは変更はできないが、確定を取り消して変更することができる。
 * <li>販売確定後は商品の返品を持って返金することができる。（会計連動は未実装）
 * </ul>
 */
@Entity
@ToString
public class Sales implements Serializable {

    private static final long serialVersionUID = 4243393888959167053L;

    private static final String SALES_NO_PREFIX = "F";

    /** 販売伝票番号 */
    @Getter
    @Setter
    @Id
    @Column(unique = true, nullable = false)
    private String salesNo = null;

    /** 販売日 */
    @Getter
    @Setter
    @Column(nullable = false)
    private String salesDate = null;

    /** 販売時刻 */
    @Getter
    @Setter
    @Column(nullable = false)
    private String salesDateTime = null;

    /** 販売数合計 */
    @Getter
    @Setter
    @Column(nullable = false)
    private Integer totalQty = Integer.valueOf(0);

    /** 販売額合計 */
    @Getter
    @Setter
    @Column(nullable = false)
    private Integer totalAmount = Integer.valueOf(0);

    /** 販売確定状況ー商品と代金を交換した状態はTrue */
    @Transient
    private boolean isConfirmed = false;

    /** 販売明細 */
    @Transient
    private List<SalesDetail> salesDetailList = new ArrayList<SalesDetail>();

    /** 商品IDごとの販売明細番号インデックス（商品の販売予定個数増減の際に商品IDから販売明細の番号を検索 */
    @Transient
    private Map<String, Integer> productIdIndex = new HashMap<>();

    /**
     * <p>
     * 販売情報生成
     * </p>
     * - ビジネスロジック・ルール- <br/>
     * 販売情報の一意の番号を採番する。
     * 生成された販売伝票番号はgetSalesNo()で取得できる。
     * @throws SystemException システム例外
     */
    public Sales() throws SystemException {
        initSales();
    }

    /**
     * <p>
     * 販売情報生成
     * </p>
     * - データ保管リポジトリ連携用 - <br/>
     * リポジトリに保管されている販売情報をロードした際にリポジトリの登録データから生成する。
     * 
     * @param salesNo       販売伝票番号
     * @param salesDate     販売日
     * @param salesDateTime 販売日時
     */
    public Sales(String salesNo, String salesDate, String salesDateTime) {

        this.salesNo = salesNo;
        this.salesDate = salesDate;
        this.salesDateTime = salesDateTime;

    }

    /**
     * <p>
     * 販売明細を追加する
     * </p>
     * - データ保管リポジトリ連携用 - <br/>
     * リポジトリに保管されている販売情報をロードした際に販売明細を販売伝票に保持する。
     * 
     * @param salesDetail 販売明細
     * @exception InputException 商品明細がNullの場合
     */
    public void addSalesDetail(SalesDetail salesDetail) throws InputException {
        if (salesDetail == null) {
            throw new InputException("空の商品明細は追加できません");
        }

        this.salesDetailList.add(salesDetail);
        this.productIdIndex.put(salesDetail.getProductId(), this.salesDetailList.size() - 1);

        reCalc();
    }

    /**
     * <p>
     * 販売明細一覧を参照する
     * </p>
     * 
     * @return 販売明細一覧
     */
    public List<SalesDetail> getSalesDetailList() {
        return new ArrayList<SalesDetail>(salesDetailList);
    }

    /**
     * <p>
     * 登録されている販売明細数を参照する
     * </p>
     * 
     * @return 販売明細一覧数
     */
    public int countSalesDetail() {
        return salesDetailList.size();
    }

    /**
     * <p>
     * 販売明細取得
     * </p>
     * 
     * @param detailNo 明細番号
     * @return 販売明細
     * @exception SystemException 指定された明細番号に該当する販売伝票明細が存在しない場合
     */
    public SalesDetail getSalesDetail(final int detailNo) throws SystemException {
        try {
            return salesDetailList.get(detailNo);
        } catch (Exception e) {
            throw new SystemException("指定された明細番号の販売伝票明細はありません 明細番号:" + detailNo, e);
        }
    }

    /**
     * <p>
     * 販売番号再採番
     * </p>
     * 販売伝票番号が重複する場合は再度日時から改めて採番する。<br/>
     * データベースなどのリポジトリ保管時に番号が重複する場合に使用する。
     * 販売伝票明細がない空の新規販売伝票保管時の利用を想定しているため
     * 商品追加後の利用はできない。
     * 商品を全削除後に再採番する場合はストレージなどに残る再採番前のデータ
     * は必ず削除すること。 
     * @throws StatusException 商品がすでに追加されている場合変更不可
     * @throws SystemException 販売伝票NO採番でエラーが発生する場合（日付取得負荷以外なし）
     */
    public void renumberingSalesNo() throws StatusException, SystemException {

        if (this.salesDetailList.size() > 0) {
            throw new StatusException("商品追加後の伝票番号の再採番はできません");
        }

        initSales();
    }

    /**
     * <p>
     * 販売対象商品追加・点数追加
     * </p>
     * - ドメイン・ビジネスロジック -<br/>
     * 指定された商品を指定された単価と点数で販売対象に追加する。<br/>
     * すでに同じ商品が販売対象の時は元の販売点数に今回指定した点数を追加し今回指定した単価で再計算する。<br/>
     * （以前の単価は新たな単価で上書きされる）
     * 
     * @param productId   商品ID
     * @param productName 商品名
     * @param unitPrice   単価
     * @param increased   販売商品の追加数量
     * @return 追加・更新した販売明細情報のListインデックス番号
     * @exception InputException  追加対象の商品ID、商品名が未指定、もしくは販売単価が0円未満で指定された場合
     * @exception StatusException すでに販売が確定しており追加ができない場合
     * @exception SystemException データベース処理などでシステムエラーが発生した場合
     */
    public int addProduct(String productId, String productName, int unitPrice, final int increased)
            throws InputException, StatusException, SystemException {

        if (this.isConfirmed) {
            throw new StatusException("すでに確定している販売は変更できません");
        }

        if (productId == null || productId.isEmpty() || productName == null || productName.isEmpty()
                || unitPrice <= 0) {
            throw new InputException("商品ID、商品名なし、あるいは単価0円以下の商品は販売できません");
        }

        // すでに販売明細に登録されている商品か検索
        Integer index = productIdIndex.get(productId);

        // 今回の販売で初めて選んだ商品は新しく明細を追加
        if (index == null) {

            String salesDetailNo = getNextDetailNo();
            salesDetailList.add(new SalesDetail(this.salesNo, salesDetailNo, productId, productName,
                    unitPrice, increased));

            index = Integer.valueOf(salesDetailList.size() - 1);
            productIdIndex.put(productId, index);
        } else {
            // すでに選んでいる商品の場合はすでにある明細の単価を更新し商品の点数を増加
            salesDetailList.get(index.intValue()).setUnitPrice(unitPrice);
            salesDetailList.get(index.intValue()).addQty(increased);
        }

        reCalc();

        return index.intValue();
    }

    /**
     * <p>
     * 販売対象商品の商品点数を減らす
     * </p>
     * - ドメイン・ビジネスロジック -<br/>
     * 販売対象商品の販売点数を減らし点数がゼロになる場合は販売伝票明細を削除する。
     * 
     * @param productId 販売対象の商品ID
     * @param decreased 販売対象商品の削減数
     * @exception InputException  販売対象になっていない商品の商品IDを指定した場合
     * @exception StatusException 販売が確定している時に数量を変更した場合
     */
    public void reduceProduct(final String productId, final int decreased)
            throws InputException, StatusException {

        if (this.isConfirmed) {
            throw new StatusException("すでに確定している販売は変更できません");
        }

        final Integer index = productIdIndex.get(productId);

        if (index == null) {
            throw new InputException(
                    "販売予定でない商品の点数を減らそうとしました " + "商品ID:" + productId + " 減少点数:" + decreased);
        }

        // 選ばれている商品の点数から減らすと点数がゼロになる場合は該当商品の明細を削除
        if (salesDetailList.get(index.intValue()).getQty() <= decreased) {
            removeProduct(productId);
        } else {
            salesDetailList.get(index.intValue()).reduceQty(decreased);
        }

        reCalc();

    }

    /**
     * <p>
     * 商品を販売対象から取り消す
     * </p>
     * - ドメイン・ビジネスロジック - <br/>
     * 指定された商品IDを販売対象から取り消し販売点数、合計金額を再計算する。
     * 
     * @param productId 商品ID
     * @exception InputException  選択している商品IDが販売対象でない商品の場合
     * @exception StatusException すでに販売が確定している場合
     */
    public void removeProduct(final String productId) throws InputException, StatusException {

        if (this.isConfirmed) {
            throw new StatusException("すでに確定している販売は変更できません");
        }

        final Integer index = productIdIndex.get(productId);

        if (index == null) {
            throw new InputException("選ばれていない商品の点数を減らそうとしました。" + "商品ID:" + productId);
        }

        salesDetailList.remove(index.intValue());

        //商品INDEX再作成
        recreateProductIdIndex();

        //再計算
        reCalc();

    }

    /**
     * <p>
     * 商品IDで販売伝票明細を検索
     * </p>
     * 指定した商品IDと一致する販売伝票明細を検索する。
     * 
     * @param productId 商品ID
     * @return 商品IDに販売商品が該当した販売明細、該当しない場合はnullを返す
     */
    public SalesDetail getSalesDetailByProductId(String productId) {
        Integer index = productIdIndex.get(productId);
        if (index == null) {
            return null;
        }
        return salesDetailList.get(index);
    }

    /**
     * <p>
     * 販売を確定する
     * </p>
     * - ドメイン・ビジネスロジック - <br/>
     * 販売を確定したあとはキャンセルや返品をしないと商品や点数の増減は以後できない。
     */
    public void confirm() {
        this.isConfirmed = true;
    }

    /**
     * <p>
     * 販売の確定を取り消しする
     * </p>
     * - ドメイン・ビジネスロジック - <br/>
     * 販売の確定を取り消しし商品や点数の増減をできるようにする。
     */
    public void cancelConfirm() {
        this.isConfirmed = false;
    }

    /**
     * <p>
     * * 販売確定状況を確認する
     * </p>
     * 
     * @return 販売が確定している場合 True
     */
    public boolean isConfirmed() {
        return this.isConfirmed;
    }

    /**
     * <p>
     * 販売伝票番号を採番し生成する
     * </p>
     * 
     * @exception SystemException 販売伝票番号採番において書式エラーが発生する場合（仕様が変わらない限り発生しない）
     */
    private void initSales() throws SystemException {

        LocalDateTime now = LocalDateTime.now();
        this.salesDateTime = DateTimeUtil.toSimpleString(now);
        this.salesDate = DateTimeUtil.toSimpleString(now.toLocalDate());

        this.salesNo = SALES_NO_PREFIX + DateTimeUtil.toSimpleString(now);
    }

    /**
     * <p>
     * 販売伝票明細番号を採番する
     * </p>
     * 
     * @exception SystemException データベース処理などにおいてシステムエラーが発生する場合
     */
    private String getNextDetailNo() throws SystemException {

        if (salesDetailList.size() == 0) {
            return this.salesNo + "-001";
        }

        SalesDetail salesDetail = salesDetailList.get(salesDetailList.size() - 1);

        String lastDetailNo = salesDetail.getSalesDetailNo();

        int nextDetailNo = 0;
        try {
            nextDetailNo = Integer.parseInt(
                    lastDetailNo.substring(lastDetailNo.length() - 3, lastDetailNo.length())) + 1;
        } catch (NumberFormatException nume) {
            throw new SystemException("販売明細番号の採番でエラーが発生しました", nume);
        }

        return this.salesNo + String.format("-%3d", nextDetailNo).replace(" ", "0");

    }

    /**
     * <p>
     * 明細点数・合計金額再計算処理
     * </p>
     * 販売伝票明細の状態により販売伝票の合計点数、合計金額を再計算する。
     */
    private void reCalc() {
        this.totalQty = 0;
        this.totalAmount = 0;

        for (SalesDetail salesDetail : this.salesDetailList) {
            this.totalQty += salesDetail.getQty();
            this.totalAmount += salesDetail.getAmount();
        }
    }

    /**
     * <p>
     * 商品別明細INDEX再作成処理
     * 明細が削除された時にINDEXを再編成する
     * </p>
     */
    private void recreateProductIdIndex() {
    
        this.productIdIndex.clear();

        for (int i = 0; i < this.salesDetailList.size(); i++) {
            this.productIdIndex.put(salesDetailList.get(i).getProductId(), i);
        }
        
    }
}
