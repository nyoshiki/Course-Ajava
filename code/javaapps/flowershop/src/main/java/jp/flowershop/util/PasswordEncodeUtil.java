package jp.flowershop.util;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.Banner;
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.boot.WebApplicationType;
// import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.boot.builder.SpringApplicationBuilder;
// import org.springframework.context.annotation.Bean;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Component;

/**
 * BCryptパスワードエンコーディング
 */
//mainクラスが複数にならない様にコメントアウトしているが実行する場合は
//下記をクラス定義として置き換えmainメソッドとrunメソッドを有効にする。
//@SpringBootApplication     
//@Component
//    public class PasswordEncodeUtil implements CommandLineRunner {
public class PasswordEncodeUtil {

    // private static final Logger logger = LoggerFactory.getLogger(PasswordEncodeUtil.class);

    @Autowired
    PasswordEncoder passwordEncoder;
    /*
    public static void main(String[] args){
        new SpringApplicationBuilder(PasswordEncodeUtil.class)
                                    .web(WebApplicationType.NONE)
                                    .bannerMode(Banner.Mode.OFF).run(args);
    }
    
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Override
    public void run(String... args){
    
        if (args.length == 0){
            logger.info("[PASSWORD ENCODE UTIL] Execute mode is web then exit.");
            return;
        }
        String password = args[0];
        String digest = passwordEncoder.encode(password);
        logger.info("[PASSWORD ENCODE UTIL] CRAPPED VALUE [" + digest + "]");
    
        if (passwordEncoder.matches(password, digest)) {
            logger.info("[PASSWORD ENCODE UTIL] match test done.");
        }
        else {
            logger.info("[PASSWORD ENCODE UTIL] unmatched. please retry.");
        }
    
    }
*/
}
