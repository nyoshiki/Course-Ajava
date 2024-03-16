# -------------------------------
# VS Codeがファイルの変更を検知するのはOSのファイル変更監視をもちいている
# ファイル変更管理の数は最大数が決まっておりそれ以上の監視はアラートが発生する
# その場合、OSのパラメータを変更する必要がある
# https://code.visualstudio.com/docs/setup/linux#_visual-studio-code-is-unable-to-watch-for-file-changes-in-this-large-workspace-error-enospc
# 一方でコンテナはシステムパラメータはReadOnlyでありコンテナで変更はできない
# コンテナはホストOSとリソースを共有しているためホストOS側で起動するコンテナやホストOSのバランスを図り
# ホストOS側で最大数を調整する。
# 本シェルは最大値で更新するようにしているがコンテナ数が多くなりリソースが超過する場合は調整いただきたい
# -------------------------------
MAX_FILE_WATCHES=`cat /proc/sys/fs/inotify/max_user_watches`

if [ ${MAX_FILE_WATCHES} -lt 50000 ]; then
  echo "max_user_watches ${MAX_FILE_WATCHES} -> update"
  echo "fs.inotify.max_user_watches = 524288" | sudo tee -a /etc/sysctl.conf
  sudo sysctl -p
  echo "update completed -> max_user_watches `cat /proc/sys/fs/inotify/max_user_watches`"
else
  echo "max_user_watches ${MAX_FILE_WATCHES} -> already updated"
fi
