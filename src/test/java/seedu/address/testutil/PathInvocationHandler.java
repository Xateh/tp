package seedu.address.testutil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.Iterator;

/**
 * InvocationHandler that adapts a delegated Path to appear to belong to the
 * test wrapper FileSystem. Used by {@link AtomicMoveUnsupportedFileSystem}.
 */
final class PathInvocationHandler implements InvocationHandler {
    private final AtomicMoveUnsupportedFileSystem fileSystem;
    private final Path delegate;
    PathInvocationHandler(AtomicMoveUnsupportedFileSystem fileSystem, Path delegate) {
        this.fileSystem = fileSystem;
        this.delegate = delegate;
    }

    Path getDelegate() {
        return delegate;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String name = method.getName();
        if ("getFileSystem".equals(name) && method.getParameterCount() == 0) {
            return fileSystem;
        }
        if ("equals".equals(name) && method.getParameterCount() == 1) {
            Object other = args[0];
            if (other instanceof Path) {
                return delegate.equals(fileSystem.unwrap((Path) other));
            }
            return false;
        }
        if ("compareTo".equals(name) && method.getParameterCount() == 1) {
            Path other = fileSystem.unwrap((Path) args[0]);
            return delegate.compareTo(other);
        }

        Object[] convertedArgs = args;
        if (args != null && args.length > 0) {
            convertedArgs = new Object[args.length];
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                if (arg instanceof Path) {
                    convertedArgs[i] = fileSystem.unwrap((Path) arg);
                } else if (arg instanceof Path[]) {
                    Path[] original = (Path[]) arg;
                    Path[] converted = new Path[original.length];
                    for (int j = 0; j < original.length; j++) {
                        converted[j] = fileSystem.unwrap(original[j]);
                    }
                    convertedArgs[i] = converted;
                } else {
                    convertedArgs[i] = arg;
                }
            }
        }

        Object result = method.invoke(delegate, convertedArgs);

        if (result instanceof Path) {
            return fileSystem.wrap((Path) result);
        }
        if ("iterator".equals(name) && result instanceof Iterator) {
            Iterator<?> iterator = (Iterator<?>) result;
            return new Iterator<Path>() {
                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                @Override
                public Path next() {
                    return fileSystem.wrap((Path) iterator.next());
                }
            };
        }
        return result;
    }
}
