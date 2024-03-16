#FROM bellsoft/liberica-openjdk-alpine:17 AS java-build
FROM  openjdk:17-jdk-slim AS java-build


ARG JAR_FILE
ARG JRE_LIBS_FILE
ARG BOOT_LIB_DIR=/opt/flowershop
COPY ${JAR_FILE} ${BOOT_LIB_DIR}/app.jar
COPY ${JRE_LIBS_FILE} ${BOOT_LIB_DIR}/libs.list

# jlinkでjdpsで解析したアプリケーションの依存関係があるライブラリだけを格納したJREを生成しサイズを圧縮する。
ENV PATH $JAVA_HOME/bin:$PATH
WORKDIR /jlink
RUN cat ${BOOT_LIB_DIR}/libs.list && \
    jlink --strip-java-debug-attributes --no-header-files --no-man-pages --compress=2 --module-path $JAVA_HOME \
    --add-modules $(cat ${BOOT_LIB_DIR}/libs.list) \
    --output jre-min

FROM debian:stretch-slim

# Spring Bootを実行するシステムユーザとグループを生成。システムユーザはログイン不可に変更
# ログが書き込めるようにログ出力先はオーナーを変更する
RUN groupadd -r spring && useradd -r -g spring -s /bin/false spring 
RUN mkdir -p /var/log/flowershop && chown spring:spring /var/log/flowershop
USER spring:spring

# アプリケーションに必要な最小構成に圧縮したJREを配置
COPY --from=java-build /jlink/jre-min /opt/jre-min
ARG JAR_FILE
COPY ${JAR_FILE} /opt/flowershop/app.jar
ENV PATH /opt/jre-min/bin:$PATH
WORKDIR /
ENTRYPOINT ["java", "-jar", "/opt/flowershop/app.jar"]
