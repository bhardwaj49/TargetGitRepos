package co.avinash.targetgitrepos.network;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.zip.GZIPInputStream;

public class GZIPUtils {


    public static Reader convertReader(byte[] jsonBytes) {
        Reader reader = null;
        try {
            GZIPInputStream gStream = new GZIPInputStream(new ByteArrayInputStream(jsonBytes));
            reader = new InputStreamReader(gStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reader;
    }
}

