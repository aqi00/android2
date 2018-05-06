package org.vudroid.djvudroid.codec;

import org.vudroid.core.codec.CodecDocument;

public class DjvuDocument implements CodecDocument
{
    private long documentHandle;
    private final Object waitObject;

    private DjvuDocument(long documentHandle, Object waitObject)
    {
        this.documentHandle = documentHandle;
        this.waitObject = waitObject;
    }

    static DjvuDocument openDocument(String fileName, DjvuContext djvuContext, Object waitObject)
    {
        return new DjvuDocument(open(djvuContext.getContextHandle(), fileName), waitObject);
    }

    private native static long open(long contextHandle, String fileName);
    private native static long getPage(long docHandle, int pageNumber);
    private native static int getPageCount(long docHandle);
    private native static void free(long pageHandle);

    public DjvuPage getPage(int pageNumber)
    {
        return new DjvuPage(getPage(documentHandle, pageNumber), waitObject);
    }

    public int getPageCount()
    {
        return getPageCount(documentHandle);
    }

    @Override
    protected void finalize() throws Throwable
    {
        recycle();
        super.finalize();
    }

    public synchronized void recycle() {
        if (documentHandle == 0) {
            return;
        }
        free(documentHandle);
        documentHandle = 0;
    }
}
