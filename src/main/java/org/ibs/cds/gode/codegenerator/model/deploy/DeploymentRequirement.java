package org.ibs.cds.gode.codegenerator.model.deploy;

import org.ibs.cds.gode.codegenerator.entity.CodeApp;
import org.ibs.cds.gode.codegenerator.entity.CodeEntity;
import org.ibs.cds.gode.codegenerator.entity.StorePolicy;
import org.ibs.cds.gode.entity.store.StoreType;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DeploymentRequirement {

   public static Map<StoreType, Set<StorePolicy>> getStoreRequirements(CodeApp codeApp){
       Set<CodeEntity> entities = new HashSet();
       entities.addAll(codeApp.getEntities());
       entities.addAll(codeApp.getDependencies());
       return entities.stream()
               .map(k->k.getStorePolicy())
               .filter(k->k.isAvailable())
               .map(k->k.getPolicy())
               .collect(Collectors.groupingBy(s->s.getStoreName().getStoreType(), Collectors.toUnmodifiableSet()));
   }

    public static boolean isCacheNeeded(Map<StoreType, Set<StorePolicy>> container){
       return container.values().stream().flatMap(k->k.stream().filter(StorePolicy::isCached)).count() > 0;
    }

    public static boolean isCacheNeeded(Map<StoreType, Set<StorePolicy>> container, CodeApp codeApp){
        return container.values().stream().flatMap(k->k.stream().filter(StorePolicy::isCached)).count() > 0;
    }

    public static boolean isQueueManagerNeeded(Map<StoreType, Set<StorePolicy>> container){
        return container.values().stream().flatMap(k->k.stream().filter(StorePolicy::isAsyncStoreSync)).count() > 0;
    }
}
