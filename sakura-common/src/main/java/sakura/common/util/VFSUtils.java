package sakura.common.util;

import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.jooq.lambda.fi.util.function.CheckedConsumer;
import org.jooq.lambda.fi.util.function.CheckedFunction;
import sakura.common.lang.EX;
import sakura.common.lang.Objects;
import sakura.common.lang.annotation.Nullable;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * <虚拟文件处理类,基于vfs进行二次封装，针对常用的，文件写入，文件读取，文件拷贝进行封装>
 * <p>
 * see http://commons.apache.org/vfs/filesystems.html
 * <p>
 */
@Slf4j
@UtilityClass
public class VFSUtils {

    public static final FileSystemManager FILE_SYSTEM_MANAGER;

    static {
        try {
            FILE_SYSTEM_MANAGER = VFS.getManager();
        } catch (FileSystemException e) {
            throw EX.unchecked(e, "Init vfs fileSystemManager fail.");
        }
    }

    public static FileObject getFile(URL url) {
        return getFile(url.toString());
    }

    @SneakyThrows
    public static FileObject getFile(String filePath) {
        return FILE_SYSTEM_MANAGER.resolveFile(filePath);
    }

    @SneakyThrows
    public static <T> T read(String filePath, CheckedFunction<InputStream, T> func) {
        @Cleanup val file = getFile(filePath);
        Validate.isTrue(file.exists(), "File %s not exists", filePath);
        Validate.isTrue(file.isFile(), "File %s exists but is a directory", filePath);
        @Cleanup val in = file.getContent().getInputStream();
        return func.apply(in);
    }

    public static byte[] readFileToByteArray(String filePath) {
        return read(filePath, IOUtils::toByteArray);
    }

    public static byte[] readFileToByteArray(URL url) {
        return readFileToByteArray(url.toString());
    }

    public static String readFileToString(String filePath, @Nullable Charset encoding) {
        return read(filePath, in -> IOUtils.toString(in, encoding));
    }

    public static String readFileToString(String filePath) {
        return read(filePath, in -> IOUtils.toString(in, StandardCharsets.UTF_8));
    }

    public static String readFileToString(URL url, @Nullable Charset encoding) {
        return readFileToString(url.toString(), encoding);
    }

    public static String readFileToString(URL url) {
        return readFileToString(url, null);
    }

    public static List<String> readLines(String filePath, @Nullable Charset encoding) {
        return read(filePath, in -> IOUtils.readLines(in, encoding));
    }

    public static List<String> readLines(String filePath) {
        return read(filePath, in -> IOUtils.readLines(in, StandardCharsets.UTF_8));
    }

    public static List<String> readLines(URL url, @Nullable Charset encoding) {
        return readLines(url.toString(), encoding);
    }

    public static List<String> readLines(URL url) {
        return readLines(url, null);
    }

    @SneakyThrows
    public static void write(String filePath, CheckedConsumer<OutputStream> consumer) {
        @Cleanup val file = getFile(filePath);
        if (!file.exists()) file.createFile();
        Validate.isTrue(file.isFile(), "File %s exists but is a directory", filePath);
        @Cleanup val out = file.getContent().getOutputStream();
        consumer.accept(out);
    }

    public static void writeStringToFile(String filePath, CharSequence data, @Nullable Charset encoding) {
        if (Objects.isEmpty(data)) return;
        write(filePath, out -> IOUtils.write(data, out, encoding));
    }

    public static void writeStringToFile(String filePath, CharSequence data) {
        if (Objects.isEmpty(data)) return;
        write(filePath, out -> IOUtils.write(data, out, StandardCharsets.UTF_8));
    }

    public static void writeStringToFile(URL url, CharSequence data, @Nullable Charset encoding) {
        writeStringToFile(url.toString(), data, encoding);
    }

    public static void writeStringToFile(URL url, CharSequence data) {
        writeStringToFile(url, data, null);
    }

    public static void writeByteArrayToFile(String filePath, byte[] data) {
        if (Objects.isEmpty(data)) return;
        write(filePath, out -> IOUtils.write(data, out));
    }

    public static void writeByteArrayToFile(URL url, byte[] data) {
        writeByteArrayToFile(url.toString(), data);
    }

} 