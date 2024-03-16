# 次世代開発コースA Level1 店舗業務デジタル化 Level2 モバイル化

## 1. ユースケース・要件

### 【Level1】 L1-UC-060 花き市場で仕入たものを閉店後に記録できる

* L1-UC-060-R010 早朝の仕入で仲卸業者から買入した商品、仕入数、仕入価格を閉店後記録する。仕入数は在庫計上する。
* L1-UC-060-R020 仕入の記録は一通り入力したあとに紙と照合できるように一覧で表示する。
* L1-UC-060-R030 仕入一覧で一番上に今の商品別の在庫数と未納の納品予定数の合計を確認できる。

### 【Level2】 L2-UC-060 花き市場で仕入たものが買入したその場で記録できる

* L2-UC-060-R010 早朝の仕入で仲卸業者から買入した商品、仕入数、仕入価格を記録できる。仕入数はすぐに在庫計上する。
* L2-UC-060-R020 仕入の記録はきちんと入れたか確認するため一覧で表示して欲しい。
* L2-UC-060-R030 仕入一覧で一番上に今の商品別の在庫数と未納の納品予定数の合計を確認できる。

## 2. モデリング

### 2.1. ロバストネス分析

```plantuml

@startuml

left to right direction

actor 店長
entity 在庫取引
entity 在庫
entity 仕入伝票
entity 購買発注伝票

package "L2-UC-060 花き市場で仕入たものが買入したその場で記録できる"{

    boundary 仕入一覧ページ
    boundary 仕入ページ
    control 仕入一覧検索
    control 商品別在庫数照会
    control 商品別入荷予定数照会

    店長 --> 仕入一覧ページ
    仕入一覧ページ -> 仕入一覧検索
    仕入一覧検索 <-- 仕入伝票
    商品別入荷予定数照会 <- 仕入一覧検索
    商品別入荷予定数照会 <-- 購買発注伝票
    商品別在庫数照会 <- 商品別入荷予定数照会
    商品別在庫数照会 <-- 在庫
    商品別在庫数照会 <-- 在庫取引
    仕入一覧ページ <-- 商品別在庫数照会
    仕入一覧ページ -> 仕入ページ
    店長 <- 仕入一覧ページ

}

@enduml
```

```plantuml

@startuml

left to right direction

actor 店長
entity 在庫取引
entity 仕入伝票

package "L2-UC-060 花き市場で仕入たものが買入したその場で記録できる"{

    boundary 仕入一覧ページ
    boundary 仕入ページ
    control 仕入
    control 入庫

    店長 --> 仕入ページ
    仕入ページ --> 仕入
    仕入 --> 仕入伝票
    入庫 <- 仕入
    入庫 --> 在庫取引
    仕入ページ <-- 仕入
    仕入一覧ページ <- 仕入ページ
    店長 <-- 仕入一覧ページ

}
@enduml
```

```plantuml

@startuml

left to right direction

actor 店長
entity 在庫取引
entity 仕入伝票

package "L2-UC-060 花き市場で仕入たものが買入したその場で記録できる（仕入取消）"{

    boundary 仕入一覧ページ
    control 仕入取消
    control 入庫

    店長 --> 仕入一覧ページ
    仕入一覧ページ --> 仕入取消
    仕入取消 --> 仕入伝票 : マイナスの仕入
    入庫 <- 仕入取消 : マイナスの入庫
    入庫 --> 在庫取引
    仕入一覧ページ <-- 仕入取消
    店長 <-- 仕入一覧ページ

}
@enduml
```

### 2.2. シーケンス分析(ブラウザ)

```plantuml
@startuml
actor 店長
boundary ブラウザ
control Webコントローラ<<Controller>>
control 仕入管理<<Application>>
control 発注入荷管理<<Application>>
control 在庫管理<<Application>>
entity 仕入伝票
entity 購買発注伝票
entity 商品
entity 在庫
entity 在庫取引

title 仕入一覧照会

店長 -> ブラウザ : メニューページ\n仕入管理
ブラウザ -> Webコントローラ : GET\n仕入一覧表示
activate Webコントローラ

    Webコントローラ -> 仕入管理 : 仕入日が対象期間の\n仕入を検索する
    activate 仕入管理

        仕入管理 -> 仕入伝票 : 仕入一覧照会\nBetween仕入日
        activate 仕入伝票
        仕入管理 <- 仕入伝票
        deactivate 仕入伝票

    Webコントローラ <- 仕入管理
    deactivate 仕入管理

    Webコントローラ -> 発注入荷管理 : 商品別の入荷予定数を照会する
    activate 発注入荷管理

        発注入荷管理 -> 購買発注伝票: 購買発注状況が\n入荷待(BOK)の\n商品別入荷予定数集計
        activate 購買発注伝票
        発注入荷管理 <- 購買発注伝票
        deactivate 購買発注伝票

    Webコントローラ <- 発注入荷管理
    deactivate 発注入荷管理

    Webコントローラ -> 在庫管理 : 商品別の現在在庫数を照会する
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

    Webコントローラ <- 在庫管理
    deactivate 在庫管理

ブラウザ <- Webコントローラ : SUCCESS\n仕入一覧ページ
deactivate Webコントローラ
店長 <- ブラウザ : 仕入一覧ページ表示

@enduml
```

```plantuml
@startuml
actor 店長
boundary ブラウザ
control Webコントローラ<<Controller>>
control 仕入管理<<Application>>
control 発注入荷管理<<Application>>
control 在庫管理<<Application>>
entity 仕入伝票
entity 商品
entity 在庫取引

title 仕入

店長 -> ブラウザ : 仕入一覧ページ\n新規仕入
ブラウザ -> Webコントローラ : GET\n仕入表示
activate Webコントローラ
    Webコントローラ -> 仕入管理 : 仕入商品を選ぶため\n仕入可能な商品を検索する
    activate 仕入管理

        仕入管理 -> 商品 : 販売開始日<=本日\n本日<=販売終了日\n販売可能商品検索
        activate 商品
        仕入管理 <- 商品
        deactivate 商品

    Webコントローラ <- 仕入管理
    deactivate 仕入管理

    Webコントローラ -> Webコントローラ : 商品選択用\n仕入可能商品リスト

ブラウザ <- Webコントローラ : SUCCESS\n仕入ページ
deactivate Webコントローラ
店長 <- ブラウザ : 仕入ページ表示

店長 -> ブラウザ : 仕入ページ\n仕入内容入力\n納入
ブラウザ -> Webコントローラ : POST\n仕入
activate Webコントローラ
    Webコントローラ -> 仕入管理 : 仕入する
    activate 仕入管理

        仕入管理 -> 在庫管理 : 仕入に伴い\n商品を入庫する
        activate 在庫管理

        在庫管理 -> 在庫取引 : 仕入の仕入数分\n在庫を追加する\n在庫取引登録
        note right : 在庫取引種別:仕入(PC)\n参照取引番号:仕入伝票番号
        activate 在庫取引
        在庫管理 <- 在庫取引
        deactivate 在庫取引

        仕入管理 <- 在庫管理
        deactivate 在庫管理

        仕入管理 -> 仕入伝票 : 仕入登録
        activate 仕入伝票
        仕入管理 <- 仕入伝票
        deactivate 仕入伝票

    Webコントローラ <- 仕入管理
    deactivate 仕入管理

ブラウザ <- Webコントローラ : REDIRECT\nGET 仕入一覧表示
deactivate Webコントローラ

ブラウザ -> Webコントローラ : GET\n仕入一覧表示
activate Webコントローラ

note right : 仕入一覧表示は\n前述の通りにて省略

ブラウザ <- Webコントローラ : SUCCESS\n仕入一覧ページ
deactivate Webコントローラ
店長 <- ブラウザ : 仕入一覧ページ表示

@enduml
```

```plantuml
@startuml
actor 店長
boundary ブラウザ
control Webコントローラ<<Controller>>
control 仕入管理<<Application>>
control 発注入荷管理<<Application>>
control 在庫管理<<Application>>
entity 仕入伝票
entity 在庫取引

title 仕入取消

店長 -> ブラウザ : 仕入一覧ページ\n仕入取消
ブラウザ -> Webコントローラ : POST\n仕入取消
activate Webコントローラ
    Webコントローラ -> 仕入管理 : 仕入を取消する
    activate 仕入管理

        note right  : 仕入取消は仕入を誤入力したケースであり\n仕入と入庫自体をなかったことにするためデータを物理削除する

        仕入管理 -> 在庫管理 : 仕入取消に伴い\n商品の入庫を取消する
        activate 在庫管理

        在庫管理 -> 在庫取引 : 在庫取引削除\nBy参照取引番号=仕入伝票番号
        activate 在庫取引
        在庫管理 <- 在庫取引
        deactivate 在庫取引

        仕入管理 <- 在庫管理
        deactivate 在庫管理

        仕入管理 -> 仕入伝票 : 仕入削除\nBy仕入伝票番号
        activate 仕入伝票
        仕入管理 <- 仕入伝票
        deactivate 仕入伝票

    Webコントローラ <- 仕入管理
    deactivate 仕入管理

ブラウザ <- Webコントローラ : REDIRECT\nGET 仕入一覧表示
deactivate Webコントローラ

ブラウザ -> Webコントローラ : GET\n仕入一覧表示
activate Webコントローラ

note right : 仕入一覧表示は\n前述の通りにて省略

ブラウザ <- Webコントローラ : SUCCESS\n仕入一覧ページ
deactivate Webコントローラ
店長 <- ブラウザ : 仕入一覧ページ表示

@enduml
```

### 2.3. シーケンス分析(モバイル)

```plantuml
@startuml
actor 店長
boundary ブラウザ
boundary React<<Web Contents>>
control RESTコントローラ<<Controller>>
control 仕入管理<<Application>>
control 発注入荷管理<<Application>>
control 在庫管理<<Application>>

title 仕入一覧照会

店長 -> ブラウザ : メニュー\n仕入
ブラウザ -> React : GET\n仕入一覧表示
activate React

    React -> React : AUTH ROUTING\n仕入一覧ページ遷移
    React -> React : useEffect
    React -> React : Redux\n仕入一覧取得
    React -> RESTコントローラ : 仕入一覧取得API
    activate RESTコントローラ

    RESTコントローラ -> 仕入管理 : 仕入日が対象期間の\n仕入を検索する
    activate 仕入管理

    note right : ブラウザの\nユースケースを流用

    RESTコントローラ <- 仕入管理
    deactivate 仕入管理

    React <- RESTコントローラ
    deactivate RESTコントローラ
    React -> React : Store\n仕入一覧

    React -> React : Redux\n商品別在庫・\n入荷予定数取得
    React -> RESTコントローラ : 商品別在庫・\n入荷予定数取得API

    note right : UC-030（モバイル）\n発注一覧で実行する\n商品別在庫・入荷予定数取得API\nと同じAPIを利用

    activate RESTコントローラ

    RESTコントローラ -> 発注入荷管理 : 商品別の入荷予定数を\n照会する
    activate 発注入荷管理

    note right : ブラウザの\nユースケースを流用

    RESTコントローラ <- 発注入荷管理
    deactivate 発注入荷管理

    RESTコントローラ -> 在庫管理 : 商品別の現在在庫数\nを照会する
    activate 在庫管理

    note right : ブラウザの\nユースケースを流用

    RESTコントローラ <- 在庫管理
    deactivate 在庫管理

    React <- RESTコントローラ
    deactivate RESTコントローラ
    React -> React : Store\n商品別入荷予定数\n商品別現在在庫数

ブラウザ <- React : 仕入一覧表示
deactivate React
店長 <- ブラウザ : 仕入一覧ページ表示
@enduml
```

```plantuml
@startuml
actor 店長
boundary ブラウザ
boundary React<<Web Contents>>
control RESTコントローラ<<Controller>>
control 仕入管理<<Application>>
control 在庫管理<<Application>>

title 仕入

店長 -> ブラウザ : 仕入一覧ページ\n＋（新規仕入）
ブラウザ -> React : GET\n仕入ページ表示
activate React

    React -> React : Redux\n仕入可能商品取得
    React -> RESTコントローラ : 仕入可能商品取得API
    RESTコントローラ -> 仕入管理 : 仕入商品を選ぶため\n仕入可能な商品を検索する
    activate 仕入管理

    note right : ブラウザでの\nユースケース流用

    RESTコントローラ <- 仕入管理
    deactivate 仕入管理
    React <- RESTコントローラ
    deactivate RESTコントローラ
    React -> React : Store\n商品選択用\n仕入可能商品リスト

ブラウザ <- React : 仕入ページ表示
deactivate React
店長 <- ブラウザ : 仕入ページ表示

店長 -> ブラウザ : 仕入ページ\n仕入内容入力\n納入
ブラウザ -> React : EVENT\n納入
activate React
React -> React : Redux\n仕入
React -> RESTコントローラ : 仕入API
activate RESTコントローラ

    RESTコントローラ -> 仕入管理 : 仕入する
    activate 仕入管理

    note right : ブラウザでの\nユースケース流用

        仕入管理 -> 在庫管理 : 仕入に伴い\n商品を入庫する
        activate 在庫管理
        仕入管理 <- 在庫管理
        deactivate 在庫管理

    RESTコントローラ <- 仕入管理
    deactivate 仕入管理

React <- RESTコントローラ
deactivate RESTコントローラ
React -> React : Redux\n仕入結果判定
ブラウザ <- React : AUTH ROUTING\n仕入一覧ページ遷移
deactivate React
ブラウザ -> React : GET\n仕入一覧表示
activate React

note right : 仕入一覧表示は\n前述の通りにて省略

ブラウザ <- React : 仕入一覧表示
deactivate React
店長 <- ブラウザ : 仕入一覧ページ表示

@enduml
```

```plantuml
@startuml
actor 店長
boundary ブラウザ
boundary React<<Web Contents>>
control RESTコントローラ<<Controller>>
control 仕入管理<<Application>>
control 発注入荷管理<<Application>>
control 在庫管理<<Application>>

title 仕入取消

店長 -> ブラウザ : 仕入一覧ページ\n仕入取消
ブラウザ -> React : EVENT\n仕入取消
activate React
React -> React : Redux\n仕入取消
React -> RESTコントローラ : 仕入取消API
activate RESTコントローラ

    RESTコントローラ -> 仕入管理 : 仕入を取消する
    activate 仕入管理

    note right : ブラウザでの\nユースケース流用

        仕入管理 -> 在庫管理 : 仕入取消に伴い\n商品の入庫を取消する
        activate 在庫管理

        仕入管理 <- 在庫管理
        deactivate 在庫管理

    RESTコントローラ <- 仕入管理
    deactivate 仕入管理

React <- RESTコントローラ
deactivate RESTコントローラ
ブラウザ <- React : AUTH ROUTING\n仕入一覧ページ遷移
deactivate React
ブラウザ -> React : GET\n仕入一覧表示
activate React

note right : 仕入一覧表示は\n前述の通りにて省略

ブラウザ <- React : 仕入一覧表示
deactivate React
店長 <- ブラウザ : 仕入一覧ページ表示

@enduml
```
