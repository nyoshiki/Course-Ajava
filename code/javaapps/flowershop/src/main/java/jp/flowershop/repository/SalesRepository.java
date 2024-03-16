package jp.flowershop.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import jp.flowershop.domain.Sales;

/**
 * <p>
 * 販売伝票データベース処理リポジトリインタフェース
 * </p>
 */
@Repository
public interface SalesRepository extends JpaRepository<Sales, String> {

    /**
     * <p>
     * 指定した検索期間に該当する販売伝票の一覧を販売伝票テーブルから取得する
     * </p>
     * @param since 検索対象期間開始年月日
     * @param until 検索対象期間終了年月日
     * @return 指定した検索期間に該当する販売伝票の一覧
     */
    List<Sales> findBySalesDateBetween(String since, String until);
}