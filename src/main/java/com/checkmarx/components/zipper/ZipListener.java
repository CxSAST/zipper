package com.checkmarx.components.zipper;

/**
 * Receives notifications of the zipping progress. Each time a new file begins
 * being compressed the listener is notified.
 *
 * @author Denis Krivitski
 */

public interface ZipListener {

    /**
     * Zip progress notification.
     *
     * @param currentFileName Name of the file that starts being compressed
     * @param currentCompressedSize Size of compressed data currently in the output stream
     */
    void updateProgress(String currentFileName,long currentCompressedSize);

}
