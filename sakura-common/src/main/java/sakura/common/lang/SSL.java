package sakura.common.lang;

import lombok.Data;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import sakura.common.lang.annotation.Nullable;

import javax.net.ssl.*;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * Created by haomu on 2018/4/20.
 */
@Slf4j
@UtilityClass
public class SSL {

    public static final UnSafeHostnameVerifier UN_SAFE_HOSTNAME_VERIFIER = new UnSafeHostnameVerifier();
    public static final UnSafeTrustManager UN_SAFE_TRUST_MANAGER = new UnSafeTrustManager();

    public static SSLParams getSSLParams(InputStream[] certificates, InputStream bksFile, String password) {
        try {
            TrustManager[] trustManagers = loadTrustManager(certificates);
            KeyManager[] keyManagers = loadKeyManager(bksFile, password);
            SSLContext sslContext = SSLContext.getInstance("TLS");
            X509TrustManager trustManager;
            if (trustManagers != null) {
                trustManager = new DefaultTrustManager(chooseTrustManager(trustManagers));
            } else {
                trustManager = UN_SAFE_TRUST_MANAGER;
            }
            sslContext.init(keyManagers, new TrustManager[]{trustManager}, null);
            return new SSLParams(sslContext.getSocketFactory(), trustManager);
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            throw EX.unchecked(e);
        }
    }

    @Nullable
    private static TrustManager[] loadTrustManager(@Nullable InputStream... certificates) {
        if (certificates == null || certificates.length <= 0) return null;
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            int index = 0;
            for (InputStream certificate : certificates) {
                String certificateAlias = Integer.toString(index++);
                keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));
                IOUtils.closeQuietly(certificate);
            }
            TrustManagerFactory managerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            managerFactory.init(keyStore);
            return managerFactory.getTrustManagers();
        } catch (Exception e) {
            log.error("Load TrustManager failed", e);
        }
        return null;

    }

    @Nullable
    private static KeyManager[] loadKeyManager(@Nullable InputStream bksFile, @Nullable String password) {
        if (bksFile == null || password == null) return null;
        try {
            KeyStore clientKeyStore = KeyStore.getInstance("BKS");
            clientKeyStore.load(bksFile, password.toCharArray());
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(clientKeyStore, password.toCharArray());
            return keyManagerFactory.getKeyManagers();
        } catch (Exception e) {
            log.error("Load KeyManager failed", e);
        }
        return null;
    }

    @Nullable
    private static X509TrustManager chooseTrustManager(TrustManager[] trustManagers) {
        for (TrustManager trustManager : trustManagers) {
            if (trustManager instanceof X509TrustManager) {
                return (X509TrustManager) trustManager;
            }
        }
        return null;
    }

    @Data
    public static class SSLParams {
        public final SSLSocketFactory socketFactory;
        public final X509TrustManager trustManager;
    }

    public class UnSafeHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    public static class UnSafeTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }

    public static class DefaultTrustManager implements X509TrustManager {
        private X509TrustManager defaultTrustManager;
        private X509TrustManager localTrustManager;

        public DefaultTrustManager(@Nullable X509TrustManager localTrustManager) throws NoSuchAlgorithmException, KeyStoreException {
            TrustManagerFactory factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            factory.init((KeyStore) null);
            defaultTrustManager = chooseTrustManager(factory.getTrustManagers());
            this.localTrustManager = localTrustManager;
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            try {
                defaultTrustManager.checkServerTrusted(chain, authType);
            } catch (CertificateException ce) {
                localTrustManager.checkServerTrusted(chain, authType);
            }
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

}
