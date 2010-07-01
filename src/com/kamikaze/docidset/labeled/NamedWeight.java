/**
 * 
 */
package com.kamikaze.docidset.labeled;

import java.io.IOException;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.ComplexExplanation;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.search.Weight;


public class NamedWeight extends Weight {
  private static final long serialVersionUID = 1L;
  
  protected final String name;
  protected final Weight delegateWeight;
  protected final float boost;
  protected final Similarity sim;
  
  public NamedWeight(String name, Weight weight, float boost, Similarity sim)
  {
    this.name = name;
    delegateWeight = weight;
    this.boost = boost;
    this.sim = sim;
  }
  
  public String getName()
  {
    return name;
  }
  
  public Explanation explain(IndexReader reader, int doc) throws IOException
  {
    Explanation exp = delegateWeight.explain(reader, doc);
    ComplexExplanation cExp = new ComplexExplanation();
    cExp.setDescription(name + "^" + boost + " : ");
    cExp.addDetail(exp);
    cExp.setValue(exp.getValue() * boost);
    cExp.setMatch(exp.isMatch());
    return cExp;
  }

  public Query getQuery()
  {
    return delegateWeight.getQuery();
  }

  public float getValue()
  {
    return delegateWeight.getValue();
  }

  public void normalize(float norm)
  {
    try
    {
      float delegateSumOfSquaredWeights = delegateWeight.sumOfSquaredWeights();
      float delegateNorm = sim.queryNorm(delegateSumOfSquaredWeights);
      delegateWeight.normalize(delegateNorm);
      return;
    }
    catch(IOException ioe) 
    {
      throw new RuntimeException(ioe);
    }
    //delegateWeight.normalize(norm);
  }

  @Override
  public Scorer scorer(IndexReader reader, boolean scoreDocsInOrder, boolean topScorer) throws IOException {
    return new NamedScorer(this, delegateWeight.scorer(reader, scoreDocsInOrder, topScorer), boost);
  }

  public float sumOfSquaredWeights() throws IOException
  {
    return -1;//delegateWeight.sumOfSquaredWeights();
  }
}