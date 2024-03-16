# 次世代開発コースA Level1 店舗業務デジタル化 Level2 モバイル化

## 1. ユースケース・要件

### 【Level1】 L1-UC-040 閉店後に日中連絡があった卸業者からの納入予定の変更を記録する

* L1-UC-040-R010 取次先から発注に対して、発注番号ごとに納入予定日、仕入数、仕入価格を変更できる。
* L1-UC-040-R020 発注の変更一覧は残さない。最新の発注だけ記録する。
* L1-UC-040-R030 納入予定が複数回に分かれる場合は１つの発注で管理せず発注を分けて管理する。
* L1-UC-040-R040 内部の発注番号と別に取次先の管理番号を記録できるようにする。

### 【Level2】 L2-UC-040 日中に卸業者から納入予定の変更連絡があったらすぐに記録する

* L2-UC-040-R010 取次先から発注に対して、発注番号ごとに納入予定日、仕入数、仕入価格を変更できる。
* L2-UC-040-R020 発注の変更一覧は残さない。最新の発注だけ記録する。
* L2-UC-040-R030 納入予定が複数回に分かれる場合は１つの発注で管理せず発注を分けて管理する。
* L2-UC-040-R040 内部の発注番号と別に取次先の管理番号を記録できるようにする。

## 2. モデリング

### 2.1. ロバストネス分析

```plantuml

@startuml

left to right direction

actor 店長
entity 購買発注伝票
entity 入荷伝票

package "L2-UC-040 日中に卸業者から納入予定の変更連絡があったらすぐに記録する"{

    boundary 発注一覧ページ
    boundary 発注内容ページ
    boundary 発注変更ページ
    control 購買発注照会
    control 入荷伝票照会
    control 購買発注訂正

    店長 --> 発注内容ページ
    発注内容ページ --> 購買発注照会
    購買発注照会 <-- 購買発注伝票
    入荷伝票照会 <- 購買発注照会
    入荷伝票照会 <-- 入荷伝票
    発注内容ページ <-- 入荷伝票照会
    発注内容ページ -> 発注変更ページ
    店長 <-- 発注内容ページ
    店長 --> 発注変更ページ
    発注変更ページ --> 購買発注訂正 : 変更
    購買発注訂正 --> 購買発注伝票
    発注変更ページ <-- 購買発注訂正
    発注一覧ページ <- 発注変更ページ
    店長 <-- 発注一覧ページ

}

@enduml
```

```plantuml

@startuml

left to right direction

actor 店長
entity 購買発注伝票
entity 購買発注状況
entity 入荷伝票

package "L2-UC-040 日中に卸業者から納入予定の変更連絡があったらすぐに記録する(発注取消)"{

    boundary 発注一覧ページ
    boundary 発注内容ページ
    boundary 発注変更ページ
    control 購買発注照会
    control 入荷伝票照会
    control 購買発注取消

    店長 --> 発注内容ページ
    発注内容ページ --> 購買発注照会
    購買発注照会 <-- 購買発注伝票
    入荷伝票照会 <- 購買発注照会
    入荷伝票照会 <-- 入荷伝票
    発注内容ページ <-- 入荷伝票照会
    発注内容ページ -> 発注変更ページ
    店長 <-- 発注内容ページ
    店長 --> 発注変更ページ
    発注変更ページ --> 購買発注取消 : 削除
    購買発注取消 --> 購買発注状況
    発注変更ページ <-- 購買発注取消
    発注一覧ページ <- 発注変更ページ
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
entity 商品
entity 購買発注伝票
entity 購買発注状況
entity 入荷伝票

title 発注内容照会

店長 -> ブラウザ : 発注一覧ページ\n一覧より発注選択
ブラウザ -> Webコントローラ : GET\n発注内容表示
activate Webコントローラ

    Webコントローラ -> 発注入荷管理 : 購買発注伝票番号で\n購買発注の内容を\n照会する
    activate 発注入荷管理

        発注入荷管理 -> 購買発注伝票 : 購買発注照会
        activate 購買発注伝票
            購買発注伝票 -> 購買発注状況 :  Join\n購買発注伝票番号
            activate 購買発注状況
            購買発注伝票 <- 購買発注状況
            deactivate 購買発注状況
            購買発注伝票 -> 入荷伝票 : Outer Join\n購買発注伝票番号
            activate 入荷伝票
            購買発注伝票 <- 入荷伝票
            deactivate 入荷伝票
        発注入荷管理 <- 購買発注伝票
        deactivate 購買発注伝票

    Webコントローラ <- 発注入荷管理
    deactivate 発注入荷管理

ブラウザ <- Webコントローラ : SUCCESS\n発注内容ページ
deactivate Webコントローラ
店長 <- ブラウザ : 発注内容ページ表示

@enduml
```

```plantuml
@startuml
actor 店長
boundary ブラウザ
control Webコントローラ<<Controller>>
control 発注入荷管理<<Application>>

title 発注訂正

店長 -> ブラウザ : 発注内容ページ\n変更
ブラウザ -> Webコントローラ : GET\n発注変更表示

note right : 発注変更は取引先、商品は変更不可\n処理は発注内容照会と同じ処理を流用\n変更入力可能なHTMLテンプレートのみ変更\n\n分納の場合入荷予定が別日の分を\n新たに発注する

activate Webコントローラ
    Webコントローラ -> 発注入荷管理 : 購買発注伝票番号で\n購買発注の内容を\n照会する
    activate 発注入荷管理

    note right : 発注内容照会の\nユースケース流用

    Webコントローラ <- 発注入荷管理
    deactivate 発注入荷管理

ブラウザ <- Webコントローラ : SUCCESS\n発注変更ページ
deactivate Webコントローラ
店長 <- ブラウザ : 発注変更ページ表示

@enduml
```

```plantuml
@startuml
actor 店長
boundary ブラウザ
control Webコントローラ<<Controller>>
control 発注入荷管理<<Application>>
entity 商品
entity 購買発注伝票
entity 購買発注状況
entity 入荷伝票

title 発注取消

店長 -> ブラウザ : 発注変更ページ\n削除
ブラウザ -> Webコントローラ : POST\n発注取消
activate Webコントローラ
    Webコントローラ -> 発注入荷管理 : 発注の内容 を取消する
    activate 発注入荷管理

        発注入荷管理 -> 購買発注伝票 : 購買発注照会
        activate 購買発注伝票

            購買発注伝票 -> 購買発注状況 :  Join\n購買発注伝票番号
            activate 購買発注状況
            購買発注伝票 <- 購買発注状況
            deactivate 購買発注状況
            購買発注伝票 -> 入荷伝票 : Outer Join\n購買発注伝票番号
            activate 入荷伝票
            購買発注伝票 <- 入荷伝票
            deactivate 入荷伝票

        発注入荷管理 <- 購買発注伝票
        deactivate 購買発注伝票

        発注入荷管理 -> 発注入荷管理 : 購買発注状況\n入荷待(BOK)でない\n場合取消不可

    Webコントローラ <-- 発注入荷管理 : 発注取消不可エラー
ブラウザ <-- Webコントローラ : REDIRECT\nGET 発注変更表示\nエラー表示

note left : 発注変更表示は\n前述の通り

        発注入荷管理 -> 購買発注状況 : 購買発注状況更新By購買発注番号
        note right : 状況更新による論理削除
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

title 発注内容照会

店長 -> ブラウザ : 発注一覧ページ\n一覧より発注選択
ブラウザ -> React : GET\n発注内容表示
activate React

    React -> React : AUTH ROUTING\n発注内容ページ遷移
    React -> React : useEffect
    React -> React : Redux\n発注取得
    React -> RESTコントローラ : 発注照会API
    activate RESTコントローラ

    RESTコントローラ -> 発注入荷管理 : 購買発注伝票番号で\n購買発注の内容を\n照会する
    activate 発注入荷管理

    note right : ブラウザの\nユースケースを流用

    RESTコントローラ <- 発注入荷管理
    deactivate 発注入荷管理

    React <- RESTコントローラ
    deactivate RESTコントローラ
    React -> React : Store\n発注

ブラウザ <- React : 発注内容表示
deactivate React
店長 <- ブラウザ : 発注内容ページ表示

@enduml
```

```plantuml
@startuml
actor 店長
boundary ブラウザ
boundary React<<Web Contents>>
control RESTコントローラ<<Controller>>
control 発注入荷管理<<Application>>

title 発注訂正

店長 -> ブラウザ : 発注内容ページ\n変更
ブラウザ -> React : GET\n発注変更表示
activate React

    React -> React : AUTH ROUTING\n発注変更
    React -> React : useEffect
    note right : 発注内容ページと同内容のため\nAPI呼び出しなしを検討
    React -> React : Redux\n発注取得
    React -> RESTコントローラ : 発注照会API
    activate RESTコントローラ

    RESTコントローラ -> 発注入荷管理 : 購買発注伝票番号で\n購買発注の内容を\n照会する
    activate 発注入荷管理

    note right : ブラウザの\nユースケースを流用

    RESTコントローラ <- 発注入荷管理
    deactivate 発注入荷管理

    React <- RESTコントローラ
    deactivate RESTコントローラ
    React -> React : Store\n発注

ブラウザ <- React : 発注変更表示
deactivate React
店長 <- ブラウザ : 発注変更ページ表示

@enduml
```

```plantuml
@startuml
actor 店長
boundary ブラウザ
boundary React<<Web Contents>>
control RESTコントローラ<<Controller>>
control 発注入荷管理<<Application>>

title 発注取消

店長 -> ブラウザ : 発注変更ページ\n削除
ブラウザ -> React : EVENT\n発注取消
activate React
React -> React : Redux\n発注取消
React -> RESTコントローラ : 発注取消API
activate RESTコントローラ

    RESTコントローラ -> 発注入荷管理 : 発注内容を取消する
    activate 発注入荷管理

    note right : ブラウザの\nユースケースを流用

    RESTコントローラ <- 発注入荷管理
    deactivate 発注入荷管理

React <- RESTコントローラ
deactivate RESTコントローラ
React -> React  : Redux\n発注取消結果判定
ブラウザ <-- React : 発注取消不可エラー
店長 <-- ブラウザ : 発注変更ページ表示\nエラー表示

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
