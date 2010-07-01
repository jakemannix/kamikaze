/**
 * 
 */
package com.kamikaze.docidset.labeled;

import java.io.IOException;

import org.apache.lucene.search.Scorer;

public class NamedScorer extends Scorer
{
  protected String name;
  protected int id;
  protected Scorer delegateScorer;
  protected float boost;
  protected NamedScorer(NamedWeight namedWeight, Scorer scorer, float boost)
  {
    super(scorer.getSimilarity());
    name = namedWeight.getName();
    delegateScorer = scorer;
    this.boost = boost;
  }
  
  public String getName()
  {
    return name;
  }
  
  public int getId()
  {
    return id;
  }
  
  public void setId(int id)
  {
    this.id = id;
  }

  @Override
  public float score() throws IOException
  {
    return delegateScorer.score() * boost;
  }

  @Override
  public int docID()
  {
    return delegateScorer.docID();
  }

  @Override
  public int nextDoc() throws IOException
  {
    return delegateScorer.nextDoc();
  }

  @Override
  public int advance(int target) throws IOException
  {
    return delegateScorer.advance(target);
  }

}