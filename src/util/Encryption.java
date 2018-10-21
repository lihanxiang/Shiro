package util;

import entity.User;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.springframework.stereotype.Component;

@Component
public class Encryption {

    //生成随机盐值
    private SecureRandomNumberGenerator randomNumberGenerator = new SecureRandomNumberGenerator();

    //加密算法名称
    private String algorithmName = "MD5";

    //加密次数
    private int iterationTimes = 5;

    public SecureRandomNumberGenerator getRandomNumberGenerator() {
        return randomNumberGenerator;
    }

    public void setRandomNumberGenerator(SecureRandomNumberGenerator randomNumberGenerator) {
        this.randomNumberGenerator = randomNumberGenerator;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public void setAlgorithmName(String algorithmName) {
        this.algorithmName = algorithmName;
    }

    public int getIterationTimes() {
        return iterationTimes;
    }

    public void setIterationTimes(int iterationTimes) {
        this.iterationTimes = iterationTimes;
    }

    public void encryptPassword(User user){
        if (user.getPassword() != null){
            //设置盐值
            user.setSalt(randomNumberGenerator.nextBytes().toHex());
            //加密
            String password = new SimpleHash(algorithmName, user.getPassword(),
                    ByteSource.Util.bytes(user.getSalt()), iterationTimes).toHex();
            user.setPassword(password);
        }
    }
}
