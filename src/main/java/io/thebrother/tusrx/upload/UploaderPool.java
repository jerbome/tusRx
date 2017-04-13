package io.thebrother.tusrx.upload;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import rx.Observable;

public class UploaderPool {
    
    private final Map<UUID, TusUpload> uploaders = new ConcurrentHashMap<>();
    
    private final Path rootDir;
    
    public UploaderPool(Path rootDir) {
        this.rootDir = rootDir;
    }

    public Observable<UUID> newUploader() {
        UUID uuid = UUID.randomUUID();
        
        TusUpload upload = new TusUpload(uuid, this, rootDir);
        Observable<UUID> start = upload.start().ignoreElements().cast(UUID.class);
        uploaders.put(uuid, upload);
        return start.concatWith(Observable.just(uuid));
    }

    public Optional<TusUpload> getUploader(UUID uuid) {
        return Optional.ofNullable(uploaders.get(uuid));
    }

    TusUpload remove(UUID uuid) {
        return uploaders.remove(uuid);
    }
    
}
