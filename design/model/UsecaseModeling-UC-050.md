# 次世代開発コースA Level1 店舗業務デジタル化 Level2 モバイル化

## 1. ユースケース・要件

### 【Level1】 L1-UC-050 閉店後に日中納入された商品を記録する

* L1-UC-050-R010 日中納品された商品は納品書と発注を照合して納入を記録できるようにする。
* L1-UC-050-R020 納品書の納入数と発注時の数と合わないときに発注の数を訂正し理由も記録できる。
  
### 【Level2】 L2-UC-050 日中に卸業者から納入があったら発注をチェックしてすぐに記録する

* L2-UC-050-R010 日中の納品は発注と照合してすぐに納入を記録できるようにする。
* L2-UC-050-R020 実際納入された数と発注時の数と合わないときに発注の数を訂正し理由も記録できる。

## 2. モデリング

### 2.1. ロバストネス分析

```plantuml

@startuml

left to right direction

actor 店長
entity 在庫取引
entity 購買発注伝票
entity 入荷伝票

package "L2-UC-050 日中に卸業者から納入があったら発注をチェックしてすぐに記録する"{

    boundary 発注一覧ページ
    boundary 入荷ページ
    control 入荷
    control 入庫
    control 入荷完了

    店長 --> 入荷ページ
    入荷ページ --> 入荷
    入庫 <- 入荷
    入庫 --> 在庫取引
    入荷 --> 入荷伝票
    入荷完了 <- 入荷
    入荷ページ <-- 入荷完了
    入荷完了 --> 購買発注伝票
    発注一覧ページ <- 入荷ページ
    店長 <-- 発注一覧ページ

}

@enduml
```

```plantuml

@startuml

left to right direction

actor 店長
entity 在庫取引
entity 購買発注伝票
entity 入荷伝票

package "L2-UC-050 日中に卸業者から納入があったら発注をチェックしてすぐに記録する（入荷取消）"{

    boundary 発注一覧ページ
    boundary 入荷ページ
    control 入荷取消
    control 入庫
    control 入荷待

    店長 --> 入荷ページ
    入荷ページ --> 入荷取消
    入庫 <- 入荷取消 : マイナスの入庫
    入庫 --> 在庫取引
    入荷取消 --> 入荷伝票 : マイナスの入荷
    入荷待 <- 入荷取消
    入荷ページ <-- 入荷待
    入荷待 --> 購買発注伝票
    発注一覧ページ <- 入荷ページ
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
entity 在庫取引

title 入荷

店長 -> ブラウザ : 発注内容ページ\n入荷
ブラウザ -> Webコントローラ : GET\n入荷表示

note right : 入荷時は取引先、商品は変更不可\n処理は発注内容照会と同じ処理を流用\n変更入力可能なHTMLテンプレートのみ変更\n\n入荷時に分納が判明した場合\n後納となる分を新たに発注する

activate Webコントローラ
    Webコントローラ -> 発注入荷管理 : 購買発注伝票番号で\n購買発注の内容を\n照会する
    activate 発注入荷管理

    note right : UC-040発注内容照会の\nユースケース流用

    Webコントローラ <- 発注入荷管理
    deactivate 発注入荷管理

ブラウザ <- Webコントローラ : SUCCESS\n入荷ページ
deactivate Webコントローラ
店長 <- ブラウザ : 入荷ページ表示

店長 -> ブラウザ : 入荷ページ\n仕入価格・仕入数\n入荷日・備考変更後\n納入
ブラウザ -> Webコントローラ : POST\n入荷
activate Webコントローラ
    Webコントローラ -> 発注入荷管理 : 購買発注を確認して\n入荷する
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

        発注入荷管理 -> 発注入荷管理 : 購買発注状況\n入荷待(BOK)でない\n場合入荷不可

    Webコントローラ <-- 発注入荷管理 : 入荷不可エラー
ブラウザ <-- Webコントローラ : 入荷不可エラー
店長 <-- ブラウザ : 入荷ページ表示\n入荷不可エラー

        発注入荷管理 -> 在庫管理 : 入荷に伴い\n商品を入庫する
        activate 在庫管理

        在庫管理 -> 在庫取引 : 入荷の仕入数分\n在庫を追加する\n在庫取引登録
        note right : 在庫取引種別:入荷(ID)\n参照取引番号:入荷伝票番号
        activate 在庫取引
        在庫管理 <- 在庫取引
        deactivate 在庫取引

        発注入荷管理 <- 在庫管理
        deactivate 在庫管理

        note right : 実際の入荷と購買発注で\n仕入価格、仕入数、入荷予定日、備考が\n変更になっていても\n購買発注の変更をしない限りは\n入荷時の情報で購買発注を自動で変えない\n精算はあくまで入荷伝票と照合

        発注入荷管理 -> 入荷伝票 : 購買発注伝票に伴う\n入荷登録

        activate 入荷伝票
        発注入荷管理 <- 入荷伝票
        deactivate 入荷伝票

        発注入荷管理 -> 購買発注状況 : 購買発注状況を\n入荷済に変更\nBy購買発注伝票番号
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
entity 在庫取引

title 発注取消

店長 -> ブラウザ : 発注内容ページ\n入荷取消
ブラウザ -> Webコントローラ : POST\n入荷取消

activate Webコントローラ
    Webコントローラ -> 発注入荷管理 : 購買発注伝票番号で\n購買発注を取消する
    activate 発注入荷管理

        note right  : 入荷取消は誤入力のケースであり\n入荷と入庫自体をなかったことにするためデータを物理削除する

        発注入荷管理 -> 在庫管理 : 入荷取消に伴い\n商品の入庫を取消する
        activate 在庫管理

        在庫管理 -> 在庫取引 : 在庫取引削除\nBy参照取引番号=入荷伝票番号
        activate 在庫取引
        在庫管理 <- 在庫取引
        deactivate 在庫取引

        発注入荷管理 <- 在庫管理
        deactivate 在庫管理

        発注入荷管理 -> 入荷伝票 : 入荷削除\nBy入荷伝票番号
        activate 入荷伝票
        発注入荷管理 <- 入荷伝票
        deactivate 入荷伝票

    Webコントローラ <- 発注入荷管理
    deactivate 発注入荷管理

ブラウザ <- Webコントローラ : SUCCESS\n発注変更ページ
deactivate Webコントローラ
店長 <- ブラウザ : 発注変更ページ表示

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

title 入荷

店長 -> ブラウザ : 発注内容ページ\n入荷
ブラウザ -> React : GET\n入荷表示
activate React

    React -> React : AUTH ROUTING\n入荷ページ遷移
    React -> React : useEffect
    note right : 発注内容ページと同内容のため\nAPI呼び出しなしを検討
    React -> React : Redux\n発注取得
    React -> RESTコントローラ : 発注照会API
    activate RESTコントローラ

    RESTコントローラ -> 発注入荷管理 : 購買発注伝票番号で\n購買発注の内容を\n照会する
    activate 発注入荷管理

    note right : ブラウザの\nユースケースを流用

        発注入荷管理 -> 在庫管理 : 入荷に伴い\n商品を入庫する
        activate 在庫管理
        発注入荷管理 <- 在庫管理
        deactivate 在庫管理

    RESTコントローラ <- 発注入荷管理
    deactivate 発注入荷管理

    React <- RESTコントローラ
    deactivate RESTコントローラ
    React -> React : Store\n発注

ブラウザ <- React : 入荷表示
deactivate React
店長 <- ブラウザ : 入荷ページ表示

店長 -> ブラウザ : 入荷ページ\n仕入価格・仕入数\n入荷日・備考変更後\n納入
ブラウザ -> React : EVENT\n納入
activate React
React -> React : Redux\n入荷
React -> RESTコントローラ : 入荷API
activate RESTコントローラ

    RESTコントローラ -> 発注入荷管理 : 購買発注を確認して\n入荷する
    activate 発注入荷管理

    note right : ブラウザの\nユースケースを流用

    RESTコントローラ <- 発注入荷管理
    deactivate 発注入荷管理

React <- RESTコントローラ
deactivate RESTコントローラ
React -> React  : Redux\n入荷結果判定
ブラウザ <-- React : 入荷不可エラー
店長 <-- ブラウザ : 入荷ページ表示\nエラー表示

ブラウザ <- React : AUTH ROUTING\n発注一覧ページ遷移
deactivate React
ブラウザ -> React : GET\n発注一覧表示
activate React

note right : 発注一覧表示は\n前述の通りにて省略

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
control 在庫管理<<Application>>

title 入荷取消

店長 -> ブラウザ : 発注内容ページ\n入荷取消
ブラウザ -> React : EVENT\n入荷取消
activate React
    React -> React : Redux\n入荷取消
    React -> RESTコントローラ : 入荷取消API
    activate RESTコントローラ

    RESTコントローラ -> 発注入荷管理 : 購買発注伝票番号で\n購買発注を取消する
    activate 発注入荷管理

    note right : ブラウザの\nユースケースを流用

        発注入荷管理 -> 在庫管理 : 入荷取消に伴い\n商品の入庫を取消する
        activate 在庫管理

        発注入荷管理 <- 在庫管理
        deactivate 在庫管理

    RESTコントローラ <- 発注入荷管理
    deactivate 発注入荷管理

React <- RESTコントローラ
deactivate RESTコントローラ
React -> React  : Redux\n入荷取消結果判定
ブラウザ <-- React : 入荷取消不可エラー
店長 <-- ブラウザ : 発注内容ページ表示\nエラー表示

ブラウザ <- React : AUTH ROUTING\n発注一覧ページ遷移
deactivate React
ブラウザ -> React : GET\n発注一覧表示
activate React

note right : 発注一覧表示は\nUC-040参照

ブラウザ <- React : 発注一覧表示
deactivate React
店長 <- ブラウザ : 発注一覧ページ表示
@enduml
```
