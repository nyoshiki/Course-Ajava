# 次世代開発コースA Level1 店舗業務デジタル化 Level2 モバイル化

## 1. ユースケース・要件

### 【Level1】 L1-UC-010 WebブラウザでユーザID・パスワードで認証する

* L2-UC-010-R-010 私(店長)しか使わないので固定のユーザIDとパスワードで認証ができれば良い。
* L2-UC-010-R-020 当面アルバイトも雇える余力はないので扱えるユーザは追加できなくて良い。
  
### 【Level2】 L2-UC-010 Webブラウザと同じユーザID・パスワードで認証する

* L2-UC-010-R-010 スマートフォンからもWebブラウザと同様にサーバサイドでのユーザIDとパスワードで良い。
* L2-UC-010-R-020 店長だけでクローズドのWIFIで利用すること、そこまでセキュアな情報がシステム内にはないことから、多要素認証や端末認証までは不要。

## 2. モデリング

### 2.1. ロバストネス分析

```plantuml

@startuml

left to right direction

actor 店長

package "L2-UC-010-RB01 Webブラウザと同じユーザID・パスワードで認証する"{

    boundary ログインページ
    boundary メニューページ
    control 認証
    control 認証結果判定

    店長 -> ログインページ
    ログインページ --> 認証 : ログイン
    認証結果判定 <- 認証
    ログインページ <- 認証結果判定
    メニューページ <- ログインページ
    店長 <-- メニューページ
}

@enduml
```

### 2.2. シーケンス分析(ブラウザ)

```plantuml
@startuml
actor 店長
boundary ブラウザ
control Web認証認可<<Spring Security>>
control Webコントローラ<<Controller>>
control 認証<<Application>>

title ログイン

店長 -> ブラウザ : URL\nログインページ
ブラウザ -> Web認証認可 : GET\nログイン表示
activate Web認証認可
ブラウザ <- Web認証認可 : SUCCESS\nログインページ
deactivate Web認証認可
店長 <- ブラウザ : ログインページ表示

店長 -> ブラウザ : ユーザID\nパスワード\nログインボタン
ブラウザ -> Web認証認可 : POST\nログイン
activate Web認証認可
Web認証認可 -> 認証 : ユーザIDとパスワードで\n認証する
activate 認証
Web認証認可 <- 認証
deactivate 認証
Web認証認可 -> Web認証認可 : 認証結果判定

ブラウザ <-- Web認証認可 : 認証失敗時\nREDIRECT\nGET ログイン表示エラー表示

note left : 認証エラー時は\nログインページで\nエラー表示

ブラウザ <- Web認証認可 : 認証成功時\nREDIRECT\nGET メニュー表示
deactivate Web認証認可

ブラウザ -> Webコントローラ : GET\nメニュー表示
activate Webコントローラ
ブラウザ <- Webコントローラ : SUCCESS\nメニューページ
deactivate Webコントローラ
店長 <- ブラウザ : メニューページ表示

@enduml
```

### 2.3. シーケンス分析(モバイル)

```plantuml
@startuml
actor 店長
boundary ブラウザ
boundary React<<Web Contents>>
control API認証認可<<Spring Security>>
control 認証<<Application>>

title ログイン

店長 -> ブラウザ : URL\nログインページ
ブラウザ -> React : GET\nログイン表示
activate React
ブラウザ <- React : ROUTING\nログインページ
deactivate React
店長 <- ブラウザ : ログインページ表示

店長 -> ブラウザ : ユーザID\nパスワード\nログインボタン
ブラウザ -> React : EVENT\nログイン
activate React
React -> React : Redux\n認証
React -> API認証認可 : 認証API
activate API認証認可
API認証認可 -> 認証 : ユーザIDとパスワードで\n認証する
activate 認証
API認証認可 <- 認証
deactivate 認証
React <- API認証認可
deactivate API認証認可
React -> React : Redux\n認証結果判定
ブラウザ <- React : AUTH ROUTING\n販売状況ページ遷移
deactivate React
ブラウザ -> React : GET\n販売状況表示
activate React
note right : 販売状況表示は\nUC-020参照
ブラウザ <- React : 販売状況表示
deactivate React
店長 <- ブラウザ : 販売状況ページ表示

@enduml
```
