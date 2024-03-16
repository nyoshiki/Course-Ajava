package jp.flowershop.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import jp.flowershop.domain.SalesDetail;

/**
 * <p>
 * 販売伝票明細データベース処理リポジトリインタフェース
 * </p>
 */
@Repository
public interface SalesDetailRepository extends JpaRepository<SalesDetail, String> {

    /**
     * <p>
     * 指定した販売伝票番号に該当する販売伝票明細の一覧を販売伝票明細テーブルから取得する
     * </p>
     * @param salesNo 販売伝票番号
     * @return 指定した販売伝票番号に該当する販売伝票明細の一覧
     */
    public List<SalesDetail> findSalesDetailBySalesNo(String salesNo);

    /**
     * <p>
     * 指定した販売伝票番号に該当する販売伝票明細を販売伝票明細テーブルから一括で削除する
     * </p>
     * @param salesNo 販売伝票番号
     */
    public void deleteBySalesNo(String salesNo);

}
