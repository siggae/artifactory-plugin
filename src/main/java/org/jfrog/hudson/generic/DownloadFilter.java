package org.jfrog.hudson.generic;

import java.util.Set;

import org.jfrog.build.api.dependency.DownloadableArtifact;

public interface DownloadFilter {

	public Set<DownloadableArtifact> filter(Set<DownloadableArtifact> downloadableArtifacts);
}
