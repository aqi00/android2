package org.vudroid.pdfdroid.codec;

import org.vudroid.core.VuDroidLibraryLoader;
import org.vudroid.core.codec.CodecContext;
import org.vudroid.core.codec.CodecDocument;

import android.content.ContentResolver;

public class PdfContext implements CodecContext {
    static {
        VuDroidLibraryLoader.load();
    }

    public CodecDocument openDocument(String fileName) {
        return PdfDocument.openDocument(fileName, "");
    }

    public void setContentResolver(ContentResolver contentResolver) {
    }

    public void recycle() {
    }
}
