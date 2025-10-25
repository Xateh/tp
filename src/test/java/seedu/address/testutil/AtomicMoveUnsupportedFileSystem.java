package seedu.address.testutil;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.WatchService;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Wrapper FileSystem used by tests to present Path objects that route through
 * a custom provider which can simulate atomic move failure.
 */
public final class AtomicMoveUnsupportedFileSystem extends FileSystem {
    private final FileSystem delegate;
    private final AtomicMoveUnsupportedFileSystemProvider provider;

    /**
     * Construct a wrapper around an existing {@link FileSystem} delegating
     * operations to {@code delegate} while exposing a custom provider.
     */
    public AtomicMoveUnsupportedFileSystem(FileSystem delegate,
            AtomicMoveUnsupportedFileSystemProvider provider) {
        this.delegate = Objects.requireNonNull(delegate);
        this.provider = Objects.requireNonNull(provider);
        this.provider.setFileSystem(this);
    }

    /** Wraps a {@link Path} so it appears to belong to this FileSystem. */
    public Path wrap(Path path) {
        if (path == null) {
            return null;
        }
        if (Proxy.isProxyClass(path.getClass())) {
            try {
                java.lang.reflect.InvocationHandler handler = java.lang.reflect.Proxy.getInvocationHandler(path);
                if (handler instanceof PathInvocationHandler) {
                    return path;
                }
            } catch (IllegalArgumentException e) {
                // fall through
            }
        }
        return (Path) java.lang.reflect.Proxy.newProxyInstance(
                AtomicMoveUnsupportedFileSystem.class.getClassLoader(),
                new Class<?>[] { Path.class },
                new PathInvocationHandler(this, path));
    }

    /** Unwraps a previously wrapped {@link Path} back to the delegate. */
    public Path unwrap(Path path) {
        if (path == null) {
            return null;
        }
        if (Proxy.isProxyClass(path.getClass())) {
            java.lang.reflect.InvocationHandler handler = java.lang.reflect.Proxy.getInvocationHandler(path);
            if (handler instanceof PathInvocationHandler) {
                return ((PathInvocationHandler) handler).getDelegate();
            }
        }
        return path;
    }

    @Override
    public FileSystemProvider provider() {
        return provider;
    }

    @Override
    public void close() {
        // Intentionally no-op: we don't close the default filesystem in tests
    }

    @Override
    public boolean isOpen() {
        return delegate.isOpen();
    }

    @Override
    public boolean isReadOnly() {
        return delegate.isReadOnly();
    }

    @Override
    public String getSeparator() {
        return delegate.getSeparator();
    }

    @Override
    public Iterable<Path> getRootDirectories() {
        List<Path> roots = new ArrayList<>();
        for (Path root : delegate.getRootDirectories()) {
            roots.add(wrap(root));
        }
        return Collections.unmodifiableList(roots);
    }

    @Override
    public Iterable<FileStore> getFileStores() {
        return delegate.getFileStores();
    }

    @Override
    public Set<String> supportedFileAttributeViews() {
        return delegate.supportedFileAttributeViews();
    }

    @Override
    public Path getPath(String first, String... more) {
        return wrap(delegate.getPath(first, more));
    }

    @Override
    public PathMatcher getPathMatcher(String syntaxAndPattern) {
        PathMatcher matcher = delegate.getPathMatcher(syntaxAndPattern);
        return path -> matcher.matches(unwrap(path));
    }

    @Override
    public UserPrincipalLookupService getUserPrincipalLookupService() {
        return delegate.getUserPrincipalLookupService();
    }

    @Override
    public WatchService newWatchService() throws IOException {
        return delegate.newWatchService();
    }
}
