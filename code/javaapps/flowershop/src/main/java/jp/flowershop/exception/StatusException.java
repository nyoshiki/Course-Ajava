package jp.flowershop.exception;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 * 状態エラー発生時例外クラス
 * </p>
 * データベースやサーバなどの状態が、処理の前提条件を満たしていない場合のエラー
 * 発生時に利用する。。
 */
public class StatusException extends Exception {

    private static final long serialVersionUID = -742146227939251653L;
    private static Logger logger = Logger.getLogger(StatusException.class.getName());

    /**
     * <p>
     * 状態エラー発生時例外クラス生成
     * </p>
     * @param message エラーメッセージ
     */
    public StatusException(String message) {
        super(message);
        logger.warning(message);
    }

    /**
     * <p>
     * 状態エラー発生時例外クラス生成
     * </p>
     * @param message エラーメッセージ
     * @param t 発生源となる例外クラス
     */
    public StatusException(String message, Throwable t) {
        super(message, t);
        logger.log(Level.WARNING, message.replaceAll("[\r\n]",""), t);
    }
}
