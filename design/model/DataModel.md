# 次世代開発コースA 店舗業務デジタル化・モバイル化 データモデル

## 利用方法

* 本資料はLevel1店舗業務のデジタル化、Level2店舗業務のモバイル化時点までのデータモデルのサンプルである。
* 当初の目的を果たすことができればデータモデルは下記に従う必要なない。
* VS CodeにてPlant UMLでモデリングを実施したあと、同じフォルダにある`plantuml2mysql.py`を利用し、MySQLのテーブル生成用DDLを作成できる。`./plantuml2mysql DataModel.md flower_db`を実行する。

---

## データ要件

### 1. 仕入先と商品

* 仕入先は花き市場で花の種類や季節により仕入先が異なるが、現時点では仕入先を識別する意味が少ないとのことにて花き市場「東京フラワーポート」のみを登録する。
* 花の種類は要件にもあるようにバリエーションごとのSKUは扱わないためシンプルに商品IDと販売単価を管理
* 商品は販売開始終了を項目として保持するが将来拡張用のためであり当初は利用しない。

### 2. 在庫の扱い

* 在庫は直近の棚卸時点の商品別の在庫数を定点の在庫として保持する。
* 在庫の移動は在庫取引として在庫の増減と発生理由を保持する。
* 現在の在庫数は前回棚卸し時点の在庫に前回棚卸し時点からの在庫取引の増減を加味して算定する
* 棚卸の履歴は将来必要に応じて在庫とは別に履歴を保持する。
* 現在は取引数が少ないことから現在在庫数を照会する際に都度前回棚卸しの在庫数と以後の在庫取引で在庫計算をするが、将来取引数が増加した場合は都度計算した在庫数を保持することも検討する。

### 3. 発注・入荷・仕入の扱い

* 発注できる商品は取り扱い商品となっている商品のみ。仕入先も取引先として管理されている仕入先に対してのみ発注できる。
* 発注状況は発注伝票と別管理にするが必ず１：１で存在することを保証する。
* １回の発注に対して入荷も１回とする。分納の場合は発注も別途起票する。
* 発注取消となった場合は入荷は存在しない。
* 仕入先に入荷後返品する場合は発注数を減らし入荷数を減らすように起票する。
* 仕入先に入荷後返品する場合は現在の在庫数を超えて返品はできない。
* 入荷が完了した発注は変更できない。必ず入荷を取消してから発注を変更するため発注と入荷の内容は必ず一致する。
* 入荷時に仕入単価や点数が変更になった場合は入荷と同時に発注にも変更が反映される。
* 入荷時は入荷点数分を入庫して在庫を増やす。
* 仕入先への返品時は別途発注数、入荷数を減らす伝票を起票し、返品数を出庫して在庫を減らす。
* 入荷を取り消した場合は入庫数を減らし在庫を減らす。（出庫扱いにしない。）
* 仕入できる商品は取り扱い商品となっている商品のみ。仕入先も取引先として管理されている仕入先に対してのみ発注できる。
* 仕入時に仕入点数分を入庫することで在庫を仕入点数分増やす。
* 仕入を取り消す場合は入庫も取り消すことで在庫を減らす。（出庫扱いにしない。）

### 4. 販売の扱い

* 販売できる商品は取り扱い商品となっている商品のみ。
* 商品の現在の在庫数を超えて販売することはできない。（Level2の現時点では入荷予定は加味した販売はできない）
* 販売状況は別のテーブルで管理するが必ず１：１で存在することを保証する。
* 販売と連動して出庫することで販売点数分在庫を減らす。
* 返品時は返品点数分を入庫して在庫を増やす。

---

## データモデル

```plantuml

UML legend:

table = class
#pkey
+index

@startuml

class product <<(T,white)>> {
    商品
    ==
    #product_id : varchar(20) not null -- 商品ID(FW+連番)
    product_name : varchar(50) not null -- 商品名
    unit_price : int not null -- 販売単価
    sales_start_date : varchar(8) not null -- 販売開始日
    sales_end_date : varchar(8) not null -- 販売終了日
}

class supplier <<(T,white)>> {
    仕入先
    ==
    #supplier_id : varchar(10) not null -- 仕入先ID(FSP+連番)
    supplier_name : varchar(50) not null -- 仕入先名
    contact_name : varchar(50) not null -- 担当者名
    contact_tel : varchar(11) not null -- 担当者電話番号
    contact_mail : varchar(128) not null -- 担当者メールアドレス
}

class stock <<(T,white)>> {
    在庫
    ==
    #product_id : varchar(20) not null -- 商品ID:product
    qty : int not null -- 在庫数
    stock_taking_date_time : varchar(14) not null -- 棚卸日時
}

stock "1" -- "1" product

class inventory_history <<(T,white)>> {
    棚卸履歴
    ==
    #inventory_history_no : varchar(26) not null -- 棚卸履歴番号(IH+棚卸日時+商品ID)
    +product_id : varchar(20) not null -- 商品ID:product
    product_name : varchar(50) not null-- 商品名(棚卸時点の商品名)
    qty : int not null -- 在庫数
    stock_taking_date_time : varchar(14) not null -- 棚卸日時
}

inventory_history "1" -- "1" product
inventory_history "1" -- "1" stock

class inventory_transaction <<(T,white)>> {
    在庫取引
    ==
    #inventory_trans_no : varchar(26) not null -- 在庫取引番号(IT+在庫取引日時+商品ID)
    inventory_trans_type_cd : varchar(2) not null -- 在庫取引種別(ID:入荷、PC:仕入、SD:販売、OD:出荷、IA:在庫調整)
    trans_date : varchar(8) not null -- 在庫取引日
    trans_date_time : varchar(14) not null -- 在庫取引日時
    product_id : varchar(20) not null -- 商品ID:product
    qty : int not null -- 取引点数
    refelence_trans_no : varchar(26) not null -- 参照取引番号:入荷・仕入・販売
}

product "1" -- "0..*" inventory_transaction
stock "1" -- "0..*" inventory_transaction

class purchase_order <<(T,white)>> {
    購買発注伝票
    ==
    #purchase_order_no : varchar(26) not null -- 購買発注番号(PO+発注日時+商品ID)
    purchase_order_date : varchar(8) not null -- 発注日
    purchase_order_date_time : varchar(14) not null -- 発注日時
    supplier_id : varchar(10) not null --   仕入先ID:supplier
    product_id : varchar(20) not null -- 商品ID:product
    product_name : varchar(50) not null-- 商品名(発注時点の商品名)
    purchase_unit_price : int not null -- 仕入単価
    qty : int not null -- 仕入点数
    amount : int not null -- 仕入額
    expected_recieved_date : varchar(8) not null -- 入荷予定日
    supplier_transaction_no : varchar(30) -- 仕入先取引番号
    memo : varchar(100) -- 備考
}

product "1" -- "0..*" purchase_order
supplier "1" -- "0..*" purchase_order

class purchase_order_status <<(T,white)>> {
    購買発注状況
    ==
    #purchase_order_no : varchar(26) not null -- 購買発注番号:purchase_order
    purchase_status_cd : varchar(3) not null -- 購買発注状況(BKO:入荷待、INS:入荷済、CAN:発注取消)
}

purchase_order "1" -- "1" purchase_order_status

class inbound_delivery  <<(T,white)>> {
    入荷伝票
    ==
    #inbound_delivery_no : varchar(26) not null -- 入荷伝票番号(ID+入荷日時+商品ID)
    purchase_order_no : varchar(26) not null -- 購買発注番号:purchase_order
    recieved_date : varchar(8) not null -- 入荷日
    recieved_date_time : varchar(14) not null -- 入荷日時
    product_id : varchar(20) not null -- 商品ID:product
    product_name : varchar(50) not null-- 商品名(入荷時点の商品名)
    purchase_unit_price : int not null -- 仕入単価
    qty : int not null -- 仕入点数
    amount : int not null -- 仕入額
    supplier_transaction_no : varchar(30) -- 仕入先取引番号
    memo : varchar(100) -- 備考
}

product "1" -- "0..*" inbound_delivery
purchase_order "1" -- "0..*" inbound_delivery
inbound_delivery "1" -- "1" inventory_transaction

class purchase  <<(T,white)>> {
    仕入伝票
    ==
    #purchase_no : varchar(26) not null -- 仕入伝票番号(PC+仕入日時+商品ID)
    purchase_date : varchar(8) not null -- 仕入日
    purchase_date_time : varchar(14) not null -- 仕入日時
    supplier_id : varchar(10) not null --   仕入先ID:supplier
    product_id : varchar(20) not null -- 商品ID:product
    product_name : varchar(50) not null-- 商品名(仕入時点の商品名)
    purchase_unit_price : int not null -- 仕入単価
    qty : int not null -- 仕入点数
    amount : int not null -- 仕入額
    supplier_transaction_no : varchar(30) -- 仕入先取引番号
    memo : varchar(100) -- 備考
}

product "1" -- "0..*" purchase
supplier "1" -- "0..*" purchase
purchase "1" -- "1" inventory_transaction

class sales <<(T,white)>> {
    販売伝票
    ==
    #sales_no : varchar(15) not null-- 販売伝票番号
    sales_date : varchar(8) not null-- 販売日
    sales_date_time : varchar(14) not null-- 販売時刻
    total_qty : int not null-- 販売点数
    total_amount : int not null-- 合計金額
}

class sales_detail <<(T,white)>> {
    販売伝票明細
    ==
    #sales_detail_no : varchar(20) not null-- 販売伝票明細番号
    sales_no : varchar(15) not null-- 販売伝票番号
    product_id : varchar(20) not null-- 商品ID
    product_name : varchar(50) not null-- 商品名
    unit_price : int not null-- 販売単価
    qty : int not null-- 数量
    amount : int not null-- 小計金額
}

sales "1" -- "0..*" sales_detail
product "1" -- "0..*" sales_detail
sales_detail "1" -- "1" inventory_transaction

class sales_status <<(T,white)>> {
    販売状況
    ==
    #sales_no : varchar(15) not null-- 販売伝票番号
    sales_status_cd : varchar(3) not null -- 販売状況(PRE:未精算、CMP:販売完了、RET:返品)
}

sales "1" -- "１" sales_status

@enduml
```
