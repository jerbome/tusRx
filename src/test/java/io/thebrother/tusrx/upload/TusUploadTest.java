package io.thebrother.tusrx.upload;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.*;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.thebrother.tusrx.entry.TusRequest;

import rx.Observable;

public class TusUploadTest {
    private TusUpload upload;

    private final UUID uuid = UUID.randomUUID();
    private Path rootDir;
    @Mock
    private UploaderPool pool;
    @Mock
    private TusRequest request;
    @Mock
    private FileChannel mockedFileChannel;
    
    private long uploadLength;
    private Observable<Long> pseudoTimer = Observable.just(0L);

    @Before
    public void setupTest() {
        MockitoAnnotations.initMocks(this);
        uploadLength = 100L;
        createUploadDir();
        upload = new TusUpload(uuid, rootDir, pool, uploadLength, pseudoTimer);
    }

    private void createUploadDir() {
        Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwxr-x---");
        FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(perms);
        try {
            rootDir = Files.createTempDirectory("uploadTest", attr);
        } catch (IOException e) {
            fail("couldn't create a temp directory", e);
        }
    }

    @Test
    public void testOffsetIsInitiallyZero() {
        // arrange

        // act
        AtomicLong offset = upload.getOffset();

        // assert
        assertThat(offset.get()).isEqualTo(0L);
    }

    @Test
    public void testStartLaunchesTimer() {
        // arrange

        // act
        upload.start();

        // assert
        verify(pool).remove(uuid);
    }

    @Test
    public void testUploadChunk() {
        // arrange
        Observable<ByteBuffer> content = Observable.just(ByteBuffer.wrap(new byte[100]));
        when(request.getContent()).thenReturn(content);
        upload.start().subscribe();

        // act
        Observable<Long> uploadChunk = upload.uploadChunk(request);

        // assert
        assertThat(uploadChunk.toBlocking().single()).isEqualTo(100);
    }
}
