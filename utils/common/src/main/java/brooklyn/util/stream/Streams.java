package brooklyn.util.stream;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import brooklyn.util.exceptions.Exceptions;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.io.ByteStreams;
import com.google.common.io.InputSupplier;

public class Streams {

    private static final Logger log = LoggerFactory.getLogger(Streams.class);
    
    public static void closeQuietly(Closeable x) {
        try {
            x.close();
        } catch (Exception e) {
            if (log.isDebugEnabled())
                log.debug("Error closing (ignored) "+x+": "+e);
        }
    }

    public static InputStream fromString(String input) {
        try {
            byte[] bytes = checkNotNull(input, "input").getBytes(Charsets.UTF_8);
            InputSupplier<ByteArrayInputStream> supplier = ByteStreams.newInputStreamSupplier(bytes);
            InputStream stream = supplier.getInput();
            return stream;
        } catch (IOException ioe) {
            if (log.isDebugEnabled()) log.debug("Error creating InputStream from String: " + ioe.getMessage());
            throw Exceptions.propagate(ioe);
        }
    }

    public static Supplier<Integer> sizeSupplier(final ByteArrayOutputStream src) {
        Preconditions.checkNotNull(src);
        return new Supplier<Integer>() {
            @Override
            public Integer get() {
                return src.size();
            }
        };
    }

    public static ByteArrayOutputStream byteArrayOfString(String in) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            stream.write(in.getBytes());
        } catch (IOException e) {
            throw Exceptions.propagate(e);
        }
        return stream;
    }

    public static boolean logStreamTail(Logger log, String message, ByteArrayOutputStream stream, int max) {
        if (stream!=null && stream.size()>0) {
            String streamS = stream.toString();
            if (max>=0 && streamS.length()>max)
                streamS = "... "+streamS.substring(streamS.length()-max);
            log.info(message+":\n"+streamS);
            return true;
        }
        return false;
    }

    public static Reader reader(InputStream stream) {
        return new InputStreamReader(stream);
    }
    
    public static Reader reader(InputStream stream, Charset charset) {
        return new InputStreamReader(stream, charset);
    }

}