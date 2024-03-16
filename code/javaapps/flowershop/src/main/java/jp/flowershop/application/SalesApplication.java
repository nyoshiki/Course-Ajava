package jp.flowershop.application;

import java.time.LocalDate;
import java.util.List;
import jp.flowershop.domain.Product;
import jp.flowershop.domain.Sales;
import jp.flowershop.exception.InputException;
import jp.flowershop.exception.StatusException;
import jp.flowershop.exception.SystemException;

/**
 * <p>
 * 販売管理アプリケーションインタフェース
 * </p>
 * 販売のユースケースを実現するアプリケーションのインタフェースを定義
 */
public interface SalesApplication {

    /**
     * <p>
     * 販売伝票の一覧を参照する
     * </p>
     * 
     * @return 販売伝票のリスト
     * @throws StatusException 登録されている販売伝票がない場合
     * @throws SystemException データベースエラーが発生した場合
     */
    public List<Sales> showSalesList() throws StatusException, SystemException;

    /**
     * <p>
     * 指定された期間の販売伝票・販売伝票明細の一覧を参照する
     * </p>
     * @param from 販売日の開始日
     * @param to 販売日の終了日
     * @return 指定された期間の販売伝票・販売伝票明細の一覧
     * @throws InputException 検索期間が年月日でない場合
     * @throws SystemException データベースエラーが発生した場合
     */
    public List<Sales> showSalesListWithDetail(LocalDate from, LocalDate to) throws  InputException, SystemException;

    /**
     * <p>
     * 販売の明細を参照する
     * </p>
     * @param salesNo 販売伝票番号
     * @return Sales 販売伝票
     * @throws InputException  販売伝票番号未指定、もしくは該当する販売伝票がない場合
     * @throws SystemException データベース接続エラーなどシステムエラーが発生した場合
     */
    public Sales showSales(String salesNo) throws InputException, SystemException;

    /**
     * <p>
     * 新たに販売を受け付ける
     * </p>
     * 新たに販売を開始するため販売伝票を新しい伝票番号で採番し作成する
     * 
     * @return Sales 新たに販売伝票番号を採番し生成した販売伝票
     * @throws SystemException データベース接続などでシステムエラーが発生した場合
     */
    public Sales newSales() throws SystemException;

    /**
     * <p>
     * 購入可能な商品の一覧を参照する
     * </p>
     * お花屋さんの初期は3商品のみの販売のためインメモリでの商品リスティングとしている。 将来の商品追加に伴い、商品管理アプリケーションに移設されるメソッド
     * 
     * @return 購入可能な商品の一覧
     */
    public List<Product> showSalesProducts();

    /**
     * <p>
     * 商品を選択する
     * </p>
     * 単価がマスタと異なる場合は指定された単価で更新する すでに標準単価で商品が選択されている分の単価も更新される
     * 
     * @param salesNo 販売伝票番号
     * @param productId 商品ID
     * @param unitPrice 単価
     * @param qty       数量
     * @return Sales 商品選択後の販売伝票
     * @throws InputException 商品選択入力項目チェックエラー
     * @throws StatusException 存在しない商品など前提条件チェックエラー
     * @throws SystemException データベース接続などでシステムエラーが発生した場合
     */
    public Sales selectProduct(String salesNo, String productId, int unitPrice, int qty)
            throws InputException, StatusException, SystemException;

    /**
     * <p>
     * 選んだ商品を全て購入対象から取り消す
     * </p>
     * 
     * @param salesNo   販売伝票番号
     * @param productId 購入対象を取り消す商品ID
     * @return Sales 販売伝票
     * @throws InputException  指定された商品IDが購入予定に選ばれていない場合
     * @throws StatusException すでに注文が確定している場合
     * @throws SystemException データベース接続などでシステムエラーが発生した場合
     */
    public Sales removeSalesDetail(String salesNo, String productId)
            throws InputException, StatusException, SystemException;

    /**
     * <p>
     * 商品の返品を受ける
     * </p>
     * 商品を受取り検品しお支払額を返金する 販売情報の記録は抹消される。 ※将来は返品の記録をするようにしていく
     * 
     * @param salesNo 販売伝票番号
     * @return 返金する額
     * @throws InputException  指定された販売伝票番号に該当する記録がない場合
     * @throws SystemException データベース接続などでシステムエラーが発生した場合
     */
    public int cancelSales(String salesNo) throws InputException, SystemException;

}
