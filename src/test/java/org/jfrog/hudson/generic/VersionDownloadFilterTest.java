package org.jfrog.hudson.generic;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.jfrog.build.api.dependency.DownloadableArtifact;
import org.jfrog.build.api.dependency.pattern.PatternType;
import org.junit.Test;

public class VersionDownloadFilterTest {

	@Test
	public void testVersionCompare(){
		
		String [][] testvector = {
				{ "0", "1.0.0", "1.0.0" },
				{ "-1", "1.0.0", "1.0.1" },
				{ "1", "1.0.1", "1.0" },
				{ "0", "1.0-0", "1.0.0" },
				{ "-1", "11.200.0-build.94", "11.200.1" },
				{ "1", "11.200.0-build.94", "11.200.0-build.85" },
				{ "1", "11.200.0.3.94", "11.200.0-build.85" },
				{ "1", "11.200.0", "zzz" }
		};
		
		for ( String[] vector : testvector)
		{
			int result = VersionDownloadFilter.versionCompare(vector[1], vector[2]);
			System.out.println(result);
			assertEquals (Integer.parseInt(vector[0]), result);
		}
		
	}
	
	@Test
	public void testFilter(){
		VersionDownloadFilter filter = new VersionDownloadFilter(new ConsoleLog());
		HashSet<DownloadableArtifact> downloadableArtifactsSet = new HashSet<DownloadableArtifact>();
		for(DownloadableArtifact artifact : downloadableArtifacts)
			downloadableArtifactsSet.add(artifact);
		Set<DownloadableArtifact> filteredArtifacts = filter.filter((Set<DownloadableArtifact>) downloadableArtifactsSet);
		assertEquals (2, filteredArtifacts.size());
		
	}
	
	DownloadableArtifact[] downloadableArtifacts = {
			new DownloadableArtifact(
					"https://artifactory/repo_key",
					"target_path",
					"path_to_version/2.1/dir/a.ext",
					"build.name=name_of_build",
					"path_to_version/*/**", PatternType.NORMAL),
			new DownloadableArtifact(
					"https://artifactory/repo_key",
					"target_path",
					"path_to_version/2.0/dir/b.ext",
					"build.name=name_of_build",
					"path_to_version/*/**", PatternType.NORMAL),
			new DownloadableArtifact(
					"https://artifactory/repo_key",
					"target_path",
					"path_to_version/2.0/dir/a.ext",
					"build.name=name_of_build",
					"path_to_version/*/**", PatternType.NORMAL),
			new DownloadableArtifact(
					"https://artifactory/repo_key",
					"target_path",
					"path_to_version/2.0/dir/c.ext",
					"build.name=name_of_build",
					"path_to_version/*/**", PatternType.NORMAL),
			new DownloadableArtifact(
					"https://artifactory/repo_key",
					"target_path",
					"path_to_version2/3.0/dir/e.ext",
					"build.name=name_of_build",
					"path_to_version2/*/**", PatternType.NORMAL),
			new DownloadableArtifact(
					"https://artifactory/repo_key",
					"target_path",
					"path_to_version2/3.1/dir/d.ext",
					"build.name=name_of_build",
					"path_to_version2/*/**", PatternType.NORMAL)
			 };
}
