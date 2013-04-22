/*
 * Copyright (C) Qatar Computing Research Institute, 2013.
 * All rights reserved.
 */

package qa.qcri.nadeef.core.operator;

import com.google.common.collect.Lists;
import qa.qcri.nadeef.core.datamodel.TupleCollection;
import qa.qcri.nadeef.core.datamodel.TuplePair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Iterator which generates pair tuples.
 */
public class TuplePairIterator extends PairIterator {
    /**
     * Execute the operator.
     *
     * @param tupleCollections input tuples.
     * @return output object.
     */
    @Override
    public Collection<TuplePair> execute(Collection<TupleCollection> tupleCollections)
        throws Exception {
        ArrayList<TuplePair> result = new ArrayList();
        List<TupleCollection> collectionList = Lists.newArrayList(tupleCollections);
        for (TupleCollection tuples : collectionList) {
            for (int i = 0; i < tuples.size(); i ++) {
                for (int j = i + 1; j < tuples.size(); j ++) {
                    TuplePair pair = new TuplePair(tuples.get(i), tuples.get(j));
                    result.add(pair);
                }
            }
        }
        return result;
    }
}
