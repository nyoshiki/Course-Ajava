package jp.flowershop.exception;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 * 入力エラー発生時例外クラス
 * </p>
 * ユーザの入力誤りを検知した場合い利用する。
 */
public class InputException extends Exception {

    private static final long serialVersionUID = 1539793924355437262L;
    private static Logger logger = Logger.getLogger(InputException.class.getName());

    /**
     * <p>
     * 入力エラー発生時例外クラス生成
     * </p>
     * @param message エラーメッセージ
     */
    public InputException(String message) {
        super(message);
        logger.info(message.replaceAll("[\r\n]",""));
    }

    /**
     * <p>
     * 入力エラー発生時例外クラス生成
     * </p>
     * @param message エラーメッセージ
     * @param t 発生源となる例外クラス
     */
    public InputException(String message, Throwable t) {
        super(message, t);
        logger.log(Level.INFO, message.replaceAll("[\r\n]",""), t);
    }
}
