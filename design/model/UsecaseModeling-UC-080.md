# 次世代開発コースA Level1 店舗業務デジタル化 Level2 モバイル化

## 1. ユースケース・要件

### 【Level1】 L1-UC-080 定期的に棚卸しをして理論在庫と実在庫を照合・調整できる

* L1-UC-080-R010 前回棚卸時に記録した在庫数と納入、仕入、販売による入出庫による在庫数の増減が計算され現在の商品別の理論在庫を確認できる。
* L1-UC-080-R020 理論在庫と実在庫に差異があり、過去の納入、仕入、販売を訂正できない場合は、在庫訂正を記録して在庫数を調整できる。
* L1-UC-080-R030 棚卸を実施するとその時点での商品別の在庫数が最新の棚卸結果の在庫数として記録される。
* L1-UC-080-R040 過去の棚卸は履歴として記録されあとから閲覧することができる。

## 2. モデリング

### 2.1. ロバストネス分析

```plantuml

@startuml

left to right direction

actor 店長
entity 在庫
entity 在庫取引
entity 在庫履歴

package "L2-UC-080 定期的に棚卸しをして理論在庫と実在庫を照合・調整できる"{

    boundary 在庫状況ページ
    boundary 在庫履歴ページ
    control 在庫集計
    control 在庫履歴検索

    店長 --> 在庫状況ページ
    在庫状況ページ -> 在庫集計
    在庫集計 <-- 在庫
    在庫集計 <-- 在庫取引
    在庫状況ページ <-- 在庫集計
    店長 <- 在庫状況ページ
    在庫状況ページ -> 在庫履歴ページ
    在庫履歴ページ --> 在庫履歴検索
    在庫履歴検索 <-- 在庫履歴
    在庫履歴ページ <-- 在庫履歴検索
    店長 <- 在庫履歴ページ
}

@enduml
```

```plantuml

@startuml

left to right direction

actor 店長
entity 在庫
entity 在庫取引
entity 在庫履歴

package "L2-UC-080 定期的に棚卸しをして理論在庫と実在庫を照合・調整できる(在庫訂正・棚卸)"{

    boundary 在庫状況ページ
    control 在庫集計
    control 棚卸
    control 在庫訂正

    店長 --> 在庫状況ページ
    在庫状況ページ --> 在庫訂正 : 訂正
    在庫訂正 --> 在庫取引
    在庫集計 <- 在庫訂正
    在庫集計 <-- 在庫取引
    在庫集計 <-- 在庫
    在庫状況ページ <-- 在庫集計
    在庫状況ページ --> 棚卸 : 棚卸
    棚卸 --> 在庫
    棚卸 --> 在庫履歴
    在庫状況ページ <-- 棚卸
    店長 <- 在庫状況ページ
}

@enduml
```

### 2.2. シーケンス分析(ブラウザ)

```plantuml
@startuml
actor 店長
boundary ブラウザ
control Webコントローラ<<Controller>>
control 在庫管理<<Application>>
entity 在庫
entity 在庫取引

title 在庫状況照会

店長 -> ブラウザ : メニューページ\n在庫管理
ブラウザ -> Webコントローラ : GET\n在庫状況表示
activate Webコントローラ

    Webコントローラ -> 在庫管理 : 商品別の現在在庫数を照会する
    activate 在庫管理

        note right : UC-030 発注一覧のユースケース\n「商品別の現在在庫を照会する」\nと同一処理

        在庫管理 -> 在庫管理 : 現在在庫数照会\nBy商品ID
        activate 在庫管理
            在庫管理 -> 在庫 : 現在の在庫数を\n商品別に計算
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

        在庫管理 -> 在庫管理 : 前回棚卸以降\n商品別に１回\n実施できる\n在庫訂正取得
        activate 在庫管理
            在庫管理 -> 在庫取引 : 在庫取引\nBy在庫取引種別\nBetween在庫取引日時\n・IA:在庫調整\n・前回棚卸以降
            activate 在庫取引

            在庫管理 <- 在庫取引
            deactivate 在庫取引
        deactivate 在庫管理

    Webコントローラ <- 在庫管理
    deactivate 在庫管理

ブラウザ <- Webコントローラ : SUCCESS\n在庫状況ページ
deactivate Webコントローラ
店長 <- ブラウザ : 在庫状況ページ表示
@enduml
```

```plantuml
@startuml
actor 店長
boundary ブラウザ
control Webコントローラ<<Controller>>
control 在庫管理<<Application>>
entity 在庫履歴

title 在庫履歴照会

店長 -> ブラウザ : 在庫状況照会ページ\n商品の現在在庫数\n履歴
ブラウザ -> Webコントローラ : GET\n在庫履歴表示
activate Webコントローラ

    Webコントローラ -> 在庫管理 : 指定された商品の\n直近１ヶ月の\n在庫履歴を照会する
    activate 在庫管理

        在庫管理 -> 在庫履歴 : 在庫履歴照会\nBy商品ID\nBetween在庫日時
        activate 在庫履歴
        在庫管理 <- 在庫履歴
        deactivate 在庫履歴

    Webコントローラ <- 在庫管理
    deactivate 在庫管理

ブラウザ <- Webコントローラ : SUCCESS\n在庫履歴ページ
deactivate Webコントローラ
店長 <- ブラウザ : 在庫履歴aページ表示
@enduml
```

```plantuml
@startuml
actor 店長
boundary ブラウザ
control Webコントローラ<<Controller>>
control 在庫管理<<Application>>
entity 在庫
entity 在庫取引
entity 在庫履歴

title 棚卸

店長 -> ブラウザ : 在庫状況ページ\n棚卸
ブラウザ -> Webコントローラ : POST\n棚卸
activate Webコントローラ
    Webコントローラ -> 在庫管理 : 棚卸する
    activate 在庫管理

        note right : UC-030 発注一覧のユースケース\n「商品別の現在在庫を照会する」\nと同一処理

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

        在庫管理 -> 在庫 : 在庫数更新\nBy商品ID\n(現在在庫数で更新)
        note right : データベース更新\nエラー発生時は\nすべてロールバック

        在庫管理 -> 在庫履歴 : 在庫履歴登録\n・棚卸日時\n・商品ID\n・現在在庫数
        note right : データベース更新\nエラー発生時は\nすべてロールバック

    Webコントローラ <- 在庫管理
    deactivate 在庫管理

ブラウザ <- Webコントローラ : REDIRECT\nGET 在庫状況表示
deactivate Webコントローラ

ブラウザ -> Webコントローラ : GET\n在庫状況表示
activate Webコントローラ

note right : 在庫状況表示は\n前述の通りにて省略

ブラウザ <- Webコントローラ : SUCCESS\n在庫状況ページ
deactivate Webコントローラ
店長 <- ブラウザ : 在庫状況ページ表示

@enduml
```

```plantuml
@startuml
actor 店長
boundary ブラウザ
control Webコントローラ<<Controller>>
control 在庫管理<<Application>>
entity 在庫
entity 在庫取引
entity 在庫履歴

title 在庫訂正

店長 -> ブラウザ : 在庫状況ページ\n商品別在庫訂正数入力\n在庫訂正
ブラウザ -> Webコントローラ : POST\n在庫訂正
activate Webコントローラ
    Webコントローラ -> 在庫管理 : 商品別の在庫を訂正する
    activate 在庫管理

        在庫管理 -> 在庫 : 在庫照会\nBy商品ID\n(前回棚卸日時取得)
        activate 在庫
        在庫管理 <- 在庫
        deactivate 在庫

        在庫管理 -> 在庫管理 : 前回棚卸以降の\n在庫訂正取得
        activate 在庫管理
            在庫管理 -> 在庫取引 : 在庫取引\nBy在庫取引種別\nBetween在庫取引日時\n・IA:在庫調整\n・前回棚卸以降
            activate 在庫取引

            在庫管理 <- 在庫取引
            deactivate 在庫取引
        deactivate 在庫管理

        在庫管理 -> 在庫管理 : 前回棚卸以後で\n在庫訂正が\nすでにある場合
        activate 在庫管理
            在庫管理 -> 在庫取引 : 在庫取引更新\nBy在庫取引番号\n(在庫取引数)
            activate 在庫取引

            在庫管理 <- 在庫取引
            deactivate 在庫取引
        deactivate 在庫管理

        在庫管理 -> 在庫管理 : 前回棚卸以後で\n在庫訂正が\nない場合
        activate 在庫管理
            在庫管理 -> 在庫取引 : 在庫取引登録\n・商品ID\n・在庫取引種別:IA:在庫調整\n・在庫取引数：在庫訂正数
            activate 在庫取引

            在庫管理 <- 在庫取引
            deactivate 在庫取引
        deactivate 在庫管理

    Webコントローラ <- 在庫管理
    deactivate 在庫管理

ブラウザ <- Webコントローラ : REDIRECT\nGET 在庫状況表示
deactivate Webコントローラ

ブラウザ -> Webコントローラ : GET\n在庫状況表示
activate Webコントローラ

note right : 在庫状況表示は\n前述の通りにて省略

ブラウザ <- Webコントローラ : SUCCESS\n在庫状況ページ
deactivate Webコントローラ
店長 <- ブラウザ : 在庫状況ページ表示

@enduml
```

### 2.3. シーケンス分析(モバイル)

* 棚卸し機能はモバイル化の対象外
