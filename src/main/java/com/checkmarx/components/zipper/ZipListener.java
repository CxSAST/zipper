package com.checkmarx.components.zipper;

/**
 * Created with IntelliJ IDEA.
 * User: denis
 * Date: 13/11/2013
 * Time: 13:15
 * Description:
 */
public interface ZipListener {

    void updateProgress(String currentFileName,long currentCompressedSize);
    void sizeLimitReached(long currentCompressedSize, long maxZipSize);

}
