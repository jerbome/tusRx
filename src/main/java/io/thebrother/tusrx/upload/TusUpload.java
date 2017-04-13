package io.thebrother.tusrx.upload;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.thebrother.tusrx.entry.TusRequest;

import rx.Observable;
import rx.schedulers.Schedulers;

public class TusUpload {
    private static final Logger logger = LoggerFactory.getLogger(TusUpload.class);

    private final UUID uuid;

    private final Path rootDir;

    private final UploaderPool pool;

    private int chunkNumber = 0;

    public TusUpload(UUID uuid, UploaderPool pool, Path rootDir) {
        this.uuid = uuid;
        this.pool = pool;
        this.rootDir = rootDir;
    }

    public Observable<Long> uploadChunk(TusRequest request) {
        try {
            FileChannel fChannel = FileChannel.open(
                    Paths.get("/Users/jlefrere/perso/tusRx/tmp/", uuid.toString(), "chunk-" + ++chunkNumber),
                    new StandardOpenOption[] { StandardOpenOption.CREATE, StandardOpenOption.WRITE });

            return request.getContent().doOnNext(bb -> logger.debug("received some ByteBuffer"))
                    .buffer(100, TimeUnit.MILLISECONDS)
                    .filter(bb -> bb.size() > 0)
                    .timeout(300, TimeUnit.MILLISECONDS)
                    .flatMap(bb -> Observable.fromCallable(() -> {
                        try {
                            logger.debug("writing " + bb.size() + " buffers  to file");
                            return fChannel.write(bb.toArray(new ByteBuffer[bb.size()]));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }).subscribeOn(Schedulers.io()))
                    .doOnError(x -> { 
                        logger.error("something went wrong, cleaning up", x);
                        cleanup(fChannel);
                    })
                    .doOnCompleted(() -> { 
                        logger.debug("cool bro, it is done, cleaning up");
                    });
        } catch (IOException e1) {
            return Observable.error(e1);
        }
    }

    public Observable<Path> start() {
        return Observable.fromCallable(() -> { 
            Set<PosixFilePermission> perms =
                    PosixFilePermissions.fromString("rwxr-x---");
                FileAttribute<Set<PosixFilePermission>> attr =
                    PosixFilePermissions.asFileAttribute(perms);
                return Files.createDirectories(rootDir.resolve(uuid.toString()), attr);
        });
    }

    private void cleanup(FileChannel fChannel) {
        try {
            fChannel.close();
        } catch (IOException e) {
            logger.error("failed to close channel", e);
        }
        pool.remove(uuid);
    }

}
