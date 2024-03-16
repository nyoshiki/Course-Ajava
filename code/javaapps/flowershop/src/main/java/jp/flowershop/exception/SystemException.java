package jp.flowershop.exception;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 * システムエラー発生時例外クラス
 * </p>
 * ミドルウェアやサーバなどのシステムを理由にエラーが発生した時に利用する。。
 */
public class SystemException extends Exception {

    private static final long serialVersionUID = -2423796390209117640L;
    private static Logger logger = Logger.getLogger(SystemException.class.getName());

    /**
     * <p>
     * システムエラー発生時例外クラス生成
     * </p>
     * @param message エラーメッセージ
     * @param t 発生源となる例外クラス
     */
    public SystemException(final String message, final Throwable t) {
        super(message, t);
        logger.log(Level.SEVERE, message.replaceAll("[\r\n]",""), t);
    }
}
