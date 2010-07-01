package com.kamikaze.docidset.labeled;

import com.kamikaze.docidset.impl.OrDocIdSet;
import org.apache.lucene.search.DocIdSet;

import java.util.List;

public class NamedOrDocIdSet extends OrDocIdSet {

  public NamedOrDocIdSet(List<DocIdSet> docSets) {
    super(docSets);
  }
}
