package com.checkmarx.components.zipper;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tools.ant.DirectoryScanner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * File zipper with filter.
 * <p>
 * This class implements a file zipper with filter. Zipper will traverse a specified base directory
 * and archive all files passing the specified filter test. The filter string is a comma separated list
 * of include and exclude patterns.
 * <p>
 *
 * Pattern Syntax: (taken from DirectoryScanner documentation)
 * <p>
 * A given directory is recursively scanned for all files
 * and directories. Each file/directory is matched against a set of selectors,
 * including special support for matching against filenames with include and
 * and exclude patterns. Only files/directories which match at least one
 * pattern of the include pattern list or other file selector, and don't match
 * any pattern of the exclude pattern list or fail to match against a required
 * selector will be placed in the list of files/directories found.
 * <p>
 * When no list of include patterns is supplied, "**" will be used, which
 * means that everything will be matched. When no list of exclude patterns is
 * supplied, an empty list is used, such that nothing will be excluded. When
 * no selectors are supplied, none are applied.
 * <p>
 * The filename pattern matching is done as follows:
 * The name to be matched is split up in path segments. A path segment is the
 * name of a directory or file, which is bounded by
 * <code>File.separator</code> ('/' under UNIX, '\' under Windows).
 * For example, "abc/def/ghi/xyz.java" is split up in the segments "abc",
 * "def","ghi" and "xyz.java".
 * The same is done for the pattern against which should be matched.
 * <p>
 * The segments of the name and the pattern are then matched against each
 * other. When '**' is used for a path segment in the pattern, it matches
 * zero or more path segments of the name.
 * <p>
 * There is a special case regarding the use of <code>File.separator</code>s
 * at the beginning of the pattern and the string to match:<br>
 * When a pattern starts with a <code>File.separator</code>, the string
 * to match must also start with a <code>File.separator</code>.
 * When a pattern does not start with a <code>File.separator</code>, the
 * string to match may not start with a <code>File.separator</code>.
 * When one of these rules is not obeyed, the string will not
 * match.
 * <p>
 * When a name path segment is matched against a pattern path segment, the
 * following special characters can be used:<br>
 * '*' matches zero or more characters<br>
 * '?' matches one character.
 * <p>
 * Examples:
 * <p>
 * "**\*.class" matches all .class files/dirs in a directory tree.
 * <p>
 * "test\a??.java" matches all files/dirs which start with an 'a', then two
 * more characters and then ".java", in a directory called test.
 * <p>
 * "**" matches everything in a directory tree.
 * <p>
 * "**\test\**\XYZ*" matches all files/dirs which start with "XYZ" and where
 * there is a parent directory called test (e.g. "abc\test\def\ghi\XYZ123").
 * <p>
 * Case sensitivity may be turned off if necessary. By default, it is
 * turned on.
 * <p>
 * Example of usage:
 * <pre>
 *   String[] includes = {"**\\*.class"};
 *   String[] excludes = {"modules\\*\\**"};
 *   ds.setIncludes(includes);
 *   ds.setExcludes(excludes);
 *   ds.setBasedir(new File("test"));
 *   ds.setCaseSensitive(true);
 *   ds.scan();
 *
 *   System.out.println("FILES:");
 *   String[] files = ds.getIncludedFiles();
 *   for (int i = 0; i < files.length; i++) {
 *     System.out.println(files[i]);
 *   }
 * </pre>
 * This will scan a directory called test for .class files, but excludes all
 * files in all proper subdirectories of a directory called "modules"
 *
 *  @author  Denis Krivitski
 *  <p>
 *  Date: 12/11/2013
 */
public class Zipper {

    private static final Logger logger = Logger.getLogger(Zipper.class);


    /**
     * Scans the base directory, filters the files, and writes the compressed file content to
     * the provided output stream.
     *
     * @param baseDir Contents of this directory will he filtered and zipped
     * @param filterPatterns Filter wildcard patterns
     * @param outputStream Compressed file content is written to this stream
     * @param maxZipSize Limits the number of bytes that will be written to the output stream.
     *                   Zero value means no limit. When the limit is reached, MaxZipSizeReached exception
     *                   is thrown.
     *                   NOTE: This limit is checked on file boundaries. Once the limit is reached
     *                   no more files will be written to the output stream. Therefore, the maxZipSize limit
     *                   may be beached by the compressed size of the last file.
     * @param listener A listener that is notified of the compression progress. Notification is sent
     *                 each time a new file begins compression.
     * @throws Zipper.MaxZipSizeReached If maxZipSize limit is reached
     * @throws NoFilesToZip If there are no files to zip. Either the base directory is empty or does not exists, or all
     *                      the files are filtered out by the filter.
     * @throws IOException
     */

    public void zip(File baseDir, String filterPatterns, OutputStream outputStream, long maxZipSize, ZipListener listener) throws IOException
    {
        assert baseDir!=null : "baseDir must not be null";
        assert outputStream !=null : "outputStream must not be null";

        DirectoryScanner ds = createDirectoryScanner(baseDir,filterPatterns);
        ds.scan();
        printDebug(ds);
        if (ds.getIncludedFiles().length == 0)
        {
            outputStream.close();
            logger.info("No files to zip");
            throw new NoFilesToZip();
        }
        zipFile(baseDir,ds.getIncludedFiles(),outputStream,maxZipSize,listener);
        return;
    }

    /**
     * Scans the base directory, filters the files, and returns the compressed contents as a byte array.
     *
     * @param baseDir Contents of this directory will he filtered and zipped
     * @param filterPatterns Filter wildcard patterns
     * @param maxZipSize Limits the number of bytes that will be written to the output stream.
     *                   Zero value means no limit. When the limit is reached, MaxZipSizeReached exception
     *                   is thrown.
     *                   NOTE: This limit is checked on file boundaries. Once the limit is reached
     *                   no more files will be written to the output stream. Therefore, the maxZipSize limit
     *                   may be beached by the compressed size of the last file.
     * @param listener A listener that is notified of the compression progress. Notification is sent
     *                 each time a new file begins compression.
     * @throws Zipper.MaxZipSizeReached If maxZipSize limit is reached
     * @throws NoFilesToZip If there are no files to zip. Either the base directory is empty or does not exists, or all
     *                      the files are filtered out by the filter.
     * @throws IOException
     */

    public byte[] zip(File baseDir, String filterPatterns, long maxZipSize, ZipListener listener) throws IOException {
        ByteOutputStream byteOutputStream = new ByteOutputStream();
        zip(baseDir,filterPatterns,byteOutputStream,maxZipSize,listener);
        return byteOutputStream.getBytes();
    }

    private void zipFile(File baseDir, String[] files, OutputStream outputStream, long maxZipSize,ZipListener listener) throws IOException {
        ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
        long compressedSize = 0;
        final double AVERAGE_ZIP_COMPRESSION_RATIO = 4.0;

        for(String fileName : files)
        {
            logger.debug("Adding file to zip: " + fileName);

            File file = new File(baseDir,fileName);

            if (maxZipSize > 0 && compressedSize + (file.length()/AVERAGE_ZIP_COMPRESSION_RATIO) > maxZipSize)
            {
                logger.info("Maximum zip file size reached. Zip size: " + compressedSize + " bytes Limit: " + maxZipSize + " bytes");
                zipOutputStream.close();
                throw new MaxZipSizeReached(compressedSize,maxZipSize);
            }

            if (listener!=null)
            {
                listener.updateProgress(fileName,compressedSize);
            }

            ZipEntry zipEntry = new ZipEntry(fileName);
            zipOutputStream.putNextEntry(zipEntry);
            FileInputStream fileInputStream = new FileInputStream(file);
            IOUtils.copy(fileInputStream, zipOutputStream);
            fileInputStream.close();
            zipOutputStream.closeEntry();
            compressedSize+=zipEntry.getCompressedSize();
        }

        zipOutputStream.close();
    }


    private DirectoryScanner createDirectoryScanner(File baseDir, String filterPatterns)
    {
        DirectoryScanner ds = new DirectoryScanner();
        ds.setBasedir(baseDir);
        ds.setCaseSensitive(false);
        ds.setFollowSymlinks(false);
        ds.setErrorOnMissingDir(false);


        LinkedList<String> includePatterns = new LinkedList<String>();
        LinkedList<String> excludePatterns = new LinkedList<String>();


        // Parse filter patterns
        String[] patterns;
        if (filterPatterns!=null)
        {
            patterns = StringUtils.split(filterPatterns,",\n");
        } else {
            patterns = new String[]{};
        }

        for(String pattern : patterns)
        {
            pattern = pattern.trim();
            if (pattern.length()>0)
            {
                if (pattern.startsWith("!"))
                {
                    pattern = pattern.substring(1); // Trim the "!"
                    excludePatterns.add(pattern);
                    logger.debug("Exclude pattern detected: >" + pattern + "<");
                } else {
                    includePatterns.add(pattern);
                    logger.debug("Include pattern detected: >" + pattern + "<");
                }
            }
        }

        if (includePatterns.size()>0)
        {
            ds.setIncludes(includePatterns.toArray(new String[]{}));
        }
        if (excludePatterns.size()>0)
        {
            ds.setExcludes(excludePatterns.toArray(new String[]{}));
        }
        return ds;
    }


    private void printDebug(DirectoryScanner ds)
    {
        if (!logger.isDebugEnabled())
        {
            return;
        }

        logger.debug("Base Directory: " + ds.getBasedir());

        for(String file : ds.getIncludedFiles())
        {
            logger.debug("Included: " + file);
        }

        for(String file : ds.getExcludedFiles())
        {
            logger.debug("Excluded File: " + file);
        }

        for(String file : ds.getExcludedDirectories())
        {
            logger.debug("Excluded Dir: " + file);
        }

        for(String file : ds.getNotFollowedSymlinks())
        {
            logger.debug("Not followed symbolic link: " + file);
        }
    }

    /**
     * Thrown when the number of bytes in output stream reaches maxZipSize limit
     */
    public static class MaxZipSizeReached extends IOException
    {
        private long compressedSize;
        private long maxZipSize;

        public MaxZipSizeReached(long compressedSize, long maxZipSize)
        {
            super("Zip compressed size reached a limit of " + maxZipSize + " bytes");
        }

        public long getCompressedSize() {
            return compressedSize;
        }

        public long getMaxZipSize() {
            return maxZipSize;
        }
    }

    /**
     * Thrown when there are no files to zip. Either the base directory is empty or does not exists, or all
     * the files are filtered out by the filter.
     */
    public static class NoFilesToZip extends IOException
    {
        public NoFilesToZip()
        {
            super("No files to zip");
        }
    }
}
