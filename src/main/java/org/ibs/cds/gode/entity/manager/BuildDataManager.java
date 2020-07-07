package org.ibs.cds.gode.entity.manager;

import org.ibs.cds.gode.entity.repo.BuildDataRepository;
import org.ibs.cds.gode.entity.type.BuildData;
import org.ibs.cds.gode.pagination.PageContext;
import org.ibs.cds.gode.pagination.PagedData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BuildDataManager extends PureEntityManager< BuildData, Long> {

    @Autowired
    public BuildDataManager(BuildDataRepository repo) {
        super(repo,null);
    }

    public BuildData findLatestBuild(String appName, Long appVersion){
        BuildDataRepository repo = super.repository.get();
        return repo.findLatestBuild(appName, appVersion);
    }

    public BuildData findLatestBuild(Long appId){
        BuildDataRepository repo = repository.get();
        return repo.findLatestBuild(appId);
    }

    public PagedData<BuildData> findBuild(String appName, Long appVersion, PageContext context){
        BuildDataRepository repo = repository.get();
        return repo.findBuild(appName, appVersion, context);
    }

    public PagedData<BuildData> findBuild(Long appId, PageContext context){
        BuildDataRepository repo = repository.get();
        return repo.findBuild(appId, context);
    }

    @Override
    public Optional<BuildData> doSave(Optional<BuildData> entityOps) {
       return entityOps.map(entity->{
            BuildData data = this.findLatestBuild(entity.getApp().getName(), entity.getApp().getVersion());
            if (data != null) {
                data.setLatest(false);
                BuildDataRepository repo = repository.get();
                repo.save(data);
            }
            entity.setLatest(true);
            return super.doSave(Optional.of(entity));
        }).orElse(Optional.empty());

    }
}
