package seedu.address.testutil;

import java.io.IOException;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessMode;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.spi.FileSystemProvider;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * FileSystemProvider used in tests to simulate unsupported atomic move
 * operations while delegating other calls to a real provider.
 */
public final class AtomicMoveUnsupportedFileSystemProvider extends FileSystemProvider {
    private final FileSystemProvider delegate;
    private AtomicMoveUnsupportedFileSystem fileSystem;
    private final AtomicBoolean atomicMoveAttempted = new AtomicBoolean(false);
    private final AtomicBoolean fallbackMoveUsed = new AtomicBoolean(false);

    /**
     * Create a provider that delegates to {@code delegate}.
     */
    public AtomicMoveUnsupportedFileSystemProvider(FileSystemProvider delegate) {
        this.delegate = delegate;
    }

    void setFileSystem(AtomicMoveUnsupportedFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    /** Returns true if an atomic move was attempted. */
    public boolean wasAtomicMoveAttempted() {
        return atomicMoveAttempted.get();
    }

    /** Returns true if the non-atomic fallback was used. */
    public boolean wasFallbackMoveUsed() {
        return fallbackMoveUsed.get();
    }

    private Path wrap(Path path) {
        return fileSystem.wrap(path);
    }

    private Path unwrap(Path path) {
        return fileSystem.unwrap(path);
    }

    @Override
    public String getScheme() {
        return delegate.getScheme();
    }

    @Override
    public FileSystem newFileSystem(URI uri, Map<String, ?> env) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public FileSystem getFileSystem(URI uri) {
        return fileSystem;
    }

    @Override
    public Path getPath(URI uri) {
        return wrap(delegate.getPath(uri));
    }

    @Override
    public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options,
            FileAttribute<?>... attrs) throws IOException {
        return delegate.newByteChannel(unwrap(path), options, attrs);
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(Path dir, DirectoryStream.Filter<? super Path> filter)
            throws IOException {
        DirectoryStream<Path> baseStream = delegate.newDirectoryStream(
                unwrap(dir), entry -> filter.accept(wrap(entry)));
        return new DirectoryStream<Path>() {
            @Override
            public Iterator<Path> iterator() {
                Iterator<Path> iterator = baseStream.iterator();
                return new Iterator<Path>() {
                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public Path next() {
                        return wrap(iterator.next());
                    }
                };
            }

            @Override
            public void close() throws IOException {
                baseStream.close();
            }
        };
    }

    @Override
    public void createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException {
        delegate.createDirectory(unwrap(dir), attrs);
    }

    @Override
    public void delete(Path path) throws IOException {
        delegate.delete(unwrap(path));
    }

    @Override
    public void copy(Path source, Path target, CopyOption... options) throws IOException {
        delegate.copy(unwrap(source), unwrap(target), options);
    }

    @Override
    public void move(Path source, Path target, CopyOption... options) throws IOException {
        if (options != null) {
            for (CopyOption option : options) {
                if (option == StandardCopyOption.ATOMIC_MOVE) {
                    atomicMoveAttempted.set(true);
                    throw new java.nio.file.AtomicMoveNotSupportedException(
                            source.toString(), target.toString(), "Simulated");
                }
            }
        }
        fallbackMoveUsed.set(true);
        delegate.move(unwrap(source), unwrap(target), options);
    }

    @Override
    public boolean isSameFile(Path path, Path path2) throws IOException {
        return delegate.isSameFile(unwrap(path), unwrap(path2));
    }

    @Override
    public boolean isHidden(Path path) throws IOException {
        return delegate.isHidden(unwrap(path));
    }

    @Override
    public FileStore getFileStore(Path path) throws IOException {
        return delegate.getFileStore(unwrap(path));
    }

    @Override
    public void checkAccess(Path path, AccessMode... modes) throws IOException {
        delegate.checkAccess(unwrap(path), modes);
    }

    @Override
    public <V extends FileAttributeView> V getFileAttributeView(Path path, Class<V> type, LinkOption... options) {
        return delegate.getFileAttributeView(unwrap(path), type, options);
    }

    @Override
    public <A extends BasicFileAttributes> A readAttributes(Path path, Class<A> type, LinkOption... options)
            throws IOException {
        return delegate.readAttributes(unwrap(path), type, options);
    }

    @Override
    public java.util.Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options)
            throws IOException {
        return delegate.readAttributes(unwrap(path), attributes, options);
    }

    @Override
    public void setAttribute(Path path, String attribute, Object value, LinkOption... options) throws IOException {
        delegate.setAttribute(unwrap(path), attribute, value, options);
    }

    @SuppressWarnings("unused")
    Path createTempFile(Path dir, String prefix, String suffix, FileAttribute<?>... attrs) throws IOException {
        Path created = Files.createTempFile(unwrap(dir), prefix, suffix, attrs);
        return wrap(created);
    }
}
