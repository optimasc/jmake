package com.optimasc.pkgedit.targets;

import java.util.List;

/** Represents a build target */
public interface Target
{
  /** Returns this target name. */
  public String getTargetName();
  
  public List<String> getOtherDepedenciesFiles();
  public void setOtherDepedenciesFiles(List<String> otherDepedenciesFiles);
  public List<String> getSourceFiles();
  public void setSourceFiles(List<String> sourceFiles);
  public List<String> getIncludeDirectories();
  public void setIncludeDirectories(List<String> searchPaths);
  public List<String> getLinkDirectories();
  public void setLinkDirectories(List<String> linkDirectories);
  
  /** Returns a list of compile defines. */
  public List<String> getCompileDefines();
  /** Sets the list of compile defines. */
  public void setCompileDefines(List<String> compileDefines);
  
  /** Retrieves this target's compiler options. */
  public String getCompileOptions();
  /** Sets this compiler's options. */
  public void setCompileOptions(String options);
  
  /** Retrieves the object associated to a key on a this node. 
   *  The object must first have been set to this node by calling setUserData with the same key. */ 
  public Object getUserData(String key);
  /** Associate an object to a key on this node. 
   * The object can later be retrieved from this node by calling getUserData with the same key. 
   * @param key
   * @param data
   * @return
   */
  public Object setUserData(String key, Object data);
  
  
  public List<String> getLibraryFiles();
  public void setLibraryFiles(List<String> libraryFiles);
  
}
