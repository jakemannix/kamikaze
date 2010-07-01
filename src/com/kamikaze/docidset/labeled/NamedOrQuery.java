/**
 * 
 */
package com.kamikaze.docidset.labeled;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.ComplexExplanation;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.Weight;

/**
 * @author jmannix
 *
 */
public class NamedOrQuery extends Query
{
  private static final long serialVersionUID = 1L;

  protected List<NamedQuery> namedQueries = new ArrayList<NamedQuery>();
  
  public NamedOrQuery(NamedQuery... queries)
  {
    namedQueries.addAll(Arrays.asList(queries));
  }
  
  public void addQuery(String name, Query query)
  {
    addQuery(new NamedQuery(name, query));
  }
  
  public void addQuery(NamedQuery nq)
  {
    namedQueries.add(nq);
  }
  
  @Override
  public Weight createWeight(Searcher searcher) throws IOException
  {
    NamedWeight[] namedWeights = new NamedWeight[namedQueries.size()];
    for(int i=0; i<namedWeights.length; i++)
    {
      namedWeights[i] = (NamedWeight) namedQueries.get(i).createWeight(searcher);
      namedWeights[i].normalize(namedQueries.get(i).getBoost());
    }
    return new NamedFieldOrWeight(namedWeights);
  }
  
  @Override
  public void extractTerms(Set terms) 
  {
    for(NamedQuery q : namedQueries)
    {
      q.extractTerms(terms);
    }
  }
  
  protected class NamedFieldOrWeight extends Weight
  {
    private static final long serialVersionUID = 1L;

    protected NamedWeight[] namedWeights;
    
    public NamedFieldOrWeight(NamedWeight...namedWeights)
    {
      this.namedWeights = namedWeights;
    }
    
    public Explanation explain(IndexReader reader, int doc) throws IOException
    {
      ComplexExplanation exp = new ComplexExplanation();
      exp.setDescription("sum of: ");
      boolean isMatch = false;
      for(NamedWeight w : namedWeights)
      {
        Explanation subExp = w.explain(reader, doc);
        if(subExp.isMatch()) 
        {
          exp.setValue(exp.getValue() + subExp.getValue());
          exp.addDetail(subExp);
          isMatch = true;
        }
      }
      exp.setMatch(isMatch);
      return exp;
    }

    public Query getQuery()
    {
      return NamedOrQuery.this;
    }

    public float getValue()
    {
      return 1f;
    }

    public void normalize(float norm)
    {
      // TODO: need to do this?
    }

    public Scorer scorer(IndexReader reader, boolean scoreDocsInOrder, boolean topDocsScorer) throws IOException
    {
      NamedScorer[] namedScorer = new NamedScorer[namedWeights.length];
      for(int i=0; i<namedWeights.length; i++)
        namedScorer[i] = (NamedScorer) namedWeights[i].scorer(reader, scoreDocsInOrder, topDocsScorer);
      return new NamedDisjunctionSumScorer(Arrays.asList(namedScorer));
    }

    public float sumOfSquaredWeights() throws IOException
    {
      return 1f;
    }
    
  }

  /* (non-Javadoc)
   * @see org.apache.lucene.search.Query#toString(java.lang.String)
   */
  @Override
  public String toString(String field)
  {
    StringBuilder buf = new StringBuilder().append("( ");
    for(NamedQuery q : namedQueries)
    {
      buf.append(q.toString(field)).append(" ");
    }
    buf.append(")");
    return buf.toString();
  }

}
