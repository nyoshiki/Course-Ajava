package jp.flowershop.util;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import jp.flowershop.exception.SystemException;

/**
 * <p>
 * 日付・日時変換ユーティリティ
 * </p>
 */
public class DateTimeUtil {

    private static final String SIMPLE_DATE_FORMAT = "yyyyMMdd";
    private static final String SIMPLE_DATETIME_FORMAT = "yyyyMMddHHmmss";
    private static final String VIEW_DATE_FORMAT = "yyyy/MM/dd";
    private static final String VIEW_DATETIME_FORMAT = "yyyy/MM/dd HH:mm:ss";
    private static final String VIEW_JP_DATE_FORMAT = "yyyy年MM月dd日";
    private static final String VIEW_JP_DATETIME_FORMAT = "yyyy年MM月dd日 HH時mm分ss秒";

    /**
     * <p>
     * 共通日付形式文字の日付変換
     * </p>
     * 
     * @param yyyyMMdd yyyyMMdd形式の文字列
     * @return 変換した日付
     * @exception SystemException 書式変換エラー
     */
    public static LocalDate toDate(String yyyyMMdd) throws SystemException {

        try {
            return LocalDate.parse(yyyyMMdd, DateTimeFormatter.ofPattern(SIMPLE_DATE_FORMAT));
        } catch (DateTimeParseException e) {
            throw new SystemException("日付変換エラー 変換元文字:" + yyyyMMdd + "はyyyyMMdd形式である必要があります", e);
        }
    }

    /**
     * <p>
     * 共通日時形式文字の日時変換
     * </p>
     * 
     * @param yyyyMMddHHmmss yyyyMMddHHmmss形式の文字列
     * @return 変換した日時
     * @exception SystemException 書式変換エラー
     */
    public static LocalDateTime toDateTime(String yyyyMMddHHmmss) throws SystemException {

        try {
            return LocalDateTime.parse(yyyyMMddHHmmss,
                    DateTimeFormatter.ofPattern(SIMPLE_DATETIME_FORMAT));
        } catch (DateTimeParseException e) {
            throw new SystemException(
                    "日付変換エラー 変換元文字:" + yyyyMMddHHmmss + "はyyyyMMddHHmmss形式である必要があります", e);
        }
    }

    /**
     * <p>
     * 日付の共通日付型文字列変換
     * </p>
     * 
     * @param date 変換対象の日付
     * @return yyyyMMdd形式に変換した日付型文字列
     * @exception SystemException 書式変換エラー
     */
    public static String toSimpleString(LocalDate date) throws SystemException {

        return format(date, SIMPLE_DATE_FORMAT);

    }

    /**
     * <p>
     * 日時の共通日時型文字列変換
     * </p>
     * 
     * @param dateTime 変換対象の日時
     * @return yyyyMMddHHmmss形式に変換した日時型文字列
     * @exception SystemException 書式変換エラー
     */
    public static String toSimpleString(LocalDateTime dateTime) throws SystemException {

        return format(dateTime, SIMPLE_DATETIME_FORMAT);
    }

    /**
     * <p>
     * 日付の表示用文字列変換
     * </p>
     * 
     * @param date 変換対象の日時
     * @return yyyy/MM/dd形式に変換した日時型文字列
     * @exception SystemException 書式変換エラー
     */
    public static String toViewString(LocalDate date) throws SystemException {

        return format(date, VIEW_DATE_FORMAT);

    }

    /**
     * <p>
     * 日時の表示用文字列変換
     * </p>
     * 
     * @param dateTime 変換対象の日時
     * @return yyyy/MM/dd HH:mm:ss形式に変換した日時型文字列
     * @exception SystemException 書式変換エラー
     */
    public static String toViewString(LocalDateTime dateTime) throws SystemException {

        return format(dateTime, VIEW_DATETIME_FORMAT);
    }

    /**
     * <p>
     * 日時の日本語表記表示用文字列変換
     * </p>
     * 
     * @param dateTime 変換対象の日付
     * @return yyyy年MM月dd日 HH時mm分ss秒の形式に変換した日時型文字列
     * @exception SystemException 書式変換エラー
     */
    public static String toViewJpString(LocalDateTime dateTime) throws SystemException {

        return format(dateTime, VIEW_JP_DATETIME_FORMAT);
    }

    /**
     * <p>
     * 日付の日本語表記文字列変換
     * </p>
     * 
     * @param date 変換対象の日付
     * @return yyyy年MM月dd日形式に変換した日付型文字列
     * @exception SystemException 書式変換エラー
     */
    public static String toViewJpString(LocalDate date) throws SystemException {

        return format(date, VIEW_JP_DATE_FORMAT);

    }

    private static String format(LocalDate date, String pattern) throws SystemException {

        try {
            if (date == null) {
                date = LocalDate.now();
            }
            return DateTimeFormatter.ofPattern(pattern).format(date);
        } catch (DateTimeException e) {
            throw new SystemException("日付変換エラー", e);
        }

    }

    private static String format(LocalDateTime dateTime, String pattern) throws SystemException {

        try {
            if (dateTime == null) {
                dateTime = LocalDateTime.now();
            }
            return DateTimeFormatter.ofPattern(pattern).format(dateTime);
        } catch (DateTimeException e) {
            throw new SystemException("日時変換エラー", e);
        }

    }
}
