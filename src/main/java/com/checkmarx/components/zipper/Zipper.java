package com.checkmarx.components.zipper;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tools.ant.DirectoryScanner;

import java.io.File;
import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: Denis Krivitski
 * Date: 12/11/2013
 * Time: 15:10
 * Description:
 *  This class implements a file zipper with filter. Zipper will traverse a specified base directory
 *  and archive all files passing the specified filter test. The filter string is a comma separated list
 *  of include and exclude patterns.
 *
 */
public class Zipper {

    private static final Logger logger = Logger.getLogger(Zipper.class);

    public byte[] zip(File baseDir, String filterPatterns)
    {
        DirectoryScanner ds = createDirectoryScanner(baseDir,filterPatterns);
        ds.scan();
        printDebug(ds);


        return null;
    }


    private DirectoryScanner createDirectoryScanner(File baseDir, String filterPatterns)
    {
        DirectoryScanner ds = new DirectoryScanner();
        ds.setBasedir(baseDir);

        LinkedList<String> includePatterns = new LinkedList<String>();
        LinkedList<String> excludePatterns = new LinkedList<String>();


        // Parse filter patterns
        String[] patterns = StringUtils.split(filterPatterns,",\n");
        for(String pattern : patterns)
        {
            pattern = pattern.trim();
            if (pattern.length()>0)
            {
                if (pattern.startsWith("!"))
                {
                    pattern = pattern.substring(1); // Trim the "!"
                    excludePatterns.add(pattern);
                } else {
                    includePatterns.add(pattern);
                }
            }
        }

        ds.setIncludes(includePatterns.toArray(new String[]{}));
        ds.setExcludes(excludePatterns.toArray(new String[]{}));
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
     }
}
