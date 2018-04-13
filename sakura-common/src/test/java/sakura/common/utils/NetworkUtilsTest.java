package sakura.common.utils;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.net.InetAddress;

/**
 * Created by liupin on 2017/9/14.
 */
public class NetworkUtilsTest {

    @Test
    public void getLocalhostTest() {
        InetAddress localhost = NetworkUtils.getLocalhost();
        Assert.assertNotNull(localhost);
    }

    @Test
    @Ignore
    public void getLocalMacAddressTest() {
        String macAddress = NetworkUtils.getLocalMacAddress();
        Assert.assertNotNull(macAddress);

        //验证MAC地址正确
        boolean match = RegexUtils.isMatch(RegexUtils.MAC_ADDRESS, macAddress);
        Assert.assertTrue(match);
    }

    @Test
    public void longToIpTest() {
        String ipv4 = NetworkUtils.longToIpv4(2130706433L);
        Assert.assertEquals("127.0.0.1", ipv4);
    }

    @Test
    public void ipToLongTest() {
        long ipLong = NetworkUtils.ipv4ToLong("127.0.0.1");
        Assert.assertEquals(2130706433L, ipLong);
    }

}
