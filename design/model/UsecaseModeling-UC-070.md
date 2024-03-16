# 次世代開発コースA Level1 店舗業務デジタル化 Level2 モバイル化

## 1. ユースケース・要件

### 【Level1】 L1-UC-070 閉店後に販売を記録できる

* L1-UC-070-R010 お客様の購入した商品が何で、一ついくらで、いくつ購入していくらの販売額になり在庫がいくつ減ったかを記録できるようにしたい。
* L1-UC-070-R020 値引きは単価でしか値引きしない。総額似たしする値引きはしていない。
* L1-UC-070-R030 購入商品を選ぶ時は今扱っている3種類の商品から選べれば良い。
* L1-UC-070-R040 商品の販売単価を販売ごとに変えられる。一度の販売で商品につき一つの販売単価だけ扱えれば良い。
* L1-UC-070-R050 商品を間違って選んでしまったときのために商品単位で購入対象から除外できるようにする。販売数を間違えたときは一度商品を除外してから再度商品を選び直す。
* L1-UC-070-R060 誤って販売を登録したときのために販売ごと削除できるようにする。

### 【Level2】 L2-UC-070 接客しながら販売の記録と精算や釣銭計算ができる

* L2-UC-070-R010 お客様と接客しながらお客様の購入予定の商品が何で、一ついくらで、いくつ購入するかを記録すると請求額を計算できるようにしたい。
* L2-UC-070-R020 お客様からの預かり金を入れるとお釣りも計算できるようにしたい。
* L2-UC-070-R030 購入商品を選ぶ時は今扱っている3種類の商品からさっと選べると良い。
* L2-UC-070-R040 商品の販売単価を販売ごとに変えられる。一度の販売で商品につき一つの販売単価だけ扱えれば良い。値引きも同じ商品であれば同じ値引きを適用する。
* L2-UC-070-R050 お客様が購入する花を変えるときのために商品単位で購入対象から除外できるようにする。めったにない購入予定の花を減らす機能はあってもなくてもよい。
* L2-UC-070-R060 花をアレンジせず購入直後であれば返品を受け付けて返金する。花をアレンジしているかは見ればわかるので特に管理はいらないが戻した商品の在庫がわかるようにする。
  
## 2. モデリング

### 2.1. ロバストネス分析

```plantuml

@startuml

left to right direction

actor 店長
entity 販売伝票

package "L2-UC-070 接客しながら販売の記録と精算や釣銭計算ができる（新規販売・継続販売）"{

    boundary 販売一覧ページ
    boundary 販売明細ページ
    control 販売一覧検索

    店長 --> 販売一覧ページ
    販売一覧ページ -> 販売一覧検索
    販売一覧検索 <-- 販売伝票
    販売一覧ページ <-- 販売一覧検索
    販売一覧ページ -> 販売明細ページ
    店長 <- 販売一覧ページ

}

@enduml
```

```plantuml

@startuml

left to right direction

actor 店長
entity 販売伝票
entity 販売伝票明細
entity 販売状況

package "L2-UC-070 接客しながら販売の記録と精算や釣銭計算ができる(購入商品選択)"{

    boundary 販売明細ページ
    boundary 購入商品選択ページ
    control 販売伝票照会
    control 購入商品選択
    control 販売点数集計
    control 販売合計計算

    店長 --> 販売明細ページ
    販売明細ページ --> 販売伝票照会
    販売伝票照会 <-- 販売伝票
    販売伝票照会 <-- 販売伝票明細
    販売伝票照会 <-- 販売状況
    販売明細ページ <-- 販売伝票照会
    購入商品選択ページ <- 販売明細ページ
    店長 --> 購入商品選択ページ
    購入商品選択ページ --> 購入商品選択
    購入商品選択 --> 販売伝票明細
    販売点数集計 <- 購入商品選択
    販売合計計算 <- 販売点数集計
    購入商品選択ページ <-- 販売合計計算
    販売明細ページ <- 購入商品選択ページ
    店長 <-- 販売明細ページ

}
@enduml
```

```plantuml

@startuml

left to right direction

actor 店長
entity 販売伝票
entity 販売伝票明細
entity 販売状況

package "L2-UC-070 接客しながら販売の記録と精算や釣銭計算ができる(購入商品除外)"{

    boundary 販売明細ページ
    control 販売伝票照会
    control 購入商品除外
    control 販売点数集計
    control 販売合計計算


    店長 --> 販売明細ページ
    販売明細ページ --> 販売伝票照会 : 購入商品除外
    販売伝票照会 <-- 販売伝票
    販売伝票照会 <-- 販売伝票明細
    販売伝票照会 <-- 販売状況
    購入商品除外 <- 販売伝票照会
    購入商品除外 -->販売伝票明細 : 明細削除
    販売点数集計 <- 購入商品除外
    販売合計計算 <- 販売点数集計
    販売明細ページ <-- 販売合計計算
    店長 <-- 販売明細ページ

}
@enduml
```

```plantuml

@startuml

left to right direction

actor 店長
entity 販売伝票
entity 販売伝票明細
entity 販売状況
entity 在庫取引

package "L2-UC-070 接客しながら販売の記録と精算や釣銭計算ができる（精算）"{

    boundary 販売明細ページ
    boundary 精算ページ
    boundary 販売一覧ページ
    control 販売伝票照会
    control 精算
    control 出庫

    店長 --> 販売明細ページ
    精算ページ <- 販売明細ページ
    販売伝票照会 <-- 販売伝票
    販売伝票照会 <-- 販売伝票明細
    販売伝票照会 <-- 販売状況
    精算ページ <-- 販売伝票照会
    店長 <-- 精算ページ
    店長 --> 精算ページ
    精算ページ --> 販売伝票照会 : 精算
    精算 <- 販売伝票照会 
    精算 --> 販売状況
    出庫 <- 精算 
    出庫 --> 在庫取引
    精算ページ <-- 出庫
    販売一覧ページ <- 精算ページ
    店長 <-- 販売一覧ページ

}
@enduml
```

```plantuml

@startuml

left to right direction

actor 店長
entity 在庫取引
entity 販売状況

package "L2-UC-070 接客しながら販売の記録と精算や釣銭計算ができる(返品)"{

    boundary 販売明細ページ
    boundary 販売一覧ページ
    control 返品
    control 入庫

    店長 --> 販売明細ページ : 返品
    販売明細ページ --> 返品
    返品 --> 販売状況
    入庫 <- 返品
    入庫 --> 在庫取引
    販売明細ページ <-- 返品
    販売一覧ページ <- 販売明細ページ
    店長 <-- 販売一覧ページ

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
entity 商品
entity 販売伝票
entity 販売状況
entity 販売伝票明細

title 販売一覧照会

店長 -> ブラウザ : メニューページ\n販売管理
ブラウザ -> Webコントローラ : GET\n販売一覧表示
activate Webコントローラ

    Webコントローラ -> 販売管理 : 販売日が対象期間の\n販売伝票を検索する
    activate 販売管理

        販売管理 -> 販売伝票 : 販売一覧照会\nBetween販売日
        activate 販売伝票
            販売伝票 -> 販売状況 :  Join\n販売伝票番号
            activate 販売状況
            販売伝票 <- 販売状況
            deactivate 販売状況
            販売伝票 -> 販売伝票明細 : Outer Join\n販売伝票番号
            activate 販売伝票明細
            販売伝票 <- 販売伝票明細
            deactivate 販売伝票明細
        販売管理 <- 販売伝票
        deactivate 販売伝票

    Webコントローラ <- 販売管理
    deactivate 販売管理

ブラウザ <- Webコントローラ : SUCCESS\n販売一覧ページ
deactivate Webコントローラ
店長 <- ブラウザ : 販売一覧ページ表示
@enduml
```

```plantuml
@startuml
actor 店長
boundary ブラウザ
control Webコントローラ<<Controller>>
control 販売管理<<Application>>
control 在庫管理<<Application>>
entity 商品
entity 販売伝票
entity 販売状況
entity 販売伝票明細

title 新規販売

店長 -> ブラウザ : 販売一覧ページ\n新規販売
ブラウザ -> Webコントローラ : POST\n新規販売
activate Webコントローラ
    Webコントローラ -> 販売管理 : 新たに販売の記録をはじめる
    activate 販売管理

        販売管理 -> 販売伝票 : 新規販売伝票作成
        activate 販売伝票
        販売伝票 -> 販売伝票 : 新規販売伝票番号採番

        販売管理 <- 販売伝票
        deactivate 販売伝票

        販売管理 -> 販売伝票 : 販売伝票登録
        activate 販売伝票
        販売管理 <- 販売伝票
        deactivate 販売伝票

        販売管理 -> 販売状況 : 未精算(PRE)\n販売状況登録
        activate 販売状況
        販売管理 <- 販売状況
        deactivate 販売状況

    Webコントローラ <- 販売管理
    deactivate 販売管理

ブラウザ <- Webコントローラ : REDIRECT\n販売明細一覧
deactivate Webコントローラ

ブラウザ -> Webコントローラ : GET\n販売明細表示
activate Webコントローラ

note right : 販売明細表示は\n後述

ブラウザ <- Webコントローラ : SUCCESS\n販売明細ページ
deactivate Webコントローラ
店長 <- ブラウザ : 販売明細ページ表示

@enduml
```

```plantuml
@startuml
actor 店長
boundary ブラウザ
control Webコントローラ<<Controller>>
control 販売管理<<Application>>
control 在庫管理<<Application>>
entity 商品
entity 販売伝票
entity 販売状況
entity 販売伝票明細

title 販売明細照会

店長 -> ブラウザ : 販売一覧ページ\n新規販売\n一覧より販売選択
ブラウザ -> Webコントローラ : GET\n販売明細表示
activate Webコントローラ

    Webコントローラ -> 販売管理 : 販売伝票番号で\n販売伝票明細を\n検索する
    activate 販売管理

        販売管理 -> 販売伝票 : 販売伝票照会\nBy販売伝票番号
        activate 販売伝票
            販売伝票 -> 販売状況 :  Join\n販売伝票番号
            activate 販売状況
            販売伝票 <- 販売状況
            deactivate 販売状況
            販売伝票 -> 販売伝票明細 : Outer Join\n販売伝票番号
            activate 販売伝票明細
            販売伝票 <- 販売伝票明細
            deactivate 販売伝票明細
        販売管理 <- 販売伝票
        deactivate 販売伝票

    Webコントローラ <- 販売管理
    deactivate 販売管理

ブラウザ <- Webコントローラ : SUCCESS\n販売明細ページ
deactivate Webコントローラ

店長 <- ブラウザ : 販売明細ページ表示
@enduml
```

```plantuml
@startuml
actor 店長
boundary ブラウザ
control Webコントローラ<<Controller>>
control 販売管理<<Application>>
entity 商品
entity 販売伝票
entity 販売状況
entity 販売伝票明細

title 購入商品選択

店長 -> ブラウザ : 販売明細ページ\n新規商品選択
ブラウザ -> Webコントローラ : GET\n購入商品選択表示
activate Webコントローラ
    Webコントローラ -> 販売管理 : 購入商品を選ぶため\n販売可能な商品を検索する
    activate 販売管理

        販売管理 -> 商品 : 販売開始日<=本日\n本日<=販売終了日\n販売可能商品検索
        activate 商品
        販売管理 <- 商品
        deactivate 商品

    Webコントローラ <- 販売管理
    deactivate 販売管理

    Webコントローラ -> Webコントローラ : 商品選択用\n購入可能商品\nリスト

ブラウザ <- Webコントローラ : SUCCESS\n購入商品選択ページ
deactivate Webコントローラ
店長 <- ブラウザ : 購入商品選択ページ表示

店長 -> ブラウザ : 購入商品選択ページ\n販売単価(値引き含)\n購入点数\n(購入額自動計算)\n選択
ブラウザ -> Webコントローラ : POST\n購入商品選択
activate Webコントローラ
    Webコントローラ -> 販売管理 : 購入商品を選ぶ
    activate 販売管理

        販売管理 -> 販売伝票 : 販売伝票照会\nBy販売伝票番号
        activate 販売伝票
            販売伝票 -> 販売状況 :  Join\n販売伝票番号
            activate 販売状況
            販売伝票 <- 販売状況
            deactivate 販売状況
            販売伝票 -> 販売伝票明細 : Outer Join\n販売伝票番号
            activate 販売伝票明細
            販売伝票 <- 販売伝票明細
            deactivate 販売伝票明細
        販売管理 <- 販売伝票
        deactivate 販売伝票

        販売管理 -> 販売管理 : 販売状況が\n精算済または取消済の場合\n商品追加不可
    Webコントローラ <-- 販売管理 : 販売状況エラー
ブラウザ <-- Webコントローラ : 販売状況エラー
店長 <-- ブラウザ : 販売状況エラー

        販売管理 -> 販売管理 : 同一商品の販売伝票明細あり
        activate 販売管理

            販売管理 -> 販売管理 : 増減により販売点数が0以下
            activate 販売管理
                販売管理 -> 販売伝票明細 : 販売伝票明細削除\nBy販売伝票明細番号
                activate 販売伝票明細
                販売管理 <- 販売伝票明細
                deactivate 販売伝票明細
            deactivate 販売管理

            販売管理 -> 販売管理 : 増減により販売点数が1以上
            activate 販売管理
                販売管理 -> 販売伝票明細 : 該当販売伝票明細の\n販売点数増減\n販売単価・小計更新
                activate 販売伝票明細
                販売管理 <- 販売伝票明細
                deactivate 販売伝票明細

                販売管理 -> 販売伝票明細 : 販売伝票明細更新\nBy販売伝票明細番号
                activate 販売伝票明細
                販売管理 <- 販売伝票明細
                deactivate 販売伝票明細
            deactivate 販売管理

        deactivate 販売管理

        販売管理 -> 販売管理 : 同一商品の販売伝票明細なし
        activate 販売管理
            販売管理 -> 販売伝票明細 : 選択された\n商品・販売単価・販売点数・小計で\n販売明細追加
            activate 販売伝票明細

            販売伝票明細 -> 販売伝票明細 : 販売伝票明細番号採番\n（最大番号＋１）

            note right : 中間の明細が\n削除された場合\n抜け番号となる

            販売管理 <- 販売伝票明細
            deactivate 販売伝票明細

            販売管理 -> 販売伝票明細 : 販売伝票明細登録
            activate 販売伝票明細
            販売管理 <- 販売伝票明細
            deactivate 販売伝票明細

        deactivate 販売管理

        販売管理 -> 販売管理 : 販売点数合計集計
        販売管理 -> 販売管理 : 販売額合計計算

        販売管理 -> 販売伝票 : 販売点数合計\n販売額合計更新\nBy販売伝票番号

    Webコントローラ <- 販売管理
    deactivate 販売管理

ブラウザ <- Webコントローラ : REDIRECT\nGET 販売明細表示
deactivate Webコントローラ

ブラウザ -> Webコントローラ : GET\n販売明細表示
activate Webコントローラ

note right : 販売明細表示は\n前述の通りにて省略

ブラウザ <- Webコントローラ : SUCCESS\n販売明細ページ
deactivate Webコントローラ
店長 <- ブラウザ : 販売明細ページ表示

@enduml
```

```plantuml
@startuml
actor 店長
boundary ブラウザ
control Webコントローラ<<Controller>>
control 販売管理<<Application>>
entity 商品
entity 販売伝票
entity 販売状況
entity 販売伝票明細

title 購入商品除外

店長 -> ブラウザ : 販売明細ページ\n購入商品除外
ブラウザ -> Webコントローラ : POST\n購入商品除外
activate Webコントローラ
    Webコントローラ -> 販売管理 : 購入対象から\n商品を除外する
    activate 販売管理

        販売管理 -> 販売伝票 : 販売伝票照会\nBy販売伝票番号
        activate 販売伝票
            販売伝票 -> 販売状況 :  Join\n販売伝票番号
            activate 販売状況
            販売伝票 <- 販売状況
            deactivate 販売状況
            販売伝票 -> 販売伝票明細 : Outer Join\n販売伝票番号
            activate 販売伝票明細
            販売伝票 <- 販売伝票明細
            deactivate 販売伝票明細
        販売管理 <- 販売伝票
        deactivate 販売伝票

        販売管理 -> 販売管理 : 販売状況が\n精算済または取消済の場合\n商品追加不可
    Webコントローラ <-- 販売管理 : 販売状況エラー
ブラウザ <-- Webコントローラ : 販売状況エラー
店長 <-- ブラウザ : 販売状況エラー

        販売管理 -> 販売管理 : 販売明細に\n存在しない商品が√選ばれた場合\n商品除外不可
    Webコントローラ <-- 販売管理 : 該当商品なしエラー
ブラウザ <-- Webコントローラ : 該当商品なしエラー
店長 <-- ブラウザ : 該当商品なしエラー

        販売管理 -> 販売伝票明細 : 販売伝票明細削除\nBy販売伝票明細番号
        activate 販売伝票明細
        販売管理 <- 販売伝票明細
        deactivate 販売伝票明細

        販売管理 -> 販売管理 : 販売点数合計集計
        販売管理 -> 販売管理 : 販売額合計計算

        販売管理 -> 販売伝票 : 販売点数合計\n販売額合計更新\nBy販売伝票番号

    Webコントローラ <- 販売管理
    deactivate 販売管理

ブラウザ <- Webコントローラ : REDIRECT\nGET 販売明細表示
deactivate Webコントローラ

ブラウザ -> Webコントローラ : GET\n販売明細表示
activate Webコントローラ

note right : 販売明細表示は\n前述の通りにて省略

ブラウザ <- Webコントローラ : SUCCESS\n販売明細ページ
deactivate Webコントローラ
店長 <- ブラウザ : 販売明細ページ表示

@enduml
```

```plantuml
@startuml
actor 店長
boundary ブラウザ
control Webコントローラ<<Controller>>
control 販売管理<<Application>>
control 在庫管理<<Application>>
entity 商品
entity 販売伝票
entity 販売状況
entity 販売伝票明細
entity 在庫取引

title 精算

店長 -> ブラウザ : 販売明細ページ\n精算
ブラウザ -> Webコントローラ : GET\n精算表示
activate Webコントローラ
     Webコントローラ -> 販売管理 : 販売伝票番号で\n販売伝票明細を\n検索する
    activate 販売管理

    note right : 販売明細照会と同処理\nにて前述参照

    Webコントローラ <- 販売管理
    deactivate 販売管理

ブラウザ <- Webコントローラ : SUCCESS\n精算ページ
deactivate Webコントローラ
店長 <- ブラウザ : 精算ページ表示

店長 -> ブラウザ : 精算ページ\n預り金入力\n(釣り銭自動計算)\n精算\n※現金のみ
ブラウザ -> Webコントローラ : POST\n精算
activate Webコントローラ
    Webコントローラ -> 販売管理 : 精算する
    activate 販売管理

        販売管理 -> 販売伝票 : 販売伝票照会\nBy販売伝票番号
        activate 販売伝票
            販売伝票 -> 販売状況 :  Join\n販売伝票番号
            activate 販売状況
            販売伝票 <- 販売状況
            deactivate 販売状況
            販売伝票 -> 販売伝票明細 : Outer Join\n販売伝票番号
            activate 販売伝票明細
            販売伝票 <- 販売伝票明細
            deactivate 販売伝票明細
        販売管理 <- 販売伝票
        deactivate 販売伝票

        販売管理 -> 販売管理 : 販売状況が\n精算済または取消済の場合\n精算不可
    Webコントローラ <-- 販売管理 : 販売状況エラー
ブラウザ <-- Webコントローラ : 販売状況エラー
店長 <-- ブラウザ : 販売状況エラー

        販売管理 -> 在庫管理 : 販売明細ごとに\n商品を販売点数分出庫する
        activate 在庫管理

        在庫管理 -> 在庫管理 : 現在在庫数照会\nBy商品ID
        activate 在庫管理

        note right : UC-030発注一覧における\n現在在庫数照会と同処理

        deactivate 在庫管理

        在庫管理 -> 在庫管理 : 在庫数 < 販売点数\nの場合在庫不足エラー

        note right : エラーの場合\n前の明細の在庫取引登録\nはロールバックで取消

        販売管理 <-- 在庫管理 : 在庫不足エラー
    Webコントローラ <-- 販売管理 : 在庫不足エラー
ブラウザ <- Webコントローラ : REDIRECT\nGET 購入商品選択\n在庫不足エラー

        在庫管理 -> 在庫取引 : 販売点数分\n在庫減\n在庫取引登録
        activate 在庫取引
        note right : 在庫取引種別:販売(SD)\n参照取引番号:販売伝票明細番号
        在庫管理 <- 在庫取引
        deactivate 在庫取引

        販売管理 <- 在庫管理
        deactivate 在庫管理

        販売管理 -> 販売状況 : 精算済(PAY)\n販売状況更新\nBy販売伝票番号
        activate 販売状況
        販売管理 -> 販売状況
        deactivate 販売状況

    Webコントローラ <- 販売管理
    deactivate 販売管理

ブラウザ <- Webコントローラ : REDIRECT\nGET 販売一覧表示
deactivate Webコントローラ

ブラウザ -> Webコントローラ : GET\n販売一覧表示
activate Webコントローラ

note right : 販売一覧表示\n前述参照

ブラウザ <- Webコントローラ : SUCCESS\n販売一覧ページ
deactivate Webコントローラ
店長 <- ブラウザ : 販売一覧ページ表示

@enduml
```

```plantuml
@startuml
actor 店長
boundary ブラウザ
control Webコントローラ<<Controller>>
control 販売管理<<Application>>
control 在庫管理<<Application>>
entity 商品
entity 販売伝票
entity 販売状況
entity 販売伝票明細
entity 在庫取引

title 返品

店長 -> ブラウザ : 販売明細ページ\n返品
ブラウザ -> Webコントローラ : POST\n返品
activate Webコントローラ
    Webコントローラ -> 販売管理 : 返品する
    activate 販売管理

        販売管理 -> 販売伝票 : 販売伝票照会\nBy販売伝票番号
        activate 販売伝票
            販売伝票 -> 販売状況 :  Join\n販売伝票番号
            activate 販売状況
            販売伝票 <- 販売状況
            deactivate 販売状況
            販売伝票 -> 販売伝票明細 : Outer Join\n販売伝票番号
            activate 販売伝票明細
            販売伝票 <- 販売伝票明細
            deactivate 販売伝票明細
        販売管理 <- 販売伝票
        deactivate 販売伝票

        販売管理 -> 販売管理 : 販売状況が\n未精算または取消済の場合\n返品不可
    Webコントローラ <-- 販売管理 : 販売状況エラー
ブラウザ <-- Webコントローラ : 販売状況エラー
店長 <-- ブラウザ : 販売状況エラー

        販売管理 -> 在庫管理 : 販売明細ごとに\n商品を販売点数分入庫する

        在庫管理 -> 在庫取引 : 販売点数分\n在庫増\n在庫取引登録
        activate 在庫取引
        note right : 在庫取引種別:販売(SD)\n参照取引番号:販売伝票明細番号
        在庫管理 <- 在庫取引
        deactivate 在庫取引

        販売管理 <- 在庫管理
        deactivate 在庫管理

        販売管理 -> 販売状況 : 取消済(RET)\n販売状況更新\nBy販売伝票番号
        activate 販売状況
        販売管理 -> 販売状況
        deactivate 販売状況

    Webコントローラ <- 販売管理
    deactivate 販売管理

ブラウザ <- Webコントローラ : REDIRECT\nGET 販売一覧表示
deactivate Webコントローラ

ブラウザ -> Webコントローラ : GET\n販売一覧表示
activate Webコントローラ

note right : 販売一覧表示\n前述参照

ブラウザ <- Webコントローラ : SUCCESS\n販売一覧ページ
deactivate Webコントローラ
店長 <- ブラウザ : 販売一覧ページ表示

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

title 販売一覧照会

店長 -> ブラウザ : メニュー\n販売
ブラウザ -> React : GET\n販売一覧表示
activate React

    React -> React : AUTH ROUTING\n販売一覧ページ遷移
    React -> React : useEffect
    React -> React : Redux\n販売一覧取得
    React -> RESTコントローラ : 販売一覧取得API
    activate RESTコントローラ

    RESTコントローラ -> 販売管理 : 販売日が対象期間の\n販売伝票を検索する
    activate 販売管理

    note right : ブラウザでの\nユースケース流用

    RESTコントローラ <- 販売管理
    deactivate 販売管理

    React <- RESTコントローラ
    deactivate RESTコントローラ
    React -> React : Store\n販売一覧

ブラウザ <- React : 販売一覧表示
deactivate React
店長 <- ブラウザ : 販売一覧ページ表示
@enduml
```

```plantuml
@startuml
actor 店長
boundary ブラウザ
boundary React<<Web Contents>>
control RESTコントローラ<<Controller>>
control 販売管理<<Application>>

title 新規販売

店長 -> ブラウザ : 販売一覧ページ\n＋（新規販売）
ブラウザ -> React : EVENT\n新規販売
activate React

    React -> React : Redux\n新規販売
    React -> RESTコントローラ : 新規販売開始API
    activate RESTコントローラ

    RESTコントローラ -> 販売管理 : 新たに販売の記録をはじめる
    activate 販売管理

    note right : ブラウザでの\nユースケース流用

    RESTコントローラ <- 販売管理
    deactivate 販売管理

    React <- RESTコントローラ
    deactivate RESTコントローラ
    React -> React : Store\n新規販売伝票

ブラウザ <- React : 販売明細ページ表示
deactivate React
店長 <- ブラウザ : 販売明細ページ表示
@enduml
```

```plantuml
@startuml
actor 店長
boundary ブラウザ
boundary React<<Web Contents>>
control RESTコントローラ<<Controller>>
control 販売管理<<Application>>

title 販売明細照会

店長 -> ブラウザ : メニュー\n販売
ブラウザ -> React : GET\n販売明細表示
activate React

    React -> React : AUTH ROUTING\n販売明細ページ遷移
    React -> React : useEffect
    React -> React : Redux\n販売明細照会
    React -> RESTコントローラ : 販売明細照会API
    activate RESTコントローラ

    RESTコントローラ -> 販売管理 : 販売伝票番号で\n販売伝票明細を\n検索する
    activate 販売管理

    note right : ブラウザでの\nユースケース流用

    RESTコントローラ <- 販売管理
    deactivate 販売管理

    React <- RESTコントローラ
    deactivate RESTコントローラ
    React -> React : Store\n販売伝票明細リスト

ブラウザ <- React : 販売明細ページ表示
deactivate React
店長 <- ブラウザ : 販売明細ページ表示
@enduml
```

```plantuml
@startuml
actor 店長
boundary ブラウザ
boundary React<<Web Contents>>
control RESTコントローラ<<Controller>>
control 販売管理<<Application>>

title 購入商品選択

店長 -> ブラウザ : 販売 ページ\n＋（購入商品選択）
ブラウザ -> React : EVENT\n購入商品選択
activate React
    React -> React : Redux\n購入可能商品取得
    React -> RESTコントローラ : 購入可能商品取得API
    activate RESTコントローラ

    RESTコントローラ -> 販売管理 : 購入商品を選ぶため\n販売可能な商品を検索する
    activate 販売管理

    note right : ブラウザでの\nユースケース流用

    RESTコントローラ <- 販売管理
    deactivate 販売管理
    React <- RESTコントローラ
    deactivate RESTコントローラ
    React -> React : Store\n商品選択用\n購入可能商品\nリスト

ブラウザ <- React : 購入商品選択ページ表示
deactivate React
店長 <- ブラウザ : 購入商品選択ページ表示


店長 -> ブラウザ : 購入商品選択ページ\n商品選択\n販売単価(値引き含)\n購入点数\n(購入額自動計算)\n選択
ブラウザ -> React : EVENT\n選択
activate React
React -> React : Redux\n購入商品選択
React -> RESTコントローラ : 購入商品選択API
activate RESTコントローラ

    RESTコントローラ -> 販売管理 : 購入商品を選ぶ
    activate 販売管理

    note right : ブラウザでの\nユースケース流用

    RESTコントローラ <- 販売管理
    deactivate 販売管理

React <- RESTコントローラ
deactivate RESTコントローラ
React -> React : Redux\n購入商品選択結果判定
ブラウザ <-- React : 購入商品選択エラー発生時\n購入商品選択エラー表示

ブラウザ <- React : 購入商品選択成功時\nAUTH ROUTING\n販売明細ページ遷移
deactivate React
ブラウザ -> React : GET\n販売明細表示
activate React

note right : 販売明細表示は\n前述の通りにて省略

ブラウザ <- React : 販売明細表示
deactivate React
店長 <- ブラウザ : 販売明細ページ表示

@enduml
```

```plantuml
@startuml
actor 店長
boundary ブラウザ
boundary React<<Web Contents>>
control RESTコントローラ<<Controller>>
control 販売管理<<Application>>

title 購入商品除外

店長 -> ブラウザ : 販売明細ページ\n購入商品除外
ブラウザ -> React : EVENT\n購入商品除外
activate React
React -> React : Redux\n購入商品除外
React -> RESTコントローラ : 購入商品除外API
activate RESTコントローラ

    RESTコントローラ -> 販売管理 : 購入対象から\n商品を除外する
    activate 販売管理

    note right : ブラウザでの\nユースケース流用

    RESTコントローラ <-- 販売管理
    deactivate 販売管理

React <- RESTコントローラ
deactivate RESTコントローラ

React -> React : Redux\n購入商品除外結果判定
ブラウザ <-- React : 購入商品除外エラー発生時\n購入商品除外エラー表示\n・販売状況エラー\n・該当商品なしエラー

ブラウザ <- React : 購入商品除外成功時\nAUTH ROUTING\n販売明細ページ遷移
deactivate React
ブラウザ -> React : GET\n販売明細表示
activate React

note right : 販売明細表示は\n前述の通りにて省略

ブラウザ <- React : 販売明細表示
deactivate React
店長 <- ブラウザ : 販売明細ページ表示

@enduml
```

```plantuml
@startuml
actor 店長
boundary ブラウザ
boundary React<<Web Contents>>
control RESTコントローラ<<Controller>>
control 販売管理<<Application>>
control 在庫管理<<Application>>

title 精算

店長 -> ブラウザ : 販売明細ページ\n精算
ブラウザ -> React : EVENT\n精算
activate React
React -> React : AUTH ROUTING\n精算ページ遷移
React -> React : useEffect
activate React
React -> React : Redux\n精算
React -> RESTコントローラ : 精算API
activate RESTコントローラ

     RESTコントローラ -> 販売管理 : 販売伝票番号で\n販売伝票明細を\n検索する
    activate 販売管理

    note right : 販売明細照会と同処理\nにて前述参照

    RESTコントローラ <- 販売管理
    deactivate 販売管理

    React <- RESTコントローラ
    deactivate RESTコントローラ
    React -> React : Store\n販売伝票明細リスト

ブラウザ <- React : 精算ページ表示
deactivate React
店長 <- ブラウザ : 精算ページ表示

店長 -> ブラウザ : 精算ページ\n・預り金入力\n・釣り銭自動計算\n精算
ブラウザ -> React : EVENT\n精算
activate React
React -> React : Redux\n精算
React -> RESTコントローラ : 精算API
activate RESTコントローラ

    RESTコントローラ -> 販売管理 : 精算する
    activate 販売管理

        note right : ブラウザでの\nユースケース流用

        販売管理 -> 在庫管理 : 販売明細ごとに\n商品を販売点数分出庫する
        activate 在庫管理

        販売管理 <- 在庫管理
        deactivate 在庫管理

    RESTコントローラ <-- 販売管理
    deactivate 販売管理

React <- RESTコントローラ
deactivate RESTコントローラ

React -> React : Redux\n精算結果判定
ブラウザ <-- React : 精算エラー発生時\n精算エラー表示\n・販売状況エラー\n・在庫不足エラー

ブラウザ <- React : 精算成功時\nAUTH ROUTING\n販売一覧ページ遷移
deactivate React
ブラウザ -> React : GET\n販売一覧表示
activate React

note right : 販売一覧表示は\n前述の通りにて省略

ブラウザ <- React : 販売一覧表示
deactivate React
店長 <- ブラウザ : 販売一覧ページ表示

@enduml
```

```plantuml
@startuml
actor 店長
boundary ブラウザ
boundary React<<Web Contents>>
control RESTコントローラ<<Controller>>
control 販売管理<<Application>>
control 在庫管理<<Application>>

title 返品

店長 -> ブラウザ : 販売明細ページ\n返品
ブラウザ -> React : EVENT\n返品
activate React
React -> React : Redux\n返品
React -> RESTコントローラ : 返品API
activate RESTコントローラ

    RESTコントローラ -> 販売管理 : 返品する
    activate 販売管理

        note right : ブラウザでの\nユースケース流用

        販売管理 -> 在庫管理 : 販売明細ごとに\n商品を販売点数分入庫する
        activate 在庫管理

        販売管理 <- 在庫管理
        deactivate 在庫管理

    RESTコントローラ <-- 販売管理
    deactivate 販売管理

React <- RESTコントローラ
deactivate RESTコントローラ

React -> React : Redux\n返品結果判定
ブラウザ <-- React : 返品エラー発生時\n返品エラー表示\n・販売状況エラー

ブラウザ <- React : 返品成功時\nAUTH ROUTING\n販売一覧ページ遷移
deactivate React
ブラウザ -> React : GET\n販売一覧表示
activate React

note right : 販売一覧表示は\n前述の通りにて省略

ブラウザ <- React : 販売一覧表示
deactivate React
店長 <- ブラウザ : 販売一覧ページ表示

@enduml
```
