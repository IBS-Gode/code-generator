package org.ibs.cds.gode.codegenerator.counter;

import org.ibs.cds.gode.counter.CounterGenerator;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.math.BigInteger;
import java.util.concurrent.ConcurrentMap;

@Service
public class CodeGenerationCounter implements CounterGenerator {

    private ConcurrentMap<String,BigInteger> map;
    private DB db;
    private CodeGenerationCounter(){
        this.db = DBMaker.fileDB("counter.db").fileMmapEnable().make();
        this.map = db.hashMap("map", Serializer.STRING, Serializer.BIG_INTEGER).createOrOpen();
    }
    @Override
    public BigInteger getNextValue(String context) {
        BigInteger nextValue = getCurrentValue(context).add(BigInteger.ONE);
        map.put(context, nextValue);
        return nextValue;
    }

    @Override
    public BigInteger getCurrentValue(String context) {
        map.computeIfAbsent(context, k -> BigInteger.ONE );
        return map.get(context);
    }

    @Override
    public boolean increment(String context) {
        BigInteger nextValue = getCurrentValue(context).add(BigInteger.ONE);
        map.put(context, nextValue);
        return true;
    }

    @Override
    public boolean atomicIncrement(String context) {
        return increment(context);
    }

    @PreDestroy
    public void close(){
        this.db.close();
    }

}
