package org.jfrog.hudson.generic;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jfrog.build.api.util.Log;
import org.jfrog.build.api.dependency.DownloadableArtifact;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;

public class VersionDownloadFilter implements DownloadFilter {

	private Log log = null;
	Table<String, String, Set<DownloadableArtifact>> versionTable;

	public VersionDownloadFilter(Log log)
	{
		this.log = log;
		versionTable = HashBasedTable.create();
	}
	
	public Set<DownloadableArtifact> filter(
			Set<DownloadableArtifact> downloadableArtifacts) {
		
		Set<DownloadableArtifact> filteredDownloadableArtifacts = Sets.newHashSet();
		for (DownloadableArtifact downloadableArtifact : downloadableArtifacts)
		{
			
			log.info("new DownloadableArtifact( "
					+ "\"" + downloadableArtifact.getRepoUrl()
					+ "\", \""+ downloadableArtifact.getTargetDirPath() 
					+ "\", \""+ downloadableArtifact.getFilePath()
					+ "\", \""+ downloadableArtifact.getMatrixParameters()
					+ "\", \""+ downloadableArtifact.getSourcePattern()
					//+ "\", \""+ downloadableArtifact.getPatternType()
					+ "\", PatternType.NORMAL);");
			int firstWildcard = downloadableArtifact.getSourcePattern().indexOf("*");
			//check that it is a single wildcard and not double (**)
			if(firstWildcard > 0
					&& (downloadableArtifact.getSourcePattern().length() > firstWildcard)
					&& (downloadableArtifact.getSourcePattern().charAt(firstWildcard+1) != '*'))
			{
				//we are safe now that the first wildcard is a single one
				String regexPart1 = downloadableArtifact.getSourcePattern().substring(0, firstWildcard);
				String regexPart2 = downloadableArtifact.getSourcePattern().substring(firstWildcard+1).replaceAll("(\\*\\*|\\*)", ".*");
				String regex = regexPart1 + "([^"+ downloadableArtifact.getSourcePattern().charAt(firstWildcard+1) +"]*)" + regexPart2;
				//log.error(regex);
				Pattern p = Pattern.compile(regex);
				Matcher m = p.matcher(downloadableArtifact.getFilePath());
				if(m.matches())
				{
					//log.error(m.groupCount() + " " + m.group());
					//log.error("Version: " + m.group(1));
					if(versionTable.get(regexPart1, m.group(1)) == null)
					{
						Set<DownloadableArtifact> newSet = Sets.newHashSet();
						versionTable.put(regexPart1, m.group(1), newSet);
					}
					versionTable.get(regexPart1, m.group(1)).add(downloadableArtifact);
				}
				else
				{
					log.error("Pattern does not match filepath!");
					filteredDownloadableArtifacts.add(downloadableArtifact);
				}
			}
			else
			{
				filteredDownloadableArtifacts.add(downloadableArtifact);
			}
		}
		for(String rowKey : versionTable.rowKeySet())
		{
			String latestVersion = "";
			for(String version : versionTable.row(rowKey).keySet())
			{
				log.debug(rowKey + ":" + version + ":" + versionTable.get(rowKey, version).size());
				if(-1 == versionCompare(latestVersion, version))
					latestVersion = version;
			}
			log.info(rowKey + ":" + latestVersion+ " will be downloaded");
			/*should the diredctory with the wildcard kept?
			 * if(false)
				filteredDownloadableArtifacts.addAll(versionTable.get(rowKey, latestVersion));
			else*/
			{
				for(DownloadableArtifact da : versionTable.get(rowKey, latestVersion))
				{
					DownloadableArtifact daNew = new FilteredDownloadableArtifact(da);
					filteredDownloadableArtifacts.add(daNew);
					log.debug(daNew.toString());
					
				}
				//log.debug("filteredDownloadableArtifacts for key "+ rowKey + ": " +filteredDownloadableArtifacts.size());
			}
		}
		
		return filteredDownloadableArtifacts;
	}

	/**
	 * taken originally from http://stackoverflow.com/questions/6701948/efficient-way-to-compare-version-strings-in-java
	 * Compares two version strings. 
	 * 
	 * Use this instead of String.compareTo() for a non-lexicographical 
	 * comparison that works for version strings. e.g. "1.10".compareTo("1.6").
	 * 
	 * @note It does not work if "1.10" is supposed to be equal to "1.10.0".
	 * 
	 * @param str1 a string of ordinal numbers separated by decimal points. 
	 * @param str2 a string of ordinal numbers separated by decimal points.
	 * @return The result is a negative integer if str1 is _numerically_ less than str2. 
	 *         The result is a positive integer if str1 is _numerically_ greater than str2. 
	 *         The result is zero if the strings are _numerically_ equal.
	 */
	public static Integer versionCompare(String str1, String str2)
	{
	    String[] vals1 = str1.split("\\.|-");
	    String[] vals2 = str2.split("\\.|-");
	    int i = 0;
	    // set index to first non-equal ordinal or length of shortest version string
	    while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i])) 
	    {
	      i++;
	    }
	    // compare first non-equal ordinal number
	    if (i < vals1.length && i < vals2.length) 
	    {
	    	Integer val1 = null;
	    	Integer val2 = null;
	    	try { val1 = Integer.valueOf(vals1[i]); } catch(NumberFormatException nfe) { }
	    	try { val2 = Integer.valueOf(vals2[i]); } catch(NumberFormatException nfe) { }

	    	if( val1 == null && val2 == null)
	    	{
	    		return Integer.signum(vals1[i].compareTo(vals2[i]));
	    	}
	    	else if(val1 == null)
	    	{
	    		return -1;
	    	}
	    	else if(val2 == null)
	    	{
	    		return 1;
	    	}
	    	else
	    	{	
	    		int diff = val1.compareTo(val2);
	        	return Integer.signum(diff);
	    	}
	    }
	    // the strings are equal or one string is a substring of the other
	    // e.g. "1.2.3" = "1.2.3" or "1.2.3" < "1.2.3.4"
	    else
	    {
	        return Integer.signum(vals1.length - vals2.length);
	    }
	}
}
