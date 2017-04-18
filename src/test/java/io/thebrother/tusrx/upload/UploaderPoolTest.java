package io.thebrother.tusrx.upload;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

public class UploaderPoolTest {
    private UploaderPool pool;

    @Before
    public void before() {
        Path mockedPath = mock(Path.class);
        when(mockedPath.resolve(ArgumentMatchers.anyString())).thenReturn(mockedPath);
        FileSystem mockedFS = mock(FileSystem.class);
        FileSystemProvider fsProvider = mock(FileSystemProvider.class);
        when(mockedFS.provider()).thenReturn(fsProvider);
        pool = new UploaderPool(mockedPath);
        when(mockedPath.getFileSystem()).thenReturn(mockedFS);
    }

    @Test
    public void testNewUploaderIsPooled() {
        // arrange

        // act
        UUID newUploader = pool.newUploader(50L).toBlocking().first();

        // assert
        assertThat(pool.getUploader(newUploader)).isPresent();
    }

    @Test
    public void testNewUploaderHasRightUploadLength() {
        // arrange

        // act
        UUID newUploader = pool.newUploader(50L).toBlocking().first();

        // assert
        assertThat(pool.getUploader(newUploader)).map(TusUpload::getUploadLength)
                .contains(50L);
    }

    @Test
    public void testuploadIsRemovedWhenCallingRemove() {
        // arrange
        UUID newUploader = pool.newUploader(50L).toBlocking().first();

        // act
        pool.remove(newUploader);

        // assert
        assertThat(pool.getUploader(newUploader)).isEmpty();

    }
}
