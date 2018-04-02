package com.github.beihaifeiwu.sakura.common.io;

import com.github.beihaifeiwu.sakura.common.io.vfs.VFS;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * Created by haomu on 2018/4/1.
 */
public class VFSTest {

    @Test
    public void test() throws IOException {
        String path = StringUtils.replaceChars(ClassUtils.getPackageCanonicalName(getClass()), '.', '/');
        List<String> resources = VFS.getInstance().list(path);
        resources.forEach(System.out::println);
    }

}
