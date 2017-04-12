package io.thebrother.tusrx.upload;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UploaderPool {
    
    private final Map<UUID, TusUpload> uploaders = new ConcurrentHashMap<>();

    public UUID newUploader() {
        UUID uuid = UUID.randomUUID();
        
        TusUpload upload = new TusUpload(uuid, this);
        upload.start();
        uploaders.put(uuid, upload);
        return uuid;
    }

    public Optional<TusUpload> getUploader(UUID uuid) {
        return Optional.ofNullable(uploaders.get(uuid));
    }

    TusUpload remove(UUID uuid) {
        return uploaders.remove(uuid);
    }
    
}
