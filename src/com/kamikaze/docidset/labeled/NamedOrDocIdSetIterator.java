package com.kamikaze.docidset.labeled;

import com.kamikaze.docidset.impl.OrDocIdSetIterator;
import org.apache.lucene.search.DocIdSet;

import java.io.IOException;
import java.util.List;

public class NamedOrDocIdSetIterator extends OrDocIdSetIterator {

  NamedOrDocIdSetIterator(List<DocIdSet> sets) throws IOException {
    super(sets);
  }


}
