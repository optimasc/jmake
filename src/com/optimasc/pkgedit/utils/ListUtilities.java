package com.optimasc.pkgedit.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListUtilities
{
  public static final String LIST_VALUE_SEPARATOR = ";";

  public ListUtilities()
  {
  }

  /**
   * Splits a string separated by LIST_VALUE_SEPARATOR into its different
   * components
   */
  public static final String[] split(String s)
  {
    /* Return empty array if string is empty */
    if (s.length() == 0)
      return new String[]{};
    return s.split(LIST_VALUE_SEPARATOR);
  }

  /**
   * Splits a string separated by LIST_VALUE_SEPARATOR into its different
   * components
   */
  public static final List<String> splitToList(String s)
  {
    /* Return empty array if string is empty */
    if (s.length() == 0)
      return new ArrayList<String>(0);
    String[] values = s.split(LIST_VALUE_SEPARATOR);
    ArrayList<String> elements = new ArrayList<String>(values.length);
    for (int i = 0; i < values.length; i++)
    {
      elements.add(values[i]);
    }
    return elements;
  }

  public static List<String> stringsToList(String[] values)
  {
    if (values.length == 0)
      return new ArrayList<String>(0);
    ArrayList<String> elements = new ArrayList<String>(values.length);
    for (int i = 0; i < values.length; i++)
    {
      elements.add(values[i]);
    }
    return elements;
  }

  /** Creates a string separated by LIST_VALUE_SEPARATOR from a string array. */
  public static final String merge(String[] stringList)
  {
    String s = "";
    for (int i = 0; i < stringList.length - 1; i++)
    {
      s = s + stringList[i] + LIST_VALUE_SEPARATOR;
    }
    if (stringList.length > 0)
      s = s + stringList[stringList.length - 1];
    return s;
  }

  /** Creates a string separated by LIST_VALUE_SEPARATOR from a string array. */
  public static final String merge(List<String> stringList)
  {
    String s = "";
    for (int i = 0; i < stringList.size() - 1; i++)
    {
      s = s + stringList.get(i) + LIST_VALUE_SEPARATOR;
    }
    if (stringList.size() > 0)
      s = s + stringList.get(stringList.size() - 1);
    return s;
  }

  /** Creates a string separated by c from a string array. */
  public static final String mergeWithChar(String[] stringList, char c)
  {
    String s = "";
    for (int i = 0; i < stringList.length - 1; i++)
    {
      s = s + stringList[i] + c;
    }
    if (stringList.length > 0)
      s = s + stringList[stringList.length - 1];
    return s;
  }
  
  /** Creates a string separated by c from a string array. */
  public static final String mergeWithChar(List<String> stringList, char c)
  {
    String s = "";
    for (int i = 0; i < stringList.size() - 1; i++)
    {
      s = s + stringList.get(i) + c;
    }
    if (stringList.size() > 0)
      s = s + stringList.get(stringList.size() - 1);
    return s;
  }
  
  public static String listToString(List<String> v)
  {
    StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < v.size(); i++)
    {
      buffer.append(v.get(i));
      buffer.append(' ');
    }
    return buffer.toString().trim();
  }
  
  
  public static <T> boolean hasDuplicate(Iterable<T> all) 
  {
    Set<T> set = new HashSet<T>();
    // Set#add returns false if the set does not change, which
    // indicates that a duplicate element has been added.
    for (T each: all) if (!set.add(each)) return true;
    return false;
}

}
