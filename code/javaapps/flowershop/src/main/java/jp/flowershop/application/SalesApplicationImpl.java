package jp.flowershop.application;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jp.flowershop.domain.Product;
import jp.flowershop.domain.Sales;
import jp.flowershop.domain.SalesDetail;
import jp.flowershop.exception.InputException;
import jp.flowershop.exception.StatusException;
import jp.flowershop.exception.SystemException;
import jp.flowershop.repository.SalesDetailRepository;
import jp.flowershop.repository.SalesRepository;
import jp.flowershop.util.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * 販売管理アプリケーション
 * </p>
 * 販売のユースケースを実現するアプリケーション SalesやProductのビジネスエンティティ（ルール・情報） を利用して販売のユースケースを実現する
 */
@Service
@Transactional
@Slf4j
public class SalesApplicationImpl implements SalesApplication {

    // Slf4jアノテーションにて宣言不要
    // private static final Logger logger = LoggerFactory.getLogger(SalesApplicationImpl.class);

    private List<Product> salesProductList = null; // 購入可能商品一覧
    private Map<String, Product> productIdMap = null; // 商品コードをキーとしたマップ

    @Autowired
    private SalesRepository repositorySales;

    @Autowired
    private SalesDetailRepository repositoryDetail;

    /**
     * <p>
     * 販売管理アプリケーション初期化処理
     * </p>
     * @throws InputException 自動で用意する商品情報のデータ誤りの場合
     */
    public SalesApplicationImpl() throws InputException {

        // 最初の花屋さんは３つの商品のみを扱う
        salesProductList = new ArrayList<>();
        productIdMap = new HashMap<>();

        salesProductList.add(new Product("FW00000010", "バラ", 500, "20200601"));
        salesProductList.add(new Product("FW00000020", "ガーベラ", 300, "20200601"));
        salesProductList.add(new Product("FW00000030", "ラナンキュラス", 400, "20200601"));

        for (Product product : salesProductList) {
            productIdMap.put(product.getProductId(), product);
        }
    }

    /**
     * <p>
     * 販売伝票の一覧を参照する
     * </p>
     * 
     * @return 販売伝票のリスト
     * @throws StatusException 登録されている販売伝票がない場合
     * @throws SystemException データベースエラーが発生した場合
     */
    @Override
    public List<Sales> showSalesList() throws StatusException, SystemException {

        log.info("[START]" + Thread.currentThread().getStackTrace()[1].getMethodName());

        try {

            List<Sales> salesList = repositorySales.findAll();

            if (salesList == null) {
                throw new StatusException("登録されている販売伝票はありません。");
            }

            if (salesList.size() == 0) {
                throw new StatusException("登録されている販売伝票はありません。");
            }

            return salesList;

        } catch (StatusException statuse) {
            throw statuse;
        } catch (Exception e) {
            log.error("[ERROR]", e);
            throw new SystemException("販売一覧の取得処理でエラーが発生しました。", e);
        }
    }

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
    public List<Sales> showSalesListWithDetail(LocalDate from, LocalDate to) throws InputException, SystemException {

        log.info("[START]" + Thread.currentThread().getStackTrace()[1].getMethodName());

        List<Sales> salesList = repositorySales.findBySalesDateBetween(
            DateTimeUtil.toSimpleString(from), DateTimeUtil.toSimpleString(to)
        );

        for (Sales sales : salesList) {
            List<SalesDetail> detailList = repositoryDetail.findSalesDetailBySalesNo(sales.getSalesNo());

            for (SalesDetail detail : detailList) {
                sales.addSalesDetail(detail);
            }
        }

        return salesList;
    }

    /**
     * <p>
     * 販売の明細を参照する
     * </p>
     * @param salesNo 販売伝票番号
     * @return Sales 販売伝票
     * @throws InputException  販売伝票番号未指定、もしくは該当する販売伝票がない場合
     * @throws SystemException データベース接続エラーなどシステムエラーが発生した場合
     */
    @Override
    public Sales showSales(String salesNo) throws InputException, SystemException {

        log.info("[START]" + Thread.currentThread().getStackTrace()[1].getMethodName());

        try {
            if (salesNo == null) {
                throw new InputException("販売伝票番号が指定されておりません。");
            }

            // Keyにてエンティティを取得する
            // fineByIdと類似するメソッドでgetOneメソッドがあるが遅延ロードがあり
            // 直後のnullチェックができないためfindByIdを使う
            Optional<Sales> salesOptional = repositorySales.findById(salesNo);

            Sales sales = salesOptional.orElse(null);

            if (sales == null) {
                throw new InputException("指定された販売伝票番号に該当する販売はありませんでした" + " 販売伝票番号:" + salesNo);
            }

            List<SalesDetail> detailList = repositoryDetail.findSalesDetailBySalesNo(salesNo);

            for (SalesDetail detail : detailList) {
                sales.addSalesDetail(detail);
            }

            return sales;
        } catch (InputException inpute) {
            throw inpute;
        } catch (Exception e) {
            log.error("[ERROR]", e);
            throw new SystemException("販売明細の取得でエラーが発生しました。", e);
        }
    }

    /**
     * <p>
     * 新たに販売を受け付ける
     * </p>
     * 新たに販売を開始するため販売伝票を新しい伝票番号で採番し作成する
     * 
     * @return Sales 新たに販売伝票番号を採番し生成した販売伝票
     * @throws SystemException データベース接続などでシステムエラーが発生した場合
     */
    @Override
    public Sales newSales() throws SystemException {

        log.info("[START]" + Thread.currentThread().getStackTrace()[1].getMethodName());

        try {

            Sales sales = new Sales();

            repositorySales.save(sales);

            return sales;

        } catch (Exception e) {
            log.error("[ERROR]", e);
            throw new SystemException("新規販売でエラーが発生しました。", e);
        }
    }

    /**
     * <p>
     * 購入可能な商品の一覧を参照する
     * </p>
     * お花屋さんの初期は3商品のみの販売のためインメモリでの商品リスティングとしている。 将来の商品追加に伴い、商品管理アプリケーションに移設されるメソッド
     * 
     * @return 購入可能な商品の一覧
     */
    @Override
    public List<Product> showSalesProducts() {

        log.info("[START]" + Thread.currentThread().getStackTrace()[1].getMethodName());

        return new ArrayList<Product>(salesProductList);

    }

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
    @Override
    public Sales selectProduct(String salesNo, String productId, int unitPrice, int qty)
            throws InputException, StatusException, SystemException {

        log.info("[START]" + Thread.currentThread().getStackTrace()[1].getMethodName());

        try {
            Sales sales = showSales(salesNo);

            if (productId == null) {
                throw new InputException("商品コードを選択し数量を入力してください。");
            }

            Product selected = productIdMap.get(productId);
            if (selected == null) {
                throw new InputException("選択した商品コードは販売中ではありません " + "商品コード:" + productId);
            }

            int editDetailNo = sales.addProduct(selected.getProductId(), selected.getProductName(),
                    unitPrice, qty);
            SalesDetail targetDetail = sales.getSalesDetail(editDetailNo);

            repositoryDetail.save(targetDetail);

            repositorySales.save(sales);

            return sales;

        } catch (InputException inpute) {
            throw inpute;
        } catch (StatusException statuse) {
            throw statuse;
        } catch (SystemException syse) {
            throw syse;
        } catch (Exception e) {
            log.error("[ERROR]", e);
            throw new SystemException("商品の選択でエラーが発生しました。", e);
        }
    }

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
    @Override
    public Sales removeSalesDetail(String salesNo, String productId)
            throws InputException, StatusException, SystemException {

        log.info("[START]" + Thread.currentThread().getStackTrace()[1].getMethodName());

        try {
            Sales sales = showSales(salesNo);

            SalesDetail salesDetail = sales.getSalesDetailByProductId(productId);

            if (salesDetail == null) {
                throw new InputException("選ばれていない商品の点数を減らそうとしました " + "商品ID:" + productId);
            }

            repositoryDetail.delete(salesDetail);

            sales.removeProduct(productId);

            repositorySales.save(sales);

            return sales;
        } catch (InputException inpute) {
            throw inpute;
        } catch (StatusException statuse) {
            log.error("[ERROR]", statuse);
            throw statuse;
        } catch (SystemException syse) {
            throw syse;
        } catch (Exception e) {
            log.error("[ERROR]", e);
            throw new SystemException("商品の取消処理でエラーが発生しました。", e);
        }
    }

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
    @Override
    public int cancelSales(String salesNo) throws InputException, SystemException {

        log.info("[START]" + Thread.currentThread().getStackTrace()[1].getMethodName());

        try {
            // データを保管しているリポジトリからSalesエンティティを照会
            Sales sales = showSales(salesNo);

            // 返金額を算定
            int repaymentAmount = sales.getTotalAmount();

            // 販売情報を抹消
            repositoryDetail.deleteBySalesNo(sales.getSalesNo());
            repositorySales.delete(sales);

            // 返金する
            return repaymentAmount;
        } catch (InputException inpute) {
            throw inpute;
        } catch (SystemException syse) {
            throw syse;
        } catch (Exception e) {
            log.error("[ERROR]", e);
            throw new SystemException("返品時にエラーが発生しました。", e);
        }
    }
}
