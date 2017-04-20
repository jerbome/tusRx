package io.thebrother.tusrx.upload;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import rx.Observable;

public class UploaderPool {
    
    private final Map<UUID, TusUpload> uploaders = new ConcurrentHashMap<>();
    
    private final Path rootDir;
    
    public UploaderPool(Path rootDir) {
        this.rootDir = rootDir;
    }

    public Observable<UUID> newUploader(long uploadLength) {
        UUID uuid = UUID.randomUUID();
        
        TusUpload upload = new TusUpload(uuid, rootDir, this, uploadLength, Observable.timer(3, TimeUnit.MINUTES));
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
