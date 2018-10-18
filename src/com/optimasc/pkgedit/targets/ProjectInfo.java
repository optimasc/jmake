package com.optimasc.pkgedit.targets;

import java.util.Map;

public interface ProjectInfo 
{
	public String getName();
	public void setName(String name);
	public String getVersionMajor();
	public void setVersionMajor(String versionMajor);
	public String getVersionMinor();
	public void setVersionMinor(String versionMinor);
	public String getVersionPatch();
	public void setVersionPatch(String versionPatch);
	public String getRootDirectory();
	public void setRootDirectory(String rootDirectory);
	public void addTarget(String name,Target target);
	/** Returns a read only Map of all targets defined in this project */
    public Map<String,Target> getTargets();
    /** Return a specific target. */
    public Target getTarget(String name);
}
