package com.kamikaze.docidset.labeled;

import java.io.IOException;
import java.util.Set;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.Weight;

public class NamedQuery extends Query
{
  private static final long serialVersionUID = 1L;

  protected final String name;
  protected final Query delegate;
  
  public NamedQuery(String name, Query delegate)
  {
    this.name = name;
    this.delegate = delegate;
  }
  
  public String getName()
  {
    return name;
  }
  
  public Query getDelegate()
  {
    return delegate;
  }
  
  @Override
  public void extractTerms(Set terms)
  {
    delegate.extractTerms(terms);
  }
  
  @Override
  public Weight weight(Searcher searcher) throws IOException
  {
    return super.weight(searcher);
  }

  @Override
  public Weight createWeight(Searcher searcher) throws IOException
  {
    return new NamedWeight(name, delegate.weight(searcher), getBoost(), searcher.getSimilarity());
  }

  @Override
  public String toString(String field)
  {
    return name + "{" + delegate.toString(field) + "}^" + getBoost();
  }

}
