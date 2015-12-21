package ch.mse.mobop.geojobfinder.job.utils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * Created by xetqL on 21/12/2015.
 */
public class APIResponsesUtils {

    private static final Charset UTF_8 = Charset.forName("UTF-8");
    public static String decode(String input) throws UnsupportedEncodingException{
        return StringEscapeUtils.unescapeJava(input);
    }

    public static String readStream(InputStream in) throws IOException{
        StringWriter writer = new StringWriter();
        IOUtils.copy(in, writer, Charset.forName("UTF-8"));
        return writer.toString();
    }

}
