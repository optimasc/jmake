package com.optimasc.pkgedit.writers;

import java.io.IOException;

import com.optimasc.pkgedit.targets.ProjectInfo;

/** This interface needs to be implemented by each project exporter.  
 * 
 */
public interface ProjectWriter
{
  /** Return the name of this streamer. */
  public String getName();
  
  /** Called by the system to initialize this plugin.
   * 
   * @param project Project information that will be exported.
   */
  public void init(ProjectInfo project);

  /** This is called by the system to actually store the the configuration
   *  information for the project in the correct format.
   *  
   * @param project Full project information that needs to be exported.
   * @throws IOException
   * @throws UnsupportedOperationException
   */
  public void store(ProjectInfo project) throws IOException, UnsupportedOperationException;
  
  /** Called by the system to shutdown this plugin.
   * 
   * @param project Project information that was exported.
   */
  public void done(ProjectInfo project);
  
  /** Called by the system to determine if this exporter supports the specified language.
   * 
   * 
   * @param language The language to verify, the value is passed in upper-case letters.
   * @return true if the language is supported by the exporter, otherwise false;
   */
  public boolean isSupportedLanguage(String language);

}
