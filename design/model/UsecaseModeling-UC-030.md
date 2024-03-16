# 次世代開発コースA Level1 店舗業務デジタル化 Level2 モバイル化

## 1. ユースケース・要件

### 【Level1】 L1-UC-030 閉店後に今の在庫、入荷予定数を見て翌日締め切り分の発注できる

* L1-UC-030-R010 未納の発注を発注一覧で確認できるようにする。
* L1-UC-030-R020 発注一覧で一番上に今の商品別の在庫数と未納の納品予定数の合計を確認できる。
* L1-UC-030-R030 発注は商品別に記録できれば良い。商品別に取次先と調整した発注数、仕入価格と納入予定日を記録する。
* L1-UC-030-R040 花屋で管理する発注番号は取次先、商品、発注日単位に発番して管理する。
* L1-UC-030-R050 取次先の取引番号は取次先取引番号として発注に記録できるようにする。取次先取引番号は20桁あれば足りる。

### 【Level2】 L2-UC-030 日中に今の在庫、入荷予定数を見て当日締め切り分の発注に間に合わせることができる

* L2-UC-030-R010 未納の発注を発注一覧で確認できるようにする。
* L2-UC-030-R020 発注一覧で一番上に今の商品別の在庫数と未納の納品予定数の合計を確認できる。
* L2-UC-030-R030 発注は商品別に記録できれば良い。商品別に取次先と調整した発注数、仕入価格と納入予定日を記録する。
* L2-UC-030-R040 花屋で管理する発注番号は取次先、商品、発注日単位に発番して管理する。
* L2-UC-030-R050 取次先の取引番号は取次先取引番号として発注に記録できるようにする。取次先取引番号は20桁あれば足りる。

## 2. モデリング

### 2.1. ロバストネス分析

```plantuml

@startuml

left to right direction

actor 店長
entity 在庫取引
entity 在庫
entity 購買発注伝票
entity 入荷伝票

package "L2-UC-030 日中に今の在庫、入荷予定数を見て発注できる"{

    boundary 発注一覧ページ
    boundary 発注ページ
    control 購買発注一覧検索
    control 商品別在庫数照会
    control 商品別入荷予定数照会

    店長 --> 発注一覧ページ
    発注一覧ページ -> 購買発注一覧検索
    購買発注一覧検索 <-- 購買発注伝票
    購買発注一覧検索 <-- 入荷伝票
    商品別入荷予定数照会 <- 購買発注一覧検索
    商品別入荷予定数照会 <-- 購買発注伝票
    商品別在庫数照会 <- 商品別入荷予定数照会
    商品別在庫数照会 <-- 在庫
    商品別在庫数照会 <-- 在庫取引
    発注一覧ページ <-- 商品別在庫数照会
    発注一覧ページ -> 発注ページ
    店長 <- 発注一覧ページ

}

@enduml
```

```plantuml

@startuml

left to right direction

actor 店長
entity 購買発注伝票

package "L2-UC-030 日中に今の在庫、入荷予定数を見て発注できる"{

    boundary 発注一覧ページ
    boundary 発注ページ
    control 発注

    店長 --> 発注ページ
    発注ページ --> 発注
    発注 --> 購買発注伝票
    発注ページ <- 発注
    発注一覧ページ <- 発注ページ
    店長 <-- 発注一覧ページ
}

@enduml
```

### 2.2. シーケンス分析(ブラウザ)

```plantuml
@startuml
actor 店長
boundary ブラウザ
control Webコントローラ<<Controller>>
control 発注入荷管理<<Application>>
control 在庫管理<<Application>>
entity 商品
entity 購買発注伝票
entity 購買発注状況
entity 入荷伝票
entity 在庫
entity 在庫取引

title 発注一覧照会

店長 -> ブラウザ : メニューページ\n発注管理
ブラウザ -> Webコントローラ : GET\n発注一覧表示
activate Webコントローラ

    Webコントローラ -> 発注入荷管理 : 入荷予定日が対象期間の\n購買発注を検索する
    activate 発注入荷管理

        発注入荷管理 -> 購買発注伝票 : 購買発注一覧照会\nBetween入荷予定日
        activate 購買発注伝票
            購買発注伝票 -> 購買発注状況 :  Join\n購買発注
            activate 購買発注状況
            購買発注伝票 <- 購買発注状況
            deactivate 購買発注状況
            購買発注伝票 -> 入荷伝票 : Outer Join\n購買
            activate 入荷伝票
            購買発注伝票 <- 入荷伝票
            deactivate 入荷伝票
        発注入荷管理 <- 購買発注伝票
        deactivate 購買発注伝票

    Webコントローラ <- 発注入荷管理
    deactivate 発注入荷管理

    Webコントローラ -> 発注入荷管理 : 商品別の入荷予定数を照会する
    activate 発注入荷管理

        発注入荷管理 -> 購買発注伝票: 購買発注状況が\n入荷待(BOK)の\n商品別入荷予定数集計
        activate 購買発注伝票
        発注入荷管理 <- 購買発注伝票
        deactivate 購買発注伝票

    Webコントローラ <- 発注入荷管理
    deactivate 発注入荷管理

    Webコントローラ -> 在庫管理 : 商品別の現在在庫数を照会する

    note right : UC-080 在庫状況照会と同一処理

    activate 在庫管理

        在庫管理 -> 在庫管理 : 現在在庫数照会\nBy商品ID
        activate 在庫管理
            在庫管理 -> 在庫 : 現在の在庫数を商品別に計算
            activate 在庫

            在庫 -> 在庫 : 前回棚卸時の\n在庫数を\n商品別に取得

            在庫 -> 在庫取引 : 在庫取引日が\n前回棚卸日時より\n後の取引点数を\n商品別に集計
            activate 在庫取引
            在庫 <- 在庫取引
            deactivate 在庫取引

            在庫 -> 在庫 : 商品別に\n前回棚卸時の\n在庫数からそれ以後の\n取引点数を減算し\n現在の在庫数計算

            在庫管理 <- 在庫
            deactivate 在庫
        deactivate 在庫管理

    Webコントローラ <- 在庫管理
    deactivate 在庫管理

ブラウザ <- Webコントローラ : SUCCESS\n発注一覧ページ
deactivate Webコントローラ
店長 <- ブラウザ : 発注一覧ページ表示
@enduml
```

```plantuml
@startuml
actor 店長
boundary ブラウザ
control Webコントローラ<<Controller>>
control 発注入荷管理<<Application>>
control 在庫管理<<Application>>
entity 商品
entity 購買発注伝票
entity 購買発注状況

title 発注

店長 -> ブラウザ : 発注一覧ページ\n新規発注
ブラウザ -> Webコントローラ : GET\n発注表示
activate Webコントローラ
    Webコントローラ -> 発注入荷管理 : 発注商品を選ぶため\n発注可能な商品を検索する
    activate 発注入荷管理

        発注入荷管理 -> 商品 : 販売開始日<=本日\n本日<=販売終了日\n販売可能商品検索
        activate 商品
        発注入荷管理 <- 商品
        deactivate 商品

    Webコントローラ <- 発注入荷管理
    deactivate 発注入荷管理

    Webコントローラ -> Webコントローラ : 商品選択用\n発注可能商品リスト

ブラウザ <- Webコントローラ : SUCCESS\n発注ページ
deactivate Webコントローラ
店長 <- ブラウザ : 発注ページ表示

店長 -> ブラウザ : 発注ページ\n発注内容入力\n発注
ブラウザ -> Webコントローラ : POST\n発注
activate Webコントローラ
    Webコントローラ -> 発注入荷管理 : 発注する
    activate 発注入荷管理

        発注入荷管理 -> 発注入荷管理 : 販売可能商品チェック
        activate 発注入荷管理
        発注入荷管理 -> 商品 : 販売開始日<=本日\n本日<=販売終了日\n販売可能商品検索
        activate 商品
        発注入荷管理 <- 商品
        deactivate 商品
        deactivate 発注入荷管理

        発注入荷管理 -> 購買発注伝票 : 新規発注
        activate 購買発注伝票
        発注入荷管理 <- 購買発注伝票
        deactivate 購買発注伝票

        発注入荷管理 -> 購買発注状況 : 入荷待
        activate 購買発注状況
        発注入荷管理 <- 購買発注状況
        deactivate 購買発注状況

    Webコントローラ <- 発注入荷管理
    deactivate 発注入荷管理

ブラウザ <- Webコントローラ : REDIRECT\nGET 発注一覧表示
deactivate Webコントローラ

ブラウザ -> Webコントローラ : GET\n発注一覧表示
activate Webコントローラ

note right : 発注一覧表示は\n前述の通りにて省略

ブラウザ <- Webコントローラ : SUCCESS\n発注一覧ページ
deactivate Webコントローラ
店長 <- ブラウザ : 発注一覧ページ表示

@enduml
```

### 2.3. シーケンス分析(モバイル)

```plantuml
@startuml
actor 店長
boundary ブラウザ
boundary React<<Web Contents>>
control RESTコントローラ<<Controller>>
control 発注入荷管理<<Application>>
control 在庫管理<<Application>>

title 発注一覧表示

店長 -> ブラウザ : メニュー\n発注
ブラウザ -> React : GET\n発注一覧表示
activate React

    React -> React : AUTH ROUTING\n発注一覧ページ遷移
    React -> React : useEffect
    React -> React : Redux\n発注一覧取得
    React -> RESTコントローラ : 発注一覧取得API
    activate RESTコントローラ

    RESTコントローラ -> 発注入荷管理 : 入荷予定日が対象期間の\n購買発注を検索する
    activate 発注入荷管理

    note right : ブラウザでの\nユースケース流用

    RESTコントローラ <- 発注入荷管理
    deactivate 発注入荷管理

    React <- RESTコントローラ
    deactivate RESTコントローラ
    React -> React : Store\n発注一覧

    React -> React : Redux\n商品別在庫・\n入荷予定数取得
    React -> RESTコントローラ : 商品別在庫・\n入荷予定数取得API
    activate RESTコントローラ
    RESTコントローラ -> 発注入荷管理 : 商品別の入荷予定数を照会する
    activate 発注入荷管理

    note right : ブラウザでの\nユースケース流用

    RESTコントローラ <- 発注入荷管理
    deactivate 発注入荷管理

    RESTコントローラ -> 在庫管理 : 商品別の現在在庫数を照会する
    activate 在庫管理

    note right : ブラウザでの\nユースケース流用

    RESTコントローラ <- 在庫管理
    deactivate 在庫管理
    React <- RESTコントローラ
    deactivate RESTコントローラ
    React -> React : Store\n商品別入荷予定数\n商品別現在在庫数

ブラウザ <- React : 発注一覧表示
deactivate React
店長 <- ブラウザ : 発注一覧ページ表示
@enduml
```

```plantuml
@startuml
actor 店長
boundary ブラウザ
boundary React<<Web Contents>>
control RESTコントローラ<<Controller>>
control 発注入荷管理<<Application>>

title 発注

店長 -> ブラウザ : 発注一覧ページ\n＋（新規発注）
ブラウザ -> React : GET\n発注ページ表示
activate React

    React -> React : Redux\n発注可能商品取得
    React -> RESTコントローラ : 発注可能商品取得API
    activate RESTコントローラ

    RESTコントローラ -> 発注入荷管理 : 発注商品を選ぶため\n発注可能な商品を検索する
    activate 発注入荷管理

    note right : ブラウザでの\nユースケース流用

    RESTコントローラ <- 発注入荷管理
    deactivate 発注入荷管理
    React <- RESTコントローラ
    deactivate RESTコントローラ
    React -> React : Store\n商品選択用\n発注可能商品\nリスト

ブラウザ <- React : 発注ページ表示
deactivate React
店長 <- ブラウザ : 発注ページ表示

店長 -> ブラウザ : 発注ページ\n発注内容入力\n発注ボタン
ブラウザ -> React : EVENT\n発注
activate React
React -> React : Redux\n発注
React -> RESTコントローラ : 発注API
activate RESTコントローラ

    RESTコントローラ -> 発注入荷管理 : 発注する
    activate 発注入荷管理

    note right : ブラウザでの\nユースケース流用

    RESTコントローラ <- 発注入荷管理
    deactivate 発注入荷管理

React <- RESTコントローラ
deactivate RESTコントローラ
ブラウザ <- React : AUTH ROUTING\n発注一覧表示
deactivate React
ブラウザ -> React : GET\n発注一覧表示
activate React

note right : 発注一覧表示は\n前述の通りにて省略

ブラウザ <- React : 発注一覧表示
deactivate React
店長 <- ブラウザ : 発注一覧ページ表示

@enduml
```
