# デザインからテーブル生成DDLとドメインモデルの自動生成

## 自動生成

- フォルダを開くで~/workspace/flowershop/designを開きます

- domain/DataModel.mdを開き、右上の右から3つ目「Markdown Preview Enhanced」を開きます。より詳細なデータ要件とモデルが表示されます。（設計ヒントより店長とユースケースを話し合い具体化しているため一部要件の調整に合わせて変更になっている想定です。）


- 「Ctrl＋@」でターミナルを起動し下記のコマンドを実行します。

  ```
  cd model
  bash generate_flower_model.sh
  ```


- 実行後、下記2つのファイルができあがります
  - 4_create_flower_db_tables.sql　・・・　テーブル生成用DDL
  - domain/*.java ・・・ドメインクラスJava　※Sales.javaやSalesDetail.java、Product .java はすでにビジネスロジックも実装しているものを皆さんに配布してますが、ここで生成されたものは純粋なデータクラスのみですので上書きしないようご注意ください。


## 生成されたDDLからMySQLにテーブルを生成する

  1.  MySQLのコンテナ生成時に取り込む方法

    - 4.create_flower_db_tables.sqlをMySQLコンテナ生成時に取込されるように1_create_tables.sqlを上書きして配置しコンテナを再生成

      ```
      mv ~/workspace/flowershop/docker/devenv/mysql/initdb.d/1_create_tables.sql .
      cp -p 4_create_flower_db_tables.sql ~/workspace/flowershop/docker/devenv/mysql/initdb.d/1_create_tables.sql
      
      cd ~/workspace/flowershop/docker/util
      bash c7-stop_container.sh
      bash remove_db_volumes.sh
      bash c1-build_and_start_compose.sh
      ```
      

  2.  VS CodeのSQLToolsで直接データベースへCREATE TABLEを実行する方法

    - VS CodeのSQLToolsを開きます。

    - コマンドパレット「Ctrl＋Shift+p」で「sqltools newsqlfile」を入力しSQLToolsのSQLファイルを空で開きます。

    - 4.create_flower_db_tables.sqlから「USE flower_db;」と作成したいテーブルの「CREATE TABLE」文をコピーします。

    - ファイルの上の「Run on active connection」をクリックします。

    - QUERYエラーとなるケースがありますがSQLToolあるいはMySQLコンテナから参照すると成功しています。


  3. MySQLコンテナに接続して実行する方法
  
    - u5*でコンテナに接続し、mysqlコマンドでSQLを実行します。
