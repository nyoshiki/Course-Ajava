# 次世代開発コースA Level1 店舗業務デジタル化 Level2 モバイル化

## 1. ユースケース・要件

### 【Level1】 L1-UC-020 閉店後に商品ごとに販売数と在庫数の推移が確認できる

* L1-UC-020-R010 商品ごとに直近１ヶ月の販売点数と日別に商品ごとの販売数の推移が可視化できるようにする。
* L1-UC-020-R020 商品ごとに直近１ヶ月の在庫数の推移を見れるようにし翌日以降いついくつ発注すべきか判断する際の参考にする。
  
### 【Level2】 L2-UC-020 日中でも商品がどのくらい売れているか確認できる

* L2-UC-020-R010 商品ごとに過去１ヶ月の販売点数と日別に商品ごとの販売数の推移が円グラフと折れ線グラフで可視化できるようにする。
* L2-UC-020-R020 折れ線グラフでは、将来、発注、納品、販売から計算された在庫の推移も重ねて見れるようにする。【優先度低】

## 2. モデリング

### 2.1. ロバストネス分析

```plantuml

@startuml

left to right direction

actor 店長
entity 販売伝票
entity 販売伝票明細
entity 在庫取引

entity 販売伝票
entity 販売伝票明細
entity 在庫
entity 在庫取引
entity 購買発注伝票

package "L2-UC-020 日中でも商品がどのくらい売れているか確認できる"{
    boundary 販売状況ページ
    control 商品別販売数額取得
    control 商品別販売推移取得
    control 商品別在庫推移取得
    control グラフデータ編集

    店長 --> 販売状況ページ
    販売状況ページ --> 商品別販売数額取得
    商品別販売推移取得 <- 商品別販売数額取得
    商品別販売数額取得 <-- 販売伝票明細
    商品別在庫推移取得 <- 商品別販売推移取得
    商品別販売推移取得 <-- 販売伝票
    商品別販売推移取得 <-- 販売伝票明細
    グラフデータ編集 <- 商品別在庫推移取得
    商品別在庫推移取得 <-- 在庫
    商品別在庫推移取得 <-- 在庫取引
    商品別在庫推移取得 <-- 購買発注伝票
    販売状況ページ <-- グラフデータ編集
    店長 <- 販売状況ページ

}

@enduml
```

### 2.2. シーケンス分析(ブラウザ)

```plantuml
@startuml
actor 店長
boundary ブラウザ
control Webコントローラ<<Controller>>
control 販売管理<<Application>>
control 在庫管理<<Application>>
entity 販売伝票
entity 販売伝票明細
entity 在庫
entity 在庫取引
entity 購買発注伝票

title 販売状況照会

店長 -> ブラウザ : メニューページ\n販売状況
ブラウザ -> Webコントローラ : GET\n販売状況表示
activate Webコントローラ

    Webコントローラ -> 販売管理 : 商品別に対象期間の\n販売数と販売額を\n集計する
    activate 販売管理

        販売管理 -> 販売伝票明細 : 対象期間\n商品別販売数集計
        activate 販売伝票明細
        販売管理 <- 販売伝票明細
        deactivate 販売伝票明細

        販売管理 -> 販売伝票明細 : 対象期間\n商品別販売額集計
        activate 販売伝票明細
        販売管理 <- 販売伝票明細
        deactivate 販売伝票明細

    Webコントローラ <- 販売管理
    deactivate 販売管理

    Webコントローラ -> 販売管理 : 商品別に対象期間の\n一日の販売数の推移を\n取得する
    activate 販売管理

        販売管理 -> 販売伝票: 対象期間\n日別商品別販売数集計
        activate 販売伝票

        販売伝票 -> 販売伝票明細
        activate 販売伝票明細
        販売伝票 <- 販売伝票明細
        deactivate 販売伝票明細

        販売管理 <- 販売伝票
        deactivate 販売伝票

    Webコントローラ <- 販売管理
    deactivate 販売管理

    Webコントローラ -> 在庫管理 : 商品別に対象期間の\n在庫数の推移を\n取得する
    activate 在庫管理

        在庫管理 -> 在庫 : 商品別在庫数
        activate 在庫

        在庫 -> 在庫取引 : 対象期間\n商品別\n在庫取引\n集計
        activate 在庫取引
        在庫 <- 在庫取引
        deactivate 在庫取引

        在庫管理 <- 在庫
        deactivate 在庫

    Webコントローラ <- 在庫管理
    deactivate 在庫管理

    Webコントローラ -> Webコントローラ : グラフデータ編集
ブラウザ <- Webコントローラ : SUCCESS\n販売状況ページ
deactivate Webコントローラ
店長 <- ブラウザ : 販売状況ページ表示

@enduml
```

### 2.3. シーケンス分析(モバイル)

```plantuml
@startuml
actor 店長
boundary ブラウザ
boundary React<<Web Contents>>
control RESTコントローラ<<Controller>>
control 販売管理<<Application>>
control 在庫管理<<Application>>

title 販売状況照会

店長 -> ブラウザ : ログイン成功後\nまたはメニュー\n販売状況
ブラウザ -> React : GET\n販売状況表示
activate React

    React -> React : Redux\n商品別販売数額取得
    React -> RESTコントローラ : 商品別販売数額取得API
    activate RESTコントローラ
    RESTコントローラ -> 販売管理 : 商品別に対象期間の\n販売数と販売額を\n集計する
    activate 販売管理

    note right : ブラウザでの\nユースケース流用

    RESTコントローラ <- 販売管理
    deactivate 販売管理
    React <- RESTコントローラ
    deactivate RESTコントローラ
    React -> React : Store\n商品別販売数・販売額

    React -> React : Redux\n商品別販売推移取得
    React -> RESTコントローラ : 商品別販売推移取得API
    activate RESTコントローラ
    RESTコントローラ -> 販売管理 : 商品別に対象期間の\n一日の販売数の推移を\n取得する
    activate 販売管理

    note right : ブラウザでの\nユースケース流用

    RESTコントローラ <- 販売管理
    deactivate 販売管理
    React <- RESTコントローラ
    deactivate RESTコントローラ
    React -> React : Store\n日別商品別販売数

    React -> React : Redux\n商品別在庫推移取得
    React -> RESTコントローラ : 商品別在庫推移取得API
    activate RESTコントローラ
    RESTコントローラ -> 在庫管理 : 商品別に対象期間の\n在庫数の推移を\n取得する
    activate 在庫管理

    note right : ブラウザでの\nユースケース流用

    RESTコントローラ <- 在庫管理
    deactivate 在庫管理
    React <- RESTコントローラ
    deactivate RESTコントローラ
    React -> React : Store\n日別商品別在庫数

    React -> React : Redux\nグラフデータ編集

ブラウザ <- React : 販売状況表示
deactivate React
店長 <- ブラウザ : 販売状況ページ表示

@enduml
```
