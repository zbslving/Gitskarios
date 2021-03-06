package core.repositories.readme;

import com.alorma.github.sdk.bean.ReadmeInfo;
import core.datasource.CacheDataSource;
import core.datasource.CloudDataSource;
import core.datasource.SdkItem;
import core.repository.GenericRepository;
import rx.Observable;

public class RepoReadmeRepository extends GenericRepository<ReadmeInfo, String> {

  private CacheDataSource<ReadmeInfo, String> cache;
  private CloudDataSource<ReadmeInfo, String> cloud;
  private final CloudDataSource<String, String> githubMarkdwonCloud;

  public RepoReadmeRepository(CacheDataSource<ReadmeInfo, String> cache, CloudDataSource<ReadmeInfo, String> cloud,
      CloudDataSource<String, String> githubMarkdwonCloud) {
    super(cache, cloud);
    this.cache = cache;
    this.cloud = cloud;
    this.githubMarkdwonCloud = githubMarkdwonCloud;
  }

  @Override
  public Observable<SdkItem<String>> execute(SdkItem<ReadmeInfo> request) {

    Observable<SdkItem<String>> cacheObs = Observable.empty();
    Observable<SdkItem<String>> cloudObs = Observable.empty();
    if (cache != null) {
      cacheObs = cache.getData(request);
    }
    if (cacheObs == null) {
      cacheObs = Observable.empty();
    }
    if (cloud != null) {
      cloudObs = cloud.execute(request);
    }
    if (cloudObs == null) {
      cloudObs = Observable.empty();
    }
    cloudObs = cloudObs.flatMap(githubMarkdwonCloud::execute)
        .onErrorResumeNext(fallbackApi() != null ? fallbackApi() : Observable.empty())
        .doOnNext(StringSdkItem -> {
          if (cache != null) {
            cache.saveData(request, StringSdkItem);
          }
        });
    return Observable.concat(cacheObs, cloudObs).first();
  }
}
